package de.timbogen.whattheprice.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
     * The type of item
     */
    private Type type;
    /**
     * The filtered list of items
     */
    private ArrayList<Item> items = new ArrayList<>();
    /**
     * The filter for the list
     */
    private String filter = "";
    /**
     * The adapter for the list
     */
    private ItemAdapter adapter;

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
        setupLayout();
        update();
        return fragment;
    }

    /**
     * Method to catch results of other activities
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            update();
        }
    }

    /**
     * Method to setup the layout
     */
    private void setupLayout() {
        // Setup the text watcher for the search bar
        EditText search = fragment.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter = s.toString();
                update();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Add an action to the add button
        fragment.findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem();
            }
        });

        // Fill the list
        ListView list = fragment.findViewById(R.id.list);
        adapter = new ItemAdapter(activity, items, db);
        list.setAdapter(adapter);
    }

    /**
     * Method to filter the items
     */
    private void applyFilter() {
        // Refill the items
        items.clear();
        items.addAll(type == Type.DRINK ? activity.drinks : activity.food);

        // Check if filter is empty
        if (filter.equals("")) {
            return;
        }

        // Filter the list
        for (int index = 0; index < items.size(); index++) {
            if (!items.get(index).name.toLowerCase().contains(filter.toLowerCase())) {
                items.remove(items.get(index));
                index--;
            }
        }
    }

    /**
     * Method to update the fragment
     */
    public void update() {
        // Filter the items and check if no items hint should be visible
        applyFilter();
        if (items.size() == 0) {
            fragment.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
        } else {
            fragment.findViewById(R.id.no_items).setVisibility(View.GONE);
        }

        // Refresh the list
        adapter.notifyDataSetChanged();
    }

    /**
     * Method to start the new item activity
     */
    private void newItem() {
        NewItemActivity.type = type;
        startActivityForResult(new Intent(activity, NewItemActivity.class), 0);
    }
}
