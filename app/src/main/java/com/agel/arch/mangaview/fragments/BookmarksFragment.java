package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agel.arch.mangaview.MainActivity;
import com.agel.arch.mangaview.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookmarksFragment extends Fragment {

    public BookmarksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bookrmarks_layout, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.section_label);
        tv.setText("BookmarksFragment");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.BookmarksSection);
    }
}
