package com.wakarkhan.deliverydrop.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.ProfileActivity;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.network.UserRequestInterface;
import com.wakarkhan.deliverydrop.model.Auth;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wakar on 18/5/17.
 */

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private EditText etUsername;
    private EditText etPassword;
    private TextInputLayout tiUsername;
    private TextInputLayout tiPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        compositeDisposable = new CompositeDisposable();
        initViews(view);
        initSharedPreferences();
        return view;
    }

    private void initViews(View v) {
        etUsername = (EditText)v.findViewById(R.id.et_username);
        etPassword = (EditText)v.findViewById(R.id.et_password);
        tiUsername = (TextInputLayout)v.findViewById(R.id.ti_username);
        tiPassword = (TextInputLayout)v.findViewById(R.id.ti_password);
        btnLogin = (Button)v.findViewById(R.id.btn_login);
        tvRegister = (TextView)v.findViewById(R.id.tv_register);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);

        btnLogin.setOnClickListener(view -> login());
        tvRegister.setOnClickListener(view -> goToRegister());
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        loginProcess(username,password);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void loginProcess(String username,String password) {
        UserRequestInterface userRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserRequestInterface.class);

        compositeDisposable.add(userRequestInterface.login(username,password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Auth auth) {
        progressBar.setVisibility(View.GONE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String token = auth.getToken();
        editor.putString("token","Token "+token);
        editor.apply();
        etUsername.setText(null);
        etPassword.setText(null);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(getView(),"Error "+error.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
    }

    private void goToRegister() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.replace(R.id.fragment_frame,fragment,RegisterFragment.TAG);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
