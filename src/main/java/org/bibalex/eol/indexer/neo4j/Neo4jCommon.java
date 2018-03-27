package org.bibalex.eol.indexer.neo4j;

import com.fasterxml.jackson.core.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;


public class Neo4jCommon {

    public ArrayList<JSONObject> getJSonObject(int[] generatedNodeIds)  {
        ArrayList<JSONObject> returnedJSon = httpConnect("http://localhost:8010/eol/neo4j/getNodesJson",generatedNodeIds);
        return returnedJSon;
    }

    public ArrayList<JSONObject> httpConnect(String uri, int[]  generatedNodeIds)  {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");


        org.springframework.http.HttpEntity<Object> requestEntity = new org.springframework.http.HttpEntity<Object>(generatedNodeIds,headers);
        ResponseEntity<ArrayList<JSONObject>> rateResponse = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,new ParameterizedTypeReference<ArrayList<JSONObject>>() {});

        System.out.println(rateResponse.getBody());
        return rateResponse.getBody();



    }

}
