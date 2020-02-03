package com.libangliang.supermarket.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.libangliang.supermarket.Prevalent.Prevalent;
import com.libangliang.supermarket.R;
import com.libangliang.supermarket.Settings;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    private CircleImageView profileImageView;
    private EditText phoneNumberInput, fullNameInput, addressInput;
    private Button updateButton;

    private Uri imageUri;
    private String imageUrl;
    private StorageReference storageProfileImageReference;
    private String checker = "";

    private StorageTask uploadTask;

    private ProgressBar progressBar;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);


        profileImageView = root.findViewById(R.id.settings_profile_image);
        phoneNumberInput = root.findViewById(R.id.settings_phone_number);
        fullNameInput = root.findViewById(R.id.settings_full_name);
        addressInput = root.findViewById(R.id.settings_address);
        updateButton = root.findViewById(R.id.settings_update_button);

        progressBar = root.findViewById(R.id.settings_progress_bar);

        storageProfileImageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");




        userInfoDisplay(profileImageView,fullNameInput,phoneNumberInput,addressInput);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker == "clicked"){
                    userInfoUpdate();
                }
                else{
                    updateUserInfoOnly();
                }

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send to select gallery
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(getContext(),SettingsFragment.this);
            }
        });




        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("App_debug","requestCode: "+requestCode);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
            Toast.makeText(getActivity(),"Test: Selected Image", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(),"Error: Please Try Again", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateUserInfoOnly() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullNameInput.getText().toString());
        userMap.put("address",addressInput.getText().toString());
        userMap.put("phoneOrder",phoneNumberInput.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        Toast.makeText(getActivity(),"Update Profile Successfully",Toast.LENGTH_SHORT).show();

    }

    private void userInfoUpdate() {
        if(TextUtils.isEmpty(fullNameInput.getText().toString())){
            Toast.makeText(getActivity(),"Please Enter Your Full Name",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(addressInput.getText().toString())){
            Toast.makeText(getActivity(),"Please Enter Your Address",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(phoneNumberInput.getText().toString())){
            Toast.makeText(getActivity(),"Please Enter Phone Number",Toast.LENGTH_SHORT);
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }


    }

    private void uploadImage() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if(imageUri != null){
            final StorageReference fileRef = storageProfileImageReference.child(Prevalent.currentOnlineUser.getPhone()+".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        imageUrl = downloadURL.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",fullNameInput.getText().toString());
                        userMap.put("address",addressInput.getText().toString());
                        userMap.put("phoneOrder",phoneNumberInput.getText().toString());
                        userMap.put("image",imageUrl);

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);

                        Toast.makeText(getActivity(),"Update Profile Successfully",Toast.LENGTH_SHORT).show();


                    }
                    else {
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
            });

        }
        else{
            Toast.makeText(getActivity(),"Image Not Selected",Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameInput, final EditText phoneNumberInput, final EditText addressInput) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //phone number exists
                if(dataSnapshot.exists()){
                    if(true){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameInput.setText(name);
                        addressInput.setText(address);
                        phoneNumberInput.setText(phone);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}