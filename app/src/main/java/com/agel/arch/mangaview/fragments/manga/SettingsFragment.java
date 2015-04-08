package com.agel.arch.mangaview.fragments.manga;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.section_label);
        tv.setText("SettingsFragment");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.SettingsSection);
    }
}
