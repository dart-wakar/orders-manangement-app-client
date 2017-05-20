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
import com.wakarkhan.deliverydrop.model.Order;
import com.wakarkhan.deliverydrop.network.OrderRequestInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderEditFragment extends Fragment {

    public static final String TAG = OrderEditFragment.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private EditText etOrderTitle;
    private EditText etOrderWebsite;
    private Button btnSave;
    private Button btnCancel;

    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private String token;
    private Order currentOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        currentOrder = (Order) bundle.getSerializable("current_order");
        Log.d(TAG,currentOrder.getTitle());
        View view = inflater.inflate(R.layout.fragment_order_edit, container, false);
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
        etOrderTitle = (EditText)v.findViewById(R.id.et_order_edit_title);
        etOrderWebsite = (EditText)v.findViewById(R.id.et_order_edit_website);
        btnSave = (Button)v.findViewById(R.id.btn_order_edit_save);
        btnCancel = (Button)v.findViewById(R.id.btn_order_edit_cancel);

        etOrderTitle.setText(currentOrder.getTitle());
        etOrderWebsite.setText(currentOrder.getWebsite_name());

        btnSave.setOnClickListener((View view) -> saveOrder());
        btnCancel.setOnClickListener((View view) -> cancelSave());
    }

    private void saveOrder() {
        Order odr = currentOrder;
        odr.setTitle(etOrderTitle.getText().toString());
        odr.setWebsite_name(etOrderWebsite.getText().toString());

        OrderRequestInterface orderRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderRequestInterface.class);

        compositeDisposable.add(orderRequestInterface.editOrder(odr,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Order ordr) {
        Toast.makeText(getContext(),"Update Successful!",Toast.LENGTH_SHORT).show();
        currentOrder = ordr;
        goToOrderDetailsFragment(currentOrder.getId());
    }

    private void handleError(Throwable error) {
        Toast.makeText(getContext(),"Error: "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    private void cancelSave() {
        goToOrderDetailsFragment(currentOrder.getId());
    }

    private void goToOrderDetailsFragment(int orderId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("orderId",orderId);
        fragment.setArguments(args);
        ft.replace(R.id.content_frame,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Edit Order");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}