package com.cpa;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sidhavratha on 27/12/14.
 */
@Aspect
public class CpaAspect {

    public static final InheritableThreadLocal<Set<String>> THREAD_LOCAL = new InheritableThreadLocal();

    private static Map<Integer, CpaServerSocket> PORT_TO_SERVERSOCKET = new HashMap<Integer, CpaServerSocket>();

    private static Set<Socket> CPA_SOCKETS = new HashSet<Socket>();

    private static Set<Socket> CLIENT_SOCKETS = new HashSet<Socket>();
    /*@Around("execution(* Example.*(..))")
    public Object aroundServer(ProceedingJoinPoint point) {
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("Around Example : "+MethodSignature.class.cast(point.getSignature()).getMethod().getName());
        return "Modified "+ result;
    }
    */
    @Pointcut("call (* *..*(..))")
    void inject()
    {

    }

    //execution (* java..*(..)) ||
    @Pointcut("call (* com.cpa.CpaAspect.*(..)) || call (* com.cpa.ClassCollector.*(..)) || call (* javax..*(..)) || call (* java..*(..)) || call (* org.aspectj..*(..)) || call (* org.junit..*(..))")
    void doNotInject()
    {

    }

    @Pointcut("inject() && !doNotInject()")
    void everything()
    {

    }

    @Pointcut("call(java.net.ServerSocket.new(..))")
    void newServerSocket()
    {

    }

    @Pointcut("call (* java.net.ServerSocket.accept(..))")
    void serverSocketMethod()
    {

    }


    @Pointcut("call(java.net.Socket.new(..))")
    void newSocket()
    {

    }

    @Pointcut("call (* java.net.Socket.getInputStream(..))")
    void socketMethodInput()
    {

    }

    @Pointcut("call (* java.net.Socket.close(..))")
    void socketMethodOutputClose()
    {

    }

    @Pointcut("call (* java.net.Socket.getOutputStream(..))")
    void socketMethodOutput()
    {

    }

    //todo - not catching extended new
    @AfterReturning("newServerSocket()")
    public void afterNewServerSocket(JoinPoint.EnclosingStaticPart staticPoint, JoinPoint point, Object result) throws Throwable
    {
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        int port = (Integer) point.getArgs()[0];
        final CpaServerSocket cpaServerSocket = new CpaServerSocket(port+1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket cpaSocket = null;
                PrintWriter printStream =null;

                try {
                    System.out.println("At server waiting for CPA connection");
                    cpaSocket = cpaServerSocket.accept();
                    System.out.println("At server received CPA connection");
                    CPA_SOCKETS.add(cpaSocket);
                    BufferedReader br = new BufferedReader(new InputStreamReader(cpaSocket.getInputStream()));
                    String address = cpaSocket.getInetAddress().getHostAddress();
                    String port = br.readLine();
                    printStream = new PrintWriter(cpaSocket.getOutputStream(), true);
                    String outputClasses = getAsString(ClassCollector.getClasses(ServerCpaConnection.getIdForAddress(address, port)));
                    printStream.println(outputClasses);
                    printStream.flush();
                    System.out.println("At server output written "+address+"#"+port+" for "+ServerCpaConnection.getIdForAddress(address, port)+ " : "+outputClasses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (printStream != null) {
                        printStream.close();
                    }
                    if (cpaSocket != null) {
                        try {
                            cpaSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("At server stream and socket closed");
                }
            }
        }).start();
        PORT_TO_SERVERSOCKET.put(port, cpaServerSocket);
        System.out.println("New server socket : "+point+" with result "+result);
    }

    private String getAsString(Set<String> classes) {
        StringBuffer sb = new StringBuffer();
        for(String className : classes)
        {
            sb.append(className+";");
        }
        return sb.toString();
    }

    private Set<String> getAsSet(String classesAsString) {
        Set<String> classes = new HashSet<String>();
        for(String className : classesAsString.split(";"))
        {
            if(className!=null && !className.trim().equals(""))
            {
                classes.add(className+";");
            }
        }
        return classes;
    }

    @Around("serverSocketMethod()")
    public Object aroundServerSocketAccept(ProceedingJoinPoint point) throws Throwable {
        Object result = null;

        if(point.getTarget() instanceof CpaServerSocket)
        {
            return point.proceed();
        }

        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }

        String address = ((Socket)result).getInetAddress().getHostAddress();
        String port = String.valueOf(((Socket) result).getPort()); 

        String id = ServerCpaConnection.generateIdForAddress(address, port);
        System.out.println("At server generated id : "+id+ " for address#port : "+address+"#"+port);
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        //System.out.println("Around Example : "+MethodSignature.class.cast(point.getSignature()).getMethod().getName());
        System.out.println("Around Example : "+point+" ------------------ "+point.getSignature().getDeclaringType().getName()+" :: "+ClassCollector.getClasses());
        return result;
    }

