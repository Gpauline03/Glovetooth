package com.example.glovetooth;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

//import com.example.glovetooth.ui.main.MainFragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.MediaPlayer;

import com.example.glovetooth.ui.main.MainFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public final static String MODULE_MAC = "20:15:09:29:03:74";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    Button buttonStart, buttonPlay, buttonForward, buttonVolSup, buttonVolInf;

    final MediaPlayer mPlayer = new MediaPlayer();
    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    boolean deviceConnected=false;
    TextView textView;
    EditText editText;
    byte buffer[];
    boolean stopThread;

    //ConnectedThread btt = null;
    boolean found = false;
    TextView response;
    boolean playFlag = false;
    boolean forwardFlag = false;
    boolean volSupFlag = false;
    boolean volInfFlag = false;
    public Handler mHandler;

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Pause the playback
            }
        }
    }

    private static final String TAG = "[BLUETOOTH]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    public void setUiEnabled(boolean bool){
        buttonStart.setEnabled(!bool);
        /*buttonPlay.setEnabled(bool);
        buttonForward.setEnabled(bool);
        buttonVolSup.setEnabled(bool);
        buttonVolInf.setEnabled(bool);*/
        textView.setEnabled(bool);
    }

    public boolean BTInit() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't Support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!btAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
        }

        Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(MODULE_MAC)) {
                    btDevice = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTConnect() {

        boolean connected = true;
        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    public void onClickStart(View view){
        if(BTInit()){
            if(BTConnect()){
                setUiEnabled(true);
                deviceConnected=true;
                beginListenForData();
                textView.append("\nConnection Opened!\n");
            }
        }
    }

    void beginListenForData(){
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable(){
            public void run(){
                while(!Thread.currentThread().isInterrupted() && !stopThread){
                    try{
                        int byteCount = inputStream.available();
                        if(byteCount > 0){
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run(){
                                    textView.append(string);
                                }
                            });
                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickSend(View view) {
        String string = editText.getText().toString();
        string.concat("\n");
        try {
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.append("\nSent Data:"+string+"\n");
    }

    public void AudioInit(){

        final Button buttonPlay = findViewById(R.id.playPause);
        final Button buttonForward = findViewById(R.id.Forward);
        final Button buttonVolSup = findViewById(R.id.volumeSup);
        final Button buttonVolInf = findViewById(R.id.volumeInf);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        buttonPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
            }
        });


        /*buttonForward.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

            }
        });

        buttonVolSup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

            }
        });

        buttonVolInf.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

            }
        });
        Log.i(TAG,"onCreate");*/
    }
}
