package com.libangliang.supermarket.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.libangliang.supermarket.Interface.ItemClickListener;
import com.libangliang.supermarket.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice, txtProductQuantity, txtSubTotal;

    public ImageView productImage;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.card_product_name);
        txtProductPrice = itemView.findViewById(R.id.card_product_price);
        txtProductQuantity = itemView.findViewById(R.id.card_product_quantity);
        txtSubTotal = itemView.findViewById(R.id.card_product_subtotal);
        productImage = itemView.findViewById(R.id.card_product_image);



    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
