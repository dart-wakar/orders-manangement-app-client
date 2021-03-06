package com.wakarkhan.deliverydrop.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.adapter.OrdersDataAdapter;
import com.wakarkhan.deliverydrop.model.Order;
import com.wakarkhan.deliverydrop.network.OrderRequestInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdersFragment extends Fragment {

    public static final String BASE_URL = "http://10.0.2.2:8000/";
    public static final String TAG = OrdersFragment.class.getSimpleName();
    private String token;

    private ProgressBar progressBar;
    private TextView tv_noOrders;
    private FloatingActionButton fabAddOrder;

    private RecyclerView recyclerView;
    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private OrdersDataAdapter ordersDataAdapter;
    private ArrayList<Order> orderArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders,container,false);
        compositeDisposable = new CompositeDisposable();
        initSharedPreferences();
        initViews(view);
        loadOrders();
        return view;
    }

    private void initViews(View v) {
        progressBar = (ProgressBar)v.findViewById(R.id.progress_orders);
        tv_noOrders = (TextView)v.findViewById(R.id.tv_no_orders);
        tv_noOrders.setVisibility(View.GONE);
        fabAddOrder = (FloatingActionButton)v.findViewById(R.id.fab_add_order);
        fabAddOrder.setOnClickListener((View view) -> loadAddOrderFragment());
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = sharedPreferences.getString("token","");
        Log.d(TAG,token+"haha");
    }

    private void loadOrders() {
        OrderRequestInterface orderRequestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderRequestInterface.class);

        compositeDisposable.add(orderRequestInterface.getUserOrders(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(List<Order> orderList) {
        progressBar.setVisibility(View.GONE);
        orderArrayList = new ArrayList<>(orderList);
        int noOfOrders = orderArrayList.size();
        if (noOfOrders == 0) {
            tv_noOrders.setVisibility(View.VISIBLE);
        }
        ordersDataAdapter = new OrdersDataAdapter(orderArrayList);
        ordersDataAdapter.setOnItemClickListener(new OrdersDataAdapter.OrderClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG,"onItemClick order id: "+orderArrayList.get(position).getId());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                OrderDetailsFragment fragment = new OrderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("orderId",orderArrayList.get(position).getId());
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        recyclerView.setAdapter(ordersDataAdapter);
    }

    private void handleError(Throwable error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Error "+error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }

    private void loadAddOrderFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        OrderAddFragment fragment = new OrderAddFragment();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();
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
        getActivity().setTitle("Orders");
    }
}