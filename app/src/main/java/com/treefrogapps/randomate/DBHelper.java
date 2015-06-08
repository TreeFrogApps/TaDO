package com.treefrogapps.randomate;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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

    public void insertIntoTitlesDatabase(String titleName){

        SQLiteDatabase database = getWritableDatabase();

        String insertQuery = "INSERT INTO title_list VALUES (NULL, '" + titleName + "')";
        database.execSQL(insertQuery);
        database.close();

    }

    public void insertIntoItemsDatabase(String titleName, String itemName){

        SQLiteDatabase database = getWritableDatabase();

        String insertQuery = "INSERT INTO items_list VALUES (NULL, '" + itemName + "'," +
                " (SELECT title_id FROM title_list WHERE title_list.title_id = '" + titleName + "'));";

        database.execSQL(insertQuery);
        database.close();
    }

    public ArrayList<ItemsListData> getItems(String titleName){

        SQLiteDatabase database = getWritableDatabase();
        ArrayList<ItemsListData> itemsListDataArrayList = new ArrayList<>();

        String getQuery = "SELECT * FROM items_list WHERE title_id =" +
                " (SELECT title_id FROM title_list WHERE title_list.title = '" + titleName + "')";

        Cursor cursor = database.rawQuery(getQuery, null);

        int title_id = cursor.getColumnIndex("title_id");
        int item = cursor.getColumnIndex("item");

        if (cursor.moveToFirst()){

            do {
                ItemsListData itemsListData = new ItemsListData();

                itemsListData.setTitleId(cursor.getInt(title_id));
                itemsListData.setItem(cursor.getString(item));

                itemsListDataArrayList.add(itemsListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return itemsListDataArrayList;
    }

    public ArrayList<TitlesListData> getTitles() {

        SQLiteDatabase database = getWritableDatabase();
        ArrayList<TitlesListData> titlesListDataArrayList = new ArrayList<>();

        String getQuery = "SELECT * FROM title_list";
        Cursor cursor = database.rawQuery(getQuery, null);

        int title_id = cursor.getColumnIndex("title_id");
        int title = cursor.getColumnIndex("title");

        if (cursor.moveToFirst()){

            do {
                TitlesListData titlesListData = new TitlesListData();

                titlesListData.setTitle_id(cursor.getInt(title_id));
                titlesListData.setTitle(cursor.getString(title));

                titlesListDataArrayList.add(titlesListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titlesListDataArrayList;
    }

    // todo - delete title
    // todo - delete item
    // todo - delete all items
    // todo - delete all titles & items


}
