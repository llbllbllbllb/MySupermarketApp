package com.libangliang.supermarket.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.libangliang.supermarket.Interface.ItemClickListener;
import com.libangliang.supermarket.R;

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cardOrdersTotalPrice, cardOrdersDateAndTime, cardOrdersState;
    public ItemClickListener listener;

    public Button view_order, cancel_order;


    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        cardOrdersTotalPrice = itemView.findViewById(R.id.orders_total_price);
        cardOrdersDateAndTime = itemView.findViewById(R.id.orders_date_and_time);
        cardOrdersState = itemView.findViewById(R.id.orders_state);

        view_order = itemView.findViewById(R.id.orders_view_order_btn);
        cancel_order = itemView.findViewById(R.id.orders_cancel_order_btn);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
