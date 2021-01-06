package com.ioannisnicos.ethiomoviesuser.fragments;

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
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallMoviesRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.database.FavouriteDBHandler;
import com.ioannisnicos.ethiomoviesuser.dialog.RentMovieDialog;
import com.ioannisnicos.ethiomoviesuser.models.Movies;


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FavouriteMoviesFragment extends Fragment {

    private RecyclerView mMoviesRecyclerView;
    private List<Movies> mMoviesList;
    private RowSmallMoviesRecyclerAdapter mMoviesAdapter;
    private LinearLayout mEmptyLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        mEmptyLayout = (LinearLayout) view.findViewById(R.id.layout_recycler_view_fav_media_empty);

        GoogleSignInAccount mMyGoogleAccountInfo = GoogleSignIn.getLastSignedInAccount(getContext());

        mMoviesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_favmovies);
        mMoviesList = new ArrayList<>();

        mMoviesAdapter = new RowSmallMoviesRecyclerAdapter(getContext(), mMoviesList);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);


        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter.setOnFavItemClickListener(new RowSmallMoviesRecyclerAdapter.OnFavouriteMovieClickListener() {
            @Override
            public void onFavouriteItemClick(int position) {
                loadEngMovies();
               // mMoviesAdapter.notifyItemChanged(position);
                mMoviesAdapter.notifyItemRemoved(position);
            }
        });



        mMoviesAdapter.setOnBKItemClickListener(new RowSmallMoviesRecyclerAdapter.OnBKItemClickListener() {
            @Override
            public void onBKItemClick(int position) {
                Intent intent = new Intent(getContext(), Movie_Detail_Activity.class);
                intent.putExtra(Movie_Detail_Activity.MOVIE_ID, mMoviesList.get(position).getImdb_id());
                intent.putExtra(Movie_Detail_Activity.MOVIE_TITLE, mMoviesList.get(position).getTitle());
                intent.putExtra(Movie_Detail_Activity.ADAPTER_POSITION, position);
                startActivityForResult(intent,  Movie_Detail_Activity.POSITION_ACTIVITY_RETURN);
            }
        });

        mMoviesAdapter.setRequestStoreClickListener(new RowSmallMoviesRecyclerAdapter.RequestMovieClickListener() {
            @Override
            public void onStoreItemClick(int position) {
                Bundle args = new Bundle();
                args.putString(RentMovieDialog.USER_ID,mMyGoogleAccountInfo.getId());
                args.putString(RentMovieDialog.MOVIE_ID, mMoviesList.get(position).getImdb_id());
                args.putString(RentMovieDialog.MOVIE_TIILE, mMoviesList.get(position).getTitle());
               // args.putInt(RentMovieDialog.STORE_ID, mStoreIDParam);
                RentMovieDialog dialog=new RentMovieDialog();


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
        mMoviesList.clear();
        List<Movies> moviesList = FavouriteDBHandler.getFavMovieBriefs(getContext());
        if (moviesList.isEmpty()) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }

        for (Movies movieBrief : moviesList) {
            mMoviesList.add(movieBrief);
        }
       // mMoviesAdapter.notifyDataSetChanged();
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//
//        if (requestCode == Movie_Detail_Activity.POSITION_ACTIVITY_RETURN) {
//            if (resultCode == Activity.RESULT_OK) {
//                int result = data.getIntExtra(Movie_Detail_Activity.ADAPTER_POSITION, 1);
//                loadEngMovies();
//
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//            }
//        }
//    }




}