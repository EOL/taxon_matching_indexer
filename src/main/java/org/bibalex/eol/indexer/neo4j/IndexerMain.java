package org.bibalex.eol.indexer.neo4j;

import org.apache.solr.client.solrj.SolrServerException;
import org.bibalex.eol.indexer.solr.Neo4jSolr;
import solrIndexing.connection;

import java.io.IOException;

public class IndexerMain {
    public static void main(String args[]) throws IOException, SolrServerException {
        int[] generatedNodeIds = {1,2,3,4,9};
        Neo4jSolr test = new Neo4jSolr();
        test.neo4jJson(generatedNodeIds);
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
