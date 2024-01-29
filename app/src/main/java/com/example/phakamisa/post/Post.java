package com.example.phakamisa.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Post extends AppCompatActivity {



private PostAdapter postAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ImageView imageView = findViewById(R.id.imageView);

        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        if(userModel != null && userModel.getUserProfile() != null){
                            ImageView imageView = findViewById(R.id.imageView);
                            Glide.with(Post.this).load(userModel.getUserProfile()).into(imageView);
                        }
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Post.this, ProfileActivity.class);
                intent.putExtra("id", FirebaseAuth.getInstance().getUid());
                startActivity(intent);
            }
        });

        postAdapter = new PostAdapter(this);
        RecyclerView postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPosts();


        View goCreatePostCV = findViewById(R.id.goCreatePost);

        goCreatePostCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Post.this, CreatePostActivity.class));
            }
        });

    }


    private void loadPosts(){
        FirebaseFirestore.getInstance().collection("Posts")
                .orderBy("postingTime", Query.Direction.DESCENDING)
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
                        Toast.makeText(Post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    }
