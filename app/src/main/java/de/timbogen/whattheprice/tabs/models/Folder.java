package de.timbogen.whattheprice.tabs.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Model for a folder
 */
public class Folder {
    /**
     * DB columns
     */
    public static String NAME = "name";
    public static String DESCRIPTION = "description";
    /**
     * Id of the folder
     */
    public long id;
    /**
     * Name of the folder
     */
    public String name;
    /**
     * Description of the folder
     */
    public String description;

    /**
     * Default constructor
     */
    public Folder() {
    }

    /**
     * Defining constructor
     */
    public Folder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Method to find a folder in a list of folders
     *
     * @param folders list
     * @param id     of the searched folder
     * @return the position of the right folder
     */
    public static int findFolder(ArrayList<Folder> folders, long id) {
        for (int index = 0; index < folders.size(); index++) {
            if (id == folders.get(index).id) {
                return index;
            }
        }
        return -1;
    }

    /**
     * @return object in string form for debugging purposes
     */
    @NonNull
    public String toString() {
        return "========================\n"
                + "ID         : " + id + "\n"
                + "Name       : " + name + "\n"
                + "Description: " + description + "\n"
                + "========================\n";
    }
}
