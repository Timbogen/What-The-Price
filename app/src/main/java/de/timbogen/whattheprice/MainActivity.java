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

import java.util.ArrayList;

import de.timbogen.whattheprice.tabs.DrinksFragment;
import de.timbogen.whattheprice.tabs.FoodFragment;
import de.timbogen.whattheprice.tabs.OverviewFragment;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.database.NewFolderActivity;
import de.timbogen.whattheprice.tabs.models.Folder;
import de.timbogen.whattheprice.tabs.models.Item;

public class MainActivity extends AppCompatActivity {
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
     * The current order
     */
    public static ArrayList<Item> order;
    /**
     * The database
     */
    private Database db = new Database(this);
    /**
     * The overview fragment
     */
    private OverviewFragment overview;
    /**
     * The drinks fragment
     */
    private DrinksFragment drinks;
    /**
     * The food fragment
     */
    private FoodFragment food;
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
        setContentView(R.layout.activity_main);

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
            updateFragments();
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
    private void updateFragments() {
        overview.setupLayout();
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

            case R.id.delete_folder_finally:
                deleteFolder();
                return true;

            case R.id.clear_order:
                clearOrder();
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
     * Method to delete the current folder
     */
    private void deleteFolder() {
        if (folders.size() <= 1) {
            Toast.makeText(this, getString(R.string.delete_folder_error), Toast.LENGTH_LONG).show();
            return;
        }
        db.deleteFolder(selectedFolderID);
        folders.remove(Folder.findFolder(folders, selectedFolderID));
        selectedFolderID = folders.get(0).id;
        saveState();
        updateLayout();
        updateFragments();
    }

    /**
     * Method to clear the current order
     */
    private void clearOrder() {

    }

    /**
     * Method to setup the pager
     */
    private void setupPager() {
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), this));
        TabLayout layout = findViewById(R.id.tab_layout);
        layout.setupWithViewPager(pager);
    }

    /**
     * Class for setting up the view pager
     */
    private class PagerAdapter extends FragmentPagerAdapter {

        /**
         * The current activity
         */
        private Activity activity;

        /**
         * Constructor
         *
         * @param fm the fragment manager
         */
        private PagerAdapter(@NonNull FragmentManager fm, Activity activity) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.activity = activity;
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
                    drinks = new DrinksFragment(activity);
                    return drinks;

                case 2:
                    food = new FoodFragment(activity);
                    return food;

                default:
                    overview = new OverviewFragment();
                    return overview;
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
