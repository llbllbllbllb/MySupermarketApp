package com.libangliang.supermarket.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libangliang.supermarket.AdminEditProductsActivity;
import com.libangliang.supermarket.HomeActivity;
import com.libangliang.supermarket.Model.Products;
import com.libangliang.supermarket.ProductDetailsActivity;
import com.libangliang.supermarket.R;
import com.libangliang.supermarket.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private DatabaseReference productRef;
    private RecyclerView recyclerView;
    private RelativeLayout searchBar;

    private ImageButton serachBtn;
    private EditText searchInput;

    private String type = "";

    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;
    FirebaseRecyclerOptions<Products> options;

    HomeActivity homeAct;



    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            type = getActivity().getIntent().getExtras().get("Admin").toString();
        }


        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = root.findViewById(R.id.recycler_view_home);

//        searchBar = root.findViewById(R.id.search_bar_layout);
        serachBtn = root.findViewById(R.id.search_btn);
        searchInput = root.findViewById(R.id.search_input);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        homeAct = (HomeActivity) getActivity();
        homeAct.fab.setVisibility(FloatingActionButton.VISIBLE);


        serachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchIn = searchInput.getText().toString();
                setupSearchQuery(searchIn);
            }
        });


        return root;
    }

    private void setupSearchQuery(String searchIn) {
        FirebaseRecyclerOptions<Products> searchOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(productRef.orderByChild("name").startAt(searchIn),Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> searchAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(searchOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                holder.cardProductName.setText(model.getName());
                holder.cardProductPrice.setText(model.getPrice()+" CAD");
                holder.cardProductDescription.setText(model.getDescription());
                Log.v("download URL",model.getImage());
                Picasso.get().load(model.getImage()).into(holder.cardProductImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder viewHolder = new ProductViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(searchAdapter);
        searchAdapter.startListening();



    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(productRef,Products.class).build();
        adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.cardProductName.setText(model.getName());
                holder.cardProductPrice.setText(model.getPrice()+" CAD");
                holder.cardProductDescription.setText(model.getDescription());
                Log.v("download URL",model.getImage());
//                下载的URL: com.google.android.gms.tasks.zzu@fbd9960
                Picasso.get().load(model.getImage()).into(holder.cardProductImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(type.equals("Admin")){
                            Intent intent = new Intent(getActivity(), AdminEditProductsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }

                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder viewHolder = new ProductViewHolder(view);
                return viewHolder;
            }
        };
        //need to connect recyclerView and adapter
        recyclerView.setAdapter(adapter);
        adapter.startListening();




    }
}