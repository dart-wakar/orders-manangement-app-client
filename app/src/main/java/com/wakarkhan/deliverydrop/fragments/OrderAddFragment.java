package com.wakarkhan.deliverydrop.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.model.Order;
import com.wakarkhan.deliverydrop.network.OrderRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderAddFragment extends Fragment {

    public static final String TAG = OrderAddFragment.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private EditText etTitle;
    private EditText etWebsite;
    private Button btnCreate;
    private Button btnCancel;

    private String token;
    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_add, container, false);
        compositeDisposable = new CompositeDisposable();
        initSharedPreferences();
        initViews(view);
        return view;
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = sharedPreferences.getString("token","");
    }

    private void initViews(View v) {
        etTitle = (EditText)v.findViewById(R.id.et_order_add_title);
        etWebsite = (EditText)v.findViewById(R.id.et_order_add_website);
        btnCreate = (Button)v.findViewById(R.id.btn_order_add_save);
        btnCancel = (Button)v.findViewById(R.id.btn_order_add_cancel);

        btnCreate.setOnClickListener((View view) -> createOrder());
        btnCancel.setOnClickListener((View view) -> cancelCreate());
    }

    private void createOrder() {
        hidekeyboard(getContext());
        Order order = new Order();
        order.setTitle(etTitle.getText().toString());
        order.setWebsite_name(etWebsite.getText().toString());

        OrderRequestInterface orderRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderRequestInterface.class);

        compositeDisposable.add(orderRequestInterface.createOrder(order,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Order odr) {
        Log.d(TAG,"Id: "+odr.getId());
        Toast.makeText(getContext(),"Order creation successful!",Toast.LENGTH_SHORT).show();
        loadOrdersFragment();
    }

    private void handleError(Throwable error){
        Log.d(TAG,error.getLocalizedMessage());
        Toast.makeText(getContext(),"Error "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    private void loadOrdersFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        OrdersFragment fragment = new OrdersFragment();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();
    }

    private void cancelCreate() {
        hidekeyboard(getContext());
        loadOrdersFragment();
    }

    private void hidekeyboard(Context ctx) {
        InputMethodManager inputMethodManager = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity)ctx).getCurrentFocus();
        if (v == null)
            return;
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Add Order");
    }
}