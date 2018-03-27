package org.bibalex.eol.indexer.solr;

import org.json.simple.JSONObject;
import java.util.ArrayList;

public class Neo4jSolr  {

    public String getString (JSONObject obj, String key)
    {
        String value = String.valueOf(obj.get(key));
        return  value;
    }


    public int getGenaratedNodeId (JSONObject obj)
    {
        int generatedNodeId = (Integer)obj.get("generatedNodeId");
        return generatedNodeId;
    }

    public boolean isHybrid (JSONObject obj)
    {
        boolean is_hybrid = Boolean.valueOf(String.valueOf(obj.get("is_hybrid"))) ;
        return is_hybrid;
    }

    public ArrayList<String> getStringArray (JSONObject obj, String key)
    {
        ArrayList<String> value = (ArrayList<String>) obj.get(key);
        return value;
    }

    public ArrayList<Integer> getIntegerArray (JSONObject obj, String key)
    {
        ArrayList<Integer> value = (ArrayList<Integer>) obj.get(key);
        return value;
    }

}
