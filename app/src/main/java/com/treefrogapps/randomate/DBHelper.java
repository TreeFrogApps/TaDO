package com.treefrogapps.randomate;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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
            Log.d("INFO", "DATABASE CREATED");

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

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.TITLE, titlesListData.getTitle());
        database.insert(Constants.TITLES_LIST, null, values);
        database.close();

    }

    public void insertIntoItemsDatabase(ItemsListData itemsListData){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.ITEM_INSERT_QUERY);

        executePreparedStatement(database, statement,
                new String[]{itemsListData.getItem(), itemsListData.getTitle()});
    }

    public ArrayList<TitlesListData> getTitles() {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<TitlesListData> titlesListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_QUERY, null);

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

    public ArrayList<ItemsListData> getItems(String titleName){

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<ItemsListData> itemsListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.ITEMS_GET_QUERY, new String[]{titleName});

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

    public void deleteTitle(String titleName){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.TITLE_DELETE_QUERY);

        executePreparedStatement(database, statement, new String[]{titleName});
    }

    public void deleteItem(ItemsListData itemsListData){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.ITEM_DELETE_SINGLE);
        executePreparedStatement(database, statement, new String[] {itemsListData.getItem(), itemsListData.getTitle()});

    }


    public void executePreparedStatement(SQLiteDatabase database, SQLiteStatement statement, String[] stringBindings){

        for (int i = 0; i < stringBindings.length; i++){
            statement.bindString((i+1), stringBindings[i]);
        }
        statement.execute();
        statement.clearBindings();
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }


    // todo - checking titles list doesn't match any new title lists made - duplicates = database nightmare
    // todo - checking items list doesn't match any new item made with same title name
    // todo   different title name duplicates allowed but not same title same item

    // todo - delete all items - for one title (statement done)
    // todo - delete all titles & items

    // todo  - update title option change title name on title_id (long press)
    // todo  - update item option change item name on item_id (long press)

    // todo - check duplicate entries titles - query database on titles - a cursor result means a duplicate (query done EVERY title entry)
    // todo - check duplicate entries items - query database items WHERE title = 'movies' - a cursor result means a duplicate (query done EVERY item entry

    // http://stackoverflow.com/questions/433392/how-do-i-use-prepared-statements-in-sqlite-in-android


}
