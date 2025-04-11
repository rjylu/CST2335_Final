package com.example.cst2335final;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The FavouritesDatabase class is a SQLiteOpenHelper class that is used to create a database for the favourite articles.
 */
public class FavouritesDatabase extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "FavouritesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "FAVOURITES";
    public final static String COL_SECTION = "SECTION";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_URL = "URL";

    /**
     * <p>This constructor is used to create a new FavouritesDatabase object.</p>
     * @param ctx for context
     */
    public FavouritesDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * This method is used to create the database.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "  + COL_SECTION + " text," + COL_TITLE + " text," + COL_URL + " text);");
    }

    /**
     * This method is used to upgrade the database. It drops the old database in favor for the new one.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

}