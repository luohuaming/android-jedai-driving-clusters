package com.anagog.jedaidrivingclustersplayground.jedaiutils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class JedAIHelper {

    //a helper function to calculate a median of an array
    private double medianArray(ArrayList<Double> array) {
        if (array.size() == 0) {
            return 0.0D;
        }
        //calculate median of home locations
        ArrayList<Double> arrayToMedian = new ArrayList<>(array);

        Collections.sort(arrayToMedian);
        double median;
        if (arrayToMedian.size() % 2 == 0)
            median = (arrayToMedian.get(arrayToMedian.size() / 2) + arrayToMedian.get(arrayToMedian.size() / 2 - 1)) / 2;
        else
            median = arrayToMedian.get(arrayToMedian.size() / 2);

        return median;
    }


    public static void copyDBonFirstRun(Context context) {
        String appDataPath = context.getApplicationInfo().dataDir;
        File dbFolder = new File(appDataPath + "/databases");
        dbFolder.mkdir();
        File jedaiDB = new File(dbFolder + "/jedai.db");
        copyDB(context, "jedai.db", jedaiDB);
    }

    private static void copyDB(Context context, String dbName, File file) {
        try {
            InputStream inputStream = context.getAssets().open(dbName);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
    }

}
