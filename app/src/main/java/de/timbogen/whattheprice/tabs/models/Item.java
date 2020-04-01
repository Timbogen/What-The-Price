package de.timbogen.whattheprice.tabs.models;

import androidx.annotation.NonNull;

/**
 * Model for a single item
 */
public class Item {
    /**
     * DB columns
     */
    public static String NAME = "name";
    public static String PRICE = "price";
    public static String INGREDIENTS = "ingredients";
    public static String TYPE = "type";
    public static String FOLDER_ID = "folder_id";
    /**
     * Id of the folder
     */
    public long id;
    /**
     * Name of the item
     */
    public String name;
    /**
     * Price of the item
     */
    public double price;
    /**
     * Ingredients of the item
     */
    public String ingredients;
    /**
     * Type of the item
     */
    public int type;
    /**
     * Id of the corresponding folder
     */
    public long folder_id;
    /**
     * The quantity of the item
     */
    public int quantity;

    /**
     * Default constructor
     */
    public Item() {
    }

    /**
     * Defining constructor
     */
    public Item(String name, double price, String ingredients, int type, long folder_id) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.type = type;
        this.folder_id = folder_id;
    }

    /**
     * @return object in string form for debugging purposes
     */
    @NonNull
    public String toString() {
        return "========================\n"
                + "ID         : " + id + "\n"
                + "Name       : " + name + "\n"
                + "Price      : " + price + " Euro\n"
                + "Ingredients: " + ingredients + "\n"
                + "Type       : " + type + "\n"
                + "Folder ID  : " + folder_id + "\n"
                + "========================\n";
    }
}
