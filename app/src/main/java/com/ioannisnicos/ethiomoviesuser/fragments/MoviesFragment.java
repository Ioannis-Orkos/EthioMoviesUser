package com.ioannisnicos.ethiomoviesuser.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ioannisnicos.ethiomoviesuser.Movie_Detail_Activity;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallMoviesRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.dialog.RentMovieDialog;
import com.ioannisnicos.ethiomoviesuser.models.Movies;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.MoviesResponsePaging;
import com.ioannisnicos.ethiomoviesuser.utils.ConnectivityBroadcastReceiver;
import com.ioannisnicos.ethiomoviesuser.utils.NetworkConnection;
import com.ioannisnicos.ethiomoviesuser.utils.SnackbarNoSwipeBehavior;

import java.util.ArrayList;
import java.util.List;


public class MoviesFragment extends Fragment {

    public static final String MOVIE_STORE_PARAM = "storeParam";
    public static final String MOVIE_TYPE_PARAM =  "movieTypeParam";
    public static final String MOVIE_SEARCH =      "movieSearchParam";

    private int      mStoreIDParam =      -1;
    private String   mMovieTypeParam =    "";
    private String   mMovieSearchParam =  "";


    private View            view;
    private ProgressBar     mProgressBar;
    private TextView        mTextViewNoContent;

    private boolean         pagesOver = false;
    private int             presentPage = 1;
    private boolean         loading = true;
    private int             previousTotal = 0;
    private int             visibleThreshold = 3;

    private Snackbar                         mConnectivitySnackbar;
    private View                             mSnackbarViewID;
    private ConnectivityBroadcastReceiver    mConnectivityBroadcastReceiver;
    private boolean isBroadcastReceiverRegistered;
    private boolean isFragmentLoaded;

    private Call<MoviesResponsePaging> mMoviesListCall;
    private List<Movies> mMoviesList;

    private SwipeRefreshLayout              mSwipeRefreshLayout;
    private boolean                         isSwipeOn = false;
    private RecyclerView                    mMoviesRecyclerView;
    private RowSmallMoviesRecyclerAdapter   mMoviesAdapter;


    private boolean[] GenrescheckedList = {false, false, false,false, false, false,false, false, false,false, false,
                                           false,false,false, false,false, false, false,false, false, false,false,
                                           false, false,false};

    private String[]  GenresList = {"comedy","short","drama","history","war","romance","action","western","fantasy",
                                    "horror","science-fiction","documentary","adventure","family","crime","music",
                                    "thriller","animation","mystery","musical","holiday","anime","superhero","suspense",
                                    "tv-movie" };


    private String mGener="all";
    private String mYear="all";
    int YearFiltercheckedItem = 2;



    public static MoviesFragment newInstance(int paramStore, String paramType,String paramSearch){

            MoviesFragment fragment = new MoviesFragment();
            Bundle args = new Bundle();
                   args.putInt(MOVIE_STORE_PARAM, paramStore);
                   args.putString(MOVIE_TYPE_PARAM, paramType);
                   args.putString(MOVIE_SEARCH, paramSearch);
            fragment.setArguments(args);

        return fragment;
    }


    public MoviesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStoreIDParam     = getArguments().getInt(MOVIE_STORE_PARAM,-1);
            mMovieTypeParam   = getArguments().getString(MOVIE_TYPE_PARAM,"");
            mMovieSearchParam = getArguments().getString(MOVIE_SEARCH,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_movies, container, false);

        mProgressBar = view.findViewById(R.id.progressBar_main_media_fragment);
        mTextViewNoContent = view.findViewById(R.id.textView_main_mediafragment_blank);

        if(!mMovieSearchParam.isEmpty())  mSnackbarViewID = getActivity().findViewById(R.id.coordinatorlayout_storemedia_search_pagger);
        else if(mStoreIDParam!=-1)        mSnackbarViewID = getActivity().findViewById(R.id.coordinatorlayout_storemedia_search_pagger);
        else                              mSnackbarViewID = getActivity().findViewById(R.id.drawer_layout_main);

        intAdapter();



