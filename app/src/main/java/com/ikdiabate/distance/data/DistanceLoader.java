package com.ikdiabate.distance.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.location.LocationServices;
import com.ikdiabate.distance.BuildConfig;
import com.ikdiabate.distance.QueryUtils;
import com.ikdiabate.distance.SortUtils;

import java.util.List;

/**
 * Performs asynchronous loading of data.
 *
 * @Author: Ibrahim Diabate
 * @Version: November 2017
 */

public class DistanceLoader extends AsyncTaskLoader<List<Distance>> {

    private static final String YOUTUBE_DATA_API =
            "https://www.googleapis.com/youtube/v3/search?";
    /** Snippet */
    private static final String PART = "snippet";
    /** Radius in miles */
    private static final String LOCATION_RADIUS = "10mi";
    /** Max results */
    private static final String MAX_RESULTS = "25";
    /** Result type */
    private static final String TYPE = "video";
    /** Default sort order */
    private static final String DEFAULT_ORDER = "default";
    /** Tag for log messages */
    private static final String LOG_TAG = DistanceLoader.class.getName();


    /** Location of the user */
    private String mLocation;
    /** Query URL */
    private String mQuery;
    /** Query URL */
    private String mOrderBy;
    /** Query URL */
    private Uri mUrl;

    // Constructor
    public DistanceLoader(Context context, String location,
                          String query, String orderBy) {
        super(context);

        this.mLocation = location;
        this.mQuery = query;
        this.mOrderBy = orderBy;

        Uri baseUri = Uri.parse(YOUTUBE_DATA_API);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("part", PART);
        uriBuilder.appendQueryParameter("location", location);
        uriBuilder.appendQueryParameter("locationRadius", LOCATION_RADIUS);
        uriBuilder.appendQueryParameter("maxResults", MAX_RESULTS);
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("type", TYPE);

        uriBuilder.appendQueryParameter("order",
                DEFAULT_ORDER.equals(orderBy) ? "relevance" : orderBy);
        uriBuilder.appendQueryParameter("key", BuildConfig.API_KEY_TOKEN);

        this.mUrl = uriBuilder.build();
    }

    @Override
    protected void onStartLoading() {
        //Forces asynchronous load of the video
        forceLoad();
    }

    /**
     * Loads videos on a background thread.
     */
    @Override
    public List<Distance> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Distance> distances = QueryUtils.fetchVideoData(mUrl.toString());

        if (DEFAULT_ORDER.equals(this.mOrderBy)) {
            return SortUtils.defaultSort(distances);
        } else {
            return distances;
        }
    }
}
