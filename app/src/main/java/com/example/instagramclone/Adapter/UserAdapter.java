package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.example.instagramclone.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private Context mContext;
    boolean isFragment;

    private FirebaseUser fUser;

    public UserAdapter(List<User> users, Context mContext, boolean isFragment) {
        this.users = users;
        this.mContext = mContext;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, int position) { //ye values ko view holder me daal deta hai
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = users.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.userName.setText(user.getUsername());
        holder.name.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
        
        isFollowed(user.getUserid(), holder.btnFollow);

        if (user.getUserid().equals(fUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnFollow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid()).child("following")
                            .child(user.getUserid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference("Follow").child(user.getUserid())
                            .child("follower").child(fUser.getUid()).setValue(true);

                    addNotification(user.getUserid());
                }
                else{
                    FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid()).child("following").child(user.getUserid()).removeValue();
                    FirebaseDatabase.getInstance().getReference("Follow").child(user.getUserid()).child("follower").child(fUser.getUid()).removeValue();

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFragment){
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getUserid()).apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }else{
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherId", user.getUserid());
                    mContext.startActivity(intent);
                }
            }
        });




    }

    private void addNotification(String userid) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userid);
        map.put("text", "Started Following");
        map.put("postId", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(fUser.getUid()).push().setValue(map);
    }


    private void isFollowed(final String userid, final Button btnFollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    btnFollow.setText("following");
                }
                else{
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // humne ye class isliye extend ki hai ki onCreateView holder sirf view holder ko pass krta hai to hume view holder banana hga

        CircleImageView imageProfile;
        TextView userName;
        TextView name;
        Button btnFollow;

        public ViewHolder(@NonNull View itemView) {  // is method ko BindViewHolder apne andar leta hai aur iske variable ko overwright krke value daal deta hai
            super(itemView);                       // uske baad onCreateViewHolder ise apne andar le leta hai aur jise return karna ho use return kr deta hai..

            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.btnFollow);

            // values ko hum yha b daal sakte hai bind ki jarurat nahi hai par ye adapter poora hum nahi bana rahe hai hum sirf kutch methods ko overwright kar rahe hai
            // to agar hum yaha value de dete hai to bindholder khali reh jaaega aur method to pehle se hi hai hum use hata b ni sakte aur agar wo mehthod khali b reh jaae to koi
            // dikkat nahi hgi agar uska istemaal sirf yha hai to kyunki wo sirf over wright kar ra hai values aur kutch nahi par agar us method ka use kahi aur b ho ra hga to
            // to system error maar dega...

        }
    }

}
