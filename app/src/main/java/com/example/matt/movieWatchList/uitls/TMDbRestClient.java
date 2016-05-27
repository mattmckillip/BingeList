package com.example.matt.movieWatchList.uitls;

import com.loopj.android.http.*;

/**
 * Created by Matt on 5/27/2016.
 */
public class TMDbRestClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
