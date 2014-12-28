package com.cpa;

import java.util.*;

/**
 * Created by Sidhavratha on 27/12/14.
 */
@ExcludeAspect
public class ClassCollector {

    private static Map<String, Set<String>> CLASS_MAP = new HashMap<String, Set<String>>();

    private static InheritableThreadLocal<String> ID_STORE = new InheritableThreadLocal<String>();

    public static String generateAndSetNewId()
    {
        String id = generateNewId();
        ID_STORE.set(id);
        return id;
    }

    protected static String generateNewId()
    {
        String id = String.valueOf(Math.random() * 100000L);
        return id;
    }

    protected static void setId(String id)
    {
        ID_STORE.set(id);
    }
      
    public static void storeClass(String className)
    {
        String id = ID_STORE.get();
        Set<String> classes = CLASS_MAP.get(id);
        if(classes==null)
        {
            classes = new HashSet<String>();
            CLASS_MAP.put(id, classes);
        }
        classes.add(className);
    }

    public static Set<String> getClasses()
    {
        String id = ID_STORE.get();
        Set<String> classes = CLASS_MAP.get(id);
        return classes;
    }

    public static String getId()
    {
        return ID_STORE.get();
    }

    public static Set<String> getClasses(String id)
    {
        Set<String> classes = CLASS_MAP.get(id);
        return classes;
    }

}