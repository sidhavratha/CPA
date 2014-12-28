package com;

import com.cpa.ClassCollector;
import com.example.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Set;

/**
 * Created by Sidhavratha on 27/12/14.
 */
public class MultiJVMTest {

    private static final String SERVER_PORT = String.valueOf(8101);

    Process process = null;

    @Before
    public void clear() throws Exception {
        ClassCollector.generateAndSetNewId();
        process = startSecondJVM();
        System.out.println("Thread id in before : "+Thread.currentThread().getId());
    }

    @After
    public void afterTest()
    {
        process.destroy();
    }

    @Test
    public void serverClientTest() throws IOException {
        Socket socket = null;
        PrintWriter ps = null;
        BufferedReader br = null;
        try
        {
            socket = new Socket("localhost", Integer.parseInt(SERVER_PORT));
            ps = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps.println("Hello from client.");
        }
        finally{
            if (ps != null) {
                ps.close();
            }
            if (br != null) {
                br.close();
            }
            if (socket != null) {
                socket.close();
            }
        }

        System.out.println("Thread id in test : "+Thread.currentThread().getId());
        Set<String> classes = ClassCollector.getClasses();
        Assert.assertTrue(classes.contains(Server.ServerA.class.getName()));
        Assert.assertTrue(classes.contains(Server.ServerB.class.getName()));
        Assert.assertTrue(classes.contains(Server.class.getName()));
    }

    public static Process startSecondJVM() throws Exception {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        final String javaagent = "-javaagent:\"C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\lib\\aspectjweaver-1.8.4.jar\"";
        String processCommand = "\"C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\bin\\java\" -cp \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 13.0.1\\lib\\idea_rt.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 13.0.1\\plugins\\junit\\lib\\junit-rt.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.7.0_03\\jre\\lib\\ext\\zipfs.jar;C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\out\\test\\CPA;C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\out\\production\\CPA;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 13.0.1\\lib\\junit-4.10.jar;C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\lib\\aspectjrt-1.8.4.jar;C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\lib\\aspectjtools-1.8.4.jar;C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\lib\\aspectjweaver-1.8.4.jar;C:/Users/Sidhavratha/IdeaProjects/CPA/lib/aspectjweaver-1.8.4.jar\" -javaagent:\"C:\\Users\\Sidhavratha\\IdeaProjects\\CPA\\lib\\aspectjweaver-1.8.4.jar\" com.example.Server "+SERVER_PORT+"\"";
        ProcessBuilder processBuilder =
                /*new ProcessBuilder(path, "-cp",
                        javaagent,
                        "\""+classpath.replaceAll("/",separator)+"\"",
                        Server.class.getName());*/
                new ProcessBuilder(processCommand);
        processBuilder.inheritIO();
        processBuilder.directory(new File(System.getProperty("user.dir")));
        System.out.println("Executing command : " + processBuilder.inheritIO());
        Process process = processBuilder.start();
        return process;
    }
}
