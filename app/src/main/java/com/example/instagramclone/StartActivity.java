package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private ImageView icon;
    private LinearLayout linearLayout;
    private Button register, login;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        icon = findViewById(R.id.iconImage);
        linearLayout = findViewById(R.id.linearLayout);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        user = FirebaseAuth.getInstance().getCurrentUser();

        linearLayout.animate().alpha(0f).setDuration(10); // alpha is used for transparency ki kitna transparent rakhna hai 0-1 ke beech me , pele linear layout ko jaldi gayab karna hai

        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1500);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());

        icon.setAnimation(animation);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LogInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*Task is a stack of activities in (LIFO) type , when we have one stack jisme 4 activity hai aur usme agar hum clear top lagate hai to ,usme serecent ko chod ke baaki
                activities clear ho jaate hai , aur us task me sirf ek activity rehti hai wahi recent wali bs*/
                /* newtask & cleartask are  single task , jisme agar hum activity b me single task lagate hai to hum agar a->b->c->d, to jaise hi hum d se b me jaaenge to ek extra activity create
                nahi hgi wo c aur dko remove kar dega as resuul jo bachega wo a->b hga */
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // clear activity on top means ki main se jab register me jaaege to stack(jisse aage peeche jane wali list) me ab sirf register rahega aur baaki sab remove
                // ho jaaege means back krne par phr se main activity nahi aaegi
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null){  // instead of creating variable and then instance and then use that variable (user) , we can directly use Firebaseauth.getInstance().getCurrentUser();
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon.clearAnimation();
            icon.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }
    }
}