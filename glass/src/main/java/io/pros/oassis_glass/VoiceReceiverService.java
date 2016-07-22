package io.pros.oassis_glass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lopejoel on 7/21/2016.
 */
public class VoiceReceiverService extends Service {
    TextToSpeech tts;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final int TAKE_PICTURE_REQUEST = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> voiceResults = intent.getExtras()
                .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        Log.w("service", "It works!");

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.speak("It works!", TextToSpeech.QUEUE_ADD, null);
                CameraUpload cameraUpload = new CameraUpload(getApplicationContext());
                cameraUpload.takePhotoAndUpload(getApplicationContext());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //getApplication().startActivity(intent);
            }
        });

        return 0;
    }


}
