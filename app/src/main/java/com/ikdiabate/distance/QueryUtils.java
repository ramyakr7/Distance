package com.ikdiabate.distance;

import android.text.TextUtils;
import android.util.Log;

import com.ikdiabate.distance.data.Distance;

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

import com.ikdiabate.distance.SortUtils;

/**
 * Added by Ramya Kumar for Viewcount on 11/27/17/
 * <p>
 * <p>
 * /**
 *
 * @Author: Ibrahim Diabate
 * @Version: November 2017
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }


    /**
     * @param requestUrl the url to fetch videos matching a query within 10 miles.
     * @return a list of videos that are uploaded within 10 miles of device's location
     */
    public static List<Distance> fetchVideoData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        Log.d("Query Utils", requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Distance> videos = extractFeatureFromJson(jsonResponse);
        return videos;
    }

    /**
     * Added on 11/26/17 for ViewCount Api by Ramya Kumar
     */
    public static List<Distance> fetchViewCount(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        Log.d("Request url: ", requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Distance> videos = extractViewcountFromJson(jsonResponse);
        return videos;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Extract relevant features from the API response
     *
     * @param videoJSON the API response
     * @return List of videos within 10 miles of device's location
     */
    private static List<Distance> extractFeatureFromJson(String videoJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(videoJSON)) {
            return null;
        }

        List<Distance> distanceList = new ArrayList<>();

        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(videoJSON);

            JSONArray videoArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < videoArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentVideo = videoArray.getJSONObject(i);

                JSONObject properties = currentVideo.getJSONObject("id");

                // Extract the value for the key called videoId
                String videoId = properties.getString("videoId");

                JSONObject snippetProperties = currentVideo.getJSONObject("snippet");

                String videoPublishedDate = snippetProperties.getString("publishedAt");
                String videoTitle = snippetProperties.getString("title");
                String videoDescription = snippetProperties.getString("description");
                String author = snippetProperties.getString("channelTitle");

                Log.e(LOG_TAG, "These are result: " + videoId + videoTitle + videoDescription + videoPublishedDate + author);

                JSONObject thumbnailProp = snippetProperties.getJSONObject("thumbnails");
                JSONObject mediumImage = thumbnailProp.getJSONObject("default");

                String imageUrl = mediumImage.getString("url");

                Distance videos = new Distance(videoId, videoTitle, videoDescription, videoPublishedDate, imageUrl, author);

                distanceList.add(videos);
            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the video JSON results", e);
        }

        return distanceList;
    }


    /**
     * Code added to Extract Viewcount from Videos api
     * Ramya Kumar 11/26/17
     ***/
    private static List<Distance> extractViewcountFromJson(String videoJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(videoJSON)) {
            return null;
        }

        //List<Distance> viewList = new ArrayList<>();
        List<Distance> viewList = new ArrayList<>();

        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(videoJSON);

            JSONArray videoArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < videoArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentVideo = videoArray.getJSONObject(i);

                //JSONObject properties = currentVideo.getJSONObject("id");

                // Extract the value for the key called videoId
                String videoId = currentVideo.getString("id");

                //JSONObject snippetProperties = currentVideo.getJSONObject("snippet");
                JSONObject statisticProperties = currentVideo.getJSONObject("statistics");
                Integer viewCount = statisticProperties.getInt("viewCount");

                Log.e(LOG_TAG, "These are result: " + videoId + viewCount);

                Distance videos = new Distance(videoId, viewCount);

                viewList.add(videos);
            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the video JSON results", e);
        }

        return viewList;
    }

}

