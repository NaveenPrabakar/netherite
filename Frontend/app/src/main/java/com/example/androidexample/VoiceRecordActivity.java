package com.example.androidexample;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VoiceRecordActivity extends AppCompatActivity {

    // Initializing all variables..
    private TextView startTV, stopTV, playTV, stopplayTV, statusTV, txthead;

    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        // initialize all variables with their layout items.
        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        stopTV = findViewById(R.id.btnStop);
        playTV = findViewById(R.id.btnPlay);
        stopplayTV = findViewById(R.id.btnStopPlay);
        txthead = findViewById(R.id.txthead);

        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start recording method will
                // start the recording of audio.
                startRecording();
            }
        });
        stopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause Recording method will
                // pause the recording of audio.
                pauseRecording();

            }
        });
        playTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play audio method will play
                // the audio which we have recorded
                playAudio();
            }
        });
        stopplayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause play method will
                // pause the play of audio
                pausePlaying();
            }
        });
    }

    private void startRecording() {
        if (CheckPermissions()) {
            txthead.setText("Audio Recording");

            // Initialize the filename for the recorded audio file with .webm extension
            mFileName = getExternalFilesDir(null).getAbsolutePath() + "/AudioRecording.m4a";
            Log.d("Voice", "startRecording: " + mFileName);

            // Release any existing MediaRecorder instance before starting a new one
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();

            try {
                // Set the audio source to the microphone
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                // Set the output format to WEBM
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                // Set the audio encoder to VORBIS, compatible with WEBM
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                // Set the output file location
                mRecorder.setOutputFile(mFileName);

                // Prepare the recorder
                mRecorder.prepare();
                mRecorder.start();
                statusTV.setText("Recording Started");

            } catch (IOException e) {
                Log.e("TAG", "prepare() failed: " + e.getMessage());
            } catch (IllegalStateException e) {
                Log.e("TAG", "start() failed: Illegal State - " + e.getMessage());
            }
        } else {
            // Request permissions if they are not already granted
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(VoiceRecordActivity.this,
                new String[]{RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }



    public void playAudio() {

        // for playing our recorded audio
        // we are using media player class.
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = new MediaPlayer();

        try {
            // below method is used to set the
            // data source which will be our file name
            Log.d("Voice", "playAudio: " + mFileName);
            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pauseRecording() {

        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;
        statusTV.setText("Recording Stopped");
        printDirectoryContents("/storage/emulated/0/Android/data/com.example.androidexample/files/");
    }

    public void pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.

        mPlayer.release();
        mPlayer = null;
        statusTV.setText("Recording Play Stopped");
    }

    private void printDirectoryContents(String path) {
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        Log.d("DirectoryContents", "Directory: " + file.getAbsolutePath());
                        // Recursively print subdirectories
                        printDirectoryContents(file.getAbsolutePath());
                    } else {
                        Log.d("DirectoryContents", "File: " + file.getAbsolutePath());
                    }
                }
            } else {
                Log.d("DirectoryContents", "No files found in directory: " + path);
            }
        } else {
            Log.e("DirectoryContents", "Directory does not exist or is not a directory: " + path);
        }
    }
}
