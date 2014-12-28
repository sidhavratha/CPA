package com;

import com.cpa.ClassCollector;
import com.example.Example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * Created by Sidhavratha on 27/12/14.
 */
public class ExampleTest {

    @Before
    public void clear()
    {
        ClassCollector.generateAndSetNewId();
    }

    @Test
    public void exampleTest1()
    {
        Example.ExampleA server = new Example.ExampleA();
        String output = server.printHelloWorld();
        Assert.assertEquals("Hello world A", output);
        Set<String> classes = ClassCollector.getClasses();
        Assert.assertTrue(classes.contains(Example.ExampleA.class.getName()));
        Assert.assertFalse(classes.contains(Example.ExampleB.class.getName()));
    }

    @Test
    public void exampleTest2()
    {
        Example.ExampleB server = new Example.ExampleB();
        String output = server.printHelloWorld();
        Assert.assertEquals("Hello world B", output);
        Set<String> classes = ClassCollector.getClasses();
        Assert.assertTrue(classes.contains(Example.ExampleB.class.getName()));
        Assert.assertFalse(classes.contains(Example.ExampleA.class.getName()));
    }

    @Test
    public void exampleServerClient()
    {
        Example example = new Example();
        example.serverClient();
        Set<String> classes = ClassCollector.getClasses();
        Assert.assertFalse(classes.contains(Example.ExampleB.class.getName()));
        Assert.assertFalse(classes.contains(Example.ExampleA.class.getName()));
        Assert.assertTrue(classes.contains(Socket.class.getName()));
        Assert.assertTrue(classes.contains(ServerSocket.class.getName()));
    }

    @Test
    public void exampleAll()
    {
        Example example = new Example();
        example.serverClient();
        Example.ExampleB exampleB = new Example.ExampleB();
        String outputA = exampleB.printHelloWorld();
        Assert.assertEquals("Hello world B", outputA);
        Example.ExampleA exampleA = new Example.ExampleA();
        String outputB = exampleA.printHelloWorld();
        Assert.assertEquals("Hello world A", outputB);
        Set<String> classes = ClassCollector.getClasses();
        Assert.assertTrue(classes.contains(Example.ExampleB.class.getName()));
        Assert.assertTrue(classes.contains(Example.ExampleA.class.getName()));
        Assert.assertTrue(classes.contains(Socket.class.getName()));
        Assert.assertTrue(classes.contains(ServerSocket.class.getName()));
    }
}
