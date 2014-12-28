package com.cpa;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sidhavratha on 27/12/14.
 */
@ExcludeAspect
public class ServerCpaConnection {

    private static Map<String, String> ADDRESS_TO_ID = new HashMap<String, String>();

    public static String generateIdForAddress(String address, String port)
    {
        String id = ClassCollector.generateNewId();
        ADDRESS_TO_ID.put(getAddress(address, port), id);
        System.out.println("Generating id for "+getAddress(address, port)+" : "+id);
        return id;
    }

    public static String getIdForAddress(String address, String port)
    {
        return ADDRESS_TO_ID.get(getAddress(address, port));
    }

    private static String getAddress(String address, String port)
    {
        return address+"#"+port;
    }

}