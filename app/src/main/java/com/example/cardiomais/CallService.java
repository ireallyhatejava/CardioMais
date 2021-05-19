package com.example.cardiomais;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class CallService extends Service {
    DatabaseHelper db = new DatabaseHelper(CallService.this);
    private NotificationChannel notificationChannel = null;
    private int contadorDeContatos = 0;
    private String numero = null;
    private ArrayList contatos;
    private Integer indexContatos;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            try {

                MainActivity.txtConditionString = "Condição Critica";
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(!contatos.isEmpty() && contadorDeContatos <= indexContatos){
                    numero = contatos.get(contadorDeContatos).toString();
                }
                else {
                    numero = contatos.get(0).toString();
                }
                callIntent.setData(Uri.parse("tel:"+numero));
                startActivity(callIntent);
                contadorDeContatos = ((contadorDeContatos % (indexContatos - 1) + 1));
                if (contadorDeContatos == indexContatos) {
                    contadorDeContatos = 0;
                }
                Thread.sleep(6000);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.out.println(e);
                Thread.currentThread().interrupt();
            }

            stopSelf(msg.arg1);

        }
    }




    @Override
    public void onCreate() {


        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        contatos = db.getAllText();
        indexContatos = contatos.size();
        db.close();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
             notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(MainActivity.txtHeartString)
                    .setContentText(MainActivity.txtHeartString).build();

            startForeground(1, notification);
        }

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    }

