package com.example.phakamisa.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phakamisa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{

    private Context context;
    private List<CommentModel> postModelList;

    public CommentsAdapter(Context context){
        this.context = context;
        postModelList = new ArrayList<>();
    }

    public void addPost(CommentModel commentModel){
        postModelList.add(commentModel);
        notifyDataSetChanged();

    }

    public void clearPost(){

        postModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comment_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommentModel commentModel = postModelList.get(position);
        holder.comment.setText(commentModel.getComment());


        String uid = commentModel.getUserId();
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                if (userModel.getUserProfile() != null) {
                                    Glide.with(context).load(userModel.getUserProfile()).into(holder.userProfile);
                                }
                                if (userModel.getUserName() != null) {

                                    holder.userName.setText(userModel.getUserName());
                                    holder.userName.setText("Unknown User");
                                }

                            } else {
                                holder.userProfile.setImageResource(R.drawable.ic_person_2_24);
                                holder.userName.setText("User Not Found");

                            }
                        } else {

                        }
                    }
                });
    }


    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, comment;
        private ImageView userProfile;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userProfile = itemView.findViewById(R.id.userProfile);
            comment= itemView.findViewById(R.id.comment);


        }

    }

}
