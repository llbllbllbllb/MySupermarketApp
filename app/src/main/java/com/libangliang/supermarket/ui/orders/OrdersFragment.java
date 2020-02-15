package com.libangliang.supermarket.ui.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libangliang.supermarket.HomeActivity;
import com.libangliang.supermarket.Interface.ItemClickListener;
import com.libangliang.supermarket.Model.Orders;
import com.libangliang.supermarket.Model.Products;
import com.libangliang.supermarket.OrderProductsDetailActivity;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.R;
import com.libangliang.supermarket.ViewHolder.OrdersViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;


    private DatabaseReference ordersRef;
    private RecyclerView recyclerView;

    private TextView no_order_txt;

    private TabLayout ordersTab;

    private String selectedTab = "Not Shipped";

    private FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter_Not_Shipped, adapter_Shipped, adapter_Delivered, adapter_Cancelled;

    private  FirebaseRecyclerOptions<Orders> options;

    HomeActivity homeAct;

    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_orders, container, false);



        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        recyclerView = root.findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        no_order_txt = root.findViewById(R.id.no_order_txt);

        ordersTab = root.findViewById(R.id.orders_tabs);

        ordersTab.addTab(ordersTab.newTab().setText("Not Shipped"));
        ordersTab.addTab(ordersTab.newTab().setText("Shipped"));
        ordersTab.addTab(ordersTab.newTab().setText("Delivered"));
        ordersTab.addTab(ordersTab.newTab().setText("Cancelled"));

        adapter_Not_Shipped = createAdapter("Not Shipped");
        adapter_Shipped = createAdapter("Shipped");
        adapter_Delivered = createAdapter("Delivered");
        adapter_Cancelled = createAdapter("Cancelled");

        ordersTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("tabs_test","onTabSelected:"+tab.getText());
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





        homeAct = (HomeActivity) getActivity();
        homeAct.fab.setVisibility(FloatingActionButton.VISIBLE);




        return root;
    }



    @Override
    public void onStart() {
        super.onStart();


        setRecyclerView();
        adapter_Not_Shipped.startListening();
        adapter_Cancelled.startListening();
        adapter_Not_Shipped.startListening();
        adapter_Shipped.startListening();
        adapter_Delivered.startListening();

    }


    protected FirebaseRecyclerAdapter<Orders, OrdersViewHolder> createAdapter(final String selectedTab){

        options = new FirebaseRecyclerOptions.Builder<Orders>().setQuery(ordersRef.child(selectedTab).orderByChild("userid").equalTo(Prevalent.currentOnlineUser.getPhone()), Orders.class).build();
        final FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter;
        adapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull final Orders model) {
                if(model.getState().equals(selectedTab)){
                    holder.cardOrdersTotalPrice.setText("Total Price: "+model.getTotalAmount()+" CAD");
                    holder.cardOrdersState.setText("Status: "+model.getState());
                    holder.cardOrdersDateAndTime.setText(model.getDate()+model.getTime());

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
                    holder.cardOrdersDateAndTime.setText("Order Time: "+oldstring);

                    holder.view_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), OrderProductsDetailActivity.class);
                            intent.putExtra("orderid",model.getOrderid());
                            intent.putExtra("status",model.getState());
                            startActivity(intent);

                        }
                    });

                    if(!model.getState().equals("Not Shipped")){
                        holder.cancel_order.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.not_enhanced_btn_boarder_grey));
                        holder.cancel_order.setTextColor(Color.DKGRAY);
                        holder.cancel_order.setVisibility(Button.INVISIBLE);
                    }

                    holder.cancel_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ordersRef.child(model.getState()).child(model.getOrderid()).child("state").setValue("Cancelled");
                            removeFromFirebase(ordersRef.child(model.getState()), ordersRef.child("Cancelled"), model.getOrderid());


                        }
                    });

                }

            }

            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_items_layout,parent,false);
                OrdersViewHolder viewHolder = new OrdersViewHolder(view);
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

    private void setRecyclerView(){
        if(selectedTab.equals("Not Shipped")){
            recyclerView.setAdapter(adapter_Not_Shipped);
            if(adapter_Not_Shipped.getItemCount() == 0){
                no_order_txt.setText("You don't have any unshipped order");
                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }

        else if(selectedTab.equals("Cancelled")){
            recyclerView.setAdapter(adapter_Cancelled);
            if(adapter_Cancelled.getItemCount() == 0){
                no_order_txt.setText("You don't have any cancelled order");
                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
                no_order_txt.setVisibility(TextView.INVISIBLE);
            }

        }
        else if(selectedTab.equals("Delivered")){
            recyclerView.setAdapter(adapter_Delivered);
            int tmp = adapter_Cancelled.getItemCount();
            Log.v("itemcount",Integer.toString(tmp));
            if(adapter_Delivered.getItemCount() == 0){
                no_order_txt.setText("You don't have any delivered order");
                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }
        else if(selectedTab.equals("Shipped")){
            recyclerView.setAdapter(adapter_Shipped);
            if(adapter_Shipped.getItemCount() == 0){
                no_order_txt.setText("You don't have any shipped order");
                no_order_txt.setVisibility(TextView.VISIBLE);
            }
            else{
                no_order_txt.setVisibility(TextView.INVISIBLE);
            }
        }

    }
}