package solrIndexing;

import org.neo4j.driver.v1.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class neo4jMethods {
     private static Session session;
     private static Driver driver ;

    public Session getSession ()
    {
        if (session == null || !session.isOpen())
        {
            driver = GraphDatabase.driver("bolt://172.16.0.161:7687", AuthTokens.basic("neo4j", "neo4j"));
            session = driver.session();

        }
        return session;
    }
    public void neo4jClose()
    {
        driver.close();
    }

    public void getNode()
    {

        String query = "MATCH (n) WHERE n.generated_auto_id= {generatedAutoId}" +
                " RETURN n.generated_auto_id";

        StatementResult result = getSession().run(query, parameters("generatedAutoId",190));

        if (result.hasNext())
        {
            Record record = result.next();

            System.out.println(record.get("n.generated_auto_id"));
        }
        else
        {
            System.out.println("nooo");
        }
    }

    public void getNodeData(int generated_auto_id) throws IOException {
        URL url = new URL("http://172.16.0.161:80/eol/neo4j/getNodeData/"+generated_auto_id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestProperty("Content-Type", "application/json");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());




    }
}
