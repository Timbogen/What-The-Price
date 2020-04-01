package de.timbogen.whattheprice.tabs.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.database.Database;
import de.timbogen.whattheprice.tabs.models.Item;

public class ItemDetailActivity extends AppCompatActivity {
    /**
     * The database
     */
    private Database db = new Database(this);
    /**
     * The item that is viewed
     */
    public static Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.detailed_title));
        setContentView(R.layout.activity_item_detail);
        setupLayout();
    }

    /**
     * Method to setup the layout
     */
    private void setupLayout() {
        System.out.println(item.name);
        // The name
        TextView name = findViewById(R.id.name);
        name.setText(item.name);

        // The price
        TextView price = findViewById(R.id.price);
        String priceText = Double.toString(item.price);
        price.setText(priceText);

        // The price
        TextView quantity = findViewById(R.id.quantity);
        String quantityText = Integer.toString(item.quantity);
        quantity.setText(quantityText);

        // The ingredients
        if (!item.ingredients.equals("")) {
            TextView ingredients = findViewById(R.id.ingredients);
            ingredients.setText(item.ingredients);
        }
    }

    /**
     * Method to delete an item
     *
     * @param view that was clicked
     */
    public void deleteItem(View view) {
        if (db.deleteItem(item)) {
            setResult(Activity.RESULT_OK, null);
            Toast.makeText(this, getString(R.string.detailed_delete_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.detailed_delete_error), Toast.LENGTH_LONG).show();
        }
        finish();

    }

    /**
     * Method to close the activity
     *
     * @param view that was clicked
     */
    public void cancel(View view) {
        finish();
    }
}
