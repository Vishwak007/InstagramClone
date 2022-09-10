package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Model.Notification;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.example.instagramclone.R;
import com.example.instagramclone.fragment.PostDetailsFragment;
import com.example.instagramclone.fragment.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NotificatonAdapter extends RecyclerView.Adapter<NotificatonAdapter.ViewHolder> {

    Context mContext;
    List<Notification> notList;

    public NotificatonAdapter(Context mContext, List<Notification> notList) {
        this.mContext = mContext;
        this.notList = notList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificatonAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification = notList.get(position);
        getUser(holder.imageProfile, holder.userName, notification.getUserId());

        holder.comment.setText(notification.getText());

        if (notification.isPost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostId());
        }else{
            holder.postImage.setVisibility(View.GONE); // this will also remove the space reserved for post image;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isPost()){
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            .putString("postId", notification.getPostId()).apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailsFragment()).commit();
                }else{
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                            .putString("profileId", notification.getUserId()).apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }

    private void getPostImage(final ImageView postImage, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(final ImageView imageProfile, final TextView userName, String userId) {

        FirebaseDatabase.getInstance().getReference().child("User").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")){
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }

                userName.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return notList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public  ImageView postImage;
        public TextView userName;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_not);
            postImage = itemView.findViewById(R.id.postImage_not);
            userName = itemView.findViewById(R.id.username_not);
            comment =itemView.findViewById(R.id.comment_not);



        }
    }
}
