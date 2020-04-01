package de.timbogen.whattheprice.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.database.NewItemActivity;
import de.timbogen.whattheprice.tabs.models.Type;

public class DrinksFragment extends Fragment {

    /**
     * The fragment
     */
    private View fragment;
    /**
     * The active activity
     */
    private Activity activity;

    /**
     * Constructor
     */
    public DrinksFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        fragment =  inflater.inflate(R.layout.fragment_drinks, container, false);
        setupLayout();
        return fragment;
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
    }

    /**
     * Method to start the new item activity
     */
    private void newItem() {
        NewItemActivity.type = Type.DRINK;
        startActivity(new Intent(activity, NewItemActivity.class));
    }
}
