package de.timbogen.whattheprice.tabs.shared;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.WTPActivity;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.models.Item;

public class ItemAdapter extends ArrayAdapter<Item> {
    /**
     * The database
     */
    private Database db;
    /**
     * The items of the list
     */
    private ArrayList<Item> items;
    /**
     * The active context
     */
    private WTPActivity activity;

    /**
     * Constructor
     */
    public ItemAdapter(@NonNull WTPActivity activity, ArrayList<Item> items, Database db) {
        super(activity, 0, items);
        this.activity = activity;
        this.items = items;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Get the layout
        if (item == null) {
            item = LayoutInflater.from(activity).inflate(R.layout.adapter_item, parent, false);
        }

        // Set the name
        TextView name = item.findViewById(R.id.name);
        name.setText(items.get(position).name);

        // Set the price
        TextView price = item.findViewById(R.id.price);
        String priceText = Double.toString(items.get(position).price);
        price.setText(priceText);

        // Set the price
        TextView quantity = item.findViewById(R.id.quantity);
        String quantityText = Integer.toString(items.get(position).quantity);
        quantity.setText(quantityText);

        // Set the on click method for info button
        ImageView info = item.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(items.get(position));
            }
        });

        // Set the on click method for add button
        ImageView increase = item.findViewById(R.id.increase);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(items.get(position));
            }
        });

        // Set the on click method for add button
        ImageView decrease = item.findViewById(R.id.decrease);
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(items.get(position));
            }
        });

        // Return the modified item
        return item;
    }

    /**
     * Method to show the detailed info for one item
     *
     * @param item to show info for
     */
    private void showInfo(Item item) {
        // Set the item
        ItemDetailActivity.item = item;
        // Start the activity for result
        activity.startActivityForResult(new Intent(activity, ItemDetailActivity.class), 0);
    }

    /**
     * Method to increase the quantity of an item
     * @param item to be increased
     */
    private void increaseQuantity(Item item) {
        // Increase
        item.quantity++;
        // Check if item has to be added to the order
        if (item.quantity == 1) {
            WTPActivity.order.add(item);
        }
        // Update the item
        db.updateItem(item);
        // Refresh the fragments
        activity.updateFragments();
    }

    /**
     * Method to decrease the quantity of an item
     * @param item to be decreased
     */
    private void decreaseQuantity(Item item) {
        // Check if item is already at min
        if (item.quantity == 0) {
            return;
        }
        // Decrease
        item.quantity--;
        // Check if item has to be removed from the order
        if (item.quantity == 0) {
            WTPActivity.order.remove(item);
        }
        // Update the item
        db.updateItem(item);
        // Refresh the fragments
        activity.updateFragments();
    }
}
