package org.bibalex.eol.indexer.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SolrCommon {
    CloudSolrClient client = null;
    SolrInputDocument doc;


    public CloudSolrClient openConnection(String collectionName)
    {
        String zkHosts = "localhost:9983";
        client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
        client.setDefaultCollection(collectionName);
        return client;
    }

    public void addDocument() throws IOException, SolrServerException {



    }




}
