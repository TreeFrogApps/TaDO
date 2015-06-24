package com.treefrogapps.TaDo;


public class Constants {

    // Shared Preferences constant
    protected final static String TADO_PREFERENCES = "com.treefrogapps.TaDo.Preferences";

    // splash screen visibility - place in shared prefs
    protected final static String SPLASH_SCREEN_VISIBILITY = "com.treefrogapps.TaDo.Visibility";
    protected final static int SPLASH_SCREEN_OFF = 0;

    protected final static String SWIPE_SPLASH_SCREEN_VISIBILITY = "com.treefrogapps.TaDo.SwipeVisibility";
    protected final static int SWIPE_SPLASH_SCREEN_OFF = 0;

    // Database constants
    protected final static String DATABASE_NAME = "main_database.db";
    protected final static int DATABASE_VERSION = 1;
    // to allow on cascade delete
    protected final static String FOREIGN_KEY_CASCADE = "PRAGMA foreign_keys = ON;";
    protected final static String DROP_TITLES_TABLE = "DROP TABLE IF EXISTS titles_list;";
    protected final static String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS items_list;";


    // Titles table
    protected final static String TITLES_TABLE =
            "CREATE TABLE IF NOT EXISTS titles_list " +
                    "(title_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, date DATETIME);";

    protected final static String TITLES_LIST = "titles_list";
    protected final static String TITLE_ID = "title_id";
    protected final static String TITLE = "title";
    protected final static String TITLE_DATETIME = "date";


    // Items Table
    protected final static String ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS items_list " +
                    "(item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " item VARCHAR, item_detail VARCHAR, title_id INTEGER NOT NULL," +
                    " duration TIME, date DATETIME, item_done CHAR, priority CHAR," +
                    " FOREIGN KEY (title_id) REFERENCES titles_list (title_id) ON DELETE CASCADE);";

    protected final static String ITEMS_LIST = "items_list";
    protected final static String ITEMS_ID = "item_id";
    protected final static String ITEM = "item";
    protected final static String ITEM_DETAIL = "item_detail";
    protected final static String ITEM_DURATION = "duration";
    protected final static String ITEM_DATETIME = "date";
    protected final static String ITEM_DONE_COLUMN = "item_done";
    protected final static String ITEM_DONE = "Y";
    protected final static String ITEM_NOT_DONE = "N";
    protected final static String ITEM_PRIORITY = "priority";
    protected final static String ITEM_PRIORITY_HIGH = "H";
    protected final static String ITEM_PRIORITY_MEDIUM = "M";
    protected final static String ITEM_PRIORITY_LOW = "L";



    // SQLite Queries - including prepared statement queries
    protected final static String ITEM_INSERT_QUERY = "INSERT INTO items_list VALUES (NULL, ?, ?," +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?), TIME (?), DATETIME (?), ?, ?);";
    // datetime format = YYYY-MM-YY HH:YY:SS

    protected final static String ITEMS_NOT_DONE_GET_QUERY = "SELECT * FROM items_list WHERE title_id =" +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?) AND item_done = 'N' ORDER BY items_list.item COLLATE NOCASE ASC;";

    protected final static String ITEMS_DONE_GET_QUERY = "SELECT * FROM items_list WHERE title_id =" +
            " (SELECT title_id FROM titles_list WHERE titles_list.title = ?) AND item_done = 'Y' ORDER BY items_list.item COLLATE NOCASE ASC;";

    protected final static String TITLES_GET_QUERY = "SELECT * FROM titles_list ORDER BY titles_list.title COLLATE NOCASE ASC";

    protected final static String TITLES_GET_TITLE_ID = "SELECT title_id FROM titles_list WHERE title_id = " +
            "(SELECT title_id FROM titles_list WHERE titles_list.title = ?)";

    protected final static String TITLES_GET_TITLE = "SELECT title FROM titles_list WHERE titles_list.title_id = ?";

    protected final static String TITLES_CHECK_TITLE_NAME_EXISTS = "SELECT title FROM titles_list WHERE title = ?";

    protected final static String TITLE_DELETE_QUERY = "DELETE FROM titles_list WHERE title_id = ?;";

    protected final static String ITEMS_DELETE_ALL_SINGLE_TITLE = "DELETE FROM items_list WHERE " +
            "title_id = (SELECT title_id FROM titles_list WHERE titles_list.title = ?);";

    protected final static String ITEM_DELETE_SINGLE = "DELETE FROM items_list WHERE items_list.item_id = ?;";

    protected final static String ITEM_UPDATE = "UPDATE items_list SET item = ?, item_detail = ?, duration = ?, priority = ? WHERE item_id = ?;";

    protected final static String ITEM_UPDATE_ITEM_DONE = "UPDATE items_list SET item_done = ? WHERE item_id = ?;";

    // RequestCode onActivityResult - HomeFragment NEW LIST
    protected final static int NEW_ITEMS_REQUEST_CODE = 30;

    // RequestCode onActivityResult - HomeFragment EDIT LIST
    protected final static String EDIT_LIST = "com.treefrogapps.TaDo.edit_list";
    protected final static String EDIT_LIST_ACTIVITY_TITLE = "EDIT LIST";


}
