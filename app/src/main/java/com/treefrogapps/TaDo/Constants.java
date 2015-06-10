package com.treefrogapps.TaDo;


public class Constants {

    // Database constants
    protected final static String DATABASE_NAME = "main_database.db";
    protected final static int DATABASE_VERSION = 1;
    // to allow on cascade delete
    protected final static String FOREIGN_KEY_CASCADE = "PRAGMA foreign_keys = ON";
    protected final static String DROP_TITLES_TABLE = "DROP TABLE IF EXISTS titles_list;";
    protected final static String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS items_list;";


    // Titles table
    protected final static String TITLES_TABLE =
            "CREATE TABLE IF NOT EXISTS titles_list " +
                    "(title_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR);";

    protected final static String TITLES_LIST = "titles_list";
    protected final static String TITLE_ID = "title_id";
    protected final static String TITLE = "title";


    // Items Table
    protected final static String ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS items_list " +
                    "(item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " item VARCHAR, title_id INTEGER NOT NULL," +
                    " FOREIGN KEY (title_id) REFERENCES title_list(title_id) ON DELETE CASCADE);";

    protected final static String ITEMS_LIST = "items_list";
    protected final static String ITEMS_ID = "item_id";
    protected final static String ITEM = "item";


    // SQLite Queries - including prepared statement queries
    protected final static String ITEM_INSERT_QUERY = "INSERT INTO items_list VALUES (NULL, ?," +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?));";

    protected final static String ITEMS_GET_QUERY = "SELECT * FROM items_list WHERE title_id =" +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";

    protected final static String TITLES_GET_QUERY = "SELECT * FROM titles_list";

    protected final static String TITLE_DELETE_QUERY = "DELETE FROM title_list WHERE title_id =" +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";

    protected final static String ITEMS_DELETE_ALL_SINGLE_TITLE = "DELETE FROM items_list WHERE " +
            "title_id = (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";

    protected final static String ITEM_DELETE_SINGLE = "DELETE FROM items_list WHERE item= ? " +
            "AND title_id = (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";

    protected final static String ITEM_UPDATE = "UPDATE items_list SET item = ? WHERE" +
            " title_id = (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";


}