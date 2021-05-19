package com.example.cardiomais;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Set;

public class ListaDisp extends ListActivity {

    private BluetoothAdapter meuBtAdapter = null;
    static byte mudanca = 0 ;

    static String ENDERECO_MAC = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> arrayBt = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        meuBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> dispositivoPareados = meuBtAdapter.getBondedDevices();

        if(dispositivoPareados.size() > 0){
            for(BluetoothDevice disp : dispositivoPareados){
                String deviceName = disp.getName();
                String deviceHardwareAddress = disp.getAddress();
                arrayBt.add(deviceName + '\n' + deviceHardwareAddress);
            }
        }

        setListAdapter(arrayBt);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String infoGeral = ((TextView)v).getText().toString();
        mudanca =1;
        String endMAC = infoGeral.substring(infoGeral.length() - 17);

        Intent retornaMAC = new Intent();
        retornaMAC.putExtra(ENDERECO_MAC, endMAC);
        setResult(RESULT_OK, retornaMAC);
        finish();


    }
}
