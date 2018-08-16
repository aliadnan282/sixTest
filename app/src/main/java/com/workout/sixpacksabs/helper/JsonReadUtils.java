package com.workout.sixpacksabs.helper;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.workout.sixpacksabs.helper.AppConstant.mContext;

/**
 * Created by adnanali on 01/03/2017.
 */

public class JsonReadUtils {
    public static String loadJSONFromAsset(String fileName) {
        String json ;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        return json;
    }

    private static boolean writeTextToFile(String fileName, String data) {
        String json = null;
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            return true;
        } catch (Exception ex) {

            return false;
        }

    }

    public static void writeFileInternalMemory(String jsontemp, String fileName) {
        File file = JsonReadUtils.createFileInternalMemory(mContext.getFilesDir(), fileName);
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonReadUtils.writeTextToFile(fileName, jsontemp);
    }

    public static File createFileInternalMemory(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }

    public static List<String> getKeys(JSONObject jsonObject) {
        List<String> keysList = new ArrayList<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            keysList.add((String) keys.next());
        }
        return keysList;
    }

    public static ArrayList<String> getList(JSONArray jsonArray) {
        ArrayList<String> keysList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                keysList.add(jsonArray.getString(i));
            } catch (JSONException ex) {

            }
        }
        return keysList;
    }

    public static String readFileFromInternalMemory(String filename) {

        File file = new File(mContext.getFilesDir(), filename);
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ex) {

            return null;
            //You'll need to add proper error handling here
        }
        return text.toString();
    }
}
