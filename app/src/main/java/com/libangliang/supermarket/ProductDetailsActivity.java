package com.libangliang.supermarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libangliang.supermarket.Model.Products;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private int minteger = 1;

    ;

    private Button addToCart;

    private ImageView productImage;
    private  TextView productPrice, productDescription, productName;

    private String productID = "";

    private FloatingActionButton goToCart;

    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("CartList");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productID = getIntent().getStringExtra("pid");


        addToCart = findViewById(R.id.add_to_cart);

        productImage = findViewById(R.id.product_details_image);
        productPrice = findViewById(R.id.product_details_price);
        productDescription = findViewById(R.id.product_details_description);
        productName = findViewById(R.id.product_details_name);
        goToCart = findViewById(R.id.product_details_go_to_cart);

        getProductDetail(productID);

        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                intent.putExtra("frgToLoad","cart");
                startActivity(intent);
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });


    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());



        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",minteger);
        cartMap.put("discount","");

        cartListRef.child("UserView").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    cartListRef.child("AdminView").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProductDetailsActivity.this,"Added to cart",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                }

            }
        });


    }

    private void getProductDetail(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products product = dataSnapshot.getValue(Products.class);

                    productName.setText(product.getName());
                    productPrice.setText(product.getPrice() + " CAD");
                    productDescription.setText(product.getDescription());
                    Picasso.get().load(product.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        if(minteger<1){
            minteger = 1;
        }
        display(minteger);

    }
    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        if(minteger<1){
            minteger = 1;
        }
        display(minteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }
}
