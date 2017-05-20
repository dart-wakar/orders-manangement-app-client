package com.wakarkhan.deliverydrop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wakarkhan.deliverydrop.R;

public class OrderDetailsFragment extends Fragment {

    public static final String TAG = OrderDetailsFragment.class.getSimpleName();

    private int orderId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        Bundle bundle = getArguments();
        orderId = bundle.getInt("orderId");
        Log.d(TAG,"Order id: "+orderId);
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Order Details");
    }
}