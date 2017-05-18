package com.wakarkhan.deliverydrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
        preferences = getPreferences(0);
        loadFragment();

    }

    private void loadFragment() {
        Fragment fragment;
        if (preferences.contains("token")) {
            String token = preferences.getString("token","");
            Log.d(TAG,token);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else {
            fragment = new LoginFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_frame,fragment,LoginFragment.TAG).commit();
        }
    }
}