        FloatingActionButton filterResult = view.findViewById(R.id.floatingactionbutton_movies_main_fragment_filter);
        filterResult.setOnClickListener(view1 -> {
            //showAlertDialog();
                PopupMenu popup = new PopupMenu(getContext(), view1);
                popup.setGravity(Gravity.RIGHT);
                popup.getMenuInflater().inflate(R.menu.movie_fragmetnt_fillter_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
            });



        if (NetworkConnection.isConnected(getContext())) {
            isFragmentLoaded = true;
            loadMovies();
        }else{
            mConnectivitySnackbar = Snackbar.make(mSnackbarViewID,
                    R.string.snackbar_no_network,
                    Snackbar.LENGTH_INDEFINITE)
                    .setBehavior(new SnackbarNoSwipeBehavior())
                    .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadMovies();
                        }
                    });
            mConnectivitySnackbar.show();
        }
        return view;
    }


    public void intAdapter(){

        GoogleSignInAccount mMyGoogleAccountInfo = GoogleSignIn.getLastSignedInAccount(getContext());
        if(presentPage<=1)  mMoviesList = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_main_fragment_media);
        mMoviesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_main_media_fragment);
        mMoviesAdapter = new RowSmallMoviesRecyclerAdapter(getContext(), mMoviesList);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

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
                       args.putInt(RentMovieDialog.STORE_ID, mStoreIDParam);

                RentMovieDialog dialog=new RentMovieDialog();
                                dialog.setArguments(args);
                                dialog.show(getFragmentManager(),"dialog");

            }
        });

        // SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                isSwipeOn = true;
                mProgressBar.setVisibility(View.GONE);
                loadMovies();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mMoviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){

                int visibleItemCount =   gridLayoutManager.getChildCount();
                int totalItemCount =     gridLayoutManager.getItemCount();
                int firstVisibleItem =   gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && !pagesOver) {

                    FrameLayout.LayoutParams  layoutParams=(FrameLayout.LayoutParams)mProgressBar.getLayoutParams();
                                              layoutParams.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                    mProgressBar.setLayoutParams(layoutParams);

                    mProgressBar.setVisibility(View.VISIBLE);
                    loadMovies();
                    loading = true;
                }

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mMoviesAdapter.notifyDataSetChanged();
      }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentLoaded && NetworkConnection.isConnected(getContext())) {
            isFragmentLoaded = true;
            loadMovies();
        }

