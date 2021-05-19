package com.example.cardiomais;


import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import static com.example.cardiomais.MainActivity.txtHeartString;


public class BtService extends Service {
    UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int MESSAGE_READ = 3;
    StringBuilder dadosBt = new StringBuilder();
    private BluetoothSocket socketBt = null;
    private BluetoothDevice meuDevice = null;
    private ConnectedThread conexaoThread;
    Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        meuDevice = MainActivity.meuDevice;
        handler = new Handler(Looper.myLooper()){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleMessage(@NonNull Message msg) {

                if (msg.what == MESSAGE_READ) {

                    String recebidos = (String) msg.obj;

                    dadosBt.append(recebidos);

                    int fimInfo = dadosBt.indexOf("}");
                    if (fimInfo > 0) {
                        String dataComplete = dadosBt.substring(0, fimInfo);
                        int tamanhoInfo = dataComplete.length();
                        if (dataComplete.charAt(0) == '{') {
                            String dadosFinais = dadosBt.substring(1, tamanhoInfo);
                            txtHeartString=dadosFinais;
                            MainActivity.batimentos = Integer.parseInt(dadosFinais);
                            Intent intent = new Intent(getApplicationContext(),CallService.class);
                            if (MainActivity.batimentos >= 150) {
                                startForegroundService(intent);
                            }else{
                                MainActivity.txtConditionString = "Condição Normal";
                                stopService(intent);
                            }

                        }
                        dadosBt.delete(0, dadosBt.length());
                    }
                }
            }
        };

    }

        @Override
        public int onStartCommand(@Nullable Intent intent, int flags, int startId) {


            try {
                socketBt = meuDevice.createRfcommSocketToServiceRecord(MEU_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                socketBt.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conexaoThread = new BtService.ConnectedThread(socketBt);
            conexaoThread.start();
              //  Message msg = serviceHandler.obtainMessage();
               // msg.arg1 = startId;
               // serviceHandler.sendMessage(msg);




            return START_REDELIVER_INTENT;


        }



        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private class ConnectedThread extends Thread {
            private final InputStream mmInStream;

            public ConnectedThread(BluetoothSocket socket) {
                socketBt = socket;
                InputStream tmpIn = null;

                // Get the input and output streams; using temp objects because
                // member streams are final.
                try {
                    tmpIn = socket.getInputStream();
                } catch (IOException ignored) {

                }
                mmInStream = tmpIn;
            }

            public void run() {
                // mmBuffer store for the stream
                byte[] mmBuffer = new byte[1024];
                int numBytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);

                        String data = new String(mmBuffer, 0, numBytes);
                        // Send the obtained bytes to the UI activity.
                        Message readMsg = handler.obtainMessage(
                                MESSAGE_READ, numBytes, -1,
                                data);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        }


    }

