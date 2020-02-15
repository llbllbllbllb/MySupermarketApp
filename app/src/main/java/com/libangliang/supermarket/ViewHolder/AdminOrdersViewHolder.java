package com.libangliang.supermarket.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.libangliang.supermarket.Interface.ItemClickListener;
import com.libangliang.supermarket.R;

public class AdminOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView admin_cardOrdersTotalPrice, admin_cardOrdersDateAndTime, admin_cardOrdersState, admin_cardOrdersUserid;
    public ItemClickListener admin_listener;

    public Button admin_order_btn1, admin_order_btn2;


    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        admin_cardOrdersTotalPrice = itemView.findViewById(R.id.admin_orders_total_price);
        admin_cardOrdersDateAndTime = itemView.findViewById(R.id.admin_orders_date_and_time);
        admin_cardOrdersState = itemView.findViewById(R.id.admin_orders_state);
        admin_cardOrdersUserid = itemView.findViewById(R.id.admin_orders_user_id);

        admin_order_btn1 = itemView.findViewById(R.id.admin_orders_btn_1);
        admin_order_btn2 = itemView.findViewById(R.id.admin_orders_btn_2);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.admin_listener = listener;
    }
    @Override
    public void onClick(View v) {
        admin_listener.onClick(v,getAdapterPosition(),false);
    }
}
