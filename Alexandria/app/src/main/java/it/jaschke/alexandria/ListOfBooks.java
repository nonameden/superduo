package it.jaschke.alexandria;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;


public class ListOfBooks extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_LIST_BOOKS = 100;
    private static final String EXTRA_SEARCH_STRING = "extra-search-string";

    private BookListAdapter bookListAdapter;
    private ListView bookList;
    private int position = ListView.INVALID_POSITION;
    private EditText searchText;

    public ListOfBooks() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookListAdapter = new BookListAdapter(getActivity(), null, 0);
        getLoaderManager().initLoader(LOADER_LIST_BOOKS, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        Cursor cursor = getActivity().getContentResolver().query(
//                AlexandriaContract.BookEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterBooks();
                    }
                }
        );

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);
        bookList.setAdapter(bookListAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });

        return rootView;
    }

    private void filterBooks() {
        Bundle args = new Bundle();
        args.putString(EXTRA_SEARCH_STRING, searchText.getText().toString());
        getLoaderManager().restartLoader(LOADER_LIST_BOOKS, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args!=null) {
            final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
            String searchString = args.getString(EXTRA_SEARCH_STRING);

            if (!TextUtils.isEmpty(searchString)) {
                searchString = "%" + searchString + "%";
                return new CursorLoader(
                        getActivity(),
                        AlexandriaContract.BookEntry.CONTENT_URI,
                        null,
                        selection,
                        new String[]{searchString, searchString},
                        null
                );
            }
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            bookList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.books);
    }
}
