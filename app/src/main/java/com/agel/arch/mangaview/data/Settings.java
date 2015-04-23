package com.agel.arch.mangaview.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.widget.Toast;

@Deprecated
public class Settings {
    public static final String PrefsId_lib_root = "prefs_LibraryRoot";
    public static final String PrefsId_show_with_images = "prefs_ShowWithImages";
    public static final String PrefsId_zoom_factor = "prefs_ZoomFactor";
    public static final String PrefsId_directory_color = "prefs_DirectoryColor";
    public static final String PrefsId_file_color = "prefs_FileColor";

    public static final String PrefsId_gestures_next_page = "prefs_GestureNexPage";
    public static final String PrefsId_gestures_prev_page = "prefs_GesturePrevPage";
    public static final String PrefsId_gestures_zoom_in = "prefs_GestureZoomIn";
    public static final String PrefsId_gestures_zoom_out = "prefs_GestureZoomOut";
    public static final String PrefsId_keys_next_page = "prefs_KeyNext";
    public static final String PrefsId_keys_prev_page = "prefs_KeyPrev";
    public static final String PrefsId_keys_zoom_in = "prefs_KeyZoomIn";
    public static final String PrefsId_keys_zoom_out = "prefs_KeyZoomOut";

    private static Settings instance;
    public static Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    //static constants
    public static final int IMAGE_MAX_SIZE = 8388608;

    //settings
    public String LibraryRoot;
    public Boolean ShowOnlyFoldersWithImages;
    public int ZoomFactor;
    public int DirectoryColor;
    public int FileColor;
    public int KeyNextPage;
    public int KeyPrevPage;
    public int KeyZoomIn;
    public int KeyZoomOut;

//    public EnumMap<GestureDirections,GestureActions> mGestureSetting;
    public SparseIntArray mKeySetting;

    public void loadSettings(SharedPreferences prefs, Resources res) {
        Boolean needSave = false;

        //LibraryRoot---------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_lib_root))
        {
            LibraryRoot = Environment.getExternalStorageDirectory().getPath();
            needSave = true;
        }
        else
            LibraryRoot = prefs.getString(PrefsId_lib_root, null);

        //ShowOnlyFoldersWithImages---------------------------------------------------------------
        if(!prefs.contains(PrefsId_show_with_images))
        {
            ShowOnlyFoldersWithImages = true;
            needSave = true;
        }
        else
            ShowOnlyFoldersWithImages = prefs.getBoolean(PrefsId_show_with_images, true);

        //ZoomFactor------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_zoom_factor))
        {
            ZoomFactor = 100;
            needSave = true;
        }
        else
            ZoomFactor = prefs.getInt(PrefsId_zoom_factor, 100);

        //DirectoryColor------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_directory_color))
        {
            DirectoryColor = 0xFF009DFF;
            needSave = true;
        }
        else
            DirectoryColor = prefs.getInt(PrefsId_directory_color, 0xFF009DFF);

        //FileColor----------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_file_color))
        {
            FileColor = 0xFF000000;
            needSave = true;
        }
        else
            FileColor = prefs.getInt(PrefsId_file_color, 0xFFFFFFFF);

        //KeyNextPage----------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_keys_next_page))
        {
            KeyNextPage = KeyEvent.KEYCODE_VOLUME_DOWN;
            needSave = true;
        }
        else
            KeyNextPage = prefs.getInt(PrefsId_keys_next_page,KeyEvent.KEYCODE_VOLUME_DOWN);

        //KeyPrevPage------------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_keys_prev_page))
        {
            KeyPrevPage = KeyEvent.KEYCODE_VOLUME_UP;
            needSave = true;
        }
        else
            KeyPrevPage = prefs.getInt(PrefsId_keys_prev_page,KeyEvent.KEYCODE_VOLUME_UP);

        //KeyZoomIn----------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_keys_next_page))
        {
            KeyZoomIn = KeyEvent.KEYCODE_UNKNOWN;
            needSave = true;
        }
        else
            KeyZoomIn = prefs.getInt(PrefsId_keys_zoom_in,KeyEvent.KEYCODE_UNKNOWN);

        //KeyZoomOut------------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_keys_prev_page))
        {
            KeyZoomOut = KeyEvent.KEYCODE_UNKNOWN;
            needSave = true;
        }
        else
            KeyZoomOut = prefs.getInt(PrefsId_keys_zoom_out,KeyEvent.KEYCODE_UNKNOWN);

        if(needSave)
            saveSettings(prefs,res);

        setupBindings();
    }

    private void setupBindings(){
        Settings settings = Settings.getInstance();

        //Keys
        mKeySetting = new SparseIntArray();

        //next page
        if(settings.KeyNextPage != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyNextPage, Gestures.ACTION_NEXT);
        //prev page
        if(settings.KeyPrevPage != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyPrevPage, Gestures.ACTION_BACK);
        //zoom id
        if(settings.KeyZoomIn != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyZoomIn, Gestures.ACTION_ZOOM_IN);
        //zoom out
        if(settings.KeyZoomOut != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyZoomOut, Gestures.ACTION_ZOOM_OUT);
    }

    public Boolean validateSettings() {
        //TODO implement validation
        return true;
    }

    public void saveSettings(SharedPreferences prefs, Resources res) {
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putString(PrefsId_lib_root, LibraryRoot);
        prefsEdit.putBoolean(PrefsId_show_with_images, ShowOnlyFoldersWithImages);
        prefsEdit.putInt(PrefsId_zoom_factor, ZoomFactor);
        prefsEdit.putInt(PrefsId_directory_color, DirectoryColor);
        prefsEdit.putInt(PrefsId_file_color, FileColor);
        prefsEdit.putInt(PrefsId_keys_next_page, KeyNextPage);
        prefsEdit.putInt(PrefsId_keys_prev_page, KeyPrevPage);
        prefsEdit.commit();
    }

    static public void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}