package com.treefrogapps.TaDo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    // database query = "PRAGMA foreign_keys = ON" maybe required in onCreate (ON DELETE CASCADE), or every time database in isWriteable
    // task - use in onCreate only see if it works just only there!

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

    public void insertIntoTitlesTable(TitlesListData titlesListData){

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.TITLE, titlesListData.getTitle());
        values.put(Constants.TITLE_DATETIME, titlesListData.getDateTime());
        database.insert(Constants.TITLES_LIST, null, values);

        database.close();
    }

    public void insertIntoItemsTable(ItemsListData itemsListData){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();

        // create prepared statement
        SQLiteStatement statement = database.compileStatement(Constants.ITEM_INSERT_QUERY);

        // execute method to handle statement request
        executePreparedStatement(3, database, statement,
                new String[]{itemsListData.getItem(), itemsListData.getTitle(), itemsListData.getDuration(), itemsListData.getDateTime()});
    }

    public ArrayList<TitlesListData> getTitles() {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<TitlesListData> titlesListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_QUERY, null);

        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int title = cursor.getColumnIndex(Constants.TITLE);
        int dateTime = cursor.getColumnIndex(Constants.TITLE_DATETIME);

        if (cursor.moveToFirst()){

            do {
                TitlesListData titlesListData = new TitlesListData();

                titlesListData.setTitle_id(cursor.getString(title_id));
                titlesListData.setTitle(cursor.getString(title));
                titlesListData.setDateTime(cursor.getString(dateTime));

                titlesListDataArrayList.add(titlesListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titlesListDataArrayList;
    }

    public String getTitleId(String titleName){

        SQLiteDatabase database = this.getWritableDatabase();
        String titleId = null;

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_TITLE_ID, new String[]{titleName});

        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);

        if (cursor.moveToFirst()){

            do {
                titleId = cursor.getString(title_id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titleId;
    }

    public ArrayList<ItemsListData> getItemsForTitle(String titleName){

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<ItemsListData> itemsListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.ITEMS_GET_QUERY, new String[]{titleName});

        int item_id = cursor.getColumnIndex(Constants.ITEMS_ID);
        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int item = cursor.getColumnIndex(Constants.ITEM);
        int duration = cursor.getColumnIndex(Constants.ITEM_DURATION);
        int dateTime = cursor.getColumnIndex(Constants.ITEM_DATETIME);

        if (cursor.moveToFirst()){

            do {
                ItemsListData itemsListData = new ItemsListData();

                itemsListData.setItemId(cursor.getString(item_id));
                itemsListData.setTitleId(cursor.getString(title_id));
                itemsListData.setItem(cursor.getString(item));
                itemsListData.setDuration(cursor.getString(duration));
                itemsListData.setDateTime(cursor.getString(dateTime));

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

        executePreparedStatement(2, database, statement, new String[]{titleName});
    }

    public void deleteItem(ItemsListData itemsListData){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.ITEM_DELETE_SINGLE);
        executePreparedStatement(2, database, statement, new String[] {itemsListData.getItem(), itemsListData.getTitle()});
    }

    public void deleteAllItemsForTitle(String titleName){

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.ITEMS_DELETE_ALL_SINGLE_TITLE);
        executePreparedStatement(2, database, statement, new String[] {titleName});
    }

    public void deleteAllTitlesAndItems(){

        SQLiteDatabase database = this.getWritableDatabase();
        // delete all titles & items
        database.delete(Constants.ITEMS_TABLE, null, null);
        database.delete(Constants.TITLES_LIST, null, null);
        database.close();
    }

    public void updateTitle(TitlesListData oldTitle, TitlesListData newTitle){

        SQLiteDatabase database = this.getWritableDatabase();
        // put new values
        ContentValues values = new ContentValues();
        values.put(Constants.TITLE, newTitle.getTitle());
        values.put(Constants.TITLE_DATETIME, newTitle.getDateTime());
        // update the database passing the oldTitleName as an argument
        //                     table name  |  new values | where | arguments replacing '?' in where
        database.update(Constants.TITLES_TABLE, values, "title= ?", new String[]{oldTitle.getTitle()});
        database.close();
    }

    public void updateItem(ItemsListData oldItem, ItemsListData newItem){

        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.ITEM_UPDATE);
        executePreparedStatement(2, database, statement,
                new String[]{newItem.getItem(), newItem.getDuration(), newItem.getDateTime(), oldItem.getItemId()});
    }


    public void executePreparedStatement(int executeType, SQLiteDatabase database, SQLiteStatement statement, String[] stringBindings){

        for (int i = 0; i < stringBindings.length; i++){
            statement.bindString((i+1), stringBindings[i]);
        }

        if (executeType == 1){
            // CREATE and DROP
            statement.execute();

        } else if (executeType == 2){
            // UPDATE and DELETE
            statement.executeUpdateDelete();

        } else if (executeType == 3){
            // INSERT
            statement.executeInsert();
        }
        database.setTransactionSuccessful();
        statement.clearBindings();
        database.endTransaction();
        database.close();
    }


    // todo - checking titles list doesn't match any new title lists made - duplicates = database nightmare
    // todo - checking items list doesn't match any new item made with same title name
    // todo   same ITEM name duplicates allowed but not same title


    // todo - check duplicate entries titles - query database on titles - a cursor result means a duplicate (query done EVERY title entry)
    // todo - check duplicate entries items - query database items WHERE title = 'movies' - a cursor result means a duplicate (query done EVERY item entry

    // http://stackoverflow.com/questions/433392/how-do-i-use-prepared-statements-in-sqlite-in-android


}
