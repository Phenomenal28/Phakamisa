package com.example.phakamisa.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import com.example.phakamisa.post.PostAdapter;

public class ProfileActivity extends AppCompatActivity {

    private String userId;
    private PostAdapter postAdapter;

    private TextView userBioTextView;
    private ImageView userProfileImageView;
    private ImageView userCoverImageView;
    private RecyclerView postRecycler;

    //private Object userModel;

    UserModel userModel = new UserModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_profile);
        loadUserData();
        loadPosts();

        userBioTextView = findViewById(R.id.userBio);
        userProfileImageView = findViewById(R.id.userProfile);
        userCoverImageView = findViewById(R.id.coverPhoto);
        postRecycler = findViewById(R.id.postsRecycler);

        TextView usernameTextView = findViewById(R.id.userName);
        ImageView profileImageView = findViewById(R.id.userProfile);

        postAdapter = new PostAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        postRecycler.setAdapter(postAdapter);
        postRecycler.setLayoutManager(layoutManager);

        userId= getIntent().getStringExtra("id");

    }


    //user details
    private void loadUserData() {

        TextView userNameTextView = findViewById(R.id.userName);
        TextView userBioTextView = findViewById(R.id.userBio);
        ImageView userProfileImageView = findViewById(R.id.userProfile);
        ImageView userCoverImageView = findViewById(R.id.coverPhoto);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String displayName = currentUser.getDisplayName();

            if(displayName != null){
                userNameTextView.setText(displayName);
            } else{
                Toast.makeText(ProfileActivity.this, "Username is available", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(ProfileActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel != null) {
                        String userName = userModel.getUserName();
                        if (userName != null) {
                            userNameTextView.setText(userName);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Username is not available", Toast.LENGTH_SHORT).show();
                        }

                        String userBio = userModel.getUserBio();
                        if (userBio != null && !userBio.isEmpty()) {
                            userBioTextView.setText(userBio);
                        } else {
                            userBioTextView.setText(getString(R.string.bio));
                        }

                        String userProfile = userModel.getUserProfile();
                        if (userProfile != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(userProfile)
                                    .into(userProfileImageView);
                        }

                        String userCover = userModel.getUserCover();
                        if (userCover != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(userCover)
                                    .into(userCoverImageView);
                        }
                    }
                });


    }


    //loading a user posts
    private void loadPosts(){
        FirebaseFirestore.getInstance()
                .collection("Posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        postAdapter.clearPost();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot ds:dsList){
                            PostModel postModel = ds.toObject(PostModel.class);
                            postAdapter.addPost(postModel);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }

