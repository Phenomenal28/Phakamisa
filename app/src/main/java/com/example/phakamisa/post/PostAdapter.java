package com.example.phakamisa.post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{


    private Context context;
    private List<PostModel> postModelList;

    public PostAdapter(Context context){
        this.context = context;
        postModelList = new ArrayList<>();
    }

    public void addPost(PostModel postModel){
        postModelList.add(postModel);
        notifyDataSetChanged();

    }

    public void clearPost(){

        postModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostModel postModel = postModelList.get(position);
        if(postModel.getPostImage() != null){
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(postModel.getPostImage()).into(holder.postImage);
        }else{
            holder.postImage.setVisibility(View.GONE);
        }
        holder.postText.setText(postModel.getPostText());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentsActivity.class);
                intent.putExtra("id", postModel.getPostId());
                context.startActivity(intent);
            }
        });




        FirebaseFirestore.getInstance()
                .collection("Likes")
                .document(postModel.getPostId()+ FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot != null){
                            String data = documentSnapshot.getString("postId");
                            if(data != null){
                                postModel.setLiked(true);
                                holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_blue_24); //post is liked
                            } else{
                                postModel.setLiked(false);
                                holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_24); //post is not yet liked
                            }
                        } else{
                            postModel.setLiked(false);
                            holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        }

                    }

                });

        holder.clickProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", postModel.getUserId());
                context.startActivity(intent);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postModel.isLiked()){
                    postModel.setLiked(false);
                    holder.like.setImageResource((R.drawable.ic_baseline_thumb_up_24));
                    FirebaseFirestore
                            .getInstance()
                            .collection("Likes")
                            .document(postModel.getPostId()+ FirebaseAuth.getInstance().getUid())
                            .delete();
                }
                else{
                    postModel.setLiked(true);
                    holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_blue_24);
                    FirebaseFirestore
                            .getInstance()
                            .collection("Likes")
                            .document(postModel.getPostId()+ FirebaseAuth.getInstance().getUid())
                            .set(new PostModel("hi"));
                }
            }
        });


        //user details
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String displayName = currentUser.getDisplayName();

            if(displayName != null){
                holder.userName.setText(displayName);
            } else{
            }
        } else{

        }

        String uid = postModel.getUserId();
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel.getUserProfile() != null) {
                                String userProfile = userModel.getUserProfile();
                                if (userProfile != null) {
                                    holder.userName.setText(userProfile);

                                    Glide.with(context).load(userModel.getUserProfile()).into(holder.userProfile);
                                }
                                String userName = userModel.getUserName();
                                if (userName != null) {
                                    holder.userName.setText(userName);

                                }
                            } else {
                                holder.userProfile.setImageResource(R.drawable.ic_person_2_24);
                               holder.userName.setText("Anonymous User");//change this to anonymous


                            }
                        } else {
                            holder.userProfile.setImageResource(R.drawable.ic_person_2_24);
                            holder.userName.setText("Anonymous User");

                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, postText;
        private ImageView userProfile, postImage, like, comment;
        private RelativeLayout clickProfile;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userProfile = itemView.findViewById(R.id.userProfile);
            postText= itemView.findViewById(R.id.postText);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            clickProfile = itemView.findViewById(R.id.clickProfile);



        }

    }

}
