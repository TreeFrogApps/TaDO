package com.treefrogapps.randomate;


public class Constants {

    // Database constants
    protected final static String DATABASE_NAME = "main_database.db";
    protected final static int DATABASE_VERSION = 1;

    protected final static String TITLES_TABLE =
            "CREATE TABLE IF NOT EXISTS titles_list " +
            "(title_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR);";

    protected final static String TITLES_LIST = "titles_list";
    protected final static String TITLE_ID = "title_id";
    protected final static String TITLE = "title";

    protected final static String ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS items_list " +
            "(items_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " item VARCHAR, title_id INTEGER NOT NULL," +
            " FOREIGN KEY (title_id) REFERENCES title_list(title_id) ON DELETE CASCADE);";

    protected final static String ITEMS_LIST = "items_list";
    protected final static String ITEMS_ID = "items_id";
    protected final static String ITEM = "item";

    protected final static String FOREIGN_KEY_CASCADE = "PRAGMA foreign_keys = ON";

    protected final static String DROP_TITLES_TABLE = "DROP TABLE IF EXISTS title_list;";
    protected final static String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS items_list;";

}
