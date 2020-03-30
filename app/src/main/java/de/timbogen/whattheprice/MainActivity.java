package de.timbogen.whattheprice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import de.timbogen.whattheprice.tabs.DrinksFragment;
import de.timbogen.whattheprice.tabs.FoodFragment;
import de.timbogen.whattheprice.tabs.OrderFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPager();
    }

    /**
     * Method to setup the pager
     */
    private void setupPager() {
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        TabLayout layout = findViewById(R.id.tab_layout);
        layout.setupWithViewPager(pager);

    }

    /**
     * Class for setting up the view pager
     */
    private class PagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         * @param fm the fragment manager
         */
        private PagerAdapter(@NonNull FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        /**
         * @return the count of tabs
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Method to return the matching fragment
         * @param position of the tab
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1: return new DrinksFragment();
                case 2: return new FoodFragment();
                default: return new OrderFragment();
            }
        }

        /**
         * Method to return the matching page title
         * @param position of the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1: return getString(R.string.drinks);
                case 2: return getString(R.string.food);
                default: return getString(R.string.order);
            }
        }
    }
}
