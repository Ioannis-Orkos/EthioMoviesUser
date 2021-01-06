package com.ioannisnicos.ethiomoviesuser;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ioannisnicos.ethiomoviesuser.adapter.SubscriptionsRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.QuerryResponse;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.StoresSubscriptionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private final static String TAG = SubscriptionsActivity.class.getSimpleName();

    private String myGoogleId = "";

    //private StoresSubscriptionResponse mUserSubs;
    private List<StoresAdditionalStatus> mUserStoresSubsStatus;
    private List<Stores>                   mUsersStores;

    private Call<StoresSubscriptionResponse> mMyStoreMoviesCall;
    private Call<QuerryResponse>             mUnsubscribeStoreCall;

    private SubscriptionsRecyclerAdapter mUserSubsAdapter;
    private RecyclerView                      mUserSubsRecyclerView;

    private FloatingActionButton addStoreFButton;

    private FusedLocationProviderClient mLocationProviderClient;
    private LocationRequest             mLocationRequest;
    private LocationCallback            mLocationCallback;
    private boolean                     mLocationNotSet  = false;
    private final static int MY_PERMISSION_FINE_LOCATION =  101;

    private List<StoresAdditionalStatus> mNearUserStoresSubsStatus;
    private List<Stores>                 mNearUsersStores;

    private Call<StoresSubscriptionResponse> mNearMyStoreMoviesCall;

    private SubscriptionsRecyclerAdapter mNearUserSubsAdapter;
    private RecyclerView mNearUserSubsRecyclerView;

    private AlertDialog addSubscriptionDialog;
    //private AddSubscriptionDialog addSubscriptionDialog;

    private ProgressBar mProgressBar;

    private ProgressBar mProgressBarList;
    private TextView mTextviewNoContent;

    private ProgressBar mProgressBarListNewSub;
    private TextView mTextviewNoContentNewSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        mProgressBarList = findViewById(R.id.progressBar_list_subscriptions);
        mTextviewNoContent = findViewById(R.id.textView_movies_transaction_fragment);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subscriptions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Subscription");

        GoogleSignInAccount googleAccountInfo = GoogleSignIn.getLastSignedInAccount(SubscriptionsActivity.this);
        if (googleAccountInfo != null) myGoogleId = googleAccountInfo.getId();
        else finish();

        intLocation();

        intUserSubsAdapter();
        LoadUserSubscribedStores();

        addStoreFButton = findViewById(R.id.floating_action_button_add_subscription);
        addStoreFButton.setOnClickListener(view -> requestLocationPermission());

        mProgressBar = findViewById(R.id.progressBar_subscriptions);
        mProgressBar.setLayoutParams(addStoreFButton.getLayoutParams());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()== android.R.id.home){
                onBackPressed();}
        return true;
    }



    private void intUserSubsAdapter() {

        //mUserSubs = new StoresSubscriptionResponse(new ArrayList<>(),new ArrayList<>(),null);
        mUsersStores = new ArrayList<>();
        mUserStoresSubsStatus = new ArrayList<>();

        mUserSubsRecyclerView = findViewById(R.id.recycler_view_user_subscriptions);

        mUserSubsAdapter = new SubscriptionsRecyclerAdapter(this, mUsersStores,mUserStoresSubsStatus);
        mUserSubsRecyclerView.setAdapter(mUserSubsAdapter);
        mUserSubsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserSubsRecyclerView.setHasFixedSize(true);

        mUserSubsAdapter.setOnSubscribeButtonClickListener(
                (position -> {
                    //delete
                    //onMyItemClickMain(position);
                    UnsubscribeStore(myGoogleId, mUsersStores.get(position).getId(), position);

                }));

        mUserSubsAdapter.setOnSubscriptionBKClickListener
                (position -> {
                    //Toast.makeText(this, "open acctivity " + position, Toast.LENGTH_SHORT).show();
                    startActivity(StoreMediaPagerActivity.newInstance(SubscriptionsActivity.this, mUserStoresSubsStatus.get(position).getId()));
                });
    }

    private void launchAddSubscriptionDialog(Location location){

        mNearUsersStores = new ArrayList<>();
        mNearUserStoresSubsStatus = new ArrayList<>();

        LoadNearUserStores(location.getLatitude(),location.getLongitude());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_subscribe_subscription_activity, null);
        builder.setView(view);

        mProgressBarListNewSub = view.findViewById(R.id.progressBar_movies_transaction_d_new_subscriptions);
        mTextviewNoContentNewSub = view.findViewById(R.id.textView_movies_transaction_d_new_subscriptions);



        mNearUserSubsRecyclerView = view.findViewById(R.id.recycler_view_new_subscriptions);

        mNearUserSubsAdapter = new SubscriptionsRecyclerAdapter(this, mNearUsersStores,mNearUserStoresSubsStatus);
        mNearUserSubsRecyclerView.setAdapter(mNearUserSubsAdapter);
        mNearUserSubsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNearUserSubsRecyclerView.setHasFixedSize(true);

        mNearUserSubsAdapter.setOnSubscribeButtonClickListener(
                (position -> {
                    builder.setCancelable(false);
                    //delete
                    if( mNearUserStoresSubsStatus.get(position).getSubscription_status()==null)
                        SubscriptionsActivity.this.subscribeStore(myGoogleId,mNearUserStoresSubsStatus.get(position).getId(),position);
                    else SubscriptionsActivity.this.UnsubscribeStore(myGoogleId,mNearUserStoresSubsStatus.get(position).getId(),position);

                }));
        mNearUserSubsAdapter.setOnSubscriptionBKClickListener
                (position -> {
                    startActivity(StoreMediaPagerActivity.newInstance(SubscriptionsActivity.this, mNearUserStoresSubsStatus.get(position).getId()));
                });

        FloatingActionButton closeDialog;
        closeDialog = view.findViewById(R.id.floating_action_button_closedialog_new_subscription);
        closeDialog.setOnClickListener(view1 -> {addSubscriptionDialog.dismiss();});

        builder.setView(view);
        addSubscriptionDialog = builder.create();
        addSubscriptionDialog.setCanceledOnTouchOutside(true);
        //addSubscriptionDialog.show();

        addSubscriptionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mNearUsersStores.clear();
                mNearUserStoresSubsStatus.clear();
            }
        });

        //--------------------------------



    }



    private void intLocation() {

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()) {
                    Log.i(TAG, "Location is available");
                } else {
                    Log.i(TAG, "Location is unavailable");
                }
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "Location result is available" + locationResult.getLocations().toString());

                if(mLocationNotSet) {
                    List<Location> locationList = locationResult.getLocations();
                    if (!locationList.isEmpty()) {
                        //The last location in the list is the newest
                        Location location = locationList.get(locationList.size() - 1);
                        Toast.makeText(SubscriptionsActivity.this, location.getLatitude() + "  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        launchAddSubscriptionDialog(location);
                        mLocationProviderClient.removeLocationUpdates(mLocationCallback);
                        mLocationNotSet=false;

                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mLocationProviderClient != null) {
            mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void setLocation() {

        if (isGPSEnabled(SubscriptionsActivity.this) == false) {
            Toast.makeText(SubscriptionsActivity.this,"Please enable the device Location", Toast.LENGTH_SHORT).show();
            return;}
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;}

        mLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, SubscriptionsActivity.this.getMainLooper());
        mLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                addStoreFButton.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                //wait function

                if(location==null) {
                    //Toast.makeText(SubscriptionsActivity.this,"You just enable the location wait for a bit and try again letter", Toast.LENGTH_LONG).show();
                    mLocationNotSet = true;
                    return;}

                if(!mLocationNotSet) {
                    launchAddSubscriptionDialog(location);
                    mLocationProviderClient.removeLocationUpdates(mLocationCallback);
                }

            }
        });

        mLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("yo", "Exception while getting the location: "+e.getMessage());
            }
        });
    }





    private void LoadNearUserStores(double lat,double lng) {

        mNearMyStoreMoviesCall = ApiClient.getRetrofitApi().getNearStores(myGoogleId,lat,lng);

        mNearMyStoreMoviesCall.enqueue(new Callback<StoresSubscriptionResponse>() {
            @Override
            public void onResponse(Call<StoresSubscriptionResponse> call, Response<StoresSubscriptionResponse> response) {

                if(response.code() == 404) return;
                if (!response.isSuccessful()) {
                    mNearMyStoreMoviesCall = call.clone();
                    mNearMyStoreMoviesCall.enqueue(this);

                    addStoreFButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);

                    return;
                }

                mProgressBarListNewSub.setVisibility(View.GONE);
                if(response.code()==204 ){
                    mTextviewNoContentNewSub.setVisibility(View.VISIBLE);
                    //return;
                }


                if (!(response.body() == null)) {
                    addStoreFButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    addSubscriptionDialog.show();

                    mNearUsersStores.addAll(response.body().getStores_list());
                    mNearUserStoresSubsStatus.addAll(response.body().getStores_list_status());
                    if(addSubscriptionDialog!=null)
                    mNearUserSubsAdapter.notifyDataSetChanged();
                } else {
                    mNearUsersStores.clear();
                    mNearUserStoresSubsStatus.clear();
                    mNearUserSubsAdapter.notifyDataSetChanged();
                    addSubscriptionDialog.show();
                    addStoreFButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<StoresSubscriptionResponse> call, Throwable t) {
                addStoreFButton.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, t.toString());
            }
        });
    }

    private void LoadUserSubscribedStores() {

        mMyStoreMoviesCall = ApiClient.getRetrofitApi().getUserSubs(myGoogleId);

        mMyStoreMoviesCall.enqueue(new Callback<StoresSubscriptionResponse>() {
            @Override
            public void onResponse(Call<StoresSubscriptionResponse> call, Response<StoresSubscriptionResponse> response) {

                if(response.code() == 404) return;
                if (!response.isSuccessful()) {
                    mMyStoreMoviesCall = call.clone();
                    mMyStoreMoviesCall.enqueue(this);
                    return;
                }

                mProgressBarList.setVisibility(View.GONE);
                if(response.code()==204 ){
                    mTextviewNoContent.setVisibility(View.VISIBLE);
                    return;
                }

                if (!(response.body() == null)) {

                    mUsersStores.addAll(response.body().getStores_list());
                    mUserStoresSubsStatus.addAll(response.body().getStores_list_status());
                    mUserSubsAdapter.notifyDataSetChanged();
                } else {
                    mUsersStores.clear();
                    mUserStoresSubsStatus.clear();
                    mUserSubsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<StoresSubscriptionResponse> call, Throwable t) {
                Log.d("yo", t.toString());
            }
        });
    }

    private void UnsubscribeStore(String userGId, int storeId, int position) {

        mUnsubscribeStoreCall = ApiClient.getRetrofitApi().store_unsubscribe(userGId, storeId);

        mUnsubscribeStoreCall.enqueue(new Callback<QuerryResponse>() {
            @Override
            public void onResponse(Call<QuerryResponse> call, Response<QuerryResponse> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                int position2 = -1;

                if(mUserStoresSubsStatus != null) {
                    for(int i=0;i<mUserStoresSubsStatus.size();i++){
                        if(mUserStoresSubsStatus.get(i).getId()==storeId) {position2 = i;
                        break;}
                    }

                    if (mUserStoresSubsStatus.get(position2).getSubscription_status().equals("subscribed"))
                    { mUsersStores.remove(position2);
                        mUserStoresSubsStatus.remove(position2);}
                    else if (mUserStoresSubsStatus.get(position2).getSubscription_status().equals("request"))
                    { mUsersStores.remove(position2);
                        mUserStoresSubsStatus.remove(position2);}
                    mUserSubsAdapter.notifyItemRemoved(position2);
                    }


                if(mNearUserStoresSubsStatus != null && (mNearUserStoresSubsStatus.size() > 0) ) {
                    mNearUserStoresSubsStatus.get(position).setSubscription_status(null);
                    mNearUserSubsAdapter.notifyItemChanged(position);
                    //addSubscriptionDialog.setCancelable(true);
                }
            }

            @Override
            public void onFailure(Call<QuerryResponse> call, Throwable t) {
                Log.d("yo", t.toString());
            }
        });

        return;
    }

    private void subscribeStore(String userGId, int storeId, int position) {

        Call<QuerryResponse> SubscribeStoreCall = ApiClient.getRetrofitApi().store_subscribe(userGId, storeId);

        SubscribeStoreCall.enqueue(new Callback<QuerryResponse>() {
            @Override
            public void onResponse(Call<QuerryResponse> call, Response<QuerryResponse> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                if(addSubscriptionDialog !=null) {
                    mNearUserStoresSubsStatus.get(position).setSubscription_status("request");
                    mNearUserSubsAdapter.notifyItemChanged(position);
                    if(mUsersStores!=null) {
                        mUsersStores.add(mNearUsersStores.get(position));
                        mUserStoresSubsStatus.add(mNearUserStoresSubsStatus.get(position));
                        mUserSubsAdapter.notifyItemInserted(mUsersStores.size());
                        mTextviewNoContent.setVisibility(View.GONE);
                        //addSubscriptionDialog.setCancelable(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<QuerryResponse> call, Throwable t) {
                Log.d("yo", t.toString());
            }
        });

        return;
    }




    private void requestLocationPermission() {
        String[] perms ={ Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)){
            setLocation();
        }
        else{
            EasyPermissions.requestPermissions(this, "We need permissions to access near by rental stores",
                    MY_PERMISSION_FINE_LOCATION, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if( requestCode == MY_PERMISSION_FINE_LOCATION) setLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder b =  new AppSettingsDialog.Builder(this);
            b.build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void finishAfterTransition() {
        if(mMyStoreMoviesCall !=null)         mMyStoreMoviesCall.cancel();
        if(mUnsubscribeStoreCall !=null)      mUnsubscribeStoreCall.cancel();
        if (mLocationProviderClient != null)  mLocationProviderClient.removeLocationUpdates(mLocationCallback);

        super.finishAfterTransition();
    }

    public boolean isGPSEnabled(Context mContext)    {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}