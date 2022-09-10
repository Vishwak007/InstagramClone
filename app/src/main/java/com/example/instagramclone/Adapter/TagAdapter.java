package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTagList;
    private  List<String> mCountList;
    private DatabaseReference myRef;

    public TagAdapter(Context mContext, List<String> mTagList, List<String> mCountList) {
        this.mContext = mContext;
        this.mTagList = mTagList;
        this.mCountList = mCountList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.activity_tag, parent, false);
        return new TagAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        holder.hashTag.setText(mTagList.get(position));
        holder.postCount.setText(mCountList.get(position));

    }




    @Override
    public int getItemCount() {
        return mTagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView hashTag;
        TextView postCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            hashTag = itemView.findViewById(R.id.hash_tag);
            postCount = itemView.findViewById(R.id.no_of_posts);

        }

    }
    public void filter(List<String> filterTag, List<String> filterTagCount){
        this.mTagList= filterTag;
        this.mCountList= filterTagCount;

        notifyDataSetChanged();



    }
}
