package com.libangliang.supermarket.ui.cart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Canvas;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libangliang.supermarket.ConfirmFinalOrderActivity;
import com.libangliang.supermarket.HomeActivity;
import com.libangliang.supermarket.MainActivity;
import com.libangliang.supermarket.Model.Cart;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.ProductDetailsActivity;
import com.libangliang.supermarket.R;
import com.libangliang.supermarket.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView totalAmount, empty_cart_msg;

    HomeActivity homeAct;

    private float totalPrice = 0;

    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;

    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("CartList");



    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        final TextView textView = root.findViewById(R.id.text_cart);
        cartViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        homeAct = (HomeActivity) getActivity();
        homeAct.fab.setVisibility(FloatingActionButton.INVISIBLE);




        recyclerView = root.findViewById(R.id.cart_fragment_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = root.findViewById(R.id.cart_fragment_next_btn);
        totalAmount = root.findViewById(R.id.cart_fragment_total_price_text);
        empty_cart_msg = root.findViewById(R.id.empty_cart_msg);


        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent(getActivity(), ConfirmFinalOrderActivity.class);
                intent.putExtra("TotalPrice",String.valueOf(totalPrice));
                startActivity(intent);

            }
        });


        totalAmount.setText("Total Price: "+ String.format("%.2f",totalPrice) + " CAD");

//        loadRefreshData(cartListRef);

        if(totalPrice == 0){
            empty_cart_msg.setVisibility(TextView.VISIBLE);
            nextProcessBtn.setVisibility(Button.INVISIBLE);
        }







        return root;
    }


    private void calTotalPrice(){
        totalPrice = 0;
        for(int i = 0; i<adapter.getItemCount(); i++){
            Log.v("print object","i m running the for loop");
            TextView subtotal = recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.card_product_subtotal);
            String subtotalStr = subtotal.getText().toString();
            subtotalStr = subtotalStr.replaceAll("Subtotal: ", "");
            subtotalStr = subtotalStr.replaceAll(" CAD", "");

            Log.v("print object", subtotalStr);

            totalPrice = totalPrice + Float.parseFloat(subtotalStr);
        }
    }




    private void checkEmptyCart() {
        if(adapter.getItemCount() == 0){
            empty_cart_msg.setVisibility(TextView.VISIBLE);
            nextProcessBtn.setVisibility(Button.INVISIBLE);
        }
        else{
            empty_cart_msg.setVisibility(TextView.INVISIBLE);
            nextProcessBtn.setVisibility(Button.VISIBLE);
        }
    }


    private void loadRefreshData(){
//        if(cartListRef.child("UserView").)
            cartListRef.child("UserView").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                calTotalPrice();
                totalAmount.setText("Total Price: "+ String.format("%.2f",totalPrice) + " CAD");
                checkEmptyCart();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart(){

        super.onStart();



        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("UserView")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();



        adapter  = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantity.setText("×"+Integer.toString(model.getQuantity()));
                holder.txtProductPrice.setText(model.getPrice() + " CAD");
                holder.txtProductName.setText(model.getPname());
                float subtotal = Float.parseFloat(model.getPrice())*model.getQuantity();
                subtotal = Float.valueOf(String.format("%.2f",subtotal));
                totalPrice += subtotal;
//                checkEmptyCart();
//
//                totalAmount.setText("Total Price: "+ String.format("%.2f",totalPrice) + " CAD");
                holder.txtSubTotal.setText("Subtotal: "+String.format("%.2f",subtotal)+" CAD");


                loadRefreshData();

//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CharSequence options[] = new CharSequence[]{
//                                "Edit","Remove from cart"
//
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                        builder.setTitle("Cart Options");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if(which == 0){
//                                    //click edit button
//                                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
//                                    intent.putExtra("pid",model.getPid());
//                                    startActivity(intent);
//                                }
//                                if(which == 1){
//                                    //click the remove button
//                                    float subtotal = Float.parseFloat(model.getPrice())*model.getQuantity();
//                                    totalPrice -= subtotal;
//                                    if(totalPrice<0){totalPrice = 0;}
//                                    checkEmptyCart();
//                                    totalAmount.setText("Total Price: "+ String.format("%.2f",totalPrice) + " CAD");
//                                    cartListRef.child("UserView").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()){
//                                                Toast.makeText(getContext(),"Item removed",Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        }
//                                    });
//                                }
//
//                            }
//                        });
//                        builder.show();
//
//                    }
//                });



//                Log.v("test img url", model.getImgURL());
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



        // this function implement swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //delete item
                // 1 get the position
                int position = viewHolder.getAdapterPosition();


                DatabaseReference deleteDataPos = adapter.getRef(position);
                Log.v("print object", deleteDataPos.toString());
//                adapter.notifyItemRemoved(position);

                TextView subtotal = recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.card_product_subtotal);
                String subtotalStr = subtotal.getText().toString();
                subtotalStr = subtotalStr.replaceAll("Subtotal: ", "");
                subtotalStr = subtotalStr.replaceAll(" CAD", "");
                totalPrice = totalPrice - Float.parseFloat(subtotalStr);

                totalAmount.setText("Total Price: "+ String.format("%.2f",totalPrice) + " CAD");
                checkEmptyCart();


                deleteDataPos.removeValue();





            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .addSwipeLeftActionIcon(R.drawable.ic_cart_remove_item)

                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




    }



}