    @Around("socketMethodInput()")
    public Object aroundSocketInputStream(ProceedingJoinPoint point) throws Throwable {
        Object result = null;

        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }

        final Socket socket = (Socket) point.getTarget();
        if(CPA_SOCKETS.contains(socket))
        {
            if(socket.isClosed())
            {
                CPA_SOCKETS.remove(socket);
            }
            return result;
        }
        String address = socket.getInetAddress().getHostAddress();
        String port = String.valueOf(socket.getPort());
        String id = ServerCpaConnection.getIdForAddress(address, port);
        ClassCollector.setId(id);
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        //System.out.println("Around Example : "+MethodSignature.class.cast(point.getSignature()).getMethod().getName());
        System.out.println("Around Example : "+point+" ------------------ "+point.getSignature().getDeclaringType().getName()+" :: "+ClassCollector.getClasses());
        return result;
    }

    @Around("socketMethodOutput()")
    public Object aroundSocketOutputStream(ProceedingJoinPoint point) throws Throwable {
        Object result = null;

        result = getResult(point);

        final Socket socket = (Socket) point.getTarget();

        if(socket instanceof CpaSocket || CPA_SOCKETS.contains(socket))
        {
            if(socket.isClosed())
            {
                CPA_SOCKETS.remove(socket);
            }
            return result;
        }

        CLIENT_SOCKETS.add(socket);
        return result;
    }

    @Around("socketMethodOutputClose()")
    public Object aroundSocketClose(ProceedingJoinPoint point) throws Throwable {

        final Socket socket = (Socket) point.getTarget();

        if(socket instanceof CpaSocket || CPA_SOCKETS.contains(socket))
        {
            if(socket.isClosed())
            {
                CPA_SOCKETS.remove(socket);
            }
            return getResult(point);
        }

        if(!CLIENT_SOCKETS.contains(socket))
        {
            return getResult(point);
        }
        else
        {
            String address = socket.getLocalAddress().getHostAddress();
            String port = String.valueOf(socket.getLocalPort());

            CLIENT_SOCKETS.remove(socket);

            int cpaPort = socket.getPort()+1;
            InetAddress host = socket.getInetAddress();
            Socket cpaSocket = new CpaSocket(host, cpaPort);
            BufferedReader br = new BufferedReader(new InputStreamReader(cpaSocket.getInputStream()));
            PrintWriter printStream = new PrintWriter(cpaSocket.getOutputStream(), true);
            CPA_SOCKETS.add(cpaSocket);
            String classesAsString = null;
            try
            {
                printStream.println(port);
                printStream.flush();
                classesAsString = br.readLine();
            }
            finally
            {
                printStream.close();
                br.close();
                cpaSocket.close();
            }
            Set<String> classesFromServer = getAsSet(classesAsString);
            for(String className : classesFromServer)
            {
                ClassCollector.storeClass(className);
            }
            System.out.println("Around Example : " + point + " ------------------ " + point.getSignature().getDeclaringType().getName() + " :: " + ClassCollector.getClasses());
            return getResult(point);
        }
    }

    private Object getResult(ProceedingJoinPoint point) throws Throwable {
        Object result;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }
        return result;
    }


    private class CpaServerSocket extends ServerSocket
    {
        public CpaServerSocket(int port) throws IOException {
            super(port);
        }
    }

    private class CpaSocket extends Socket
    {
        public CpaSocket(InetAddress host, int cpaPort) throws IOException {
            super(host, cpaPort);
        }
    }

    @AfterReturning("newSocket()")
    public void afterNewSocket(JoinPoint.EnclosingStaticPart staticPoint, JoinPoint point, Object result) throws Throwable
    {
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        System.out.println("New socket : "+point+" with result "+result);
    }

    @AfterReturning("serverSocketMethod() || socketMethod()")
    public void afterSocketMethod(JoinPoint point, Object result) throws Throwable
    {
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        System.out.println("Socket method call : " + point + " with result " + result);
    }

    @Around("everything()")
    public Object aroundEverything(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }
        ClassCollector.storeClass(point.getSignature().getDeclaringType().getName());
        //System.out.println("Around Example : "+MethodSignature.class.cast(point.getSignature()).getMethod().getName());
        System.out.println("Around Example : "+point+" ------------------ "+point.getSignature().getDeclaringType().getName()+" :: "+ClassCollector.getClasses());
        return result;
    }


}