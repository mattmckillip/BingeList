package com.example.matt.movieWatchList.uitls;
import org.json.*;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Matt on 5/27/2016.
 */
public class TMDbRestClientUsage {
    public void getPublicTimeline() throws JSONException {
        /*TMDbRestClient.get("popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = timeline.get(0);
                String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(tweetText);
            }
        });*/
    }
}
