package de.timbogen.whattheprice.tabs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.timbogen.whattheprice.tabs.models.Folder;
import de.timbogen.whattheprice.tabs.models.Item;

public class Database extends SQLiteOpenHelper {
    /**
     * The default key for the id of a row
     */
    private static final String ID = "_id";
    /**
     * Version of the database
     */
    private static final int DB_VERSION = 1;
    /**
     * Name of the database
     */
    private static final String DB_NAME = "Item.db";
    /**
     * Name of the folders table
     */
    private static final String FOLDER_TABLE = "Folders";
    /**
     * Name of the items table
     */
    private static final String ITEM_TABLE = "Items";

    /**
     * Constructor
     */
    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the folder table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + FOLDER_TABLE + " ( "
                        + Folder.NAME + " TEXT NOT NULL UNIQUE, "
                        + Folder.DESCRIPTION + " TEXT, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
                        + " ) "
        );
        // Create the item table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + ITEM_TABLE + " ( "
                        + Item.NAME + " TEXT NOT NULL UNIQUE, "
                        + Item.PRICE + " REAL NOT NULL, "
                        + Item.INGREDIENTS + " TEXT, "
                        + Item.TYPE + " INTEGER NOT NULL, "
                        + Item.FOLDER_ID + " INTEGER NOT NULL, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
                        + " ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        onCreate(db);
    }

    /**
     * Method to add a folder
     *
     * @param folder to be added
     * @return true if the folder was added successfully
     */
    long addFolder(Folder folder) {
        SQLiteDatabase db = getWritableDatabase();

        // Create the value pairs
        ContentValues values = new ContentValues();
        values.put(Folder.NAME, folder.name);
        values.put(Folder.DESCRIPTION, folder.description);

        // Insert into the database
        return db.insert(FOLDER_TABLE, null, values);
    }

    /**
     * Method to add an item
     *
     * @param item to be added
     * @return true if the item was added successfully
     */
    long addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        // Create the value pairs
        ContentValues values = new ContentValues();
        values.put(Item.NAME, item.name);
        values.put(Item.PRICE, item.price);
        values.put(Item.INGREDIENTS, item.ingredients);
        values.put(Item.TYPE, item.type);
        values.put(Item.FOLDER_ID, item.folder_id);

        // Insert into the database
        return db.insert(FOLDER_TABLE, null, values);
    }

    /**
     * Method to get all folders
     *
     * @return the folders
     */
    public ArrayList<Folder> getFolders() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Folder> folders = new ArrayList<>();

        // Create the query
        Cursor cursor = db.rawQuery("SELECT * FROM " + FOLDER_TABLE, null);

        // Check if the cursor is valid
        if (!cursor.moveToFirst()) {
            return folders;
        }
        // Extract the data
        while (!cursor.isAfterLast()) {
            // Get the data
            Folder folder = new Folder();
            folder.id = cursor.getLong(cursor.getColumnIndex(ID));
            folder.name = cursor.getString(cursor.getColumnIndex(Folder.NAME));
            folder.description = cursor.getString(cursor.getColumnIndex(Folder.DESCRIPTION));
            folders.add(folder);

            // Go to the next
            cursor.moveToNext();
        }

        // Close the cursor
        cursor.close();
        return folders;
    }

    /**
     * Method to get all items for the corresponding folder/type
     *
     * @param folder of the items
     * @param type of the items
     * @return drink-items
     */
    public ArrayList<Item> getItems(Folder folder, int type) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Item> items = new ArrayList<>();

        // Create the query
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + ITEM_TABLE
                        + " WHERE " + Item.FOLDER_ID + "=" + folder.id
                        + " AND " + Item.TYPE + "=" + type,
                null
        );

        // Check if the cursor is valid
        if (!cursor.moveToFirst()) {
            return items;
        }
        // Extract the data
        while (!cursor.isAfterLast()) {
            Item item = new Item();
            item.id = cursor.getLong(cursor.getColumnIndex(ID));
            item.name = cursor.getString(cursor.getColumnIndex(Item.NAME));
            item.price = cursor.getDouble(cursor.getColumnIndex(Item.PRICE));
            item.ingredients = cursor.getString(cursor.getColumnIndex(Item.INGREDIENTS));
            item.type = cursor.getInt(cursor.getColumnIndex(Item.TYPE));
            item.folder_id = cursor.getInt(cursor.getColumnIndex(Item.FOLDER_ID));
            items.add(item);
        }

        // Close the cursor
        cursor.close();
        return items;
    }

    /**
     * Method to delete a folder with all the corresponding items
     *
     * @param id of the folder that should be deleted
     * @return true if the folder was deleted successfully
     */
    public boolean deleteFolder(long id) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete all corresponding items
        db.delete(ITEM_TABLE, Item.FOLDER_ID + "=" + id, null);

        // Delete the folder
        return db.delete(FOLDER_TABLE, ID + "=" + id, null) > 0;
    }

    /**
     * Method to delete an item
     *
     * @param item to be deleted
     * @return true if item was deleted successfully
     */
    public boolean deleteItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(ITEM_TABLE, ID + "=" + item.id, null) > 0;
    }
}
