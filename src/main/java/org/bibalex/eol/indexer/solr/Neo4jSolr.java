package org.bibalex.eol.indexer.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.bibalex.eol.indexer.neo4j.Neo4jCommon;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Neo4jSolr extends SolrCommon {

    public void neo4jJson( int[] generatedNodeIds ) throws IOException, SolrServerException {
        Neo4jCommon instance = new Neo4jCommon();
        ArrayList<JSONObject> returnedJson = instance.getJSonObject(generatedNodeIds);
        openConnection("indexer");
//        String zkHosts = "localhost:9983";
//        CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
//        client.setDefaultCollection("collection7");
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id","5");
//        doc.addField("name","test1");
//        client.add(doc);
//        client.commit();
//        client.close();
        for(int i=0 ; i < returnedJson.size();i++)
        {
            JSONObject obj= returnedJson.get(i);
            String scientificName = getString(obj, "scientific name");
            String rank = getString(obj, "Rank");
            String canonicalName = getString(obj, "canonical name");
            int generatedNodeId = getGenaratedNodeId(obj);
            boolean is_hybrid = isHybrid(obj);
            ArrayList<String> canonicalSynonyms =getStringArray(obj,"canonical synonyms");
//            System.out.println(canonicalSynonyms);
            ArrayList<String> otherCanonicalSynonyms =getStringArray(obj,"other canonical synonyms");
            ArrayList<String> synonyms =getStringArray(obj,"synonyms");
            ArrayList<String> otherSynonyms =getStringArray(obj,"other synonyms");
            ArrayList<Integer> ancestorsIds =getIntegerArray(obj,"ancestors IDS");


           super.doc = new SolrInputDocument();

           doc.addField("generated_node_id",String.valueOf(generatedNodeId));
           doc.addField("scientific_name",scientificName);
           doc.addField("rank",rank);
           doc.addField("canonical_name",canonicalName);
           doc.addField("is_hybrid",is_hybrid);
           for(int j=0 ; j<canonicalSynonyms.size() ;j++) {
               doc.addField("canonical_synonyms",canonicalSynonyms.get(j));
           }
//            for( int j=0 ; j<otherCanonicalSynonyms.size() ;j++) {
//                doc.addField("other_canonical_synonyms",otherCanonicalSynonyms.get(j));
//            }
//            for(int j=0 ; j<synonyms.size() ;j++) {
//                doc.addField("synonyms",synonyms.get(j));
//            }
//            for( int j=0 ; j<otherSynonyms.size() ;j++) {
//                doc.addField("other_synonyms",otherSynonyms.get(j));
//            }
//            for(int j=0 ; j<ancestorsIds.size() ;j++) {
//                doc.addField("ancestors_ids",ancestorsIds.get(j));
//            }
            super.client.add(doc);
            // edit commit to commit every specific number of docs
            super.client.commit();



        }
        super.client.close();
    }

    public String getString (JSONObject obj , String key)
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
