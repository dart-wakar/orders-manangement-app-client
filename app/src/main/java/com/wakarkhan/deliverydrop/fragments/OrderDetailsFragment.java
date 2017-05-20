package com.wakarkhan.deliverydrop.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.model.Order;
import com.wakarkhan.deliverydrop.network.OrderRequestInterface;

import java.text.DateFormat;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailsFragment extends Fragment {

    public static final String TAG = OrderDetailsFragment.class.getSimpleName();
    public static final String BASE_URL = "http://10.0.2.2:8000/";
    private String token;

    private ProgressBar progressBar;
    private TextView tv_title;
    private TextView tv_website;
    private TextView tv_created;
    private TextView tv_modified;
    private TextView tv_status;
    private FloatingActionButton fab_orderEdit;
    private FloatingActionButton fab_orderDelete;
    private FloatingActionButton fab_goBack;

    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;

    private int orderId;
    private Order currentOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        Bundle bundle = getArguments();
        orderId = bundle.getInt("orderId");
        Log.d(TAG,"Order id: "+orderId);
        compositeDisposable = new CompositeDisposable();
        initSharedPreferences();
        initViews(view);
        loadOrder();
        return view;
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = sharedPreferences.getString("token","");
    }

    private void initViews(View v) {
        tv_title = (TextView)v.findViewById(R.id.tv_title);
        tv_website = (TextView)v.findViewById(R.id.tv_website_name);
        tv_created = (TextView)v.findViewById(R.id.tv_created);
        tv_modified = (TextView)v.findViewById(R.id.tv_modified);
        tv_status = (TextView)v.findViewById(R.id.tv_order_status);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);
        fab_orderEdit = (FloatingActionButton)v.findViewById(R.id.fab_order_edit);
        fab_orderDelete = (FloatingActionButton)v.findViewById(R.id.fab_order_delete);
        fab_goBack = (FloatingActionButton)v.findViewById(R.id.fab_go_back);

        fab_orderEdit.setOnClickListener((View view) -> loadEditOrderFragment());
        fab_orderDelete.setOnClickListener((View view) -> promptOrderDelete());
        fab_goBack.setOnClickListener((View view) -> loadOrdersFragment());
    }

    private void loadEditOrderFragment() {

    }

    private void promptOrderDelete() {

    }

    private void loadOrdersFragment() {

    }

    private void loadOrder() {
        OrderRequestInterface orderRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderRequestInterface.class);

        compositeDisposable.add(orderRequestInterface.getOrder(orderId,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Order order) {
        currentOrder = order;
        progressBar.setVisibility(View.GONE);
        tv_title.setText(currentOrder.getTitle());
        tv_website.setText(currentOrder.getWebsite_name());
        int status = currentOrder.getStatus();
        String status_text;
        switch (status) {
            case 0:
                status_text = "Order placed";
                break;
            case 1:
                status_text = "Order confirmed";
                break;
            case 2:
                status_text = "Order on route to delivery";
                break;
            case 3:
                status_text = "Order delivered";
                break;
            default:
                status_text = "Order placed";
        }
        tv_status.setText(status_text);
        tv_created.setText(DateFormat.getDateTimeInstance().format(currentOrder.getCreated()));
        tv_modified.setText(DateFormat.getDateTimeInstance().format(currentOrder.getModified()));
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Error: "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Order Details");
    }
}