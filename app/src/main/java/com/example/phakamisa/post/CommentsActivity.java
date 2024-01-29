package com.example.phakamisa.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;

public class CommentsActivity extends AppCompatActivity {

    private String postId;
    private EditText commentEd;

    private TextView sendComment;

    private CommentsAdapter commentsAdapter;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        postId = getIntent().getStringExtra("id");

        commentsAdapter = new CommentsAdapter(this);
       recyclerView = findViewById(R.id.recycler);

       recyclerView.setAdapter(commentsAdapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentEd = findViewById(R.id.commentEd);
        sendComment = findViewById(R.id.sendComment);
        loadComments();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEd.getText().toString();
                if (comment.trim().length() > 0) {
                    comment(comment);
                }
            }


        });
    }

    private void loadComments(){
        FirebaseFirestore
                .getInstance()
                .collection("Comments")
                .whereEqualTo("postId", postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        commentsAdapter.clearPost();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot ds:dsList){
                            CommentModel commentModel = ds.toObject(CommentModel.class);
                          commentsAdapter.addPost(commentModel);
                        }
                    }
                });
    }


    private void comment(String comment){
        String id = UUID.randomUUID().toString();
        CommentModel commentModel = new CommentModel(id, postId, FirebaseAuth.getInstance().getUid(), comment);
        FirebaseFirestore
                .getInstance()
                .collection("Comments")
                .document(id)
                .set(commentModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        commentsAdapter.addPost(commentModel);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CommentsActivity.this, "Could not add comment", Toast.LENGTH_SHORT).show();
                    }
                });




    }
}