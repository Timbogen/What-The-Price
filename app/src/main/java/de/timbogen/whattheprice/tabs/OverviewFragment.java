package de.timbogen.whattheprice.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.timbogen.whattheprice.MainActivity;
import de.timbogen.whattheprice.R;
import de.timbogen.whattheprice.tabs.models.Folder;

public class OverviewFragment extends Fragment {
    /**
     * The fragment
     */
    private View fragment;

    /**
     * Create the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        fragment = inflater.inflate(R.layout.fragment_overview, container, false);
        update();
        return fragment;
    }

    public void update() {
        setupLayout();
    }

    /**
     * Method to setup the fragments layout
     */
    private void setupLayout() {
        // Get the current folder
        Folder folder = MainActivity.folders.get(Folder.findFolder(MainActivity.folders, MainActivity.selectedFolderID));
        // Set the description
        TextView description = fragment.findViewById(R.id.price);
        if (folder.description.equals("")) {
            description.setText(getString(R.string.overview_no_description));
        } else {
            description.setText(folder.description);
        }
    }

}
