package com.wakarkhan.deliverydrop.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class ProfileEditFragment extends Fragment {

    public static final String TAG = ProfileEditFragment.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private String token;

    private EditText etUsername;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnSave;
    private Button btnCancel;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        Bundle bundle = getArguments();
        currentUser = (User)bundle.getSerializable("current_user");
        Log.d(TAG,currentUser.getEmail());
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        compositeDisposable = new CompositeDisposable();
        initSharedPreferences();
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        etUsername = (EditText)v.findViewById(R.id.et_profile_username);
        etEmail = (EditText)v.findViewById(R.id.et_profile_email);
        etFirstName = (EditText)v.findViewById(R.id.et_profile_first_name);
        etLastName = (EditText)v.findViewById(R.id.et_profile_last_name);
        etPhone = (EditText)v.findViewById(R.id.et_profile_phone);
        etAddress = (EditText)v.findViewById(R.id.et_profile_address);
        etUsername.setText(currentUser.getUsername());
        etEmail.setText(currentUser.getEmail());
        etFirstName.setText(currentUser.getFirst_name());
        etLastName.setText(currentUser.getLast_name());
        etPhone.setText(currentUser.getPhone());
        etAddress.setText(currentUser.getAddress());
        btnSave = (Button)v.findViewById(R.id.btn_save);
        btnCancel = (Button)v.findViewById(R.id.btn_cancel);
        btnSave.setOnClickListener((View view) -> saveProfile());
        btnCancel.setOnClickListener((View view) -> cancelSave());
    }

    private void saveProfile() {
        User usr = currentUser;
        usr.setUsername(etUsername.getText().toString());
        usr.setEmail(etEmail.getText().toString());
        usr.setFirst_name(etFirstName.getText().toString());
        usr.setLast_name(etLastName.getText().toString());
        usr.setPhone(etPhone.getText().toString());
        usr.setAddress(etAddress.getText().toString());

        UserRequestInterface userRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserRequestInterface.class);

        compositeDisposable.add(userRequestInterface.updateUser(usr,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        Toast.makeText(getContext(),"Update Successful",Toast.LENGTH_SHORT).show();
        currentUser = user;
        goToProfile();
    }

    private void handleError(Throwable error) {
        Toast.makeText(getContext(),"Error "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    private void cancelSave() {
        goToProfile();
    }

    private void goToProfile() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ProfileFragment fragment = new ProfileFragment();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = sharedPreferences.getString("token","");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Edit Profile");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}