//        if (!isFragmentLoaded && !NetworkConnection.isConnected(getContext())) {
//            mConnectivitySnackbar = Snackbar.make(getActivity().findViewById(R.id.fragment_container_main),"No Network", Snackbar.LENGTH_INDEFINITE);
//            mConnectivitySnackbar.show();
//            mConnectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
//                @Override
//                public void onNetworkConnectionConnected() {
//                    mConnectivitySnackbar.dismiss();
//                    isFragmentLoaded = true;
//                    loadMovies();
//                    isBroadcastReceiverRegistered = false;
//                    getActivity().unregisterReceiver(mConnectivityBroadcastReceiver);
//                }
//            });
//            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//            isBroadcastReceiverRegistered = true;
//            getActivity().registerReceiver(mConnectivityBroadcastReceiver, intentFilter);
//        } else if (!isFragmentLoaded && NetworkConnection.isConnected(getContext())) {
//            isFragmentLoaded = true;
//            loadMovies();
//        }
    }




    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.gener_fillter:
                generFilterAlertDialog();
                break;
            case R.id.year_filler:
                yearFilterAlertDialog();
                break;
            default:
        }
        return true;
    };


    private void loadMovies() {
        if (pagesOver) return;

        if(!mMovieSearchParam.isEmpty()) mMoviesListCall = ApiClient.getRetrofitApi().getSearchMovies(presentPage,mMovieSearchParam,mMovieTypeParam,mGener,mYear,null);
        else if(mStoreIDParam!=-1)       mMoviesListCall = ApiClient.getRetrofitApi().getStoreMovies(presentPage,mStoreIDParam,mMovieTypeParam,mGener,mYear,null);
                              else       mMoviesListCall = ApiClient.getRetrofitApi().getMoviesList(presentPage,mMovieTypeParam,mGener,mYear,null);


        mMoviesListCall.enqueue(new Callback<MoviesResponsePaging>() {
            @Override
            public void onResponse(Call<MoviesResponsePaging> call, Response<MoviesResponsePaging> response) {
                if (!response.isSuccessful()) {
                    mConnectivitySnackbar = Snackbar.make(mSnackbarViewID,
                            R.string.snackbar_no_network,
                            Snackbar.LENGTH_INDEFINITE)
                            .setBehavior(new SnackbarNoSwipeBehavior())
                            .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadMovies();
                                }
                            });
                    mConnectivitySnackbar.show();
                    return;
                }

                if(isSwipeOn){
                    mMoviesList.clear();
                    //mMoviesAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    isSwipeOn=false;
                }
                mProgressBar.setVisibility(View.GONE);
                mTextViewNoContent.setVisibility(View.GONE);

                if(response.code()==204 && presentPage==1) {
                    mTextViewNoContent.setVisibility(View.VISIBLE);
                    return;
                }
                if(response.code()==200) {
                       if (response.body() == null) return;
                       if (response.body().getPaging() == null) return;
                       if (response.body().getMessage() != null) return;
                       if (response.body().getResults() == null) return;

                for (Movies movieBrief : response.body().getResults()) {
                    if (movieBrief != null && movieBrief.getPoster() != null)
                         mMoviesList.add(movieBrief);
                }

                if(mMoviesList.isEmpty() && presentPage==1) {
                    mTextViewNoContent.setVisibility(View.VISIBLE);
                    return;
                }

                //mMoviesList.addAll(response.body().getResults());
                mMoviesAdapter.notifyDataSetChanged();

                if (response.body().getPaging().getPage() == response.body().getPaging().getTotalPages())
                    pagesOver = true;
                else
                    presentPage++;}
            }

            @Override
            public void onFailure(Call<MoviesResponsePaging> call, Throwable t) {
                mConnectivitySnackbar = Snackbar.make(mSnackbarViewID,
                        R.string.snackbar_server_error,
                        Snackbar.LENGTH_INDEFINITE)
                        .setBehavior(new SnackbarNoSwipeBehavior())
                        .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadMovies();
                            }
                        });
                mConnectivitySnackbar.show();

                //Toast.makeText(getContext(),"Unable to connect with server",Toast.LENGTH_SHORT).show();
                  //Log.d("TAG", t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Movie_Detail_Activity.POSITION_ACTIVITY_RETURN) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Movie_Detail_Activity.ADAPTER_POSITION, 1);
                mMoviesAdapter.notifyItemChanged(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMoviesListCall != null)    mMoviesListCall.cancel();
    }


    private void generFilterAlertDialog() {

        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getContext());
        alertDialog.setTitle("Gener");

        alertDialog.setMultiChoiceItems(GenresList, GenrescheckedList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

            }

      });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), "NO Network", Toast.LENGTH_SHORT).show();
                    return;
                }
                String gener ="";

                for (int i =0 ;GenrescheckedList.length>i;i++){
                    if(GenrescheckedList[i]) gener +="'" +(GenresList[i])+"',";
                }
                if (gener.endsWith(",")) gener = gener.substring(0, gener.length()-1);

                Toast.makeText(getContext(), gener , Toast.LENGTH_LONG).show();

                mGener=gener;
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mMoviesList.clear();
                mMoviesAdapter.notifyDataSetChanged();
                loadMovies();
            }
        });
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void yearFilterAlertDialog() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getContext());
        alertDialog.setTitle("Year");

        String[] items = {"2024","2023","2022","all","2021","2020","2019","2018","2017","2018","2017","2018","2016","2015","2014","2013","2012","2000","2000"
                ,"2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000"
                ,"2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000","2000"};

        alertDialog.setSingleChoiceItems(items, YearFiltercheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), "NO Network", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), items[i] , Toast.LENGTH_SHORT).show();
                mYear=items[i];
                YearFiltercheckedItem = i;
                dialogInterface.dismiss();
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mMoviesList.clear();
                mMoviesAdapter.notifyDataSetChanged();
                loadMovies();
            }
        });


        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.show();
    }


}