package io.pros.oassis_glass;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.glass.widget.CardBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UploadPhotoTask extends AsyncTask<byte[], Void, String> {
    private final static String WATSON = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classify?api_key=4de6b3bff8a053ecfca4308dcdb9a9778ba3753a&version=2016-05-20&classifier_ids=traffic_lights_test_920709042";

    HttpURLConnection urlConnection = null;

    Activity mainActivity;
    TextToSpeech tts;

    public UploadPhotoTask(Activity activity) {
        mainActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CardBuilder cb = new CardBuilder(mainActivity, CardBuilder.Layout.TEXT);
        cb.setText("Loading, please wait...");
        //mainActivity.setContentView(cb.getView());
    }

    @Override
    protected String doInBackground(byte[]... image) {
        Log.v("AsynkTask", "Picture uploading");

        // params comes from the execute() call: params[0] is the url.
        try {
            //image[0]
            Log.v("URL", WATSON);
            URL url = new URL(WATSON);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(image[0].length);
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(image[0]);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            Log.v("RESPONSE", urlConnection.getResponseCode()+"\n" + result);
            return result;

        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }

        //return "doInBackground non-sense return";
    }

    @Override
    protected void onPostExecute(String result) {
        String res = "An error occurred";
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray array = obj.getJSONArray("images").getJSONObject(0).getJSONArray("classifiers");
            if (array.length() > 0)
                res = array.getJSONObject(0).getJSONArray("classes").getJSONObject(0).getString("class");
            else
                res = "Traffic lights not found";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalRes = res;
        tts = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.speak(finalRes, TextToSpeech.QUEUE_ADD, null);
            }
        });
        mainActivity.finish();
    }
}