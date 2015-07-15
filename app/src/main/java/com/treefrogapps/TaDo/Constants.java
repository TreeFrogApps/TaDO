package com.treefrogapps.TaDo;


public class Constants {

    // Database constants ----------------------------------------------------------------------------------

    protected static final String DATABASE_NAME = "main_database.db";
    protected static final int DATABASE_VERSION = 1;
    // to allow on cascade delete
    protected static final String FOREIGN_KEY_CASCADE = "PRAGMA foreign_keys = ON;";

    protected static final String DROP_TITLES_TABLE = "DROP TABLE IF EXISTS titles_list;";
    protected static final String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS items_list;";
    protected static final String DROP_QUEUED_ITEMS_TABLE = "DROP TABLE IF EXISTS queued_items;";
    protected static final String DROP_CURRENT_ITEM_TABLE = "DROP TABLE IF EXISTS current_item;";


    // Titles table
    protected static final String TITLES_TABLE =
            "CREATE TABLE IF NOT EXISTS titles_list " +
                    "(title_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, date DATETIME);";

    protected static final String TITLES_LIST = "titles_list";
    protected static final String TITLE_ID = "title_id";
    protected static final String TITLE = "title";
    protected static final String TITLE_DATETIME = "date";

    protected static final String TITLES_GET_QUERY = "SELECT * FROM titles_list ORDER BY titles_list.title COLLATE NOCASE ASC";
    protected static final String TITLES_GET_TITLE_ID = "SELECT title_id FROM titles_list WHERE title_id = " +
            "(SELECT title_id FROM titles_list WHERE titles_list.title = ?)";
    protected static final String TITLES_GET_TITLE = "SELECT title FROM titles_list WHERE titles_list.title_id = ?";
    protected static final String TITLES_CHECK_TITLE_NAME_EXISTS = "SELECT title FROM titles_list WHERE title = ?";
    protected static final String TITLE_DELETE_QUERY = "DELETE FROM titles_list WHERE title_id = ?;";


    // Items Table
    protected static final String ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS items_list " +
                    "(item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " item VARCHAR, item_detail VARCHAR, title_id INTEGER NOT NULL," +
                    " duration TIME, date DATETIME, item_done CHAR, priority CHAR," +
                    " FOREIGN KEY (title_id) REFERENCES titles_list (title_id) ON DELETE CASCADE);";

    protected static final String ITEMS_LIST = "items_list";
    protected static final String ITEMS_ID = "item_id";
    protected static final String ITEM = "item";
    protected static final String ITEM_DETAIL = "item_detail";
    protected static final String ITEM_DURATION = "duration";
    protected static final String ITEM_DATETIME = "date";
    protected static final String ITEM_DONE_COLUMN = "item_done";
    protected static final String ITEM_DONE = "Y";
    protected static final String ITEM_NOT_DONE = "N";
    protected static final String ITEM_PRIORITY = "priority";
    protected static final String ITEM_PRIORITY_HIGH = "H";
    protected static final String ITEM_PRIORITY_MEDIUM = "M";
    protected static final String ITEM_PRIORITY_LOW = "L";

    // SQLite Queries - including prepared statement queries
    protected static final String ITEM_INSERT_QUERY = "INSERT INTO items_list VALUES (NULL, ?, ?," +
            " ?, TIME (?), DATETIME (?), ?, ?);"; // datetime format = YYYY-MM-YY HH:YY:SS
    protected static final String ITEMS_NOT_DONE_GET_QUERY = "SELECT * FROM items_list WHERE title_id = ?" +
            " AND item_done = 'N' ORDER BY items_list.item COLLATE NOCASE ASC;";
    protected static final String ITEMS_DONE_GET_QUERY = "SELECT * FROM items_list WHERE title_id = ?" +
            " AND item_done = 'Y' ORDER BY items_list.item COLLATE NOCASE ASC;";
    protected static final String ITEMS_DELETE_ALL_SINGLE_TITLE = "DELETE FROM items_list WHERE " +
            "title_id = (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";
    protected static final String ITEM_DELETE_SINGLE = "DELETE FROM items_list WHERE items_list.item_id = ?;";
    protected static final String ITEM_GET_SINGLE = "SELECT * FROM items_list WHERE items_list.item_id = ?";
    protected static final String ITEM_UPDATE = "UPDATE items_list SET item = ?, item_detail = ?, duration = ?, priority = ? WHERE item_id = ?;";
    protected static final String ITEM_UPDATE_ITEM_DONE = "UPDATE items_list SET item_done = ? WHERE item_id = ?;";


    // Queued Items Table
    protected static final String QUEUED_ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS queued_items (queued_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " item_id INTEGER NOT NULL,  FOREIGN KEY (item_id) REFERENCES items_list (item_id) ON DELETE CASCADE);";

    protected static final String QUEUED_ITEMS_LIST = "queued_items";
    protected static final String QUEUED_ITEMS_ID = "queued_item_id";

    protected static final String QUEUED_ITEM_INSERT_QUERY = "INSERT INTO queued_items VALUES (NULL, ?);";
    protected static final String QUEUED_ITEM_DELETE_QUERY = "DELETE FROM queued_items WHERE queued_items.item_id = ?;";
    protected static final String QUEUED_ITEM_GET_ITEMS_QUERY = "SELECT * FROM queued_items";


    // Current Item Table (will only hold one item at a time)
    protected static final String CURRENT_ITEM_TABLE =
            "CREATE TABLE IF NOT EXISTS current_item (current_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " item_id INTEGER NOT NULL,  FOREIGN KEY (item_id) REFERENCES items_list (item_id) ON DELETE CASCADE);";

    protected static final String CURRENT_ITEM_LIST = "current_item";
    protected static final String CURRENT_ITEM_ID = "current_item_id";

    protected static final String CURRENT_ITEM_INSERT_QUERY = "INSERT INTO current_item VALUES (null,?);";
    protected static final String CURRENT_ITEM_DELETE_QUERY = "DELETE FROM current_item WHERE current_item.current_item_id = ?;";
    protected static final String CURRENT_ITEM_GET_ITEM_QUERY = "SELECT * FROM current_item";


    // --------------------------------------------------------------------------------------------------------------

    // splash screen visibility - place in shared prefs
    protected static final String SPLASH_SCREEN_VISIBILITY = "com.treefrogapps.TaDo.Visibility";
    protected static final int SPLASH_SCREEN_OFF = 0;

    // Shared Preferences constant
    protected static final String TADO_PREFERENCES = "com.treefrogapps.TaDo.Preferences";

    // Sync Fragment
    protected static final int REQUEST_CODE_RESOLUTION = 10;
    protected static final String SYNCED_STATE = "SYNCED_STATE";

    // RequestCode onActivityResult - HomeFragment NEW LIST
    protected static final int NEW_ITEMS_REQUEST_CODE = 30;




}
