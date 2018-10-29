package com.anagog.jedaidrivingclustersplayground;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anagog.jedai.core.api.JedAI;
import com.anagog.jedai.plugin.parking.JedAIParking;
import com.anagog.jedai.plugin.parking.JedAIParkingInfo;
import com.anagog.jedai.plugin.parking.JedAIParkingPlace;
import com.anagog.jedai.plugin.parking.JedAITrip;
import com.anagog.jedai.plugin.parking.JedAITripHistory;
import com.anagog.jedai.plugin.parking.JedAITripPoint;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private static final int chunkSize = 1024;

    private TextView textViewResult;

    private boolean isStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If we have all the permissions accepted start JedAI
        if (requestCheckPermissions()) {
            startJedAI();
        }

        textViewResult = findViewById(R.id.textViewResult);

        findViewById(R.id.buttonTestPlugin).setOnClickListener(this);
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        startJedAI();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonTestPlugin) {
            testPlugin();
        }
    }

    private void startJedAI() {
        // Get the instance of the SDK manager
        JedAI jedAI = JedAI.getInstance();

        // Check that the manager initialized correctly
        assert jedAI != null;

        // Start the SDK
        jedAI.start();

        isStarted = true;
    }

    private boolean requestCheckPermissions() {

        // Runtime permission available since Android M (23)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }

        return true;
    }

    private void testPlugin() {
        String toastText;

        if (isStarted) {
            JedAITripHistory jedAITripHistory = JedAIParking.getInstance().getTripHistory();

            String logcatOutput = createOutput(jedAITripHistory);

            writeVeryLongLog(logcatOutput);

            textViewResult.setText(logcatOutput);

            toastText = "Also please see results in the Logcat";
        } else {
            toastText = "JedAI is not started!";
        }

        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    private void writeVeryLongLog(String log) {
        for (int i = 0; i < log.length(); i += chunkSize) {
            Log.d(TAG, log.substring(i, Math.min(log.length(), i + chunkSize)));
        }
    }

    private String createOutput(JedAITripHistory jedAITripHistory) {
        StringBuilder output = new StringBuilder();

        if (jedAITripHistory == null) {
            output.append("\nJedAITripHistory is null");
        } else {
            output.append("\nJedAITripHistory contains:");

            JedAIParkingPlace[] jedAIParkingPlaces = jedAITripHistory.getJedAIParkingPlaces();
            JedAITrip[] jedAITrips = jedAITripHistory.getJedAITrips();

            if (jedAIParkingPlaces == null) {
                output.append("\nJedAIParkingPlace array is null");
            } else if (jedAIParkingPlaces.length == 0) {
                output.append("\nJedAIParkingPlace array is empty");
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIParkingPlace array contains %d elements:",
                        jedAIParkingPlaces.length));
                output.append(createOutput(jedAIParkingPlaces));
            }

            if (jedAITrips == null) {
                output.append("\nJedAITrip array is null");
            } else if (jedAITrips.length == 0) {
                output.append("\nJedAITrip array is empty");
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAITrip array contains %d elements:",
                        jedAITrips.length));
                output.append(createOutput(jedAITrips));
            }
        }

        return output.toString();
    }

    private String createOutput(JedAIParkingPlace[] jedAIParkingPlaces) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < jedAIParkingPlaces.length; ++i) {
            JedAIParkingPlace place = jedAIParkingPlaces[i];

            if (place == null) {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIParkingPlace %d is null", i));
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIParkingPlace %d:", i));
                output.append(createOutput(place));
            }

            output.append("\n");
        }

        return output.toString();
    }

    private String createOutput(JedAIParkingPlace place) {
        StringBuilder output = new StringBuilder();

        output.append(String.format(Locale.getDefault(),
                "\nPlaceId: %d", place.getPlaceId()));
        output.append(String.format(Locale.getDefault(),
                "\nLatitude: %f", place.getLatitude()));
        output.append(String.format(Locale.getDefault(),
                "\nLongitude: %f", place.getLongitude()));
        output.append(String.format(Locale.getDefault(),
                "\nLastVisited: %d", place.getLastVisited()));

        List<JedAIParkingInfo> parkingInfos = place.getParkingInfos();

        if (parkingInfos == null) {
            output.append("\nJedAIParkingInfo list is null");
        } else if (parkingInfos.size() == 0) {
            output.append("\nJedAIParkingInfo list is empty");
        } else {
            output.append(String.format(Locale.getDefault(),
                    "\nJedAIParkingInfo list contains %d elements:",
                    parkingInfos.size()));
            output.append(createOutput(parkingInfos));
        }

        return output.toString();
    }

    private String createOutput(List<JedAIParkingInfo> parkingInfos) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < parkingInfos.size(); ++i) {
            JedAIParkingInfo info = parkingInfos.get(i);

            if (info == null) {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIParkingInfo %d is null", i));
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIParkingInfo %d:", i));
                output.append(createOutput(info));
            }
        }

        return output.toString();
    }

    private String createOutput(JedAIParkingInfo info) {
        StringBuilder output = new StringBuilder();

        output.append(String.format(Locale.getDefault(),
                "\nArrivalTime: %d", info.getArrivalTime()));
        output.append(String.format(Locale.getDefault(),
                "\nDepartureTime: %d", info.getDepartureTime()));
        output.append(String.format(Locale.getDefault(),
                "\nDwellTime: %d", info.getDwellTime()));

        return output.toString();
    }

    private String createOutput(JedAITrip[] jedAITrip) {
        StringBuilder output = new StringBuilder();


        for (int i = 0; i < jedAITrip.length; ++i) {
            JedAITrip trip = jedAITrip[i];

            if (trip == null) {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAITrip %d is null", i));
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAITrip %d:", i));
                output.append(createOutput(trip));
            }

            output.append("\n");
        }

        return output.toString();
    }

    private String createOutput(JedAITrip trip) {
        StringBuilder output = new StringBuilder();

        output.append(String.format(Locale.getDefault(),
                "\nId: %d", trip.getId()));
        output.append(String.format(Locale.getDefault(),
                "\nDepartureTime: %d", trip.getDepartureTime()));
        output.append(String.format(Locale.getDefault(),
                "\nDeparturePlaceId: %d", trip.getDeparturePlaceId()));
        output.append(String.format(Locale.getDefault(),
                "\nArrivalTime: %d", trip.getArrivalTime()));
        output.append(String.format(Locale.getDefault(),
                "\nArrivalPlaceId: %d", trip.getArrivalPlaceId()));
        output.append(String.format(Locale.getDefault(),
                "\nRouteId: %d", trip.getRouteId()));

        JedAITripPoint[] jedAIGPSData = trip.getJedAITripPoint();

        if (jedAIGPSData == null) {
            output.append("\nJedAIGPSData array is null");
        } else if (jedAIGPSData.length == 0) {
            output.append("\nJedAIGPSData array is empty");
        } else {
            output.append(String.format(Locale.getDefault(),
                    "\nJedAIGPSData array contains %d elements:",
                    jedAIGPSData.length));
            output.append(createOutput(jedAIGPSData));
        }


        return output.toString();
    }

    private String createOutput(JedAITripPoint[] gpsData) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < gpsData.length; ++i) {
            JedAITripPoint gpsDataItem = gpsData[i];

            if (gpsDataItem == null) {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIGPSData %d is null", i));
            } else {
                output.append(String.format(Locale.getDefault(),
                        "\nJedAIGPSData %d:", i));
                output.append(createOutput(gpsDataItem));
            }
        }

        return output.toString();
    }


    private String createOutput(JedAITripPoint gpsDataItem) {
        StringBuilder output = new StringBuilder();

        output.append(String.format(Locale.getDefault(),
                "\nLatitude: %f", gpsDataItem.getLatitude()));
        output.append(String.format(Locale.getDefault(),
                "\nLongitude: %f", gpsDataItem.getLongitude()));
        output.append(String.format(Locale.getDefault(),
                "\nSpeed: %f:", gpsDataItem.getSpeed()));
        output.append(String.format(Locale.getDefault(),
                "\nTimestamp: %d", gpsDataItem.getTimestamp()));

        return output.toString();
    }
}
