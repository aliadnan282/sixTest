package com.workout.sixpacksabs.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.workout.sixpacksabs.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.workout.sixpacksabs.helper.AppConstant.ARABIC;
import static com.workout.sixpacksabs.helper.AppConstant.DATE_FORMAT_TEXT;
import static com.workout.sixpacksabs.helper.AppConstant.ENGLISH;
import static com.workout.sixpacksabs.helper.AppConstant.FRENCH;
import static com.workout.sixpacksabs.helper.AppConstant.GERMAN;
import static com.workout.sixpacksabs.helper.AppConstant.GET_ARABIC;
import static com.workout.sixpacksabs.helper.AppConstant.GET_ENGLISH;
import static com.workout.sixpacksabs.helper.AppConstant.GET_FRENCH;
import static com.workout.sixpacksabs.helper.AppConstant.GET_GERMAN;
import static com.workout.sixpacksabs.helper.AppConstant.GET_PORTUGUESE;
import static com.workout.sixpacksabs.helper.AppConstant.GET_RUSSIAN;
import static com.workout.sixpacksabs.helper.AppConstant.GET_SPANISH;
import static com.workout.sixpacksabs.helper.AppConstant.PORTUGUESE;
import static com.workout.sixpacksabs.helper.AppConstant.RUSSIAN;
import static com.workout.sixpacksabs.helper.AppConstant.SPANISH;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;

/**
 * Created by AdnanAli on 3/21/2018.
 */

public class AppUtils {
    private static final String DATABASE_NAME = "six_pack";

    public static void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.workout.sixpacksabs" + "/databases/" + DATABASE_NAME;
        // String test = "/data/" + mContext.getPackageName() + "/databases/";
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        // File testFile = new File(data, test);
        Log.d("exportDB", "list: " + currentDB.listFiles());
        ;
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Log.d("DB Exported!", "done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playAudio(int resourceId) {
        try {
            MediaPlayer mp = MediaPlayer.create(mContext, resourceId);
            mp.setOnCompletionListener(mediaPlayer -> mp.release());
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertSecondsToHMmSs(long seconds) {

        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d", m, s);

        //return String.format("%d:%02d:%02d", h,m,s);
    }

    public static Drawable getDrawable(String fileName) {
        try {
            String folder = "images/";
            return Drawable.createFromStream(AppConstant.mContext.getAssets().open(folder + fileName), null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getSelectedLanguage(String language) {
        switch (language) {
            case ENGLISH:
                return GET_ENGLISH;
            case FRENCH:
                return GET_FRENCH;
            case GERMAN:
                return GET_GERMAN;
            case PORTUGUESE:
                return GET_PORTUGUESE;
            case RUSSIAN:
                return GET_RUSSIAN;
            case ARABIC:
                return GET_ARABIC;
            case SPANISH:
                return GET_SPANISH;
        }
        return GET_ENGLISH;
    }

    public static String formatName(String str) {
        if (!TextUtils.isEmpty(str)) {
            String name = "";
            String[] text;
            if (str.contains("_")) ;
            text = str.split("_");
            for (String string : text) {
                name = name + Character.toUpperCase(string.charAt(0)) + string.substring(1, string.length()) + " ";
            }
            return name;
        }
        return "";
    }


    public static long getLongDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String getStringDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMAT_TEXT);

        return format1.format(calendar.getTime());
    }

    public static Intent getShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        sendIntent.putExtra(Intent.EXTRA_TEXT,
                AppConstant.mContext.getResources().getString(R.string.app_share_text_intent) + "\n " + AppConstant.GOOGLE_PLAY_URL + AppConstant.mContext.getPackageName());
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public static Intent getLikeUsIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstant.GOOGLE_PLAY_URL + AppConstant.mContext.getPackageName()));
    }

    public static Intent getMoreAppIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstant.MORE_APPS_URL));
    }

    public static Intent getVideoUrl(String exeLink) {
        String video_path = "http://www.youtube.com/watch?v=" + exeLink;
        Uri uri = Uri.parse(video_path);
        Intent videoClient = new Intent(Intent.ACTION_VIEW, uri);
        return videoClient;
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                mContext.getResources().getDisplayMetrics());
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppConstant.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}
