package com.example.phakamisa.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView profile;
    private View pickPhoto;
    private ImageView pickedImage;

    private Uri pickedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_create_post); //xml post
        loadUserData();

        pickPhoto = findViewById(R.id.pickPhoto);
        pickedImage = findViewById(R.id.pickedImage);

        profile = findViewById(R.id.userProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
            }
        });

        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


    }

    //user details
    private void loadUserData() {
        TextView userNameTV = findViewById(R.id.userName);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String displayName = currentUser.getDisplayName();

            if(displayName != null){
                userNameTV.setText(displayName);
            } else{
                Toast.makeText(CreatePostActivity.this, "Username is unavailable", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(CreatePostActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    if(userModel != null){
                        String userName = userModel.getUserName();
                        if(userName != null){
                            userNameTV.setText(userName);
                        } else{
                            Toast.makeText(CreatePostActivity.this, "Username is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.post) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Posting");
            progressDialog.show();

            String id = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Posts/" + id + "image.png");
            if (pickedImageUri != null) {

                //firestore upload code here
                storageReference.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                progressDialog.cancel();
                                finish();
                                Toast.makeText(CreatePostActivity.this, "Posted", Toast.LENGTH_SHORT).show();

                                EditText postText = findViewById(R.id.postText);
                                PostModel postModel = new PostModel(id, FirebaseAuth.getInstance().getUid(),
                                        postText.getText().toString(),
                                        uri.toString(), "0", "0", Calendar.getInstance().getTimeInMillis());

                                FirebaseFirestore.getInstance()
                                        .collection("Posts")
                                        .document(id)
                                        .set(postModel);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else {

                EditText postText = findViewById(R.id.postText);
                PostModel postModel = new PostModel(id, FirebaseAuth.getInstance().getUid(),
                        postText.getText().toString(),
                        null, "0", "0", Calendar.getInstance().getTimeInMillis());

                FirebaseFirestore
                        .getInstance()
                        .collection("Posts")
                        .document(id)
                        .set(postModel);

                progressDialog.cancel();
                finish();
                Toast.makeText(CreatePostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
            }
            return true;

        }

        return false;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                pickedImageUri = data.getData();
                pickedImage.setImageURI(pickedImageUri);
                Glide.with(CreatePostActivity.this).load(pickedImageUri).into(pickedImage);
            }else{
                Toast.makeText(this, "Image Not Picked", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
