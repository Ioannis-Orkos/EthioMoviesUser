package com.ioannisnicos.ethiomoviesuser.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ioannisnicos.ethiomoviesuser.Movie_Detail_Activity;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.Tvshow_Detail_Activity;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallMoviesRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallTvshowsRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.database.FavouriteDBHandler;
import com.ioannisnicos.ethiomoviesuser.dialog.RentMovieDialog;
import com.ioannisnicos.ethiomoviesuser.dialog.RentTvshowDialog;
import com.ioannisnicos.ethiomoviesuser.models.Tvshow;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FavouriteTvshowsFragment extends Fragment {

    private RecyclerView mTvshowsRecyclerView;
    private List<Tvshow> mTvshowsList;
    private RowSmallTvshowsRecyclerAdapter mMoviesAdapter;
    private LinearLayout mEmptyLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        mEmptyLayout = (LinearLayout) view.findViewById(R.id.layout_recycler_view_fav_media_empty);

        GoogleSignInAccount mMyGoogleAccountInfo = GoogleSignIn.getLastSignedInAccount(getContext());


        mTvshowsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_favmovies);
        mTvshowsList = new ArrayList<>();

        mMoviesAdapter = new RowSmallTvshowsRecyclerAdapter(getContext(), mTvshowsList);
        mTvshowsRecyclerView.setAdapter(mMoviesAdapter);


        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mTvshowsRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter.setOnFavItemClickListener(new RowSmallTvshowsRecyclerAdapter.OnFavouriteMovieClickListener() {
            @Override
            public void onFavouriteItemClick(int position) {
                loadEngMovies();
               // mMoviesAdapter.notifyItemChanged(position);
                mMoviesAdapter.notifyItemRemoved(position);
            }
        });



        mMoviesAdapter.setOnBKItemClickListener(new RowSmallTvshowsRecyclerAdapter.OnBKItemClickListener() {
            @Override
            public void onBKItemClick(int position) {
                Intent intent = new Intent(getContext(), Tvshow_Detail_Activity.class);
                intent.putExtra(Tvshow_Detail_Activity.MOVIE_ID, mTvshowsList.get(position).getImdb_id());
                intent.putExtra(Tvshow_Detail_Activity.MOVIE_TITLE, mTvshowsList.get(position).getTitle());
                intent.putExtra(Tvshow_Detail_Activity.ADAPTER_POSITION, position);
                startActivityForResult(intent,  Tvshow_Detail_Activity.POSITION_ACTIVITY_RETURN);
            }
        });
//
        mMoviesAdapter.setRequestStoreClickListener(new RowSmallTvshowsRecyclerAdapter.RequestTvshowClickListener() {
            @Override
            public void onStoreItemClick(int position) {
                Bundle args = new Bundle();
                args.putString(RentTvshowDialog.USER_ID,mMyGoogleAccountInfo.getId());
                args.putString(RentTvshowDialog.TVSHOW_ID, mTvshowsList.get(position).getImdb_id());
                args.putString(RentTvshowDialog.TVSHOW_TIILE, mTvshowsList.get(position).getTitle());
                //args.putInt(RentTvshowDialog.STORE_ID, mStoreIDParam);
                RentTvshowDialog dialog=new RentTvshowDialog();


//                dialog.setl(new ChooseMovieRelatedStoreAlert.PopClickedListener() {
//                    @Override
//                    public void itemClick(String username) {
//                        Toast.makeText(getContext(),username, Toast.LENGTH_LONG).show();
//                    }
//                });

                dialog.setArguments(args);
                dialog.show(getFragmentManager(),"dialog");
            }
        });
        loadEngMovies();

        return view;
    }

    private void loadEngMovies() {
        mTvshowsList.clear();
        List<Tvshow> tvshowsList = FavouriteDBHandler.getFavTVShowBriefs(getContext());
        if (tvshowsList.isEmpty()) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }

        for (Tvshow tvshowBrief : tvshowsList) {
            mTvshowsList.add(tvshowBrief);
        }
       // mMoviesAdapter.notifyDataSetChanged();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadEngMovies();



        if (requestCode == Movie_Detail_Activity.POSITION_ACTIVITY_RETURN) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Movie_Detail_Activity.ADAPTER_POSITION, 1);
                mMoviesAdapter.notifyItemRemoved(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }





}