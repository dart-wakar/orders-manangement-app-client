package com.wakarkhan.deliverydrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wakarkhan.deliverydrop.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment();

    }

    private void loadFragment() {
        Fragment fragment;
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preferences.contains("token")) {
            String token = preferences.getString("token",null);
            Log.d(TAG,token);
            Intent intent = new Intent(MainActivity.this, NavigationMain.class);
            startActivity(intent);
        } else {
            fragment = new LoginFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame,fragment,LoginFragment.TAG);
            ft.commit();
        }
    }
}
