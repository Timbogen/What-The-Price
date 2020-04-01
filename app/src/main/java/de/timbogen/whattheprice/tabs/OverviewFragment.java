package de.timbogen.whattheprice.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.timbogen.whattheprice.WTPActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.models.Folder;
import de.timbogen.whattheprice.tabs.models.Item;
import de.timbogen.whattheprice.tabs.shared.ItemAdapter;


public class OverviewFragment extends Fragment {
    /**
     * The database
     */
    private Database db;
    /**
     * The fragment
     */
    private View fragment;
    /**
     * The current activity
     */
    private WTPActivity activity;

    /**
     * Constructor
     */
    public OverviewFragment(WTPActivity activity, Database db) {
        this.activity = activity;
        this.db = db;
    }

    /**
     * Create the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        fragment = inflater.inflate(R.layout.fragment_overview, container, false);
        update();
        return fragment;
    }

    /**
     * Method to update the fragment
     */
    public void update() {
        // Get the current folder
        Folder folder = WTPActivity.folders.get(Folder.findFolder(WTPActivity.folders, WTPActivity.selectedFolderID));
        // Set the description
        TextView description = fragment.findViewById(R.id.description);
        if (folder.description.equals("")) {
            description.setText(getString(R.string.overview_no_description));
        } else {
            description.setText(folder.description);
        }

        // Fill the list
        ListView list = fragment.findViewById(R.id.list);
        list.setAdapter(new ItemAdapter(activity, WTPActivity.order, db));

        // Set the price
        double price = 0;
        for (Item item: WTPActivity.order) {
            price += item.price * item.quantity;
        }
        TextView priceText = fragment.findViewById(R.id.total_price);
        String text = Double.toString(price);
        priceText.setText(text);
    }

}
