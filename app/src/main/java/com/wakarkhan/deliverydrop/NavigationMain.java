package com.wakarkhan.deliverydrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.fragments.HomeFragment;
import com.wakarkhan.deliverydrop.fragments.LoginFragment;
import com.wakarkhan.deliverydrop.fragments.NotificationsFragment;
import com.wakarkhan.deliverydrop.fragments.OrdersFragment;
import com.wakarkhan.deliverydrop.fragments.ProfileFragment;
import com.wakarkhan.deliverydrop.model.User;
import com.wakarkhan.deliverydrop.network.UserRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = NavigationMain.class.getSimpleName();
    private View navHeader;
    private TextView tv_navUsername;
    private TextView tv_navEmail;
    private Button btnLogout;
    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private String token;

    public static final String BASE_URL = "http://10.0.2.2:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initSharedPreferences();
        compositeDisposable = new CompositeDisposable();
        navHeader = navigationView.getHeaderView(0);
        tv_navUsername = (TextView) navHeader.findViewById(R.id.tv_nav_username);
        tv_navEmail = (TextView) navHeader.findViewById(R.id.tv_nav_email);
        btnLogout = (Button) navHeader.findViewById(R.id.btn_logout);
        loadCurrentUser();
        btnLogout.setOnClickListener((View view) -> logout());
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                moveTaskToBack(true);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Press BACK again to exit!",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            },2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_orders:
                fragment = new OrdersFragment();
                break;
            case R.id.nav_notifications:
                fragment = new NotificationsFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        token = sharedPreferences.getString("token","");
    }

    private void loadCurrentUser() {
        UserRequestInterface userRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserRequestInterface.class);

        compositeDisposable.add(userRequestInterface.getCurrentUser(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        Log.d(TAG,user.getUsername());
        tv_navUsername.setText(user.getUsername());
        tv_navEmail.setText(user.getEmail());
    }

    private void handleError(Throwable error) {
        Toast.makeText(getBaseContext(),"Error "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        Log.d(TAG,token);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        loadMainActivity();
    }

    private void loadMainActivity() {
        Intent intent = new Intent(NavigationMain.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
