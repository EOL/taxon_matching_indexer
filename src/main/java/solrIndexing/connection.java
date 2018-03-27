package solrIndexing;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.SolrQuery;


import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;
//import org.apache.log4j.Logger;

public class connection {
    public static void mainPrevious ()throws IOException, SolrServerException {

        neo4jMethods neo4j= new neo4jMethods();
//        neo4j.getNode();
//        neo4j.neo4jClose();
        neo4j.getNodeData(190);

    }

    public static void conSolr() throws IOException, SolrServerException {
        String zkHosts = "localhost:9983";
        CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
        client.setDefaultCollection("indexer");

        //query Solr
        SolrQuery q = new SolrQuery("id:4");
        QueryResponse r = client.query(q);

//update value
        if(!r.getResults().isEmpty())
        {
            System.out.println("yes");
            SolrDocument oldDoc = r.getResults().get(0);
        }
        else
        {
            System.out.println("no");
        }

        SolrDocument oldDoc = r.getResults().get(0);

        SolrInputDocument newDoc = new SolrInputDocument();

        newDoc.addField("id", oldDoc.getFieldValue("id"));

//        newDoc.addField("name", "ahmedtry");
        Map<String,Object> fieldModifier = new HashMap<String,Object>(1);
        fieldModifier.put("set","ahmedtry");
        newDoc.addField("name",fieldModifier);

//        newDoc.addField("try", "ay7aga4");
        client.add(newDoc);
        client.commit();
        client.close();
    }

}
