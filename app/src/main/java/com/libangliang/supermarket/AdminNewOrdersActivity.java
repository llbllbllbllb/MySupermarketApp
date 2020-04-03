package com.libangliang.supermarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libangliang.supermarket.Model.Orders;
import com.libangliang.supermarket.ViewHolder.AdminOrdersViewHolder;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private RecyclerView recyclerView;
    private  FirebaseRecyclerOptions<Orders> options;

    private String selectedTab = "Not Shipped";

    private DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

    private FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter_Not_Shipped, adapter_Shipped, adapter_Delivered, adapter_Cancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        tabLayout = findViewById(R.id.admin_orders_tabs);

        tabLayout.addTab(tabLayout.newTab().setText("Not Shipped"));
        tabLayout.addTab(tabLayout.newTab().setText("Shipped"));
        tabLayout.addTab(tabLayout.newTab().setText("Delivered"));
        tabLayout.addTab(tabLayout.newTab().setText("Cancelled"));

        recyclerView = findViewById(R.id.admin_recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter_Not_Shipped = createAdapter("Not Shipped");
        adapter_Shipped = createAdapter("Shipped");
        adapter_Delivered = createAdapter("Delivered");
        adapter_Cancelled = createAdapter("Cancelled");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Not Shipped")){
                    selectedTab = "Not Shipped";
                }

                else if(tab.getText().equals("Cancelled")){
                    selectedTab = "Cancelled";

                }
                else if(tab.getText().equals("Delivered")){
                    selectedTab = "Delivered";
                }
                else if(tab.getText().equals("Shipped")){
                    selectedTab = "Shipped";
                }
                setRecyclerView();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        setRecyclerView();
        adapter_Not_Shipped.startListening();
        adapter_Cancelled.startListening();
        adapter_Not_Shipped.startListening();
        adapter_Shipped.startListening();
        adapter_Delivered.startListening();

    }

    protected FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> createAdapter(final String selectedTab){

        options = new FirebaseRecyclerOptions.Builder<Orders>().setQuery(ordersRef.child(selectedTab), Orders.class).build();
        final FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter;
        adapter = new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull final Orders model) {

                Log.v("totalamount",model.getTotalAmount());
                holder.admin_cardOrdersTotalPrice.setText("Total Price: "+model.getTotalAmount()+" CAD");
                holder.admin_cardOrdersUserid.setText("User ID: "+model.getUserid());
                holder.admin_cardOrdersState.setText("Order State: "+model.getState());
                String oldstring = model.getDate()+" "+model.getTime();
                Log.v("time",oldstring);
                SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy HH:mm:ss a");
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss a, dd/MM/yy");
                try{
                    Date date = format.parse(oldstring);
                    oldstring = df2.format(date);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                holder.admin_cardOrdersDateAndTime.setText("Order Time: "+oldstring);
                if(selectedTab == "Not Shipped"){
                    holder.admin_order_btn1.setText("Product List");
                    holder.admin_order_btn2.setText("Ready to ship");

                    holder.admin_order_btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // check product list
                            Intent intent = new Intent(AdminNewOrdersActivity.this, OrderProductsDetailActivity.class);
                            intent.putExtra("orderid",model.getOrderid());
                            intent.putExtra("status",model.getState());
                            startActivity(intent);
                        }
                    });

                    holder.admin_order_btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ordersRef.child(model.getState()).child(model.getOrderid()).child("state").setValue("Shipped");
                            removeFromFirebase(ordersRef.child(model.getState()), ordersRef.child("Shipped"), model.getOrderid());
                        }
                    });
                }

                else if(selectedTab == "Shipped"){
                    holder.admin_order_btn1.setText("Move to not shipped");
                    holder.admin_order_btn2.setText("Delivered");

                    holder.admin_order_btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ordersRef.child(model.getState()).child(model.getOrderid()).child("state").setValue("Not Shipped");
                            removeFromFirebase(ordersRef.child(model.getState()), ordersRef.child("Not Shipped"), model.getOrderid());
                        }
                    });

                    holder.admin_order_btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ordersRef.child(model.getState()).child(model.getOrderid()).child("state").setValue("Delivered");
                            removeFromFirebase(ordersRef.child(model.getState()), ordersRef.child("Delivered"), model.getOrderid());
                        }
                    });


                }

                else if(selectedTab == "Delivered"){
                    holder.admin_order_btn1.setText("Move to shipped");
                    holder.admin_order_btn2.setVisibility(View.INVISIBLE);

                    holder.admin_order_btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ordersRef.child(model.getState()).child(model.getOrderid()).child("state").setValue("Shipped");
                            removeFromFirebase(ordersRef.child(model.getState()), ordersRef.child("Shipped"), model.getOrderid());
                        }
                    });


                }









            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_items_layout,parent,false);
                AdminOrdersViewHolder viewHolder = new AdminOrdersViewHolder(view);
                return viewHolder;
            }


//            @Override
//            public void onDataChanged() {
//                super.onDataChanged();
//                if(adapter.getItemCount() == 0){
//                    no_order_txt.setVisibility(TextView.VISIBLE);
//                }
//                else{
//                    no_order_txt.setVisibility(TextView.INVISIBLE);
//                }
//            }
        };
        return adapter;
    }

    private void setRecyclerView(){
        if(selectedTab.equals("Not Shipped")){
            recyclerView.setAdapter(adapter_Not_Shipped);
            if(adapter_Not_Shipped.getItemCount() == 0){
//                no_order_txt.setText("You don't have any unshipped order");
//                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
//                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }

        else if(selectedTab.equals("Cancelled")){
            recyclerView.setAdapter(adapter_Cancelled);
            if(adapter_Cancelled.getItemCount() == 0){
//                no_order_txt.setText("You don't have any cancelled order");
//                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
//                no_order_txt.setVisibility(TextView.INVISIBLE);
            }

        }
        else if(selectedTab.equals("Delivered")){
            recyclerView.setAdapter(adapter_Delivered);
            int tmp = adapter_Cancelled.getItemCount();
            Log.v("itemcount",Integer.toString(tmp));
            if(adapter_Delivered.getItemCount() == 0){
//                no_order_txt.setText("You don't have any delivered order");
//                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
//                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }
        else if(selectedTab.equals("Shipped")){
            recyclerView.setAdapter(adapter_Shipped);
            if(adapter_Shipped.getItemCount() == 0){
//                no_order_txt.setText("You don't have any shipped order");
//                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
//                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }

    }

    private void removeFromFirebase(final DatabaseReference fromPath, final DatabaseReference toPath, final String key) {
        fromPath.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            // Now "DataSnapshot" holds the key and the value at the "fromPath".
            // Let's move it to the "toPath". This operation duplicates the
            // key/value pair at the "fromPath" to the "toPath".
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.child(dataSnapshot.getKey())
                        .setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Log.i("move node", "onComplete: success");
                                    // In order to complete the move, we are going to erase
                                    // the original copy by assigning null as its value.
                                    fromPath.child(key).setValue(null);
                                }
                                else {
                                    Log.e("move node", "onComplete: failure:" + databaseError.getMessage() + ": "
                                            + databaseError.getDetails());
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("move node", "onCancelled: " + databaseError.getMessage() + ": "
                        + databaseError.getDetails());
            }
        });
    }
}
