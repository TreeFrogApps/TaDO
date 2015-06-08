package com.treefrogapps.randomate;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    // database query = "PRAGMA foreign_keys = ON" maybe required in onCreate, or every time database in isWriteable
    // task - ise in onCreate only see if it works just only there

    public DBHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try{

            db.execSQL(Constants.TITLES_TABLE);
            db.execSQL(Constants.ITEMS_TABLE);
            db.execSQL(Constants.FOREIGN_KEY_CASCADE);

        } catch (Exception e){

            e.printStackTrace();
            Log.e("ERROR", "DATABASE CREATION");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Constants.DROP_TITLES_TABLE);
        db.execSQL(Constants.DROP_ITEMS_TABLE);
        onCreate(db);
    }


}
