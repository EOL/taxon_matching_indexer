package org.bibalex.eol.indexer.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.bibalex.eol.indexer.neo4j.Neo4jCommon;
import org.bibalex.eol.indexer.utils.Constants;
import org.bibalex.eol.indexer.utils.Neo4jSolr;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SolrCommon extends Neo4jSolr {
    CloudSolrClient client = null;
    java.util.logging.Logger logger =  Logger.getLogger("SolrCommon");

    public CloudSolrClient openConnection(String collectionName) {
        String zkHosts = Constants.ZOOKEEPER_URI;
        client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
        client.setDefaultCollection(collectionName);
        return client;
    }

    public void addDocuments(int[] generatedNodeIds) throws IOException, SolrServerException {
        Neo4jCommon instance = new Neo4jCommon();
        ArrayList<JSONObject> returnedJson = instance.getJSonObject(generatedNodeIds);
        openConnection(Constants.COLLECTION_NAME);

        for(int i = 0 ; i < returnedJson.size() ; i++) {
            JSONObject obj = returnedJson.get(i);
            addDocument(obj);
        }
         client.close();
    }

    public void addDocument(JSONObject obj){
        String scientificName = getString(obj, Constants.JSON_SCIENTIFIC_NAME);
        String rank = getString(obj, Constants.JSON_RANK);
        String canonicalName = getString(obj, Constants.JSON_CANONICAL_NAME);
        int generatedNodeId = getInt(obj, Constants.JSON_GENERATED_ID);
        int pageId  = getInt(obj,Constants.JSON_PAGE_ID);
        boolean is_hybrid = isHybrid(obj);
        ArrayList<String> canonicalSynonyms = getStringArray(obj, Constants.JSON_CANONICAL_SYNONYMS);
        ArrayList<String> otherCanonicalSynonyms = getStringArray(obj, Constants.JSON_OTHER_CANONICAL_SYNONYMS);
        ArrayList<String> synonyms = getStringArray(obj, Constants.JSON_SYNONYMS);
        ArrayList<String> otherSynonyms = getStringArray(obj, Constants.JSON_OTHER_SYNONYMS);
        ArrayList<String> childrenIds = getStringArray(obj, Constants.JSON_CHILDREN_IDS);
        ArrayList<Integer> ancestorsIds = getIntegerArray(obj, Constants.JSONE_ANCESTORS_IDS);

        //if-cond  to make sure that object not include id only
        if (obj.size() > 1) {
            SolrInputDocument doc = new SolrInputDocument();
            SolrQuery q = new SolrQuery("id:" + generatedNodeId);
            QueryResponse r = null;
            try {
                r = client.query(q);
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!r.getResults().isEmpty()) {
                SolrDocument oldDoc = r.getResults().get(0);
                doc.addField(Constants.NODE_GENERATED_ID, generatedNodeId);

                //scientificName is empty string canonical will be empty string also and wo,t be inserted in  solr
                //pageId is -1 won't be inserted in solr
                //rank if empty string won't be inserted in solr
                //update other canonical synonyms and other synonyms include adding old one too
                //update canonical synonyms and synonyms not include adding old one
                if (!scientificName.equals("")) {
                    doc.addField(Constants.NODE_SCIENTIFIC_NAME, mapToDoc(scientificName));
                    logger.info("updated scientific name: " + scientificName + " to node id: " + generatedNodeId);
                }

                if (!rank.equals("")) {
                    doc.addField(Constants.NODE_RANK, mapToDoc(rank));
                    logger.info("updated rank: " + rank + " to node id: " + generatedNodeId);
                }

                if(pageId!= -1) {
                    doc.addField(Constants.NODE_PAGE_ID, mapToDoc(pageId));
                    logger.info("updated page id: " + pageId + " to node id: " + generatedNodeId);
                }

                if (!canonicalName.equals("")) {
                    doc.addField(Constants.NODE_CANONICAL_NAME, mapToDoc(canonicalName));
                    logger.info("updated canonical name: " + canonicalName + " to node id: " + generatedNodeId);
                }

                if (String.valueOf(is_hybrid) != null) {
                    doc.addField(Constants.NODE_IS_HYBRID, is_hybrid);
                    logger.info("updated is hybrid: " + is_hybrid + " to node id: " + generatedNodeId);
                }

                if (canonicalSynonyms.size() > 0) {
                    doc.addField(Constants.NODE_CANONICAL_SYNONYMS, mapToDoc(canonicalSynonyms));
                    logger.info("updated canonical synonyms: " + canonicalSynonyms + " to node id: " + generatedNodeId);
                }

                if (otherCanonicalSynonyms.size() > 0) {
                    if(oldDoc.getFieldValues(Constants.NODE_CANONICAL_SYNONYMS)!=null)
                    {   ArrayList oldOtherCanonicalSynonyms = (ArrayList) oldDoc.getFieldValues(Constants.NODE_CANONICAL_SYNONYMS);
                        otherCanonicalSynonyms.addAll(oldOtherCanonicalSynonyms);
                        logger.info("old other canonical synonyms: " + oldOtherCanonicalSynonyms + " to node id: " + generatedNodeId);}
                    doc.addField("other_canonical_synonyms", mapToDoc(otherCanonicalSynonyms));
                    logger.info("updated other canonical synonyms: " + otherCanonicalSynonyms + " to node id: " + generatedNodeId);
                }

                if (synonyms.size() > 0) {
                    doc.addField(Constants.NODE_SYNONYMS, mapToDoc(synonyms));
                    logger.info("updated synonyms: " + synonyms + " to node id: " + generatedNodeId);

                }

                if (otherSynonyms.size() > 0) {
                    if(oldDoc.getFieldValues(Constants.NODE_OTHER_SYNONYMS)!=null) {
                        ArrayList oldOtherSynonyms = (ArrayList) oldDoc.getFieldValues(Constants.NODE_OTHER_SYNONYMS);
                        otherSynonyms.addAll(oldOtherSynonyms);
                        logger.info("old other synonyms: " + oldOtherSynonyms + " to node id: " + generatedNodeId);
                    }
                    doc.addField(Constants.NODE_OTHER_SYNONYMS, mapToDoc(otherSynonyms));
                    logger.info("updated other synonyms: " + otherSynonyms + " to node id: " + generatedNodeId);
                }

                if (childrenIds.size() > 0) {
                    doc.addField(Constants.NODE_CHILDREN_IDS, mapToDoc(childrenIds));
                    logger.info("updated children ids: " + childrenIds + " to node id: " + generatedNodeId);
                }

                if (ancestorsIds.size() > 0) {
                    doc.addField(Constants.NODE_ANCESTORS_IDS, mapToDoc(ancestorsIds));
                    logger.info("updated ancestors ids: " + ancestorsIds + " to node id: " + generatedNodeId);
                }

            } else {
                doc.addField(Constants.NODE_GENERATED_ID, generatedNodeId);
                if(pageId != -1) { doc.addField(Constants.NODE_PAGE_ID,pageId);}
                if (!scientificName.equals("")) {doc.addField(Constants.NODE_SCIENTIFIC_NAME, scientificName);}
                if(!rank.equals("")){doc.addField(Constants.NODE_RANK, rank);}
                if(!canonicalName.equals("")){doc.addField(Constants.NODE_CANONICAL_NAME, canonicalName);}
                if(String.valueOf(is_hybrid)!=null){doc.addField(Constants.NODE_IS_HYBRID, is_hybrid);}
                doc.addField(Constants.NODE_CANONICAL_SYNONYMS, canonicalSynonyms);
                doc.addField(Constants.NODE_OTHER_CANONICAL_SYNONYMS, otherCanonicalSynonyms);
                doc.addField(Constants.NODE_SYNONYMS, synonyms);
                doc.addField(Constants.NODE_OTHER_SYNONYMS, otherSynonyms);
                doc.addField(Constants.NODE_CHILDREN_IDS, childrenIds);
                doc.addField(Constants.NODE_ANCESTORS_IDS, ancestorsIds);

                logger.info("new added doc: "+doc);

            }

            try {
                client.add(doc);
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
