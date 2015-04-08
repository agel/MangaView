package com.agel.arch.mangaview.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.EnumMap;
import java.util.Hashtable;

public class Settings {
    public static final String PrefsId_lib_root = "prefs_LibraryRoot";
    public static final String PrefsId_show_with_images = "prefs_ShowWithImages";
    public static final String PrefsId_zoom_factor = "prefs_ZoomFactor";
    public static final String PrefsId_directory_color = "prefs_DirectoryColor";
    public static final String PrefsId_file_color = "prefs_FileColor";
    public static final String PrefsId_swipe_time_threshold = "prefs_SwipeTimeThreshold";
    public static final String PrefsId_swipe_shape_threshold = "prefs_SwipeShapeThreshold";
    public static final String PrefsId_swipe_length_threshold = "prefs_SwipeLengthThreshold";
    public static final String PrefsId_cancel_tremble_threshold = "prefs_CancelTrembleThreshold";

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
    public int SwipeTimeThreshold;
    public int SwipeShapeThreshold;
    public int SwipeLengthThreshold;
    public int CancelTrembleThreshold;
    public GestureDirections GestureNextPage;
    public GestureDirections GesturePrevPage;
    public GestureDirections GestureZoomIn;
    public GestureDirections GestureZoomOut;
    public int KeyNextPage;
    public int KeyPrevPage;
    public int KeyZoomIn;
    public int KeyZoomOut;

    public EnumMap<GestureDirections,GestureActions> mGestureSetting;
    public Hashtable<Integer,GestureActions> mKeySetting;

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

        //SwipeTimeThreshold-----------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_swipe_time_threshold))
        {
            SwipeTimeThreshold = 250;
            needSave = true;
        }
        else
            SwipeTimeThreshold = prefs.getInt(PrefsId_swipe_time_threshold, 250);

        //SwipeShapeThreshold------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_swipe_shape_threshold))
        {
            SwipeShapeThreshold = 5;
            needSave = true;
        }
        else
            SwipeShapeThreshold = prefs.getInt(PrefsId_swipe_shape_threshold, 5);

        //SwipeLengthThreshold------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_swipe_length_threshold))
        {
            SwipeLengthThreshold = 75;
            needSave = true;
        }
        else
            SwipeLengthThreshold = prefs.getInt(PrefsId_swipe_length_threshold, 55);

        //CancelTrembleThreshold------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_cancel_tremble_threshold))
        {
            CancelTrembleThreshold = 4;
            needSave = true;
        }
        else
            CancelTrembleThreshold = prefs.getInt(PrefsId_cancel_tremble_threshold, 4);

        //GestureNextPage---------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_gestures_next_page))
        {
            GestureNextPage = GestureDirections.RIGHT;
            needSave = true;
        }
        else
            GestureNextPage = GestureDirections.valueOf(GestureDirections.class, prefs.getString(PrefsId_gestures_next_page, GestureDirections.RIGHT.name()));

        //GesturePrevPage-------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_gestures_prev_page))
        {
            GesturePrevPage = GestureDirections.LEFT;
            needSave = true;
        }
        else
            GesturePrevPage = GestureDirections.valueOf(GestureDirections.class, prefs.getString(PrefsId_gestures_prev_page, GestureDirections.LEFT.name()));

        //GestureZoomIn---------------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_gestures_next_page))
        {
            GestureZoomIn = GestureDirections.NONE;
            needSave = true;
        }
        else
            GestureZoomIn = GestureDirections.valueOf(GestureDirections.class, prefs.getString(PrefsId_gestures_zoom_in, GestureDirections.NONE.name()));

        //GestureZoomOut-------------------------------------------------------------------------------------
        if(!prefs.contains(PrefsId_gestures_prev_page))
        {
            GestureZoomOut = GestureDirections.NONE;
            needSave = true;
        }
        else
            GestureZoomOut = GestureDirections.valueOf(GestureDirections.class, prefs.getString(PrefsId_gestures_zoom_out, GestureDirections.NONE.name()));

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
        //Gestures
        mGestureSetting = new EnumMap<>(GestureDirections.class);

        //next page
        if(settings.GestureNextPage != GestureDirections.NONE)
            mGestureSetting.put(settings.GestureNextPage , GestureActions.NEXT);
        //prev page
        if(settings.GesturePrevPage != GestureDirections.NONE)
            mGestureSetting.put(settings.GesturePrevPage, GestureActions.BACK);
        //zoom in
        if(settings.GestureZoomIn != GestureDirections.NONE)
            mGestureSetting.put(settings.GestureZoomIn, GestureActions.ZOOM_IN);
        //zoom out
        if(settings.GestureZoomOut != GestureDirections.NONE)
            mGestureSetting.put(settings.GestureZoomOut, GestureActions.ZOOM_OUT);

        //Keys
        mKeySetting = new Hashtable<>();

        //next page
        if(settings.KeyNextPage != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyNextPage, GestureActions.NEXT);
        //prev page
        if(settings.KeyPrevPage != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyPrevPage, GestureActions.BACK);
        //zoom id
        if(settings.KeyZoomIn != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyZoomIn, GestureActions.ZOOM_IN);
        //zoom out
        if(settings.KeyZoomOut != KeyEvent.KEYCODE_UNKNOWN)
            mKeySetting.put(settings.KeyZoomOut, GestureActions.ZOOM_OUT);
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
        prefsEdit.putInt(PrefsId_swipe_time_threshold, SwipeTimeThreshold);
        prefsEdit.putInt(PrefsId_swipe_shape_threshold, SwipeShapeThreshold);
        prefsEdit.putInt(PrefsId_swipe_length_threshold, SwipeLengthThreshold);
        prefsEdit.putInt(PrefsId_cancel_tremble_threshold, CancelTrembleThreshold);
        prefsEdit.putInt(PrefsId_keys_next_page, KeyNextPage);
        prefsEdit.putInt(PrefsId_keys_prev_page, KeyPrevPage);
        prefsEdit.commit();
    }

    static public boolean hasStorageAccessible() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }
    static public void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}