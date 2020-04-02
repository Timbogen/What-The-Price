package de.timbogen.whattheprice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.timbogen.whattheprice.tabs.ItemsFragment;
import de.timbogen.whattheprice.tabs.OverviewFragment;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.database.NewFolderActivity;
import de.timbogen.whattheprice.tabs.models.Folder;
import de.timbogen.whattheprice.tabs.models.Item;
import de.timbogen.whattheprice.tabs.models.Type;

public class WTPActivity extends AppCompatActivity {
    /**
     * The key of the shared pref
     */
    private static final String SELECTED_KEY = "SELECTED_ID";
    /**
     * The folders
     */
    public static ArrayList<Folder> folders;
    /**
     * The selected Folder
     */
    public static long selectedFolderID = -1;
    /**
     * The items shown in the list
     */
    public ArrayList<Item> drinks;
    /**
     * The items shown in the list
     */
    public ArrayList<Item> food;
    /**
     * The current order
     */
    public static ArrayList<Item> order = new ArrayList<>();
    /**
     * The database
     */
    private Database db = new Database(this);
    /**
     * The overview fragment
     */
    private OverviewFragment overviewFragment = new OverviewFragment(this, db);
    /**
     * The drinks fragment
     */
    private ItemsFragment drinksFragment = new ItemsFragment(this, db, Type.DRINK);
    /**
     * The food fragment
     */
    private ItemsFragment foodFragment = new ItemsFragment(this, db, Type.FOOD);
    /**
     * Spinner containing the folders
     */
    private Spinner spinnerFolders;

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtp);

        // Load data
        restoreState();
        loadData();

        // Setup the UI
        setupPager();
        setupToolbar();
    }

    /**
     * Method to setup the toolbar
     */
    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            // Set the icon
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.toolbar);

            // Setup the spinner
            spinnerFolders = getSupportActionBar().getCustomView().findViewById(R.id.spinner_folders);
            updateLayout();
        }
    }

    /**
     * Method to restore the app state
     */
    private void restoreState() {
        // Load the id of the selected folder
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        selectedFolderID = prefs.getLong(SELECTED_KEY, selectedFolderID);
    }

    /**
     * Method to save the app state
     */
    private void saveState() {
        // Save the id of the selected folder
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(SELECTED_KEY, selectedFolderID);
        editor.apply();
    }

    /**
     * Method to load the data
     */
    private void loadData() {
        // Load the data
        folders = db.getFolders();
        // Check if there is no folder yet
        if (folders.size() == 0) {
            // Add the default folder
            Folder folder = new Folder(getString(R.string.default_directory), getString(R.string.default_directory_description));
            folders.add(folder);
            folder.id = db.addFolder(folder);
            // Set the folder as selected
            selectedFolderID = folder.id;
            saveState();
        }
        loadItems();
    }

    /**
     * Method to load all the items
     */
    private void loadItems() {
        clearOrder(false);
        drinks = db.getItems(selectedFolderID, Type.DRINK.ordinal());
        food = db.getItems(selectedFolderID, Type.FOOD.ordinal());
    }

    /**
     * Method to catch results of other activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            saveState();
            updateLayout();
            clearOrder(false);
        }
    }

    /**
     * Method to update the layout
     */
    private void updateLayout() {
        // Setup the adapter
        String[] items = new String[folders.size()];
        for (int index = 0; index < items.length; index++) {
            items[index] = folders.get(index).name;
        }
        ArrayAdapter<String> adapterFolders = new ArrayAdapter<>(this, R.layout.spinner_folders, items);
        adapterFolders.setDropDownViewResource(R.layout.spinner_folders_dropdown);
        spinnerFolders.setAdapter(adapterFolders);

        // Set the selection
        int index = Folder.findFolder(folders, selectedFolderID);
        if (index != -1) {
            spinnerFolders.setSelection(index);
        }
    }

    /**
     * Method to update the fragments
     */
    public void updateFragments() {
        drinksFragment.update();
        foodFragment.update();
        overviewFragment.update();
    }

    /**
     * Method to create the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Add an item selected event
        spinnerFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the id
                selectedFolderID = folders.get(position).id;
                saveState();
                loadItems();
                updateFragments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return true;
    }

    /**
     * Method to handle clicks on the options menu
     *
     * @param item that was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_folder:
                addFolder();
                return true;

            case R.id.share_folder:
                shareFolder();
                return true;

            case R.id.delete_folder_finally:
                deleteFolder();
                return true;

            case R.id.clear_order:
                clearOrder(true);
                updateFragments();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to add a folder
     */
    private void addFolder() {
        startActivityForResult(new Intent(this, NewFolderActivity.class), 0);
    }

    /**
     * Method to share a folder
     */
    private void shareFolder() {
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");
            Gson gson = new Gson();

            waIntent.putExtra(Intent.EXTRA_TEXT, gson.toJson(drinks));
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Method to delete the current folder
     */
    private void deleteFolder() {
        if (folders.size() <= 1) {
            Toast.makeText(this, getString(R.string.delete_folder_error), Toast.LENGTH_LONG).show();
            return;
        }
        if (db.deleteFolder(selectedFolderID)) {
            folders.remove(Folder.findFolder(folders, selectedFolderID));
            selectedFolderID = folders.get(0).id;
            saveState();
            updateLayout();
            clearOrder(false);
            updateFragments();
        } else {
            Toast.makeText(this, getString(R.string.delete_folder_error2), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to clear the current order
     *
     * @param fullClear true if order should be completely cleared
     */
    public void clearOrder(boolean fullClear) {
        if (fullClear) {
            // Reset the quantities
            for (Item item: order) {
                item.quantity = 0;
                db.updateItem(item);
            }
        }
        // Empty the order
        order.clear();
    }

    /**
     * Method to setup the pager
     */
    private void setupPager() {
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        TabLayout layout = findViewById(R.id.tab_layout);
        layout.setupWithViewPager(pager);
    }

    /**
     * Class for setting up the view pager
     */
    private class PagerAdapter extends FragmentPagerAdapter {
        /**
         * Constructor
         *
         * @param fm the fragment manager
         */
        private PagerAdapter(@NonNull FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        /**
         * @return the count of tabs
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Method to return the matching fragment
         *
         * @param position of the tab
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return drinksFragment;

                case 2:
                    return foodFragment;

                default:
                    return overviewFragment;
            }
        }

        /**
         * Method to return the matching page title
         *
         * @param position of the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.drinks);
                case 2:
                    return getString(R.string.food);
                default:
                    return getString(R.string.overview);
            }
        }
    }
}
