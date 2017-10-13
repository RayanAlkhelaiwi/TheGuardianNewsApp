package com.example.android.theguardiannewsapp;

/**
 * Created by Rean on 10/13/2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.theguardiannewsapp.MainActivity.LOG_TAG;

public final class QueryUtils {

    //Create an empty constructor to avoid creating an object of QueryUtils
    private QueryUtils() {
    }

    //A method to convert a string entry to a URL object, if it's valid
    private static URL createURL(String strURL) {

        URL url = null;

        try {
            url = new URL(strURL);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //A method to implement the HTTP connection
    private static String makeHTTPrequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {

                Log.e("QueryUtils", "Error, with JSON response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }

        return jsonResponse;
    }

    /**
     * Query The Guardian News dataset and return a list of News objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPrequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of News
        List<News> News = extractFeatureFromJson(jsonResponse);

        // Return the list of News
        return News;
    }

    //A method to append the string to have a string builder, to then read the input stream
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder strOutput = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                strOutput.append(line);
                line = reader.readLine();
            }
        }

        return strOutput.toString();
    }

    /**
     * Return a list of News objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or news).
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            JSONArray resultsArray = responseObject.getJSONArray("results");

            // For each news in the newsArray, create a News object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = resultsArray.getJSONObject(i);

                String title = currentNews.getString("webTitle");
                String sectionName = currentNews.getString("sectionName");
                String date = currentNews.getString("webPublicationDate");
                String newsURL = currentNews.getString("webUrl");

                // Create a new news object with the magnitude, location, time,
                // and url from the JSON response.
                news.add(new News(title, sectionName, date, newsURL));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return the list of News
        return news;
    }
}