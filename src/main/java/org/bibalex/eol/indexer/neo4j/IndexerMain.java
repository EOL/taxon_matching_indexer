package org.bibalex.eol.indexer.neo4j;

import org.apache.solr.client.solrj.SolrServerException;
import org.bibalex.eol.indexer.solr.SolrCommon;

import java.io.IOException;

public class IndexerMain {
    public static void main(String args[]) throws IOException, SolrServerException {
        int global = 1; 
	while(global < 11) {
    		int[] generatedNodeIds = new int[20000];
    		for (int i = 0; i < generatedNodeIds.length; i++) {
        		generatedNodeIds[i] = global;
        		global++;
        		if (global==11)
           			break;
    		}
    		SolrCommon test = new SolrCommon();
    		test.addDocuments(generatedNodeIds);
	}

	//int[] generatedNodeIds = {1703};
	//int[] generatedNodeIds = new int[2724671];
	//for(int i=0; i< generatedNodeIds.length; i++)
    	//	generatedNodeIds[i]=i+1;
        //SolrCommon test = new SolrCommon();
        //test.addDocument(generatedNodeIds);
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
