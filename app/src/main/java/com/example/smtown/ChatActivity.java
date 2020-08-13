package com.example.smtown;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ArrayList<Chat> arrayChat=new ArrayList<>();
    EditText edtContent;
    ImageView btnSend;
    RecyclerView listChat;

    FirebaseFirestore fStore;

    String strEmail;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ChatAdapter chatAdapter;
    Intent intent;
    Spanned placeName;
    String fStoreNickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        strEmail=user.getEmail();

        fStore.collection("users")
                .whereEqualTo("email", strEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fStoreNickname = (String) document.get("nickname");
                                System.out.println(fStoreNickname+"--------------테스트--------------------");

                            }
                        }
                    }
                });
        edtContent=findViewById(R.id.edtContent);
        listChat=findViewById(R.id.listChat);
        chatAdapter=new ChatAdapter(this, arrayChat, fStoreNickname);
        listChat.setLayoutManager(new LinearLayoutManager(this));
        listChat.setAdapter(chatAdapter);

        intent = getIntent();
        placeName = Html.fromHtml(intent.getStringExtra("place_name"));

        getSupportActionBar().setTitle("한줄 평 :" + placeName);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("Rating_"+placeName);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat=dataSnapshot.getValue(Chat.class);
                arrayChat.add(chat);

                listChat.scrollToPosition(arrayChat.size()-1);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSend=findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContent=edtContent.getText().toString();
                if(strContent.equals("")){
                    Toast.makeText(ChatActivity.this,
                            "내용을 입력하세요!", Toast.LENGTH_SHORT).show();
                }else{
                    Chat chat=new Chat();
                    chat.setNickName(fStoreNickname);
                    chat.setContent(strContent);
                    SimpleDateFormat sdf=new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    String strDate=sdf.format(new Date());
                    chat.setWdate(strDate);

                    myRef=database.getReference("Rating_" + placeName).child(strDate);
                    myRef.setValue(chat);
                    edtContent.setText("");
                }
            }
        });


    }
}