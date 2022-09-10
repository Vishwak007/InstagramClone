package com.example.instagramclone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagramclone.Adapter.PhotoAdapter;
import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.EditProfileActivity;
import com.example.instagramclone.FollowersActivity;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.OptionActivity;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    RecyclerView recyclerViewSaved;
    PhotoAdapter savedPhotoAdapter;
    List<Post> savedPostList;

    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Post> postList;

    private CircleImageView imageProfile;
    private ImageView option;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView bio;
    private TextView username;
    private TextView fullname;

    private ImageView myPicture;
    private ImageView savedPicture;

    Button editProfile;

    FirebaseUser fUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")){
            profileId = fUser.getUid();
        }else{
            profileId = data;
        }


        imageProfile = view.findViewById(R.id.image_profile_p);
        option = view.findViewById(R.id.option);
        posts = view.findViewById(R.id.post_p);
        followers = view.findViewById(R.id.follower_p);
        following = view.findViewById(R.id.following_p);
        fullname = view.findViewById(R.id.fullname_p);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username_p);
        myPicture = view.findViewById(R.id.my_picture);
        savedPicture = view.findViewById(R.id.saved_picture);
        editProfile = view.findViewById(R.id.edit_profile_p);

        recyclerViewSaved = view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaved.setHasFixedSize(true);
        recyclerViewSaved.setLayoutManager(new GridLayoutManager(getContext(), 3));

        savedPostList = new ArrayList<>();
        savedPhotoAdapter = new PhotoAdapter(getContext(), savedPostList);
        recyclerViewSaved.setAdapter(savedPhotoAdapter);

        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),postList);
        recyclerView.setAdapter(photoAdapter);

        userInfo();
        followerAndFollowingCount();
        getPostCount();
        myPhotos();
        getSaved();

        if (profileId.equals(fUser.getUid())){
            editProfile.setText("Edit Profile");
        }else{
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText = editProfile.getText().toString();
                if (btnText.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else{
                    if (btnText.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("follower").child(fUser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("follower").child(fUser.getUid()).removeValue();
                    }
                }
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaved.setVisibility(View.GONE);

        myPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaved.setVisibility(View.GONE);

            }
        });

        savedPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaved.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OptionActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

    private void getSaved() {
        final List<String> postIdList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Save").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    postIdList.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedPostList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for (String item : postIdList){
                        if (post.getPostid().equals(item)){

                            savedPostList.add(post);
                        }
                    }

                }
                savedPhotoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPhotos() {


        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    editProfile.setText("following");
                }else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        counter++;
                    }
                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void followerAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText( "" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void userInfo(){
        FirebaseDatabase.getInstance().getReference().child("User").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null){
                    Picasso.get().load(user.getImageurl()).placeholder(R.drawable.ic_manpro).into(imageProfile);
                    username.setText(user.getUsername());
                    fullname.setText(user.getName());
                    bio.setText(user.getBio());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}