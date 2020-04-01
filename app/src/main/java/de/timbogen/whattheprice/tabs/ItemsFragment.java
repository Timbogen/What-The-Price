package de.timbogen.whattheprice.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.timbogen.whattheprice.WTPActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.database.NewItemActivity;
import de.timbogen.whattheprice.tabs.models.Item;
import de.timbogen.whattheprice.tabs.models.Type;
import de.timbogen.whattheprice.tabs.shared.ItemAdapter;

public class ItemsFragment extends Fragment {
    /**
     * The database
     */
    private Database db;
    /**
     * The fragment
     */
    private View fragment;
    /**
     * The active activity
     */
    private WTPActivity activity;
    /**
     * The items shown in the list
     */
    private ArrayList<Item> items;
    /**
     * The type of item
     */
    private Type type;

    /**
     * Constructor
     */
    public ItemsFragment(WTPActivity activity, Database db, Type type) {
        this.activity = activity;
        this.db = db;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        fragment =  inflater.inflate(R.layout.fragment_items, container, false);
        update(true);
        return fragment;
    }

    /**
     * Method to catch results of other activities
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            update(true);
        }
    }

    /**
     * Method to update the fragment
     *
     * @param reload true if data should also be reloaded
     */
    public void update(boolean reload) {
        if (reload) {
            loadData();
        }
        setupLayout();
    }

    /**
     * Method to load the data
     */
    private void loadData() {
        items = db.getItems(WTPActivity.selectedFolderID, type.ordinal());
    }

    /**
     * Method to setup the fragments layout
     */
    private void setupLayout() {
        // Add an action to the add button
        fragment.findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem();
            }
        });

        // Fill the list
        ListView list = fragment.findViewById(R.id.list);
        list.setAdapter(new ItemAdapter(activity, items, db));
    }

    /**
     * Method to start the new item activity
     */
    private void newItem() {
        NewItemActivity.type = type;
        startActivityForResult(new Intent(activity, NewItemActivity.class), 0);
    }
}
