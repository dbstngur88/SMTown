package com.example.smtown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail,txtPW;
    Button btnLogin, btnFindPW, btnRegister;
    String strEmail, strPW;
    private FirebaseAuth mAuth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //액션바 안보이게 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //파이어베이스 정보 가져오기
        mAuth = FirebaseAuth.getInstance();

        //전역변수 링크
        txtEmail = findViewById(R.id.email);
        txtPW = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnFindPW = findViewById(R.id.btnFindPW);
        btnRegister = findViewById(R.id.btnRegister);

        intent = getIntent();

        txtEmail.setText(intent.getStringExtra("Email"));
        txtPW.setText(intent.getStringExtra("Password"));
        strEmail = txtEmail.getText().toString();
        strPW = txtPW.getText().toString();
        if (TextUtils.isEmpty(strEmail) != true && TextUtils.isEmpty(strPW) != true) {
            Toast.makeText(LoginActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
        }
        String getResetData = intent.getStringExtra("resetPW");
        if(getResetData != null){
            Toast.makeText(LoginActivity.this, "비밀번호 재설정 이메일 전송을 완료했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginUser(String Email, String Password){
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"인증 실패, Email/PW를 확인하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin :        //로그인 버튼 누를 경우
                strEmail = txtEmail.getText().toString();
                strPW = txtPW.getText().toString();
                if (TextUtils.isEmpty(strEmail)) {
                    Toast.makeText(LoginActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strPW)) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strEmail.indexOf('@') < 0) {
                    Toast.makeText(LoginActivity.this, "이메일 형식이 올바르지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (strPW.length() < 8) {
                    Toast.makeText(LoginActivity.this, "비밀번호는 8자리 이상입니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(strEmail,strPW);
                }
                break;
            case R.id.btnRegister :
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnFindPW :
                intent = new Intent(LoginActivity.this, FindInfoActivity.class);
                startActivity(intent);
                break;
        }
    }

}