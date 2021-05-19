package com.example.cardiomais;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ListaContatos extends AppCompatActivity {

    static BluetoothDevice salvaDev = null;
    DatabaseHelper databaseHelper;
    public EditText edtNumber;
    public Button btnSend;
    public Button btnBack;
    ListView listView;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        salvaDev = MainActivity.meuDevice;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adiciona_contatos);
        listView = findViewById(R.id.list_View);
        edtNumber = findViewById(R.id.edtNumber);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        databaseHelper = new DatabaseHelper(ListaContatos.this);
        arrayList = databaseHelper.getAllText();
        arrayAdapter = new ArrayAdapter(ListaContatos.this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtNumber.getText().toString();
                if(!text.isEmpty()){
                    if(databaseHelper.addText(text)){
                        edtNumber.setText("");
                        arrayList.clear();
                        arrayList.addAll(databaseHelper.getAllText());
                        arrayAdapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();
                    }
                }

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltar = new Intent(ListaContatos.this, MainActivity.class);
                startActivity(voltar);
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String number = ((TextView)view).getText().toString();
            databaseHelper.delete(number);
            arrayList = databaseHelper.getAllText();
            arrayAdapter = new ArrayAdapter(ListaContatos.this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);

        });



    }
}
