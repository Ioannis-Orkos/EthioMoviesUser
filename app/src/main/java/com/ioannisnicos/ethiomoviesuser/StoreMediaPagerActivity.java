package com.ioannisnicos.ethiomoviesuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ioannisnicos.ethiomoviesuser.fragments.MoviesFragment;
import com.ioannisnicos.ethiomoviesuser.fragments.TvshowsFragment;

public class StoreMediaPagerActivity extends AppCompatActivity {

    public static final String STORE_ID = "storeID";


    // TODO: Rename and change types and number of parameters
    public static Intent newInstance(Context c, int storeID) {

        Intent intent = new Intent((Activity) c, StoreMediaPagerActivity.class);
        intent.putExtra(STORE_ID,storeID);

        return intent;
    }


    private int mStoreID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storemedia_search_pagger);

        Intent receivedIntent = getIntent();
        mStoreID = receivedIntent.getIntExtra(StoreMediaPagerActivity.STORE_ID,-1);


        StoreMediaPagerAdapter sectionsPagerAdapter = new StoreMediaPagerAdapter(this,mStoreID);
        ViewPager2 viewPager = findViewById(R.id.view_pager_store_movie);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_view_store_movie);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Movies");
                    } else if (position == 1) {
                        tab.setText("Tvshows");
                    }else {
                        tab.setText("");
                    }
                }
        ).attach();
    }

    public class StoreMediaPagerAdapter extends FragmentStateAdapter {

        private int storeId;
        public StoreMediaPagerAdapter(StoreMediaPagerActivity fragment, int storeId) {
            super(fragment);
            this.storeId = storeId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            switch (position) {
                case 0:
                    return MoviesFragment.newInstance(storeId,null,null);
                case 1:
                    return TvshowsFragment.newInstance(storeId,null,null);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}