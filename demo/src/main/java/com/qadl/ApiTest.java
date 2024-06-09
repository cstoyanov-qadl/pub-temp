package com.qadl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiTest {

    /**
    * Returns a JSON Array (of JSONObject) from the REST-API uri
    *
    * @param uri  The URL of the REST-API service.
    * @return The JSONArray containing the answer from the REST-API service.
    * @since 1.0
    */
    static JSONArray get(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri)).header("Accept", "application/json; charset=utf-8")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        JSONArray jsonOutput = new JSONArray(response.body());

        return jsonOutput;
    }

    /**
    * Display the id of each blogpost and the number of blogpost per user
    *
    * @param jsonArray The JSONArray to process.
    * @since 1.0
    */
    static void process(JSONArray jsonArray) {
        Iterator it = jsonArray.iterator();

        List<String> result = new ArrayList();

        while (it.hasNext()) {
            String obj = it.next().toString();
            JSONObject objJson = new JSONObject(obj);
            result.add(objJson.get("userId").toString());

            // scenario 2: display the unique ID of each blogpost
            System.out.println("- id of the blogpost: " + objJson.get("id"));
        }

        // scenario 1: display (user, number of blogposts)
        List<String> newResult = new ArrayList<>(new HashSet<>(result));
        for (String res : newResult) {
            int occurrences = Collections.frequency(result, res);
            System.out.println("(" + res + "," + occurrences + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        process(get("https://jsonplaceholder.typicode.com/posts"));
    }
}