package com.ioannisnicos.ethiomoviesuser.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.ioannisnicos.ethiomoviesuser.Tvshow_Detail_Activity;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallMoviesRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallTvshowsRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.dialog.RentMovieDialog;
import com.ioannisnicos.ethiomoviesuser.dialog.RentTvshowDialog;
import com.ioannisnicos.ethiomoviesuser.models.Movies;
import com.ioannisnicos.ethiomoviesuser.models.Tvshow;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;
import com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.TvshowsResponsePaging;
import com.ioannisnicos.ethiomoviesuser.utils.ConnectivityBroadcastReceiver;
import com.ioannisnicos.ethiomoviesuser.utils.NetworkConnection;
import com.ioannisnicos.ethiomoviesuser.utils.SnackbarNoSwipeBehavior;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ioannisnicos.ethiomoviesuser.fragments.MoviesFragment.MOVIE_SEARCH;


public class TvshowsFragment extends Fragment {

    public static final String TVSHOW_STORE_PARAM = "storeParam";
    public static final String TVSHOW_TYPE_PARAM =  "tvshowTypeParam";
    public static final String MOVIE_SEARCH =      "movieSearchParam";

    private int      mStoreIDParam   = -1;
    private String mTvshowTypeParam =  "";
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


    private Call<TvshowsResponsePaging> mTvshowsListCall;
    private List<Tvshow> mTvshowsList;

    private SwipeRefreshLayout              mSwipeRefreshLayout;
    private boolean                         isSwipeOn = false;
    private RecyclerView                    mTvshowsRecyclerView;
    private RowSmallTvshowsRecyclerAdapter  mTvshowsAdapter;


    private   boolean[] GenrescheckedList = {false, false, false,false, false, false,false, false, false,false, false,
            false,false,false, false,false, false, false,false, false, false,false,
            false, false,false};

    private String[] GenresList = {"comedy","short","drama","history","war","romance","action","western","fantasy",
            "horror","science-fiction","documentary","adventure","family","crime","music",
            "thriller","animation","mystery","musical","holiday","anime","superhero","suspense",
            "tv-movie" };

    private String mGener="all";
    private String mYear="all";
    int YearFiltercheckedItem = 2;



    public static TvshowsFragment newInstance(int paramStore, String paramType,String paramSearch){
        TvshowsFragment fragment = new TvshowsFragment();
        Bundle args = new Bundle();
        args.putInt(TVSHOW_STORE_PARAM, paramStore);
        args.putString(TVSHOW_TYPE_PARAM, paramType);
        args.putString(MOVIE_SEARCH, paramSearch);
        fragment.setArguments(args);

        return fragment;
    }

    public TvshowsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStoreIDParam   = getArguments().getInt(TVSHOW_STORE_PARAM,-1);
            mTvshowTypeParam = getArguments().getString(TVSHOW_TYPE_PARAM,"");
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
        if(presentPage<=1)  mTvshowsList = new ArrayList<Tvshow>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_main_fragment_media);
        mTvshowsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_main_media_fragment);
        mTvshowsAdapter = new RowSmallTvshowsRecyclerAdapter(getContext(), mTvshowsList);
        mTvshowsRecyclerView.setAdapter(mTvshowsAdapter);


        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mTvshowsRecyclerView.setLayoutManager(gridLayoutManager);

        mTvshowsAdapter.setOnBKItemClickListener(new RowSmallTvshowsRecyclerAdapter.OnBKItemClickListener() {
            @Override
            public void onBKItemClick(int position) {
                Intent intent = new Intent(getContext(), Tvshow_Detail_Activity.class);
                intent.putExtra(Tvshow_Detail_Activity.MOVIE_ID, mTvshowsList.get(position).getImdb_id());
                intent.putExtra(Tvshow_Detail_Activity.MOVIE_TITLE, mTvshowsList.get(position).getTitle());
                intent.putExtra(Tvshow_Detail_Activity.ADAPTER_POSITION, position);
                startActivityForResult(intent,  Tvshow_Detail_Activity.POSITION_ACTIVITY_RETURN);
            }
        });

        mTvshowsAdapter.setRequestStoreClickListener(new RowSmallTvshowsRecyclerAdapter.RequestTvshowClickListener() {
            @Override
            public void onStoreItemClick(int position) {
                Bundle args = new Bundle();
                args.putString(RentTvshowDialog.USER_ID,mMyGoogleAccountInfo.getId());
                args.putString(RentTvshowDialog.TVSHOW_ID, mTvshowsList.get(position).getImdb_id());
                args.putString(RentTvshowDialog.TVSHOW_TIILE, mTvshowsList.get(position).getTitle());
                args.putInt(RentTvshowDialog.STORE_ID, mStoreIDParam);

                RentTvshowDialog dialog=new RentTvshowDialog();
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


        mTvshowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
        mTvshowsAdapter.notifyDataSetChanged();
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

        if(!mMovieSearchParam.isEmpty()) mTvshowsListCall = ApiClient.getRetrofitApi().getSearchTvshows(presentPage,mMovieSearchParam,mTvshowTypeParam,mGener,mYear,null);
        else if (mStoreIDParam!=-1)   mTvshowsListCall = ApiClient.getRetrofitApi().getStoreTvshows(presentPage,mStoreIDParam, mTvshowTypeParam,mGener,mYear,null);
                        else    mTvshowsListCall = ApiClient.getRetrofitApi().getTvshowList(presentPage, mTvshowTypeParam,mGener,mYear,null);


        mTvshowsListCall.enqueue(new Callback<TvshowsResponsePaging>() {
            @Override
            public void onResponse(Call<TvshowsResponsePaging> call, Response<TvshowsResponsePaging> response) {
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
                    mTvshowsList.clear();
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

                    for (Tvshow movieBrief : response.body().getResults()) {
                        if (movieBrief != null && movieBrief.getPoster() != null)
                            mTvshowsList.add(movieBrief);
                    }

                    if(mTvshowsList.isEmpty() && presentPage==1) {
                        mTextViewNoContent.setVisibility(View.VISIBLE);
                        return;
                    }

                    //mMoviesList.addAll(response.body().getResults());
                    mTvshowsAdapter.notifyDataSetChanged();

                    if (response.body().getPaging().getPage() == response.body().getPaging().getTotalPages())
                        pagesOver = true;
                    else
                        presentPage++;}
            }

            @Override
            public void onFailure(Call<TvshowsResponsePaging> call, Throwable t) {
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
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == Movie_Detail_Activity.POSITION_ACTIVITY_RETURN) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Movie_Detail_Activity.ADAPTER_POSITION, 1);
                mTvshowsAdapter.notifyItemChanged(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTvshowsListCall != null)    mTvshowsListCall.cancel();
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
                mTvshowsList.clear();
                mTvshowsAdapter.notifyDataSetChanged();
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

        String[] items = {"2022","all","2021","2020","2019","2018","2017","2018","2017","2018","2016","2015","2014","2013","2012","2000","2000"
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
                mTvshowsList.clear();
                mTvshowsAdapter.notifyDataSetChanged();
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