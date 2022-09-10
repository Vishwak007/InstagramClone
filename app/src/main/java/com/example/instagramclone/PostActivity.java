package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.appcompat.widget.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;



public class PostActivity extends AppCompatActivity {
    private ImageView close, image_added;
    private TextView post;
    private SocialAutoCompleteTextView description;
    private StorageReference mStorageRef;
    private ArrayAdapter<String> des;
    String imageUrl;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CropImage.activity().start(PostActivity.this);
    }


    private void upload() {
        final ProgressDialog pD = new ProgressDialog(PostActivity.this);
        pD.setMessage("Uploading...");
        pD.show();

        if (imageUri != null) {
            String po = "Posts/";
            String po2 = System.currentTimeMillis() + "." + getExtension(imageUri);
            String pat = po.concat(po2);

//            mStorageRef.child(System.currentTimeMillis() + "." + getExtension(imageUri)).putFile(imageUri)

            final StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + ".jpeg");
            StorageTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
//                        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child("images").child(System.currentTimeMillis() + "." + getExtension(imageUri));
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                String postId = ref.push().getKey();
//                                String postId = ref.getName();

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("imageurl", imageUrl);
                                map.put("description", description.getText().toString());
                                map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                map.put("postid", postId);

                                ref.child(postId).setValue(map);

                                DatabaseReference mHashTag = FirebaseDatabase.getInstance().getReference("HashTag");

                                List<String> hashTag = description.getHashtags();

                                if (!hashTag.isEmpty()) {
                                    for (String tag : hashTag) {
                                        map.clear();
                                        map.put("Tag", tag.toLowerCase());
                                        map.put("postid", postId);

                                        mHashTag.child(tag.toLowerCase()).child(postId).setValue(map);
                                    }
                                }
                                pD.dismiss();
                                Intent intentBack = new Intent(PostActivity.this, MainActivity.class);
                                startActivity(intentBack);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pD.dismiss();
                                Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        pD.dismiss();
                        Toast.makeText(PostActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }



    }
    private String getExtension(Uri iUri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();

        return map.getExtensionFromMimeType(resolver.getType(iUri));// ye resolver yha se image ka uri lekar jaaega mime type ke database me aur \
                                                                    // uske map ke andar se extension utha laaega
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            imageUri = result.getUri();
            if (imageUri != null)
                Toast.makeText(this, "image_uri is available " , Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "image_uri is not available", Toast.LENGTH_SHORT).show();

            image_added.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Try Again...", Toast.LENGTH_SHORT).show();
            Intent intentFail = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intentFail);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayAdapter<Hashtag> des = new HashtagArrayAdapter<>(getApplicationContext());  // hashtag is the default class made for hashtag array adapter which comes with social auto complete.


        FirebaseDatabase.getInstance().getReference("Hashtag").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    des.add(new Hashtag(Objects.requireNonNull(snapshot.getKey()), (int) snapshot.getChildrenCount())); // hashtag class contains string and int value....
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        description.setHashtagAdapter(des);


    }
}