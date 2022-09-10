package com.example.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.TagAdapter;
import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<User> mUser;
    private UserAdapter adapter;
    private SocialAutoCompleteTextView searchBar;

    private RecyclerView recyclerViewTag;
    private TagAdapter tagAdapter;
    private List<String> mTag;
    private  List<String> mPostCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_user);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext()); // jo item ek baar dikh chuka h aur abi saamne nahi dikh ra hai list me to saamne wale ko dobara wahi item
                                                             // kab dikhana hai.  layout manager ka kaam hai ,, vertical aur horizontal ke liye Linear Layout Manager ka istemaal hta hai
        recyclerView.setLayoutManager(llm);


        recyclerViewTag = view.findViewById(R.id.recycler_view_tags);
        recyclerViewTag.setHasFixedSize(true);
        recyclerViewTag.setLayoutManager(new LinearLayoutManager(getContext()));

        mTag = new ArrayList<String>();
        mPostCount = new ArrayList<String>();
        tagAdapter = new TagAdapter(getContext(),mTag,mPostCount);



        mUser = new ArrayList<User>();
        adapter = new UserAdapter(mUser, getContext(),true);
        recyclerView.setAdapter(adapter);
        recyclerViewTag.setAdapter(tagAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        readUser();
        readTag();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Filter(editable.toString());

            }
        });

        return view;
    }

    private void readTag() {

        FirebaseDatabase.getInstance().getReference("HashTag").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){ // dataSnapshot means hashtag dataSnapshot.getChildren means #tag
                    mTag.add("# "+snapshot.getKey()); //  acatually snapshot is #tag ,, snapshot.getKey means #tag me jo naam save h;
                    mPostCount.add(snapshot.getChildrenCount() +" posts"); //snapshot is #tag , snapshot.getChildrenCount means  #tag ke andar jitne post hai uski counting counting..
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(searchBar.getText().toString())){
                    mUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class); // yha user.class daalne ka matlab hai value ko kis form me lena hai.. actuaaly me User class ka jo constructor hai
                        // public User(String username, String name, String email, String bio, String userid, String imageurl) uske saare attribute ko user ke andaar ki value se match krke
                        // value ko us construcor me daal dega ..... isiliye ye important hai ki jab hum constructor banae to uske attributes database ke User ke andar key se match kr rahe ho
                        //  kyunko User database ke andar ek dictionary bani hui hai jisme key hai aur value hai to wo key ko match krke value ko return kar dega
                        //  aur user naam ke object ke andar wo poora class aa jaaega jisme us user ki value dali hui hai..
                        mUser.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUser.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void Filter(String text){
        List<String> mSearchTag = new ArrayList<String>();
        List<String> mSearchTagCount = new ArrayList<String>();

        for (String item : mTag){
            if (item.toLowerCase().contains(text.toLowerCase())){
                mSearchTag.add(item);
                mSearchTagCount.add(mPostCount.get(mTag.indexOf(item)));
            }
        }
        tagAdapter.filter(mSearchTag, mSearchTagCount);

    }
}