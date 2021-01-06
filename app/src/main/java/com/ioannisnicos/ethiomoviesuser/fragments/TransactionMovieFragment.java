package com.ioannisnicos.ethiomoviesuser.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ioannisnicos.ethiomoviesuser.Movie_Detail_Activity;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.adapter.RowSmallMoviesRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.adapter.TransactionsMovieRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.dialog.RentMovieDialog;
import com.ioannisnicos.ethiomoviesuser.models.Movies;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;

import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.TransactionMovieResponse;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.TransactionsResponsePaging;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.QuerryResponse;
import com.ioannisnicos.ethiomoviesuser.utils.ConnectivityBroadcastReceiver;
import com.ioannisnicos.ethiomoviesuser.utils.NetworkConnection;
import com.ioannisnicos.ethiomoviesuser.utils.SnackbarNoSwipeBehavior;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransactionMovieFragment extends Fragment {


    public static final String UserGID_PARAM = "userGIDParam";
    private             String mUserGID;

    private View            view;
    private ProgressBar     mProgressBar;
    private TextView        mTextViewNoContent;

    private boolean pagesOver = false;
    private int     presentPage = 1;
    private boolean loading = true;
    private int     previousTotal = 0;
    private int     visibleThreshold = 5;

    private Snackbar                         mConnectivitySnackbar;
    private View                             mSnackbarViewID;
    private boolean                          isFragmentLoaded;

    //Recycler view
    private SwipeRefreshLayout                       mSwipeRefreshLayout;
    private boolean                                  isSwipeOn = false;
    private RecyclerView                             mTransactionsRecyclerView;
    private TransactionsMovieRecyclerAdapter         mTransactionsAdapter;

    private Call<TransactionsResponsePaging>         mMovieRelatedStoreMoviesCall;
    private List<TransactionMovieResponse>           mTransactionList;

    private String requestStatus = "all";



    public static TransactionMovieFragment newInstance(String paramUserGID, String param2) {
        TransactionMovieFragment fragment = new TransactionMovieFragment();
        Bundle args = new Bundle();
                      args.putString(UserGID_PARAM, paramUserGID);
        fragment.setArguments(args);

     return fragment;
    }


    public TransactionMovieFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserGID = getArguments().getString(UserGID_PARAM);
         }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transaction_movie, container, false);
        mProgressBar = view.findViewById(R.id.progressBar_main_fragment_transaction);
        mTextViewNoContent = view.findViewById(R.id.textView_main_fragment_transaction_no_content);

        mSnackbarViewID = view.findViewById(R.id.frameLayout_main_fragment_transaction);

        mTransactionList = new ArrayList<TransactionMovieResponse>();
        intAdapter();

        FloatingActionButton fab = view.findViewById(R.id.floatingactionbutton_main_fragment_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.manage_media_fillter_menu, popup.getMenu());
                if (requestStatus.equals("all")) popup.getMenu().findItem(R.id.all).setVisible(false);
                if (requestStatus.equals("pending")) popup.getMenu().findItem(R.id.pending).setVisible(false);
                if (requestStatus.equals("accept")) popup.getMenu().findItem(R.id.accept).setVisible(false);
                if (requestStatus.equals("rejected")) popup.getMenu().findItem(R.id.reject).setVisible(false);
                popup.show();
                popup.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
            }
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_main_fragment_transaction);
        mTransactionsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_main_fragment_transaction);
        mTransactionsAdapter = new TransactionsMovieRecyclerAdapter(getContext(), mTransactionList);
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mTransactionsRecyclerView.setLayoutManager(linearLayoutManager);


        mTransactionsAdapter.setOnTransactionDelButtonClickListener(new TransactionsMovieRecyclerAdapter.OnTransactionDelButtonClickListener() {
            @Override
            public void onTransactionDelAcceptButtonClick(int position) {
                RequestMovieDel(mTransactionList.get(position).getMovies().getImdb_id(),
                        mTransactionList.get(position).getUsers().getGoogle_id(),
                        mTransactionList.get(position).getStores().getId(),
                        position);
            }
        });

        ///SwipeRefreshLayout
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


        mTransactionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount =   linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && !pagesOver) {

                    if(presentPage>1) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mProgressBar.getLayoutParams();
                        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        mProgressBar.setLayoutParams(layoutParams);
                    }

                    mProgressBar.setVisibility(View.VISIBLE);
                    loadMovies();
                    loading = true;
                }

            }
        });


    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.all:
                requestStatus="all";
                getActivity().setTitle("History - All Req. Movies");
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mTransactionList.clear();
                //mTransactionsAdapter.notifyDataSetChanged();
                loadMovies();
                break;
            case R.id.accept:
                requestStatus="accept";
                getActivity().setTitle("History - Accepted Movies");
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mTransactionList.clear();
                mTransactionsAdapter.notifyDataSetChanged();
                loadMovies();
                break;
            case R.id.reject:
                requestStatus="rejected";
                getActivity().setTitle("History - Rejected Movies");
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mTransactionList.clear();
                mTransactionsAdapter.notifyDataSetChanged();
                loadMovies();
                break;
            case R.id.pending:
                requestStatus="pending";
                getActivity().setTitle("History - Pending Movies");
                pagesOver = false;
                presentPage = 1;
                previousTotal = 0;
                mTransactionList.clear();
                mTransactionsAdapter.notifyDataSetChanged();
                loadMovies();
                break;
            default:
        }
        return true;
    };

    @Override
    public void onResume() {
        super.onResume();

        if (!isFragmentLoaded && NetworkConnection.isConnected(getContext())) {
            isFragmentLoaded = true;
            loadMovies();
        }
    }

    private void loadMovies() {
        if (pagesOver) return;

        GoogleSignInAccount mMyGoogleAccountInfo = GoogleSignIn.getLastSignedInAccount(getContext());

        mMovieRelatedStoreMoviesCall = ApiClient.getRetrofitApi().getUserTransactionListMovie(presentPage,mMyGoogleAccountInfo.getId(),requestStatus);

        mMovieRelatedStoreMoviesCall.enqueue(new Callback<TransactionsResponsePaging>() {
            @Override
            public void onResponse(Call<TransactionsResponsePaging> call, Response<TransactionsResponsePaging> response) {
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

                mProgressBar.setVisibility(View.GONE);
                if(response.code()==204 && presentPage==1){
                    mTextViewNoContent.setVisibility(View.VISIBLE);
                    return;
                }

                if(isSwipeOn){
                    mTransactionList.clear();
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
                    if (response.body().getResult_data() == null) return;
                    if (response.body().getPaging() == null) return;
                    if (response.body().getQuerryMessage() != null) return;

                    mTransactionList.addAll(response.body().getResult_data());

                    if(mTransactionList.isEmpty() && presentPage==1) {
                        mTextViewNoContent.setVisibility(View.VISIBLE);
                        return;
                    }

                    //mMoviesList.addAll(response.body().getResults());
                    mTransactionsAdapter.notifyDataSetChanged();

                    if (response.body().getPaging().getPage() == response.body().getPaging().getTotalPages())
                        pagesOver = true;
                    else
                        presentPage++;
                }

            }

            @Override
            public void onFailure(Call<TransactionsResponsePaging> call, Throwable t) {
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

    private void RequestMovieDel(String movieId, String userGId, int storeId, int position){


        Call<QuerryResponse> mRequestMovieMoviesCall = ApiClient.getRetrofitApi().cancelMovieRequestRent(movieId,userGId,storeId);

        mRequestMovieMoviesCall.enqueue(new Callback<QuerryResponse>() {
            @Override
            public void onResponse(Call<QuerryResponse> call, Response<QuerryResponse> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                //if(mMovieRelatedStores.get(position).getMovie_status()== null )

                mTransactionList.remove( position);
                mTransactionsAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Call<QuerryResponse> call, Throwable t) {
                Log.d("yo",t.toString());
            }
        });

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }




//    private void showAlertDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//        alertDialog.setTitle("AlertDialog");
//
//        String[] items = {"Ready","Pending","Rejected"};
//        int checkedItem = 1;
//        alertDialog.setSingleChoiceItems(items,checkedItem, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        mQuaryStatus = "ready";
//                       // dialog.dismiss();
//                        break;
//                    case 1:
//                        mQuaryStatus = "pending";
//                       // dialog.dismiss();
//                        break;
//                    case 2:
//                        mQuaryStatus ="rejected";
//                       // dialog.dismiss();
//                        break;
//                }
//
//            }});
//       /* alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//
//                        showAlertDialog2();
//                        dialog.dismiss();
//                        break;
//                    case 1:
//                        Toast.makeText(MainActivity.this, "Clicked on android", Toast.LENGTH_LONG).show();
//                        break;
//                    case 2:
//                        Toast.makeText(MainActivity.this, "Clicked on Data Structures", Toast.LENGTH_LONG).show();
//                        break;
//                }
//                drawer.closeDrawer(GravityCompat.START);
//            }
//
//        });*/
//
//        alertDialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                loading = false;
//                pagesOver = false;
//                presentPage = 1;
//                previousTotal = 0;
//                mTransationList.clear();
//                mTransationsAdapter.notifyDataSetChanged();
//                loadMovies();
//            }
//        });
//
//       alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        AlertDialog alert = alertDialog.create();
//        alert.setCanceledOnTouchOutside(true);
//        // alert.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
//
//        alert.show();
//
//
//    }







}
