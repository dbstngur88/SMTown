package com.example.smtown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    //현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;
    Intent intent;

    ImageView signOut;
    Button btnSignout;
    TextView viewWeater;

    LinearLayout drawerView;
    DrawerLayout drawerLayout;
    TextView drawerEmail, drawerIntroMsg;

    String userEmail;
    String fStoreEmail;
    String fStoreNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 안보이게 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = fAuth.getCurrentUser();
        userEmail = currentUser.getEmail();
        System.out.println(userEmail);


        signOut = findViewById(R.id.imgSignOut);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawerView);

        drawerEmail = drawerView.findViewById(R.id.email);
        drawerIntroMsg = drawerView.findViewById(R.id.introMsg);
        btnSignout = drawerView.findViewById(R.id.btnLogout);
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getInstance().signOut();
                // Check if user is signed in (non-null) and update UI accordingly.
                currentUser = fAuth.getCurrentUser();
                if(currentUser == null){
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("logout","logout");
                    startActivity(intent);
                    finish();
                }
            }
        });
        setUserInfo();
    }

    //로그인 되어있으면 currentUser 변수에 유저정보 할당. 아닌경우 login 페이지로 이동!
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = fAuth.getCurrentUser();
        if(currentUser == null){
            intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void mClick(View view) {
        switch (view.getId()){
            case R.id.imgSignOut :      //로그아웃
                fAuth.getInstance().signOut();
                // Check if user is signed in (non-null) and update UI accordingly.
                currentUser = fAuth.getCurrentUser();
                if(currentUser == null){
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("logout","logout");
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public void setUserInfo(){
        fStore.collection("users")     //TestCode, 수정필요(courseList ->competition)
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fStoreEmail = (String) document.get("email");
                                fStoreNickname = (String) document.get("nickname");

                                drawerEmail.setText(fStoreEmail) ;
                                drawerIntroMsg.setText(fStoreNickname+"님, 환영합니다!");
                            }
                        } else {
                            Toast.makeText(MainActivity.this,"Drawer Layout 출력 오류",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}