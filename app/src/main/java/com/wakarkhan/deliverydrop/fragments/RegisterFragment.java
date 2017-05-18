package com.wakarkhan.deliverydrop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.model.User;
import com.wakarkhan.deliverydrop.network.UserRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wakar on 18/5/17.
 */

public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private EditText etUsername;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhone;
    private EditText etPassword;
    private TextInputLayout tiUsername;
    private TextInputLayout tiEmail;
    private TextInputLayout tiFirstName;
    private TextInputLayout tiLastName;
    private TextInputLayout tiPhone;
    private TextInputLayout tiPassword;
    private TextView tvLogin;
    private Button btnRegister;
    private ProgressBar progressBar;

    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register,container,false);
        compositeDisposable = new CompositeDisposable();
        initViews(view);
        return view;

    }

    private void initViews(View v) {
        etUsername = (EditText)v.findViewById(R.id.et_username);
        etEmail = (EditText)v.findViewById(R.id.et_email);
        etFirstName = (EditText)v.findViewById(R.id.et_first_name);
        etLastName = (EditText)v.findViewById(R.id.et_last_name);
        etPhone = (EditText)v.findViewById(R.id.et_phone);
        etPassword = (EditText)v.findViewById(R.id.et_password);
        tiUsername = (TextInputLayout)v.findViewById(R.id.ti_username);
        tiEmail = (TextInputLayout)v.findViewById(R.id.ti_email);
        tiFirstName = (TextInputLayout)v.findViewById(R.id.ti_first_name);
        tiLastName = (TextInputLayout)v.findViewById(R.id.ti_last_name);
        tiPhone = (TextInputLayout)v.findViewById(R.id.ti_phone);
        tiPassword = (TextInputLayout)v.findViewById(R.id.ti_password);
        tvLogin = (TextView)v.findViewById(R.id.tv_login);
        btnRegister = (Button)v.findViewById(R.id.btn_register);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);

        btnRegister.setOnClickListener(view -> register());
        tvLogin.setOnClickListener(view -> goToLogin());

    }

    private void register() {

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setPhone(phone);
        user.setPassword(password);
        progressBar.setVisibility(View.VISIBLE);
        registerProcess(user);

    }

    private void registerProcess(User user) {

        UserRequestInterface userRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserRequestInterface.class);

        compositeDisposable.add(userRequestInterface.register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        progressBar.setVisibility(View.GONE);
        String first_name = user.getFirst_name();
        Snackbar.make(getView(),"Welcome "+first_name+"! Login now!",Snackbar.LENGTH_LONG).show();
        goToLogin();
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(getView(),"Error "+error.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
    }

    private void goToLogin() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragment_frame,fragment,LoginFragment.TAG);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
