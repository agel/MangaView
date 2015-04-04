package com.agel.arch.mangaview.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agel.arch.mangaview.data.FileEntryThin;
import com.agel.arch.mangaview.data.Settings;

/**
 * Created by agel on 29/03/2015.
 */
public class FileSystemAdapter extends ArrayAdapter<FileEntryThin> {

    public FileSystemAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView txtView = (TextView)super.getView(position, convertView, parent);

        if(this.getItem(position).IsDirectory == true)
        {
            txtView.setTextColor(Settings.getInstance().DirectoryColor);
        }
        else
        {
            txtView.setTextColor(Settings.getInstance().FileColor);
        }
        return txtView;
    }
}
