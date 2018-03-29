package org.bibalex.eol.indexer.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.bibalex.eol.indexer.neo4j.Neo4jCommon;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SolrCommon extends Neo4jSolr {
    CloudSolrClient client = null;


    public CloudSolrClient openConnection(String collectionName)
    {
        String zkHosts = "localhost:9983";
        client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
        client.setDefaultCollection(collectionName);
        return client;
    }

    public void addDocument(int[] generatedNodeIds) throws IOException, SolrServerException {
        Neo4jCommon instance = new Neo4jCommon();
        ArrayList<JSONObject> returnedJson = instance.getJSonObject(generatedNodeIds);
        openConnection("indexer");

        for(int i = 0 ; i < returnedJson.size() ; i++) {
            JSONObject obj = returnedJson.get(i);
            String scientificName = getString(obj, "scientific name");
            String rank = getString(obj, "Rank");
            String canonicalName = getString(obj, "canonical name");
            int generatedNodeId = getGenaratedNodeId(obj);
            boolean is_hybrid = isHybrid(obj);
            ArrayList<String> canonicalSynonyms = getStringArray(obj, "canonical synonyms");
            ArrayList<String> otherCanonicalSynonyms = getStringArray(obj, "other canonical synonyms");
            ArrayList<String> synonyms = getStringArray(obj, "synonyms");
            ArrayList<String> otherSynonyms = getStringArray(obj, "other synonyms");
            ArrayList<String> childrenNames = getStringArray(obj, "children names");
            ArrayList<Integer> ancestorsIds = getIntegerArray(obj, "ancestors IDS");

            //if-cond  to make sure that object not include id only
            if (obj.size() > 1) {
                SolrInputDocument doc = new SolrInputDocument();
                SolrQuery q = new SolrQuery("id:" + generatedNodeId);
                QueryResponse r = client.query(q);
                if (!r.getResults().isEmpty()) {
                    SolrDocument oldDoc = r.getResults().get(0);
                    doc.addField("id", generatedNodeId);

                    if (scientificName != null) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", scientificName);
                        doc.addField("scientific_name", fieldModifier);
                    }

                    if (rank != null) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", rank);
                        doc.addField("rank", fieldModifier);
                    }

                    if (canonicalName != null) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", canonicalName);
                        doc.addField("canonical_name", fieldModifier);
                    }

                    if (String.valueOf(is_hybrid) != null) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", is_hybrid);
                        doc.addField("is_hybrid", fieldModifier);
                    }

                    if (canonicalSynonyms.size() > 0) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", canonicalSynonyms);
                        doc.addField("canonical_synonyms", fieldModifier);
                    }

                    if (otherCanonicalSynonyms.size() > 0) {
                        ArrayList oldOtherCanonicalSynonyms = (ArrayList) oldDoc.getFieldValues("other_canonical_synonyms");
                        otherCanonicalSynonyms.addAll(oldOtherCanonicalSynonyms);
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", otherCanonicalSynonyms);
                        doc.addField("other_canonical_synonyms", fieldModifier);
                    }
                    if (synonyms.size() > 0) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", synonyms);
                        doc.addField("synonyms", fieldModifier);

                    }
                    if (otherSynonyms.size() > 0) {
                        ArrayList oldOtherSynonyms = (ArrayList) oldDoc.getFieldValues("other_synonyms");
                        otherSynonyms.addAll(oldOtherSynonyms);
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", otherSynonyms);
                        doc.addField("other_synonyms", fieldModifier);
                    }

                    if (childrenNames.size() > 0) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", childrenNames);
                        doc.addField("children_names", fieldModifier);
                    }

                    if (ancestorsIds.size() > 0) {
                        Map<String, Object> fieldModifier = new HashMap<String, Object>(1);
                        fieldModifier.put("set", ancestorsIds);
                        doc.addField("ancestors_ids", fieldModifier);
                    }

                } else {
                    doc.addField("id", generatedNodeId);
                    doc.addField("scientific_name", scientificName);
                    doc.addField("rank", rank);
                    doc.addField("canonical_name", canonicalName);
                    doc.addField("is_hybrid", is_hybrid);
                    doc.addField("canonical_synonyms", canonicalSynonyms);
                    doc.addField("other_canonical_synonyms", otherCanonicalSynonyms);
                    doc.addField("synonyms", synonyms);
                    doc.addField("other_synonyms", otherSynonyms);
                    doc.addField("children_names", childrenNames);
                    doc.addField("ancestors_ids", ancestorsIds);

                }

                client.add(doc);


            }
        }
         client.close();

    }


}
