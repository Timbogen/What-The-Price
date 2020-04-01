package de.timbogen.whattheprice.tabs.shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private Context context;

    /**
     * Constructor
     */
    public ItemAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Get the layout
        if (item == null) {
            item = LayoutInflater.from(context).inflate(R.layout.adapter_item, parent, false);
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

        // Return the modified item
        return item;
    }
}
