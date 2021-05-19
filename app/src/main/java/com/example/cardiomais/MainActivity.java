package com.example.cardiomais;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private int cont = 0;
    private final int[] imagens = {

            R.mipmap.heart1, R.mipmap.heart2, R.mipmap.heart3, R.mipmap.heart4, R.mipmap.heart5
            , R.mipmap.heart6, R.mipmap.heart7, R.mipmap.heart8, R.mipmap.heart9, R.mipmap.heart10,
            R.mipmap.heart11, R.mipmap.heart12, R.mipmap.heart13, R.mipmap.heart14, R.mipmap.heart15,
            R.mipmap.heart16, R.mipmap.heart17, R.mipmap.heart18, R.mipmap.heart19, R.mipmap.heart20,
            R.mipmap.heart21, R.mipmap.heart22, R.mipmap.heart23, R.mipmap.heart24, R.mipmap.heart25, R.mipmap.heart26
            , R.mipmap.heart27, R.mipmap.heart28, R.mipmap.heart29, R.mipmap.heart30, R.mipmap.heart31,
            R.mipmap.heart32, R.mipmap.heart33, R.mipmap.heart34, R.mipmap.heart35, R.mipmap.heart36,
            R.mipmap.heart37, R.mipmap.heart38, R.mipmap.heart39, R.mipmap.heart40, R.mipmap.heart41,
            R.mipmap.heart42

    };
    TimerTask timerTask;
    private ImageView imgHeart;
    private static final int ATIVA_BLUE = 1, SOLICITA_LISTA = 2;
    public static Integer batimentos = 0;
    public BluetoothAdapter meuBt = null;
    public static BluetoothDevice meuDevice = null;
    public static String txtHeartString, txtConditionString;
    private TextView txtHeart, txtCondition;
    public static String MAC = null;
    private Button btnContacts;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ATIVA_BLUE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "BT ativado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "BT não ativado!", Toast.LENGTH_LONG).show();
                }
                break;
            case SOLICITA_LISTA:
                if (resultCode == Activity.RESULT_OK) {

                    MAC = data.getExtras().getString(ListaDisp.ENDERECO_MAC);
                    meuDevice = meuBt.getRemoteDevice(MAC);
                    try {
                        Intent intent = new Intent(MainActivity.this, BtService.class);
                        startService(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Ocorreu um erro", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Falha em obter o MAC", Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                if (!Settings.canDrawOverlays(this)) {
                    // You don't have permission
                    checkPermission();
                } else {
                    // Do as per your logic
                }


        }

    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        meuBt = BluetoothAdapter.getDefaultAdapter();
        if (meuBt == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_LONG).show();
        } else if (!meuBt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ATIVA_BLUE);

        }
        if(ListaDisp.mudanca == 0){
            Intent abreLista = new Intent(MainActivity.this, ListaDisp.class);
            startActivityForResult(abreLista, SOLICITA_LISTA);

        }





        btnContacts = findViewById(R.id.btnContacts);
        imgHeart = findViewById(R.id.imgHeart);
        txtHeart = findViewById(R.id.txtHeart);
        txtCondition = findViewById(R.id.txtCondition);

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abraContatos = new Intent(MainActivity.this, ListaContatos.class);
                startActivity(abraContatos);

            }
        });



        Handler handlerTimer = new Handler();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handlerTimer.post(() -> {



                        txtCondition.setText(txtConditionString);
                        cont = ((cont % 40) + 1);
                        txtHeart.setText(txtHeartString);
                        imgHeart.setImageResource(imagens[cont]);
                        handlerTimer.postDelayed(timerTask, (200 - batimentos));
                });

            }
        };
        handlerTimer.postDelayed(timerTask, 120);
    }

}

