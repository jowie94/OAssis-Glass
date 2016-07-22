package io.pros.oassis_glass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LostService extends Service {
    public LostService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> voiceResults = intent.getExtras()
                .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);

        final LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);

        List<String> providers = locManager.getProviders(
                criteria, true /* enabledOnly */);

        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                new AsyncLocation().execute(location);
                locManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        for (String provider : providers) {
            locManager.requestLocationUpdates(provider, 0, 0, locationListener);
        }
        //locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        /*double latitude=41.3888784;
        double longitude=2.1124079;

        for (String provider : providers) {
            Location location = locManager.getLastKnownLocation(provider);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                break;
            }
        }

        final double finalLatitude = latitude;
        final double finalLongitude = longitude;
        new AsyncLocation().execute();*/


        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static class AsyncLocation extends AsyncTask<Location, Void, Void> {
        @Override
        protected Void doInBackground(Location... location) {
            URL url = null;
            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-type", "application/json");
                urlConnection.setRequestProperty("authorization", "key=AIzaSyBsoC0o75bYxQwqyeQPBOfilT3uYHSRCww");
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                JSONObject dataObj = new JSONObject();
                dataObj.put("lat", location[0].getLatitude() + "");
                dataObj.put("lon", location[0].getLongitude() + "");
                dataObj.put("message", "is lost");

                JSONObject obj = new JSONObject();
                obj.put("data", dataObj);
                obj.put("to", "fHgGlD8Cit0:APA91bE1JLQjnbA4Pppy95r-Wk7Os_hIQ3qrLKrhyiQ570HLFAUxDg_wYnrMkRpbPbpSBRQHK1bcc6uG6wKXfiJ0LFQcCxw-LLU_ZDA_sldEGkDJN_PWqbYKJ7S9BLOLFVTNZ1oAC7NU");
                Log.d("JSON", obj.toString());
                out.write(obj.toString().getBytes());
                out.flush();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                Log.v("RESPONSE", urlConnection.getResponseCode()+"\n" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
