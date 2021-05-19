package com.example.cardiomais;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    static ArrayList<String> mandaNumero;
    private static final String DATABASE_NAME = "database_name";
    private static final String TABLE_NAME = "table_name";

    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + "(id INTEGER PRIMARY KEY, txt TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean addText(String text){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("txt", text);
        sqLiteDatabase.insert(TABLE_NAME,null, contentValues);
        return true;
    }

    public void delete(String text){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where txt = " + text);
    }

    public ArrayList getNumber(String text){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where txt = " + text, null);
            cursor.moveToFirst();
            mandaNumero.add(cursor.getString(cursor.getColumnIndex("txt")));
            cursor.close();
            return mandaNumero;
    }



    public ArrayList getAllText(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("txt")));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

}
