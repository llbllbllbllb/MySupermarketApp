package com.libangliang.supermarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libangliang.supermarket.Model.Cart;
import com.libangliang.supermarket.Model.Orders;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderProductsDetailActivity extends AppCompatActivity {


    private TextView totalPrice, deliveryAddress,phoneNumber, orderTime, orderStatus;
    private RecyclerView recyclerView;

    private DatabaseReference ordersRef;

    private String orderid, status;

    ValueEventListener listener;

    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;

    @Override
    protected void onPause() {
        super.onPause();
        ordersRef.removeEventListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products_detail);

        orderid = getIntent().getStringExtra("orderid");
        status = getIntent().getStringExtra("status");


        totalPrice = findViewById(R.id.order_products_total_price);
        deliveryAddress = findViewById(R.id.order_products_delivery_address);
        phoneNumber = findViewById(R.id.order_products_phone_number);
        orderTime = findViewById(R.id.order_products_order_time);
        orderStatus = findViewById(R.id.order_products_order_status);

        recyclerView = findViewById(R.id.order_products_product_list);

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(status).child(orderid);
    }


    @Override
    protected void onStart() {
        super.onStart();

        listener = ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Orders orders = dataSnapshot.getValue(Orders.class);
                totalPrice.setText("Order Total Price: "+orders.getTotalAmount()+" CAD");
                deliveryAddress.setText("Delivery Address: " + orders.getAddress() +", "+orders.getCity());
                phoneNumber.setText("Phone Number: "+orders.getPhone());

                String oldstring = orders.getDate()+" "+orders.getTime();
                SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy HH:mm:ss a");
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss a, dd/MM/yy");
                try{
                    Date date = format.parse(oldstring);
                    oldstring = df2.format(date);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                orderTime.setText("Order Time: "+oldstring);

                orderStatus.setText("Status: "+orders.getState());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(ordersRef.child("Products"),Cart.class).build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductQuantity.setText("×"+Integer.toString(model.getQuantity()));
                holder.txtProductPrice.setText(model.getPrice() + " CAD");
                holder.txtProductName.setText(model.getPname());
                Picasso.get().load(model.getImage()).into(holder.productImage);

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);


                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();




    }
}
