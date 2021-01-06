package com.ioannisnicos.ethiomoviesuser.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ioannisnicos.ethiomoviesuser.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


public class FavouritePagerFragment extends Fragment {

    //FavouritePagerAdapter favouritePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_pagger, container, false);

//        mSmartTabLayout = (SmartTabLayout) view.findViewById(R.id.tab_view_pager_fav);
//        mViewPager = (ViewPager) view.findViewById(R.id.view_pager_fav);
//        mViewPager.setAdapter(new FavouritePaggerAdapter(getChildFragmentManager(), getContext()));
//        mSmartTabLayout.setViewPager(mViewPager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //favouritePagerAdapter = new FavouritePagerAdapter(this);
         ViewPager2 viewPager = view.findViewById(R.id.view_pager_fav);
        //viewPager.setAdapter(favouritePagerAdapter);
         viewPager.setAdapter( new FragmentStateAdapter(this) {

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new FavouriteMoviesFragment();
                    case 1:
                        return  new FavouriteTvshowsFragment();

                }
                return null;
            }


            @Override
            public int getItemCount() {
                return 2;
            }

        });


        TabLayout tabLayout = view.findViewById(R.id.tab_view_pager_fav);
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



    public class FavouritePagerAdapter extends FragmentStateAdapter {


        public FavouritePagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            switch (position) {
                case 0:
                    return new FavouriteMoviesFragment();
                case 1:
                    return  new FavouriteTvshowsFragment();

            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
