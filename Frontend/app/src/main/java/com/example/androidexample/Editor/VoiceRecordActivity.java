package com.example.androidexample.Editor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.androidexample.R;
import com.example.androidexample.Volleys.MultipartAudioRequest;
import com.example.androidexample.Volleys.VolleySingleton;

public class VoiceRecordActivity extends AppCompatActivity {

    private TextView startTV, stopTV, playTV, stopplayTV, statusTV, txthead;
    private Button accept, reject;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private static final String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/SpeechToTextAIuse/transcribe2";
    private static final String UPLOAD_EMAIL_URL = "http://coms-3090-068.class.las.iastate.edu:8080/SpeechToTextAIuse/createSpeechUser/";

    private String email;
    private String content;
    private String password;
    private String username;
    private String fileSystem;
    private String filePath;

    private String recorded = "";

    private byte[] voiceData;

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

        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        fileSystem = extras.getString("FILESYSTEM");
        filePath = extras.getString("PATH");
        email = extras.getString("EMAIL");
        password = extras.getString("PASSWORD");
        username = extras.getString("USERNAME");
        Log.d("EMAIL", extras.getString("EMAIL"));
        Log.d("PASSWORD", extras.getString("PASSWORD"));
        Log.d("FILESYSTEM", extras.getString("FILESYSTEM"));
        Log.d("PATH", extras.getString("PATH"));
        content = extras.getString("CONTENT");

        startTV.setOnClickListener(v -> startRecording());
        stopTV.setOnClickListener(v -> stopRecording());
        playTV.setOnClickListener(v -> playAudio());
        stopplayTV.setOnClickListener(v -> stopPlaying());

        accept.setOnClickListener(v -> {
            uploadVoiceEmail();
        });

        reject.setOnClickListener(v -> {
            recorded = "";
            txthead.setText("");
        });


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
        this.voiceData = voiceData;
        if (voiceData == null) {
            Log.e("Upload", "Conversion to bytes failed");
            return;
        }

        MultipartAudioRequest multipartRequest = new MultipartAudioRequest(
                Request.Method.POST,
                UPLOAD_URL,
                voiceData,
                response -> {
                    Log.d("Upload", "Response: " + response);
                    txthead.setText(response);
                    recorded = response;
                },
                error -> Log.e("Upload", "Error: " + error.getMessage())
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    private void uploadVoiceEmail() {
        String url = UPLOAD_EMAIL_URL + email;
        if (voiceData == null) {
            Log.e("Upload", "Conversion to bytes failed");
            return;
        }

        MultipartAudioRequest multipartRequest = new MultipartAudioRequest(
                Request.Method.POST,
                url,
                voiceData,
                response -> {
                    Log.d("Upload", "Response: " + response);
                    Intent intent2 = new Intent(VoiceRecordActivity.this, TextActivity.class);
                    intent2.putExtra("EMAIL", email);
                    intent2.putExtra("PASSWORD", password);
                    intent2.putExtra("USERNAME", username);
                    intent2.putExtra("FILESYSTEM", fileSystem);
                    intent2.putExtra("PATH", filePath);
                    intent2.putExtra("CONTENT", content);
                    intent2.putExtra("RECORDED", recorded);
                    startActivity(intent2);
                    },
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
