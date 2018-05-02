package com.example.android.booklisting;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by prajakkhruasuwan on 11/12/17.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String GOOGLE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    private String FILTER = "maxResults=20";
    private String query;

    public BookLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {

        List<Book> books = null;
        if (query != null && !query.isEmpty()) {
            String url = getQueryUrl(query);

            try {
                books = QueryUtils.fetchBookData(url);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return books;
    }

    private String getQueryUrl(String query) {

        if (query == null || query.isEmpty()) {
            return null;
        }

        return GOOGLE_API_URL + "?q=" + query + "&" + FILTER;
    }
}