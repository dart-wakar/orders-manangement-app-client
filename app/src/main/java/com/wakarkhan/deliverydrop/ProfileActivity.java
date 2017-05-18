package com.wakarkhan.deliverydrop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.model.User;
import com.wakarkhan.deliverydrop.network.UserRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wakar on 17/5/17.
 */

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";
    public boolean doubleBackToExitPressedOnce = false;

    private TextView tvFirstName;
    private TextView tvEmail;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;
    private String token;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        compositeDisposable = new CompositeDisposable();
        initViews();
        initSharedPreferences();
        loadProfile();
    }

    private void initViews() {
        tvFirstName = (TextView)findViewById(R.id.tv_first_name);
        tvEmail = (TextView)findViewById(R.id.tv_email);
        progressBar = (ProgressBar)findViewById(R.id.progress);
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("token","");
    }

    private void loadProfile() {
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
        progressBar.setVisibility(View.GONE);
        tvFirstName.setText(user.getFirst_name());
        tvEmail.setText(user.getEmail());
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(findViewById(R.id.activity_profile),"Error "+error.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,"Press BACK again to exit",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
