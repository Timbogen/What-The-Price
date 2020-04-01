package de.timbogen.whattheprice.tabs.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import de.timbogen.whattheprice.MainActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.models.Item;
import de.timbogen.whattheprice.tabs.models.Type;

public class NewItemActivity extends AppCompatActivity {
    /**
     * The database
     */
    Database db = new Database(this);
    /**
     * Is true if the new item is a drink
     */
    public static Type type;

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.new_item_title);
        setContentView(R.layout.activity_new_item);
        setupSpinner();
    }

    /**
     * Method to setup the activity layout
     */
    private void setupSpinner() {
        // Setup the adapter
        Spinner spinner = findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Add a selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position == 0 ? Type.DRINK : Type.FOOD;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set the preselection
        spinner.setSelection(type == Type.DRINK ? 0 : 1);
    }

    /**
     * Method to add an item
     * @param view that was clicked
     */
    public void addItem(View view) {
        // Check if the name is valid
        EditText name = findViewById(R.id.text_name);
        if (name.getText().length() == 0 || name.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.item_name_warning), Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the price is valid
        EditText price = findViewById(R.id.text_price);
        if (price.getText().length() == 0 || Double.isNaN(Double.parseDouble(price.getText().toString()))) {
            Toast.makeText(this, getString(R.string.item_price_warning), Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the ingredients
        EditText ingredients = findViewById(R.id.text_ingredients);

        // Add the item
        Item item = new Item(name.getText().toString(), Double.parseDouble(price.getText().toString()),
                ingredients.getText().toString(), type.ordinal(), MainActivity.selectedFolderID);
        item.id = db.addItem(item);
        if (item.id != -1) {
            Toast.makeText(this, getString(R.string.item_add_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.item_add_error), Toast.LENGTH_LONG).show();
        }

        // Close the activity
        finish();
    }

    /**
     * Method to close the activity
     * @param view that was clicked
     */
    public void cancel(View view) {
        finish();
    }
}
