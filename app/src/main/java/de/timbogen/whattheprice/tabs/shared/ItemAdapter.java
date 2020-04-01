package de.timbogen.whattheprice.tabs.shared;

import android.app.Activity;
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
import de.timbogen.whattheprice.tabs.models.Item;

public class ItemAdapter extends ArrayAdapter<Item> {
    /**
     * The items of the list
     */
    private ArrayList<Item> items;
    /**
     * The active context
     */
    private Activity activity;

    /**
     * Constructor
     */
    public ItemAdapter(@NonNull Activity activity, ArrayList<Item> items) {
        super(activity, 0, items);
        this.activity = activity;
        this.items = items;
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
                // Set the item
                ItemDetailActivity.item = items.get(position);
                // Start the activity for result
                activity.startActivityForResult(new Intent(activity, ItemDetailActivity.class), 0);
            }
        });

        // Return the modified item
        return item;
    }
}
