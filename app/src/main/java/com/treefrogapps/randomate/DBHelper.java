package com.treefrogapps.randomate;


import android.content.ContentValues;
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

    public void insertIntoTitlesDatabase(TitlesListData titlesListData){

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.TITLE_ID, "NULL");
        values.put(Constants.TITLE, titlesListData.getTitle());
        database.insert(Constants.TITLES_LIST, null, values);
        database.close();

    }

    public void insertIntoItemsDatabase(String titleName, String itemName){

        // TODO - CHANGE TO INSERTING AN OBJECT ITEMSLISTDATA - contentvalues
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

    public void deleteTitle(String titleName){

        SQLiteDatabase database = getWritableDatabase();

        String deleteQuery = "DELETE FROM title_list WHERE title_id = " +
                "(SELECT title_id FROM title_list WHERE title_list.title = '" + titleName + "')";

        database.execSQL(deleteQuery);
        database.close();
    }

    // TODO - SQLITE PREPARED STATEMENTS FOR QUERIES

    // todo - delete item
    // todo - delete all items
    // todo - delete all titles & items

    // todo  - update title option change title name on title_id (long press)
    // todo  - update item option change item name on item_id (long press)

    // todo - check duplicate entries titles - query database on titles - a cursor result means a duplicate (query done EVERY title entry)
    // todo - check duplicate entries items - query database items WHERE title = 'movies' - a cursor result means a duplicate (query done EVERY item entry



}
