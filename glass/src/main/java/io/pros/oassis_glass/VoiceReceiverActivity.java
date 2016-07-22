package io.pros.oassis_glass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.glass.app.Card;
import com.google.android.glass.content.Intents;
import com.google.android.glass.widget.CardBuilder;

/**
 * Created by lopejoel on 7/21/2016.
 */
public class VoiceReceiverActivity extends Activity {
    CameraSurfaceView m_cameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //takePicture();
        m_cameraSurfaceView = new CameraSurfaceView(this);
        //RelativeLayout rl = new RelativeLayout()
        LinearLayout v = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,0);
        v.addView(m_cameraSurfaceView);
        //v.setVisibility(View.INVISIBLE);
        this.setContentView(v);
        getActionBar().hide();
        //cameraSurfaceView.takePicture();
        //takePicture();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_cameraSurfaceView.releaseCamera();
    }

    private static final int TAKE_PICTURE_REQUEST = 1;

    private void takePicture() {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);*/
        CameraUpload cu = new CameraUpload(this);
        cu.takePhotoAndUpload(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            Log.d("activity", thumbnailPath);
            Log.d("activity", picturePath);
            Log.d("activity", "Picture taken");
            // TODO: Show the thumbnail to the user while the full picture is being
            // processed.
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
