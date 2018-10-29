package com.anagog.jedaidrivingclustersplayground;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.anagog.jedai.core.api.JedAI;
import com.anagog.jedai.plugin.parking.JedAIParking;
import com.anagog.jedaidrivingclustersplayground.jedaiutils.JedAIHelper;

public class App extends Application {

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        //copy database on very first run
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains("isFirstRun")){
            JedAIHelper.copyDBonFirstRun(this);
            preferences.edit().putBoolean("isFirstRun", false).apply();
        }

        // Setup the SDK with the application context
        JedAI.setup(this);
        JedAIParking.setup(this);
    }
}
