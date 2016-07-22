package io.pros.oassis_glass;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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

        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);

        List<String> providers = locManager.getProviders(
                criteria, true /* enabledOnly */);

        double latitude=41.3888784;
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
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                URL url = null;
                try {
                    url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("content-type", "application/json");
                    urlConnection.setRequestProperty("authorization", "key=AIzaSyBsoC0o75bYxQwqyeQPBOfilT3uYHSRCww");
                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("lat", finalLatitude + "");
                    dataObj.put("lon", finalLongitude + "");
                    dataObj.put("message", "is lost");

                    JSONObject obj = new JSONObject();
                    obj.put("data", dataObj);
                    obj.put("to", "fosEhJ0ikE8:APA91bFhh4_A-3CZ_1ZQYHBUHC2P4CFKmaU_m-SJHj0GtfMUD0pSgiWN2-ODLHp9s607wkgrOR0N1YkGQOwqNdBYZS4ei5MJF25aeY-A0alNjOnD6-bEapNUnQigwEeDUx_siCp4nliy");
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
        }.execute();


        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
