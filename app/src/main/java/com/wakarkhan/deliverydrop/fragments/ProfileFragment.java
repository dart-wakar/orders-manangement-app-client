package com.wakarkhan.deliverydrop.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.model.User;
import com.wakarkhan.deliverydrop.network.UserRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    public static final String BASE_URL = "http://10.0.2.2:8000/";
    private String token;

    private ProgressBar progressBar;
    private TextView tv_firstName;
    private TextView tv_email;
    private TextView tv_username;
    private TextView tv_lastName;
    private TextView tv_phone;
    private TextView tv_address;

    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        compositeDisposable = new CompositeDisposable();
        initViews(view);
        initSharedPreferences();
        loadProfile();
        return view;
    }

    private void initViews(View v) {
        tv_firstName = (TextView)v.findViewById(R.id.tv_first_name);
        tv_email = (TextView)v.findViewById(R.id.tv_email);
        tv_username = (TextView)v.findViewById(R.id.tv_username);
        tv_lastName = (TextView)v.findViewById(R.id.tv_last_name);
        tv_phone = (TextView)v.findViewById(R.id.tv_phone);
        tv_address = (TextView)v.findViewById(R.id.tv_address);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        tv_firstName.setText(user.getFirst_name());
        tv_email.setText(user.getEmail());
        tv_username.setText(user.getUsername());
        tv_lastName.setText(user.getLast_name());
        tv_phone.setText(user.getPhone());
        tv_address.setText(user.getAddress());
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Error "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Profile");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}