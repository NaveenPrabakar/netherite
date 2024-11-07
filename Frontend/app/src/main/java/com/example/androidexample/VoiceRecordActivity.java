package com.example.androidexample;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.android.volley.Request;

public class VoiceRecordActivity extends AppCompatActivity {

    private TextView startTV, stopTV, playTV, stopplayTV, statusTV, txthead;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private static final String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/SpeechToTextAIuse/transcribe2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        stopTV = findViewById(R.id.btnStop);
        playTV = findViewById(R.id.btnPlay);
        stopplayTV = findViewById(R.id.btnStopPlay);
        txthead = findViewById(R.id.txthead);

        startTV.setOnClickListener(v -> startRecording());
        stopTV.setOnClickListener(v -> stopRecording());
        playTV.setOnClickListener(v -> playAudio());
        stopplayTV.setOnClickListener(v -> stopPlaying());
    }

    private void startRecording() {
        if (checkPermissions()) {
            txthead.setText("Audio Recording");
            mFileName = getExternalFilesDir(null).getAbsolutePath() + "/AudioRecording.m4a";
            Log.d("Voice", "startRecording: " + mFileName);

            releaseMediaRecorder();

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile(mFileName);

            try {
                mRecorder.prepare();
                mRecorder.start();
                statusTV.setText("Recording Started");
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed: " + e.getMessage());
            } catch (IllegalStateException e) {
                Log.e("TAG", "start() failed: " + e.getMessage());
            }
        } else {
            requestPermissions();
        }
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            statusTV.setText("Recording Stopped");
        } catch (RuntimeException e) {
            Log.e("TAG", "stop() failed: " + e.getMessage());
        } finally {
            releaseMediaRecorder();
        }
    }

    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void playAudio() {
        releaseMediaPlayer();
        mPlayer = new MediaPlayer();

        try {
            Log.d("Voice", "playAudio: " + mFileName);
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            statusTV.setText("Playing Recording");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    private void stopPlaying() {
        releaseMediaPlayer();
        statusTV.setText("Playback Stopped");
        uploadVoice();
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private boolean checkPermissions() {
        int resultAudio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return resultAudio == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(VoiceRecordActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void uploadVoice() {
        byte[] voiceData = convertAudioUriToBytes(Uri.fromFile(new File(mFileName)));
        if (voiceData == null) {
            Log.e("Upload", "Conversion to bytes failed");
            return;
        }

        MultipartAudioRequest multipartRequest = new MultipartAudioRequest(
                Request.Method.POST,
                UPLOAD_URL,
                voiceData,
                response -> Log.d("Upload", "Response: " + response),
                error -> Log.e("Upload", "Error: " + error.getMessage())
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    private byte[] convertAudioUriToBytes(Uri audioUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(audioUri);
             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
