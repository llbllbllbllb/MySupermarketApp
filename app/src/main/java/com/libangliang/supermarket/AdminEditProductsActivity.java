package com.libangliang.supermarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminEditProductsActivity extends AppCompatActivity {

    private Button applybtn;
    private EditText name, price, description;
    private ImageView imageView;

    private String productID = "";

    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_products);

        productID = getIntent().getStringExtra("pid");

        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applybtn = findViewById(R.id.admin_edit_products_save_btn);
        name = findViewById(R.id.admin_edit_products_card_product_name);
        price = findViewById(R.id.admin_edit_products_card_product_price);
        description = findViewById(R.id.admin_edit_products_card_product_description);
        imageView = findViewById(R.id.admin_edit_products_card_product_image);


        displayProductInfo();

        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChange();
            }
        });




    }

    private void applyChange() {
        String pname = name.getText().toString();
        String pprice = price.getText().toString();
        String pdescription = description.getText().toString();

        if(pname.equals("")){
            Toast.makeText(this,"Empty Name",Toast.LENGTH_SHORT).show();
        }
        else if(pprice.equals("")){
            Toast.makeText(this,"Empty Price",Toast.LENGTH_SHORT).show();
        }
        else if(pdescription.equals("")){
            Toast.makeText(this,"Empty Description",Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",pdescription);
            productMap.put("price",pprice);
            productMap.put("name",pname);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminEditProductsActivity.this,"Changes Applied Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminEditProductsActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else{

                    }

                }
            });
        }


    }

    private void displayProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //retrive data
                    String productName = dataSnapshot.child("name").getValue().toString();
                    String productPrice = dataSnapshot.child("price").getValue().toString();
                    String productDescription = dataSnapshot.child("description").getValue().toString();
                    String productImage = dataSnapshot.child("image").getValue().toString();

                    name.setText(productName);
                    price.setText(productPrice);
                    description.setText(productDescription);
                    Picasso.get().load(productImage).into(imageView);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
