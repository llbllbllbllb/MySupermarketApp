package com.libangliang.supermarket;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.fragment.app.Fragment;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libangliang.supermarket.Model.Products;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.ViewHolder.ProductViewHolder;
import com.libangliang.supermarket.ui.cart.CartFragment;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView userProfileName;
    private CircleImageView userProfileImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        MainActivity.getInstance().finish();


        FloatingActionButton fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_orders,
                R.id.nav_categories, R.id.nav_settings,R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();



        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        View headerView = navigationView.getHeaderView(0);
        userProfileName = headerView.findViewById(R.id.user_profile_name);
        userProfileName.setText(Prevalent.currentOnlineUser.getName());
        userProfileImage = headerView.findViewById(R.id.user_profile_image);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(userProfileImage);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_cart);
            }
        });

        DisplaySelectedScreen(navController);




    }

    public void DisplaySelectedScreen(NavController navController) {

        String fragID = "";
        if (getIntent().hasExtra("frgToLoad")) {
            fragID = getIntent().getExtras().getString("frgToLoad");
            switch (fragID) {
                case "cart":
                    navController.navigate(R.id.nav_cart);

                    break;
            }
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
