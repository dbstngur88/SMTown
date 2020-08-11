package com.example.smtown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FindInfoActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    String userEmail;
    Button changePW;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);

        //액션바 안보이게 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //파이어베이스 정보 가져오기
        fAuth = FirebaseAuth.getInstance();

        //전역변수 선언
        changePW = findViewById(R.id.btnChangePW);
        email = findViewById(R.id.email);

        userEmail = email.getText().toString();
    }

    public void mClick(View view) {
        userEmail = email.getText().toString();
        switch (view.getId()) {
            case R.id.btnChangePW :
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else if (userEmail.indexOf('@') < 0) {
                    Toast.makeText(this, "이메일 형식이 올바르지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    fAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // do something when mail was sent successfully.
                                        Intent intent = new Intent(FindInfoActivity.this,LoginActivity.class);
                                        intent.putExtra("resetPW","resetPW");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // ...
                                        Toast.makeText(FindInfoActivity.this, "이메일 전송을 실패하였습니다. 주소를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
        }
    }
}