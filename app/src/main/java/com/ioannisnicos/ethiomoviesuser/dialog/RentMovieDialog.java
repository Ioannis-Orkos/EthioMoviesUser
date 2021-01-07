package com.ioannisnicos.ethiomoviesuser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.adapter.RentMovieAdapter;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.QuerryResponse;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.StoresSubscriptionResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentMovieDialog  extends AppCompatDialogFragment {

    public static final String MOVIE_ID      ="movieidParam";
    public static final String MOVIE_TIILE   ="movieTitle";
    public static final String USER_ID       ="useridParam";
    public static final String STORE_ID      ="storeidParam";


    private String      mMovieidParam ;
    private String      mMovietitleParam ;
    private String      mUseridParam ;
    private int         mStoreidParam ;

    private TextView    mTextviewNoContent;

    private List<StoresAdditionalStatus> mRentUserStoresRentStatus;
    private List<Stores>                 mRentUsersStores;
    private RentMovieAdapter mRentMovieAdapter;
    private RecyclerView     mRentMovieRecyclerView;

    private Call<StoresSubscriptionResponse> mMovieRelatedStoreMoviesCall;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieidParam = getArguments().getString(RentMovieDialog.MOVIE_ID);
            mMovietitleParam = getArguments().getString(RentMovieDialog.MOVIE_TIILE);
            mUseridParam = getArguments().getString(RentMovieDialog.USER_ID);
            mStoreidParam = getArguments().getInt(RentMovieDialog.STORE_ID,-1);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rent_movies, null);
        builder.setView(view);


        mTextviewNoContent = view.findViewById(R.id.textView_movies_transaction_d_rent_movie);


        mRentUsersStores = new ArrayList<>();
        mRentUserStoresRentStatus = new ArrayList<>();
        LoadMovieRelatedStores();


        mRentMovieRecyclerView = view.findViewById(R.id.recycler_view_rent_movie);


        view.findViewById(R.id.floating_action_button_closedialog_rent_movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                }
        });

        mRentMovieAdapter = new RentMovieAdapter(getContext(),mRentUsersStores,mRentUserStoresRentStatus );
        mRentMovieRecyclerView.setAdapter(mRentMovieAdapter);
        mRentMovieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRentMovieRecyclerView.setHasFixedSize(true);
        mRentMovieAdapter.setOnItemClickListener(new RentMovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,String msg) {
                if(mRentUserStoresRentStatus.get(position).getMovie_status().equals("unknown") && mRentUserStoresRentStatus.get(position).getMovie_available().equals("no"))    {
                    mRentMovieAdapter.getHolder(position).mProgressBar.setVisibility(View.VISIBLE);
                    mRentMovieAdapter.getHolder(position).rentButton.setEnabled(false);
                    RequestMovie(mMovieidParam,mUseridParam,mRentUserStoresRentStatus.get(position).getId(),position,msg);
                }
                else if(mRentUserStoresRentStatus.get(position).getMovie_status().equals("unknown") && !mRentUserStoresRentStatus.get(position).getMovie_available().equals("no"))   {
                    //Toast.makeText(getContext(),"Rent Movie send notfication",Toast.LENGTH_LONG).show();
                    mRentMovieAdapter.getHolder(position).mProgressBar.setVisibility(View.VISIBLE);
                    mRentMovieAdapter.getHolder(position).rentButton.setEnabled(false);
                    RequestMovie(mMovieidParam,mUseridParam,mRentUserStoresRentStatus.get(position).getId(),position,msg);
                    //LoadMovieDetail(position,"Rent");
                }
                else if( mRentUserStoresRentStatus.get(position).getMovie_status().equals("pending") )    {
                    mRentMovieAdapter.getHolder(position).mProgressBar.setVisibility(View.VISIBLE);
                    mRentMovieAdapter.getHolder(position).rentButton.setEnabled(false);



                    RequestMovieDel(mMovieidParam,mUseridParam,mRentUserStoresRentStatus.get(position).getId(),position);
                }
            }
        });


        builder.setView(view)
                .setTitle("Request "+mMovietitleParam+" From")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        if(window != null){ // After the window is created, get the SoftInputMode
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }

    private void LoadMovieRelatedStores(){

        mMovieRelatedStoreMoviesCall = ApiClient.getRetrofitApi().getMoviesForRent(mUseridParam,mMovieidParam,mStoreidParam);

        mMovieRelatedStoreMoviesCall.enqueue(new Callback<StoresSubscriptionResponse>() {
            @Override
            public void onResponse(Call<StoresSubscriptionResponse> call, Response<StoresSubscriptionResponse> response) {
                if(response.code() == 404) return;
                if (!response.isSuccessful()) {
                    mMovieRelatedStoreMoviesCall = call.clone();
                    mMovieRelatedStoreMoviesCall.enqueue(this);
                    return;
                }
                if(response.code()==204){
                    mTextviewNoContent.setVisibility(View.VISIBLE);
                    return;
                }


                if (!(response.body() == null)) {

                    mRentUsersStores.addAll(response.body().getStores_list());
                    mRentUserStoresRentStatus.addAll(response.body().getStores_list_status());
                    mRentMovieAdapter.notifyDataSetChanged();

                } else {
                    mRentUsersStores.clear();
                    mRentUserStoresRentStatus.clear();
                    mRentMovieAdapter.notifyDataSetChanged();

                }

            }
            @Override
            public void onFailure(Call<StoresSubscriptionResponse>call, Throwable t) {
                Log.d("yo",t.toString());
            }
        });
    }

    private void RequestMovie(String movieId,String userGId,int storeId,int position,String msg){

        Call<QuerryResponse>  mRequestMovieMoviesCall = ApiClient.getRetrofitApi().requestMovieRent(movieId,userGId,storeId,msg);

        mRequestMovieMoviesCall.enqueue(new Callback<QuerryResponse>() {
            @Override
            public void onResponse(Call<QuerryResponse> call, Response<QuerryResponse> response) {
                if (!response.isSuccessful()) {

                }
                mRentMovieAdapter.getHolder(position).mProgressBar.setVisibility(View.GONE);
                mRentMovieAdapter.getHolder(position).rentButton.setEnabled(true);
                closeKeyboard();

                //if(mMovieRelatedStores.get(position).getMovie_status()== null )
                mRentUserStoresRentStatus.get(position).setMovie_status("pending") ;
                mRentUserStoresRentStatus.get(position).setReq_user_msg(msg);
                mRentMovieAdapter.notifyItemChanged(position);
                closeKeyboard();

            }

            @Override
            public void onFailure(Call<QuerryResponse>call, Throwable t) {
                //Log.d("yo",t.toString());
            }
        });
    }

    private void RequestMovieDel(String movieId,String userGId,int storeId,int position){

        Call<QuerryResponse>  mRequestMovieMoviesCall = ApiClient.getRetrofitApi().cancelMovieRequestRent(movieId,userGId,storeId);

        mRequestMovieMoviesCall.enqueue(new Callback<QuerryResponse>() {
            @Override
            public void onResponse(Call<QuerryResponse> call, Response<QuerryResponse> response) {
                if (!response.isSuccessful()) {
                }

                mRentMovieAdapter.getHolder(position).mProgressBar.setVisibility(View.GONE);
                mRentMovieAdapter.getHolder(position).rentButton.setEnabled(true);
                //if(mMovieRelatedStores.get(position).getMovie_status()== null )
                mRentUserStoresRentStatus.get(position).setMovie_status("unknown") ;
                mRentUserStoresRentStatus.get(position).setReq_user_msg(""); ;
                mRentMovieAdapter.notifyItemChanged(position);
                //mRentMovieAdapter.getHolder(position).textReqMesg.setEnabled(false);
                //mRentMovieAdapter.getHolder(position).textReqMesg.setText("");
                mRentMovieAdapter.getHolder(position).textReqMesg.setEnabled(false);

            }

            @Override
            public void onFailure(Call<QuerryResponse>call, Throwable t) {
                //Log.d("yo",t.toString());
            }
        });

    }

    private void closeKeyboard() {
        View view = getDialog().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
