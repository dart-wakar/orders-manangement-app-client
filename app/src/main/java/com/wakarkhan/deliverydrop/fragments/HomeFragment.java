package com.wakarkhan.deliverydrop.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wakarkhan.deliverydrop.R;

public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    private TextView tvYourAddress;
    private CardView cardView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initViews(view);
        return view;
    }


    private void initViews(View v) {
        tvYourAddress = (TextView)v.findViewById(R.id.tv_your_address);
        cardView = (CardView)v.findViewById(R.id.address_card);
        tvYourAddress.setText("Kapili 125 \nKamrup-781039\nIIT Guwahati\n");

        tvYourAddress.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    String textAddress = tvYourAddress.getText().toString();
                    android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clipData = android.content.ClipData.newPlainText("yourAddress",textAddress);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(),"Address copied to clipboard!",Toast.LENGTH_SHORT).show();
                    /*
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    String pastedata = item.getText().toString();
                    Log.d(TAG,pastedata);
                    */
                    return super.onDoubleTap(e);
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"Raw Event");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }

}