package com.wakarkhan.deliverydrop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.wakarkhan.deliverydrop.R;
import com.wakarkhan.deliverydrop.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wakar on 19/5/17.
 */

public class OrdersDataAdapter extends RecyclerView.Adapter<OrdersDataAdapter.ViewHolder> {

    private ArrayList<Order> orderArrayList;
    private static OrderClickListener orderClickListener;

    public OrdersDataAdapter(ArrayList<Order> orderList) {
        orderArrayList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvOrder_title.setText(orderArrayList.get(position).getTitle());
        holder.tvWebsite.setText(orderArrayList.get(position).getWebsite_name());
        int status = orderArrayList.get(position).getStatus();
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
        holder.tvStatus.setText(status_text);
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvOrder_title,tvStatus,tvWebsite;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            tvOrder_title = (TextView)view.findViewById(R.id.tv_order_title);
            tvStatus = (TextView)view.findViewById(R.id.tv_status);
            tvWebsite = (TextView)view.findViewById(R.id.tv_website);
        }

        @Override
        public void onClick(View v) {
            orderClickListener.onItemClick(getAdapterPosition(),v);
        }
    }

    public void setOnItemClickListener(OrderClickListener orderClickListener) {
        OrdersDataAdapter.orderClickListener = orderClickListener;
    }

    public interface OrderClickListener {
        void onItemClick(int position,View v);
    }
}
