package com.ioannisnicos.ethiomoviesuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.ioannisnicos.ethiomoviesuser.fragments.FavouritePagerFragment;
import com.ioannisnicos.ethiomoviesuser.fragments.MoviePagerFragment;
import com.ioannisnicos.ethiomoviesuser.fragments.TransactionPagerFragment;
import com.ioannisnicos.ethiomoviesuser.fragments.TvshowPagerFragment;
import com.ioannisnicos.ethiomoviesuser.models.Users;

import com.ioannisnicos.ethiomoviesuser.notification.MyAppsNotificationManager;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;
import com.ioannisnicos.ethiomoviesuser.utils.ConnectivityBroadcastReceiver;
import com.ioannisnicos.ethiomoviesuser.utils.NetworkConnection;
import com.ioannisnicos.ethiomoviesuser.utils.SnackbarNoSwipeBehavior;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG =  MainActivity.class.getSimpleName();

    private String notificaton = "";
    private Bundle mSavedInstanceState;

    //Navigation View
    private DrawerLayout        mDrawer;
    private NavigationView      navigationView;
        //Navigation Header
        private View            navHeader ;
        private static int      navItemIndex = 0;
        private TextView        txtNavHeaderDisp;
        private CircleImageView imgNavHeaderProfile;
        private ImageView       imgNavHeaderBg;

    //ToolBar
    private Toolbar mToolbar;

    // Google login views
    private GoogleSignInClient  mGoogleSignInClient;
    private GoogleSignInAccount mMyGoogleAccountInfo;

    //load user information from db
    private Call<Users>     getUserInfoCall;
    private Users           mUserInfo;




    //setting activity response code
    private final int REQUEST_CODE = 101;

    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;
    private Snackbar        mConnectivitySnackbar;

    private boolean doubleBackToExitPressedOnce;
    Toast mToastDoubleBackToExit          =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        setTitle(R.string.main_title);

        mSavedInstanceState = savedInstanceState;

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        navigationView = (NavigationView) findViewById(R.id.navigation_view_main);

        if (!NetworkConnection.isConnected(getApplicationContext())) {
            mConnectivitySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout_main),
                                                               R.string.snackbar_no_network,
                                                               Snackbar.LENGTH_INDEFINITE)
                                            .setBehavior(new SnackbarNoSwipeBehavior())
                                            .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            overridePendingTransition(0, 0);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                            startActivity(getIntent());
                                                            overridePendingTransition(0, 0);
                                                        }
                                                    });
            mConnectivitySnackbar.show();

        }else {


            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                notificaton =  extra.getString(MyAppsNotificationManager.NOTIFICATION_ID);
            }

            mToastDoubleBackToExit = Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT);

            intNavigation();
            intGoogle();


        }
    }


    public void intGoogle(){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    public void intNavigation(){


        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                                             R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        navHeader = navigationView.getHeaderView(navItemIndex);
        ImageView imgNavHeaderBg;

        txtNavHeaderDisp =(TextView)  navHeader.findViewById(R.id.textview_navigation_main_profile_disname);
        //imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img__bg_disname_nav_header);
        imgNavHeaderProfile = (CircleImageView) navHeader.findViewById(R.id.circleImageView_navigation_main_profile);


        mMyGoogleAccountInfo = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (mMyGoogleAccountInfo != null) {
            String personName =       mMyGoogleAccountInfo.getDisplayName();
            String personGivenName =  mMyGoogleAccountInfo.getGivenName();
            String personFamilyName = mMyGoogleAccountInfo.getFamilyName();
            String personEmail =      mMyGoogleAccountInfo.getEmail();
            String personId =         mMyGoogleAccountInfo.getId();
            Uri personPhoto =         mMyGoogleAccountInfo.getPhotoUrl();

            txtNavHeaderDisp.setText(personName);
            // Loading profile image
            Glide.with(this).load(personPhoto).into(imgNavHeaderProfile);

            getUserInfo(mMyGoogleAccountInfo.getId(),true);
        }

        // loading header background image
        //  Glide.with(this).load(urlNavHeaderBg).into(imgNavHeaderBg);

    }




    private void getUserInfo(String userGID,boolean first){

        getUserInfoCall = ApiClient.getRetrofitApi().getUserInfo(userGID);

        getUserInfoCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {

                if (!response.isSuccessful()) {
                    mConnectivitySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout_main),
                            R.string.snackbar_no_network,
                            Snackbar.LENGTH_INDEFINITE)
                            .setBehavior(new SnackbarNoSwipeBehavior())
                            .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getUserInfo(mMyGoogleAccountInfo.getId(),true);

                                }
                            });
                    mConnectivitySnackbar.show();

                    //Toast.makeText(MainActivity.this,"Code: " + response.code(), Toast.LENGTH_LONG).show();
                    //getUserInfoCall = call.clone();
                    //getUserInfoCall.enqueue(this);
                    return;
                }
                if(response.code()==204){
                    Toast.makeText(MainActivity.this,R.string.snackbar_server_error,Toast.LENGTH_SHORT).show();
                    finish();
                }

                if(response.code()==200){
                    if(mConnectivitySnackbar!=null) mConnectivitySnackbar.dismiss();

                    mUserInfo = response.body();

                    if(notificaton.equals(MyAppsNotificationManager.RENT_NOTIFICATION)) {
                        setTitle(R.string.main_history_fragment_title);
                        setFragment(new TransactionPagerFragment(),"history");
                        navigationView.setCheckedItem(R.id.nav_transaction);
                    }

                    if (first&&mSavedInstanceState == null&&!notificaton.equals(MyAppsNotificationManager.RENT_NOTIFICATION)) {
                        setTitle(R.string.main_movies_fragment_title);
                        setFragment(new MoviePagerFragment(),"movies");
                        navigationView.setCheckedItem(R.id.nav_movies);
                    }

                 txtNavHeaderDisp.setText(response.body().getDisplay_name());
                 Glide.with(MainActivity.this).load(ApiClient.BASE_URL+response.body().getProf_img()).into(imgNavHeaderProfile);
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

                mConnectivitySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout_main),
                        R.string.snackbar_server_error,
                        Snackbar.LENGTH_INDEFINITE)
                                .setBehavior(new SnackbarNoSwipeBehavior())
                                .setAction( R.string.snackbar_retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getUserInfo(mMyGoogleAccountInfo.getId(),true);
                                    }
                                });
                mConnectivitySnackbar.show();



                //Log.d(TAG,t.toString());
                //Toast.makeText(MainActivity.this,"Unable to connect with server",Toast.LENGTH_SHORT).show();
                //finish();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_menu_search, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("search here");
        //searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "search here"  + "</font>"));
        ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_arrow_back_white_24);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mUserInfo != null){
                Toast.makeText(MainActivity.this,"search for: " + query, Toast.LENGTH_LONG).show();

                startActivity(SearchActivity.newInstance(MainActivity.this,query));
                searchMenuItem.collapseActionView();
                return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


            case R.id.nav_movies:
                setTitle("Movies");
                setFragment(new MoviePagerFragment(),"movies");
                break;

            case R.id.nav_tv_show:
                setTitle("Tvshows");
                navigationView.setCheckedItem(R.id.nav_tv_show);
                setFragment(new TvshowPagerFragment(),"tvshow");
//                setTitle("Tv Shows");
//                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container,
//                new TvshowsFragment()).commit();
                break;

            case R.id.nav_favourite:
                setTitle("Favourites");
                setFragment(new FavouritePagerFragment(),"favourites");
                break;

            case R.id.nav_transaction:
                setTitle("History");
                setFragment(new TransactionPagerFragment(),"history");
                break;

            case R.id.nav_settings:
//                startActivity(new Intent(this, SettingActivity.class));
                    if(mUserInfo != null){
                        Intent i = new Intent(MainActivity.this,SettingActivity.class);
                        i.putExtra("UserInfo", mUserInfo); //I get the error The method putParcelable(String, Parcelable) in the type Bundle is not applicable for the arguments (int, A.myClass)
                        startActivityForResult(i, REQUEST_CODE);
                    }
                break;


            case R.id.subscriptions:
                if(mUserInfo != null){
                    Intent i = new Intent(MainActivity.this,SubscriptionsActivity.class);
                    i.putExtra("UserInfo", mUserInfo); //I get the error The method putParcelable(String, Parcelable) in the type Bundle is not applicable for the arguments (int, A.myClass)
                    startActivity(i);
                }
//                Intent intent = new Intent(MainActivity.this,MyMovieStoresActivity.class);
//                startActivity(new Intent(intent));
                break;


            case R.id.nav_sign_out:

                signOut();
                //Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private ArrayList<String> mFragmentsTags = new ArrayList<>();
   // private ArrayList<FragmentTag> mFragments = new ArrayList<>();


    private void setFragment(Fragment fragment,String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (manager.findFragmentByTag ( tag ) == null) { // No fragment in backStack with same tag..
             ft.add ( R.id.fragment_container_main, fragment, tag );

            mFragmentsTags.add(tag);
            ft.addToBackStack (tag);
            ft.commit ();
            for(String tempTag : mFragmentsTags){
                if(!tempTag.equals(tag))
                    ft.hide(manager.findFragmentByTag (tempTag));
            }

        }
        else {
            ft.show ( manager.findFragmentByTag ( tag ) ).commit ();
            for(String tempTag : mFragmentsTags){
                if(!tempTag.equals(tag))
                ft.hide(manager.findFragmentByTag (tempTag));
            }
        }

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
//                .replace(R.id.fragment_container_main, fragment,tag);
//        fragmentTransaction.commit();
        //getSupportFragmentManager().findFragmentByTag(fragmentTag) == null)


    }



    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        Toast.makeText(MainActivity.this,"Successfully signed out",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }



    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START))   mDrawer.closeDrawer(GravityCompat.START);
        else if(getSupportFragmentManager().getBackStackEntryCount()>=1) {

            if (doubleBackToExitPressedOnce) {
                mToastDoubleBackToExit.cancel();
                super.finishAfterTransition();
                return;
            }

            doubleBackToExitPressedOnce = true;
            mToastDoubleBackToExit.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE ){
            if(resultCode==RESULT_OK){
                Boolean useredited = data.getBooleanExtra("user_edited",false);
                if(useredited) getUserInfo(mMyGoogleAccountInfo.getId(),false);
            }
        }
    }

//    @Override
//    protected void onDestroy() {
//        if(getUserInfoCall !=null)   getUserInfoCall.cancel();
//        super.onDestroy();
//    }

}