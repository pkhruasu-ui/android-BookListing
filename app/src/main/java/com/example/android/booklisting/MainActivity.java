package com.example.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String USGS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1";

    private EditText queryView;
    private TextView emptyView;
    private ProgressBar loadingSpinner;
    private BookAdaptor bookAdaptor;

    //define callback interface
    interface SearchCallbackInterface {

        void onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views of each element;
        ListView listView = (ListView) findViewById(R.id.book_list);
        emptyView = (TextView) findViewById(R.id.empty_view);
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        queryView = (EditText) findViewById(R.id.query);

        Button searchButton = (Button) findViewById(R.id.search_btn);

        // init adaptor
        bookAdaptor = new BookAdaptor(this, new ArrayList<Book>());

        listView.setEmptyView(emptyView);

        listView.setAdapter(bookAdaptor);

        // initialize search
        search(new SearchCallbackInterface() {
            @Override
            public void onStart() {
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
            }
        });

        // pass callback when user click search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(new SearchCallbackInterface() {
                    @Override
                    public void onStart() {
                        // clear result
                        bookAdaptor.clear();
                        emptyView.setText("");

                        getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle args) {
        String query = queryView.getText().toString();
        return new BookLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // clear old list
        bookAdaptor.clear();

        loadingSpinner.setVisibility(View.GONE);

        if (books != null && !books.isEmpty()) {
            bookAdaptor.addAll(books);
        } else {
            emptyView.setText(R.string.no_result);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookAdaptor.clear();
    }

    /**
     * Helper function for checking internet connectivty
     *
     * @return true/false
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Setup and start/restart the loader base on the callback.
     *
     * @param callback SearchCallbackInterface to start/restart the loader
     */
    private void search(SearchCallbackInterface callback) {

        if (isConnected()) {
            loadingSpinner.setVisibility(View.VISIBLE);

            callback.onStart();
        } else {
            loadingSpinner.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }
    }
}
