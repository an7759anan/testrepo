package com.friendfoto.aaanikin.friendfoto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by aaanikin on 06.07.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context,"friendsDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LoginActivity.LOG_TAG,"--- onCreate database ---");
        db.execSQL("create table friends ("
                + "id integer primary key,"
                + "name text,"
                + "image_url text,"
                + "image BLOB"
                + ");"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String s="";
    }
}
