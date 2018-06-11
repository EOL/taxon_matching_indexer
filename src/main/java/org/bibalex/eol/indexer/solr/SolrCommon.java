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
import java.util.logging.Logger;

public class SolrCommon extends Neo4jSolr {
    CloudSolrClient client = null;
    java.util.logging.Logger logger =  Logger.getLogger("SolrCommon");

    public CloudSolrClient openConnection(String collectionName) {
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
            int generatedNodeId = getInt(obj,"generatedNodeId");
            int pageId  = getInt(obj,"page id");
            boolean is_hybrid = isHybrid(obj);
            ArrayList<String> canonicalSynonyms = getStringArray(obj, "canonical synonyms");
            ArrayList<String> otherCanonicalSynonyms = getStringArray(obj, "other canonical synonyms");
            ArrayList<String> synonyms = getStringArray(obj, "synonyms");
            ArrayList<String> otherSynonyms = getStringArray(obj, "other synonyms");
            ArrayList<String> childrenIds = getStringArray(obj, "children IDS");
            ArrayList<Integer> ancestorsIds = getIntegerArray(obj, "ancestors IDS");

            //if-cond  to make sure that object not include id only
            if (obj.size() > 1) {
                SolrInputDocument doc = new SolrInputDocument();
                SolrQuery q = new SolrQuery("id:" + generatedNodeId);
                QueryResponse r = client.query(q);
                if (!r.getResults().isEmpty()) {
                    SolrDocument oldDoc = r.getResults().get(0);
                    doc.addField("id", generatedNodeId);

                    //scientificName is empty string canonical will be empty string also and wo,t be inserted in  solr
                    //pageId is -1 won't be inserted in solr
                    //rank if empty string won't be inserted in solr
                    //update other canonical synonyms and other synonyms include adding old one too
                    //update canonical synonyms and synonyms not include adding old one
                    if (!scientificName.equals("")) {
                        doc.addField("scientific_name", mapToDoc(scientificName));
                        logger.info("updated scientific name: "+scientificName+" to node id: "+generatedNodeId);
                    }

                    if (!rank.equals("")) {
                        doc.addField("rank", mapToDoc(rank));
                        logger.info("updated rank: "+rank+" to node id: "+generatedNodeId);
                    }

                    if(pageId!= -1) {
                        doc.addField("page_id", mapToDoc(pageId));
                        logger.info("updated page id: "+pageId+" to node id: "+generatedNodeId);
                    }

                    if (!canonicalName.equals("")) {
                        doc.addField("canonical_name", mapToDoc(canonicalName));
                        logger.info("updated canonical name: "+canonicalName+" to node id: "+generatedNodeId);
                    }

                    if (String.valueOf(is_hybrid) != null) {
                        doc.addField("is_hybrid", is_hybrid);
                        logger.info("updated is hybrid: "+is_hybrid+" to node id: "+generatedNodeId);
                    }

                    if (canonicalSynonyms.size() > 0) {
                        doc.addField("canonical_synonyms", mapToDoc(canonicalSynonyms));
                        logger.info("updated canonical synonyms: "+canonicalSynonyms+" to node id: "+generatedNodeId);
                    }

                    if (otherCanonicalSynonyms.size() > 0) {
                        if(oldDoc.getFieldValues("other_canonical_synonyms")!=null)
                        {   ArrayList oldOtherCanonicalSynonyms = (ArrayList) oldDoc.getFieldValues("other_canonical_synonyms");
                            otherCanonicalSynonyms.addAll(oldOtherCanonicalSynonyms);
                            logger.info("old other canonical synonyms: "+oldOtherCanonicalSynonyms+" to node id: "+generatedNodeId);}
                        doc.addField("other_canonical_synonyms", mapToDoc(otherCanonicalSynonyms));
                        logger.info("updated other canonical synonyms: "+otherCanonicalSynonyms+" to node id: "+generatedNodeId);
                    }

                    if (synonyms.size() > 0) {
                        doc.addField("synonyms", mapToDoc(synonyms));
                        logger.info("updated synonyms: "+synonyms+" to node id: "+generatedNodeId);

                    }

                    if (otherSynonyms.size() > 0) {
                        if(oldDoc.getFieldValues("other_synonyms")!=null) {
                            ArrayList oldOtherSynonyms = (ArrayList) oldDoc.getFieldValues("other_synonyms");
                            otherSynonyms.addAll(oldOtherSynonyms);
                            logger.info("old other synonyms: "+oldOtherSynonyms+" to node id: "+generatedNodeId);
                        }
                        doc.addField("other_synonyms", mapToDoc(otherSynonyms));
                        logger.info("updated other synonyms: "+otherSynonyms+" to node id: "+generatedNodeId);
                    }

                    if (childrenIds.size() > 0) {
                        doc.addField("children_ids", mapToDoc(childrenIds));
                        logger.info("updated children ids: "+childrenIds+" to node id: "+generatedNodeId);
                    }

                    if (ancestorsIds.size() > 0) {
                        doc.addField("ancestors_ids", mapToDoc(ancestorsIds));
                        logger.info("updated ancestors ids: "+ancestorsIds+" to node id: "+generatedNodeId);
                    }

                } else {
                    doc.addField("id", generatedNodeId);
                    if(pageId!=-1) { doc.addField("page_id",pageId);}
                    if (!scientificName.equals("")) {doc.addField("scientific_name", scientificName);}
                    if(!rank.equals("")){doc.addField("rank", rank);}
                    if(!canonicalName.equals("")){doc.addField("canonical_name", canonicalName);}
                    if(String.valueOf(is_hybrid)!=null){doc.addField("is_hybrid", is_hybrid);}
                    doc.addField("canonical_synonyms", canonicalSynonyms);
                    doc.addField("other_canonical_synonyms", otherCanonicalSynonyms);
                    doc.addField("synonyms", synonyms);
                    doc.addField("other_synonyms", otherSynonyms);
                    doc.addField("children_ids", childrenIds);
                    doc.addField("ancestors_ids", ancestorsIds);
                    logger.info("new added doc: "+doc);

                }

                client.add(doc);
            }
        }
         client.close();

    }

}
