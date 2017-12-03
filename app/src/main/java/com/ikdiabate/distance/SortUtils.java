package com.ikdiabate.distance;

import android.net.Uri;
import android.util.Log;

import com.ikdiabate.distance.data.Distance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ramya Kumar on 11/26/2017.
 * File created to implement custom re-rank based on weights
 */

public class SortUtils {

    private static final String LOG_TAG = SortUtils.class.getName();
    private static final String YOUTUBE_VIDEOS_API = "https://www.googleapis.com/youtube/v3/videos?";


    public static List<Distance> defaultSort(List<Distance> input_sort_by_relevance) {

        ArrayList<Distance> input = (ArrayList<Distance>) ((ArrayList<Distance>) input_sort_by_relevance).clone();

        String[] sortByRelevance = sortByRelevance(input);
        String[] sortByDate = sortByPublishDate(input);
        String[] sortByViewCount = sortByViewCount(input);

        SortWeight relevance = new SortWeight(sortByRelevance, 0.5);
        SortWeight date = new SortWeight(sortByDate, 0.2);
        SortWeight viewCount = new SortWeight(sortByViewCount, 0.3);

        SortWeight[] sortWeights = new SortWeight[]{ relevance, viewCount, date };

        String[] customSortOrder = weigh(sortWeights);

        ArrayList<Distance> output = rerank(input, customSortOrder);

        return output;
    }

    private static ArrayList<Distance> rerank(ArrayList<Distance> input, String[] customSortOrder) {
        ArrayList<Distance> output = new ArrayList<>();
        for (int i = customSortOrder.length - 1; i >= 0; i--) {
            System.out.println("CustomSort Order" + customSortOrder[i]);

            String videoId = customSortOrder[i];
            for (Distance video : input) {
                if (videoId.equals(video.getVideoid())) {
                    output.add(video);
                }
            }
        }

        return output;
    }

    private static String[] sortByRelevance(ArrayList<Distance> input) {
        return extractVideoIds(input);
    }

    private static String[] sortByPublishDate(final ArrayList<Distance> input) {
        Collections.sort(input, new Comparator<Distance>() {
            public int compare(Distance o1, Distance o2) {
                try {
                    String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

                    Date o1_date = (new SimpleDateFormat(DATE_FORMAT_PATTERN))
                            .parse(o1.getPublishedDate().replaceAll("Z$", ""));

                    Date o2_date = (new SimpleDateFormat(DATE_FORMAT_PATTERN))
                            .parse(o2.getPublishedDate().replaceAll("Z$", ""));

                    return o2_date.compareTo(o1_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

        return extractVideoIds(input);
    }

    private static String[] sortByViewCount(ArrayList<Distance> input) {
        //Get list of video ids from input
        String[] videoIds = extractVideoIds(input);
        //String videoIdsString = videoIds.toString();
        String videoIdsString = "";
        for (int i = 0; i < videoIds.length; i++) {
            videoIdsString += videoIds[i] + ",";
        }
        videoIdsString = videoIdsString.substring(0, videoIdsString.length() - 2);
        Uri baseUri = Uri.parse(YOUTUBE_VIDEOS_API);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("part", "statistics");
        //uriBuilder.appendQueryParameter("id",videoIdsString );
        uriBuilder.appendQueryParameter("id", videoIdsString);
        uriBuilder.appendQueryParameter("key", BuildConfig.API_KEY_TOKEN);

        String uRitext = uriBuilder.toString();
        Log.e(LOG_TAG, "This is the link: " + uRitext);

        //Pass this to the api and fetch the view count

        List<Distance> viewcount = QueryUtils.fetchViewCount(uRitext);

        ArrayList<Distance> viewcount1 = new ArrayList<Distance>(viewcount);

        Collections.sort(viewcount1, new Comparator<Distance>() {
            public int compare(Distance o1, Distance o2) {
                try {
                    return o2.getViewcount().compareTo(o1.getViewcount());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

        //Sort by view count desc

        //Return view ids
        return extractVideoIds(viewcount1);
    }

    private static String[] extractVideoIds(ArrayList<Distance> input) {
        String[] videoIds = new String[input.size()];
        int i = 0;
        for (Distance video : input) {
            videoIds[i++] = video.getVideoid();
        }
        return videoIds;
    }

    private static String[] weigh(SortWeight[] sortWeights) {
        HashMap<String, Double> weightmap = new HashMap<>();

        for (SortWeight sortWeight : sortWeights) {
            for (int i = 0; i < sortWeight.getVideos().length; i++) {
                String videoId = (sortWeight.getVideos()[i]);
                Double weight = (i + 1) * sortWeight.getWeight();

                if (!weightmap.containsKey(videoId)) {
                    weightmap.put(videoId, weight);
                } else {
                    Double existingWeight = weightmap.get(videoId);
                    Double newWeight = existingWeight + weight;
                    weightmap.put(videoId, newWeight);
                }
            }
        }

        ArrayList<Map.Entry<String, Double>> entries = new ArrayList<>(weightmap.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue()) * -1;
            }
        });

        String[] output = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            output[i] = entries.get(i).getKey();
        }

        return output;
    }

    public static class SortWeight {
        private String[] videos;
        private double weight;

        public SortWeight(String[] videos, double weight) {
            this.videos = videos;
            this.weight = weight;
        }

        public String[] getVideos() {
            return videos;
        }

        public double getWeight() {
            return weight;
        }
    }


}
