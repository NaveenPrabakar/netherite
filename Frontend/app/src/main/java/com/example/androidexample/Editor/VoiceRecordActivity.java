package com.example.androidexample.Editor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.MultipartAudioRequest;
import com.example.androidexample.Volleys.VolleySingleton;

public class VoiceRecordActivity extends AppCompatActivity {

    private TextView  txthead, statusTV, fileHeader;
    private ImageButton startTV, playTV;
    private Button accept, reject;
    private ImageView backToText;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null, fileKey;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private static final String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/SpeechToTextAIuse/transcribe2";
    private static final String UPLOAD_EMAIL_URL = "http://coms-3090-068.class.las.iastate.edu:8080/SpeechToTextAIuse/createSpeechUser/";

    private String email;
    private String content;
    private String recorded = "";
    private Boolean isRecording = false;
    private Boolean isPlaying = false;

    private byte[] voiceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        backToText = findViewById(R.id.backToTextActivity);
        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        playTV = findViewById(R.id.btnPlay);
        txthead = findViewById(R.id.txthead);

        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        fileHeader = findViewById(R.id.headerTitleVoice);


        NavigationBar navigationBar = new NavigationBar(this);
        navigationBar.addNavigationBar();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        email = UserPreferences.getEmail(this);
        content = extras.getString("CONTENT");
        fileKey = extras.getString("FILEKEY", "");
        fileHeader.setText(fileKey);



        startTV.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            }else{
                startRecording();
            }
        });
        playTV.setOnClickListener(v -> {
            if (isPlaying) {
                stopPlaying();
            }else{
                playAudio();
            }
        });
        accept.setOnClickListener(v -> {
            uploadVoiceEmail();
        });
        reject.setOnClickListener(v -> {
            recorded = "";
            txthead.setText("");
            Intent intent2 = new Intent(VoiceRecordActivity.this, TextActivity.class);
            intent2.putExtra("CONTENT", content);
            intent2.putExtra("RECORDED", "Nothing is recorded");
            intent2.putExtra("FILEKEY", fileKey);
            startActivity(intent2);
        });
        backToText.setOnClickListener(v -> {
            recorded = "";
            Intent intent2 = new Intent(VoiceRecordActivity.this, TextActivity.class);
            intent2.putExtra("CONTENT", content);
            intent2.putExtra("RECORDED", "Nothing is recorded");
            intent2.putExtra("FILEKEY", fileKey);
            startActivity(intent2);
        });

    }
    /**
     * Starts audio recording if the required permissions are granted.
     * Initializes and configures the MediaRecorder, prepares it, and starts recording.
     * If permissions are not granted, requests them from the user.
     */
    private void startRecording() {
        isRecording = true;
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

    /**
     * Stops the audio recording and releases the MediaRecorder resources.
     * Updates the status to indicate recording has stopped.
     */

    private void stopRecording() {
        isRecording = false;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            mRecorder.stop();
            statusTV.setText("Recording Stopped");

            retriever.setDataSource(mFileName);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = Long.parseLong(durationStr);

            statusTV.setText("Duration: " + formatDuration(durationMs));


            uploadVoice();
        } catch (RuntimeException e) {
            Log.e("TAG", "stop() failed: " + e.getMessage());
        } finally {
            releaseMediaRecorder();
        }
    }

    // Helper Method to Format Duration
    private String formatDuration(long durationMs) {
        int seconds = (int) (durationMs / 1000) % 60;
        int minutes = (int) (durationMs / (1000 * 60)) % 60;
        int hours = (int) (durationMs / (1000 * 60 * 60));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Releases the MediaRecorder resources.
     */

    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * Plays the recorded audio file using the MediaPlayer.
     * Prepares and starts playback, and updates the status to indicate playback has started.
     */

    private void playAudio() {
        releaseMediaPlayer();
        mPlayer = new MediaPlayer();
        isPlaying = true;

        try {
            Log.d("Voice", "playAudio: " + mFileName);
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            statusTV.setText("Playing Recording");
            mPlayer.setOnCompletionListener( mp -> {
                isPlaying = false;
                statusTV.setText("Playback Stopped");
                releaseMediaPlayer();
            });
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    /**
     * Stops the audio playback and releases the MediaPlayer resources.
     * Updates the status to indicate playback has stopped and uploads the recorded audio.
     */

    private void stopPlaying() {
        isPlaying = false;
        releaseMediaPlayer();
        statusTV.setText("Playback Stopped");

    }

    /**
     * Releases the MediaPlayer resources.
     */

    private void releaseMediaPlayer() {
        isPlaying = false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Checks if the necessary audio recording permissions are granted.
     *
     * @return true if permissions are granted, false otherwise
     */

    private boolean checkPermissions() {
        int resultAudio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return resultAudio == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests audio recording and external storage permissions from the user.
     */

    private void requestPermissions() {
        ActivityCompat.requestPermissions(VoiceRecordActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    /**
     * Uploads the recorded audio file to the server as a multipart request.
     * Converts the audio file to a byte array and sends it via the Volley request.
     */

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
                    Toast.makeText(getApplicationContext(), "Voice Upload Sucessful", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Log.e("Upload", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Voice upload failed", Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    /**
     * Uploads the recorded audio file to the server with additional user data.
     * Redirects to the TextActivity upon successful upload.
     */

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
                    intent2.putExtra("CONTENT", content);
                    intent2.putExtra("RECORDED", recorded);
                    intent2.putExtra("FILEKEY", fileKey);
                    startActivity(intent2);
                    },
                error -> Log.e("Upload", "Error: " + error.getMessage())
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    /**
     * Converts the audio file specified by the URI into a byte array.
     *
     * @param audioUri the URI of the audio file to convert
     * @return a byte array representing the audio file, or null if conversion fails
     */

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
