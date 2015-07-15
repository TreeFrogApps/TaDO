package com.treefrogapps.TaDo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(Constants.TITLES_TABLE);
            db.execSQL(Constants.ITEMS_TABLE);
            db.execSQL(Constants.QUEUED_ITEMS_TABLE);
            db.execSQL(Constants.CURRENT_ITEM_TABLE);
            db.execSQL(Constants.FOREIGN_KEY_CASCADE);
            Log.d("INFO", "DATABASE CREATED");

        } catch (Exception e) {

            e.printStackTrace();
            Log.e("ERROR", "DATABASE CREATION");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Constants.DROP_TITLES_TABLE);
        db.execSQL(Constants.DROP_ITEMS_TABLE);
        db.execSQL(Constants.DROP_QUEUED_ITEMS_TABLE);
        db.execSQL(Constants.DROP_CURRENT_ITEM_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints - onOpen called each time access to the db is requested (this.getWritableDatabase)
            db.execSQL(Constants.FOREIGN_KEY_CASCADE);
        }
    }

    // TITLE TABLE
    public void insertIntoTitlesTable(TitlesListData titlesListData) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.TITLE, titlesListData.getTitle());
        Log.e("LIST TITLE", titlesListData.getTitle());
        values.put(Constants.TITLE_DATETIME, titlesListData.getDateTime());
        Log.e("LIST DATETIME", titlesListData.getDateTime());
        database.insert(Constants.TITLES_LIST, null, values);

        database.close();
    }

    // ITEMS TABLE
    public void insertIntoItemsTable(ItemsListData itemsListData) {

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();

        // create prepared statement
        SQLiteStatement statement = database.compileStatement(Constants.ITEM_INSERT_QUERY);

        // execute method to handle statement request
        executePreparedStatement(3, database, statement,
                new String[]{itemsListData.getItem(), itemsListData.getItemDetail(), itemsListData.getTitleId(),
                        itemsListData.getDuration(), itemsListData.getDateTime(), itemsListData.getItemDone(), itemsListData.getItemPriority()});
    }

    // TITLES TABLE
    public ArrayList<TitlesListData> getTitles() {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<TitlesListData> titlesListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_QUERY, null);

        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int title = cursor.getColumnIndex(Constants.TITLE);
        int dateTime = cursor.getColumnIndex(Constants.TITLE_DATETIME);

        if (cursor.moveToFirst()) {

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

    // TITLE TABLE
    public String getTitleId(String titleName) {

        SQLiteDatabase database = this.getWritableDatabase();
        String titleId = null;

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_TITLE_ID, new String[]{titleName});

        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);

        if (cursor.moveToFirst()) {

            do {
                titleId = cursor.getString(title_id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titleId;
    }

    // TITLE TABLE
    public String getTitle(String titleId) {

        SQLiteDatabase database = this.getWritableDatabase();
        String titleName = null;

        Cursor cursor = database.rawQuery(Constants.TITLES_GET_TITLE, new String[]{titleId});

        int title = cursor.getColumnIndex(Constants.TITLE);

        if (cursor.moveToFirst()) {

            do {
                titleName = cursor.getString(title);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titleName;
    }

    // TITLE TABLE
    public String checkTitleNameExists(String titleName) {

        SQLiteDatabase database = this.getWritableDatabase();
        String titleId = null;

        Cursor cursor = database.rawQuery(Constants.TITLES_CHECK_TITLE_NAME_EXISTS, new String[]{titleName});

        int title = cursor.getColumnIndex(Constants.TITLE);

        if (cursor.moveToFirst()) {

            do {
                titleId = cursor.getString(title);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return titleId;
    }

    // ITEMS TABLE
    public ArrayList<ItemsListData> getItemsForTitleDone(String titleId) {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<ItemsListData> itemsListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.ITEMS_DONE_GET_QUERY, new String[]{titleId});

        int item_id = cursor.getColumnIndex(Constants.ITEMS_ID);
        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int item = cursor.getColumnIndex(Constants.ITEM);
        int itemDetail = cursor.getColumnIndex(Constants.ITEM_DETAIL);
        int duration = cursor.getColumnIndex(Constants.ITEM_DURATION);
        int dateTime = cursor.getColumnIndex(Constants.ITEM_DATETIME);
        int itemDone = cursor.getColumnIndex(Constants.ITEM_DONE_COLUMN);
        int itemPriority = cursor.getColumnIndex(Constants.ITEM_PRIORITY);

        if (cursor.moveToFirst()) {

            do {
                ItemsListData itemsListData = new ItemsListData();

                itemsListData.setItemId(cursor.getString(item_id));
                Log.e("ITEM ID", itemsListData.getItemId());
                itemsListData.setTitleId(cursor.getString(title_id));
                Log.e("ITEM TITLE ID", itemsListData.getTitleId());
                itemsListData.setItem(cursor.getString(item));
                Log.e("ITEM", itemsListData.getItem());
                itemsListData.setItemDetail(cursor.getString(itemDetail));
                Log.e("ITEM DETAIL", itemsListData.getItemDetail());
                itemsListData.setDuration(cursor.getString(duration));
                Log.e("ITEM DURATION", itemsListData.getDuration());
                itemsListData.setDateTime(cursor.getString(dateTime));
                Log.e("ITEM DATE", itemsListData.getDateTime());
                itemsListData.setItemDone(cursor.getString(itemDone));
                Log.e("ITEM DONE", itemsListData.getItemDone());
                itemsListData.setItemPriority(cursor.getString(itemPriority));
                Log.e("ITEM PRIORITY", itemsListData.getItemPriority());

                itemsListDataArrayList.add(itemsListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return itemsListDataArrayList;
    }

    // ITEMS TABLE
    public ArrayList<ItemsListData> getItemsForTitleNotDone(String titleId) {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<ItemsListData> itemsListDataArrayList = new ArrayList<>();

        Cursor cursor = database.rawQuery(Constants.ITEMS_NOT_DONE_GET_QUERY, new String[]{titleId});

        int item_id = cursor.getColumnIndex(Constants.ITEMS_ID);
        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int item = cursor.getColumnIndex(Constants.ITEM);
        int itemDetail = cursor.getColumnIndex(Constants.ITEM_DETAIL);
        int duration = cursor.getColumnIndex(Constants.ITEM_DURATION);
        int dateTime = cursor.getColumnIndex(Constants.ITEM_DATETIME);
        int itemDone = cursor.getColumnIndex(Constants.ITEM_DONE_COLUMN);
        int itemPriority = cursor.getColumnIndex(Constants.ITEM_PRIORITY);

        if (cursor.moveToFirst()) {

            do {
                ItemsListData itemsListData = new ItemsListData();

                itemsListData.setItemId(cursor.getString(item_id));
                Log.e("ITEM ID", itemsListData.getItemId());
                itemsListData.setTitleId(cursor.getString(title_id));
                Log.e("ITEM TITLE ID", itemsListData.getTitleId());
                itemsListData.setItem(cursor.getString(item));
                Log.e("ITEM", itemsListData.getItem());
                itemsListData.setItemDetail(cursor.getString(itemDetail));
                Log.e("ITEM DETAIL", itemsListData.getItemDetail());
                itemsListData.setDuration(cursor.getString(duration));
                Log.e("ITEM DURATION", itemsListData.getDuration());
                itemsListData.setDateTime(cursor.getString(dateTime));
                Log.e("ITEM DATE", itemsListData.getDateTime());
                itemsListData.setItemDone(cursor.getString(itemDone));
                Log.e("ITEM DONE", itemsListData.getItemDone());
                itemsListData.setItemPriority(cursor.getString(itemPriority));
                Log.e("ITEM PRIORITY", itemsListData.getItemPriority());

                itemsListDataArrayList.add(itemsListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return itemsListDataArrayList;
    }

    // TITLES TABLE
    public void deleteTitle(String titleId) {

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.TITLE_DELETE_QUERY);

        executePreparedStatement(2, database, statement, new String[]{titleId});
    }

    // ITEMS TABLE
    public void deleteItem(ItemsListData itemsListData) {

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.ITEM_DELETE_SINGLE);
        executePreparedStatement(2, database, statement, new String[]{itemsListData.getItemId()});
    }

    // ITEMS TABLE
    public void deleteAllItemsForTitle(String titleName) {

        SQLiteDatabase database = this.getWritableDatabase();
        // Non-exclusive mode allows database file to be in readable by other threads executing queries.
        database.beginTransactionNonExclusive();
        SQLiteStatement statement = database.compileStatement(Constants.ITEMS_DELETE_ALL_SINGLE_TITLE);
        executePreparedStatement(2, database, statement, new String[]{titleName});
    }

    // ALL TABLES
    public void deleteAllTitlesAndItems() {

        SQLiteDatabase database = this.getWritableDatabase();
        // delete all titles & items
        database.delete(Constants.ITEMS_LIST, null, null);
        database.delete(Constants.TITLES_LIST, null, null);
        database.delete(Constants.QUEUED_ITEMS_LIST, null, null);
        database.delete(Constants.CURRENT_ITEM_LIST, null, null);
        database.close();
    }

    // TITLES TABLE
    public void updateTitle(TitlesListData oldTitle, TitlesListData newTitle) {

        SQLiteDatabase database = this.getWritableDatabase();
        // put new values
        ContentValues values = new ContentValues();
        values.put(Constants.TITLE, newTitle.getTitle());
        values.put(Constants.TITLE_DATETIME, newTitle.getDateTime());
        // update the database passing the oldTitleName as an argument
        //                     table name  |  new values | where | arguments replacing '?' in where
        database.update(Constants.TITLES_LIST, values, "title= ?", new String[]{oldTitle.getTitle()});
        database.close();
    }

    // ITEMS TABLE
    public ItemsListData getSingleItem(String itemId) {

        SQLiteDatabase database = this.getWritableDatabase();

        ItemsListData itemsListData = new ItemsListData();

        Cursor cursor = database.rawQuery(Constants.ITEM_GET_SINGLE, new String[]{itemId});

        int item_id = cursor.getColumnIndex(Constants.ITEMS_ID);
        int title_id = cursor.getColumnIndex(Constants.TITLE_ID);
        int item = cursor.getColumnIndex(Constants.ITEM);
        int itemDetail = cursor.getColumnIndex(Constants.ITEM_DETAIL);
        int duration = cursor.getColumnIndex(Constants.ITEM_DURATION);
        int dateTime = cursor.getColumnIndex(Constants.ITEM_DATETIME);
        int itemDone = cursor.getColumnIndex(Constants.ITEM_DONE_COLUMN);
        int itemPriority = cursor.getColumnIndex(Constants.ITEM_PRIORITY);

        if (cursor.moveToFirst()) {

            do {
                itemsListData.setItemId(cursor.getString(item_id));
                Log.e("ITEM ID", itemsListData.getItemId());
                itemsListData.setTitleId(cursor.getString(title_id));
                Log.e("ITEM TITLE ID", itemsListData.getTitleId());
                itemsListData.setItem(cursor.getString(item));
                Log.e("ITEM", itemsListData.getItem());
                itemsListData.setItemDetail(cursor.getString(itemDetail));
                Log.e("ITEM DETAIL", itemsListData.getItemDetail());
                itemsListData.setDuration(cursor.getString(duration));
                Log.e("ITEM DURATION", itemsListData.getDuration());
                itemsListData.setDateTime(cursor.getString(dateTime));
                Log.e("ITEM DATE", itemsListData.getDateTime());
                itemsListData.setItemDone(cursor.getString(itemDone));
                Log.e("ITEM DONE", itemsListData.getItemDone());
                itemsListData.setItemPriority(cursor.getString(itemPriority));
                Log.e("ITEM PRIORITY", itemsListData.getItemPriority());


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return itemsListData;

    }

    // ITEMS TABLE
    public void updateItem(ItemsListData newItemDetails, ItemsListData currentItemId) {

        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.ITEM_UPDATE);
        executePreparedStatement(2, database, statement,
                new String[]{newItemDetails.getItem(), newItemDetails.getItemDetail(),
                        newItemDetails.getDuration(), newItemDetails.getItemPriority(), currentItemId.getItemId()});
    }

    // ITEMS TABLE
    public void updateItemDone(ItemsListData itemsListData) {

        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.ITEM_UPDATE_ITEM_DONE);
        executePreparedStatement(2, database, statement,
                new String[]{itemsListData.getItemDone(), itemsListData.getItemId()});
    }

    // QUEUED ITEMS TABLE
    public void insertIntoQueuedItemsTable(QueuedItemListData queuedItemListData) {

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.QUEUED_ITEM_INSERT_QUERY);
        executePreparedStatement(3, database, statement, new String[]{queuedItemListData.getItemId()});
    }

    // QUEUED ITEMS TABLE
    public void deleteQueuedItem(QueuedItemListData queuedItemListData) {

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.QUEUED_ITEM_DELETE_QUERY);
        executePreparedStatement(2, database, statement, new String[]{queuedItemListData.getItemId()});
    }

    // QUEUED ITEMS TABLE
    public ArrayList<QueuedItemListData> getQueuedItems() {

        SQLiteDatabase database = getWritableDatabase();
        ArrayList<QueuedItemListData> queuedItemListDataArrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery(Constants.QUEUED_ITEM_GET_ITEMS_QUERY, null);

        int queue_id = cursor.getColumnIndex(Constants.QUEUED_ITEMS_ID);
        int item_id = cursor.getColumnIndex(Constants.ITEMS_ID);

        if (cursor.moveToFirst()) {

            do {
                QueuedItemListData queuedItemListData = new QueuedItemListData();

                queuedItemListData.setQueuedItemId(cursor.getString(queue_id));
                queuedItemListData.setItemId(cursor.getString(item_id));

                queuedItemListDataArrayList.add(queuedItemListData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return queuedItemListDataArrayList;
    }

    // CURRENT ITEM TABLE
    public void insertIntoCurrentItemTable(CurrentItemListData currentItemListData){

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.CURRENT_ITEM_INSERT_QUERY);
        executePreparedStatement(3, database, statement, new String[]{currentItemListData.getItemId()});
    }

    // CURRENT ITEM TABLE
    public void deleteCurrentItem(CurrentItemListData currentItemListData){

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();

        SQLiteStatement statement = database.compileStatement(Constants.CURRENT_ITEM_DELETE_QUERY);
        executePreparedStatement(2, database, statement,new String[]{currentItemListData.getCurrentItemId()});
    }

    // CURRENT ITEM TABLE
    public CurrentItemListData getCurrentItem(){

        SQLiteDatabase database = getWritableDatabase();
        CurrentItemListData currentItemListData = new CurrentItemListData();
        Cursor cursor = database.rawQuery(Constants.CURRENT_ITEM_GET_ITEM_QUERY, null);

        int currentId = cursor.getColumnIndex(Constants.CURRENT_ITEM_ID);
        int itemId = cursor.getColumnIndex(Constants.ITEMS_ID);

        if (cursor.moveToFirst()){

            do {
                currentItemListData.setCurrentItemId(cursor.getString(currentId));
                currentItemListData.setItemId(cursor.getString(itemId));

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return currentItemListData;
    }


    public void executePreparedStatement(int executeType, SQLiteDatabase database, SQLiteStatement statement, String[] stringBindings) {

        for (int i = 0; i < stringBindings.length; i++) {
            statement.bindString((i + 1), stringBindings[i]);
        }

        if (executeType == 1) {
            // CREATE and DROP
            statement.execute();

        } else if (executeType == 2) {
            // UPDATE and DELETE
            statement.executeUpdateDelete();

        } else if (executeType == 3) {
            // INSERT
            statement.executeInsert();
        }
        database.setTransactionSuccessful();
        statement.clearBindings();
        database.endTransaction();
        database.close();
    }

    // http://stackoverflow.com/questions/433392/how-do-i-use-prepared-statements-in-sqlite-in-android


}
