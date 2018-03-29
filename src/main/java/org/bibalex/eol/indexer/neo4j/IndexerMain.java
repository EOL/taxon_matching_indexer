package org.bibalex.eol.indexer.neo4j;

import org.apache.solr.client.solrj.SolrServerException;
import org.bibalex.eol.indexer.solr.Neo4jSolr;
import org.bibalex.eol.indexer.solr.SolrCommon;

import java.io.IOException;

public class IndexerMain {
    public static void main(String args[]) throws IOException, SolrServerException {
        int[] generatedNodeIds = {1,2,3,4,9};
        SolrCommon test = new SolrCommon();
        test.addDocument(generatedNodeIds);
//        connection test = new connection();
//        connection.conSolr();
//        ArrayList<Integer> generatedNodeIds= new ArrayList<>();
//        generatedNodeIds.add(1);
//        generatedNodeIds.add(2);
//        generatedNodeIds.add(3);
////        System.out.println(generatedNodeIds);
//       instance.getJSonObject(generatedNodeIds);

    }
}
