package com.example.irrigationsystem.muneer.utility;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.irrigationsystem.BuildConfig;

import java.util.List;

public class PreferenceUtil {
    public static final String TAG="preference util tag";
    public static final String COUNT_VALUE = "count_value";
    public static final String COUNT_NUM = "count";
    private static final String INSTA_TOKEN = "insta token";
    private static final String CLIENT = "to";
    private static final String COOKIE_FILE = "cookieFile";
    public static final String WEB_VIEW_COOKIE="web view cookie";
    private static PreferenceUtil sInstance;
    private SharedPreferences mPref;

    // check before publishing

    private PreferenceUtil(final Context context) {
        mPref=context.getSharedPreferences(BuildConfig.APPLICATION_ID,Context.MODE_PRIVATE);
    }
    public static PreferenceUtil getInstance(final Context context) {
        if (sInstance == null)
            sInstance = new PreferenceUtil(context);
        return sInstance;
    }

    public static String[] getPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
    }


    public static void unsupported(Context context) {
        Toast.makeText(context,"This feature is not supported on your handset",Toast.LENGTH_LONG).show();
    }

    public static void comingSoon(Context context) {
        Toast.makeText(context,"This feature will be added in upcoming version",Toast.LENGTH_LONG).show();
    }




    /*
    * code added by muneer
    *
    * */

    @Nullable
    public static ProgressDialog showAlert(String message, Context context) {
        ProgressDialog progressDialog = null;
        try{
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "showAlert: "+e.toString() );
        }
        return progressDialog;
    }




    public void putString(String key, String value) {
        mPref.edit().putString(key,value).apply();
    }

    public boolean contain(String key) {
        return mPref.contains(key);
    }



}