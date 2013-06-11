package com.ikai.mytestapplication;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String API_KEY = "YOUR API KEY HERE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.textField);
        text.setText("Account type: " + GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Started new thread!");

                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {}
                }).setApplicationName("youtube-cmdline-search-sample").build();

                YouTube.Search.List search = null;
                try {
                    search = youtube.search().list("id,snippet");
                    search.setKey(API_KEY);
                    search.setQ("dogs");
                    search.setType("video");

                    search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                    SearchListResponse searchResponse = search.execute();

                    List<SearchResult> searchResultList = searchResponse.getItems();
                    Log.i(TAG, "Number of results: " + searchResultList.size());

                    for(SearchResult result : searchResultList) {
                        Log.i(TAG, "Got video: " + result.getSnippet().getTitle());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException trying to search YouTube: " + e);
                }
            }
        }).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
