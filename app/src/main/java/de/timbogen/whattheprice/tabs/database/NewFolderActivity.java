package de.timbogen.whattheprice.tabs.database;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.timbogen.whattheprice.WTPActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.models.Folder;

public class NewFolderActivity extends AppCompatActivity {
    /**
     * The database
     */
    Database db = new Database(this);

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.new_folder_title);
        setContentView(R.layout.activity_new_folder);
    }

    /**
     * Method to add an item
     * @param view that was clicked
     */
    public void addFolder(View view) {
        // Check if the name is valid
        EditText name = findViewById(R.id.text_name);
        if (name.getText().length() == 0 || name.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.folder_name_warning), Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the ingredients
        EditText description = findViewById(R.id.text_ingredients);

        // Add the item
        Folder folder = new Folder(name.getText().toString(), description.getText().toString());
        folder.id = db.addFolder(folder);
        if (folder.id != -1) {
            // Add the new item to the existing activity
            setResult(Activity.RESULT_OK, null);
            WTPActivity.folders.add(folder);
            WTPActivity.selectedFolderID = folder.id;
            Toast.makeText(this, getString(R.string.folder_add_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.folder_add_error), Toast.LENGTH_LONG).show();
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
