package de.timbogen.whattheprice.tabs.database;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.timbogen.whattheprice.WTPActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.models.Folder;
import de.timbogen.whattheprice.tabs.models.Item;

public class NewFolderActivity extends AppCompatActivity {
    /**
     * The database
     */
    Database db = new Database(this);
    /**
     * The items that shall be imported
     */
    ArrayList<Item> items;

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.new_folder_title);
        setContentView(R.layout.activity_new_folder);
        checkIntent();
    }

    /**
     * Method to check whether user wants to import a shared folder
     */
    private void checkIntent() {
        try {
            // Check if there is a content uri
            Uri uri = this.getIntent().getData();
            assert uri != null;

            // Try to open the stream
            InputStream is = getContentResolver().openInputStream(uri);
            assert is != null;

            // Set the name of the folder
            EditText name = findViewById(R.id.text_name);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                name.setText(cursor.getString(0).split(".swtp")[0]);
                cursor.close();
            }

            // Get the data
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer, StandardCharsets.UTF_8);

            // Convert the string to an item list
            Gson gson = new Gson();
            items = gson.fromJson(text, new TypeToken<ArrayList<Item>>(){}.getType());

            // Check if items are null (for better user notification)
            if (items == null) {
                items = new ArrayList<>();
            }
        } catch (Exception e) {
            items = null;
        }
    }

    /**
     * Method to add an item
     *
     * @param view that was clicked
     */
    public void addFolder(View view) {
        // Check if the name is valid
        EditText name = findViewById(R.id.text_name);
        if (name.getText().length() == 0 || name.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.folder_name_warning), Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.getText().toString().contains(".")) {
            Toast.makeText(this, getString(R.string.folder_name_warning2), Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the ingredients
        EditText description = findViewById(R.id.text_description);

        // Add the item
        Folder folder = new Folder(name.getText().toString(), description.getText().toString());
        folder.id = db.addFolder(folder);
        if (folder.id != -1) {
            // Check if intent was started from WhatsApp
            if (items == null) {
                // Add the new folder to the existing activity
                setResult(Activity.RESULT_OK, null);
                WTPActivity.folders.add(folder);
                WTPActivity.selectedFolderID = folder.id;
                Toast.makeText(this, getString(R.string.folder_add_success), Toast.LENGTH_LONG).show();
            } else {
                addItems(folder.id);
            }
        } else {
            Toast.makeText(this, getString(R.string.folder_add_error), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    /**
     * Method to add the imported items
     *
     * @param id of the new folder
     */
    private void addItems(long id) {
        // Add items if available
        boolean importOK = true;
        for (Item item : items) {
            item.folder_id = id;
            importOK = importOK && db.addItem(item) != -1;
        }
        // Notify the user
        if (importOK && items.size() != 0) {
            Toast.makeText(this, getString(R.string.folder_import_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.folder_import_warning), Toast.LENGTH_LONG).show();
        }
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
