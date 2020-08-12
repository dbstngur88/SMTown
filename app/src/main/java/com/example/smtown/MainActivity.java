package com.example.smtown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

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

    TabLayout tab;
    ViewPager pager;

    String userEmail;
    String fStoreEmail;
    String fStoreNickname;


    ArrayList<Fragment> array;
    PageAdapter ad;

    ArrayList<HashMap<String,String>> arrayDaum = new ArrayList<>();
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 안보이게 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //로그인이 자동으로 성공할 경우 성공 문자열 넣기
        intent = getIntent();
        String getLoginData = intent.getStringExtra("autoLogin");
        if(getLoginData != null){
            Toast.makeText(MainActivity.this, "자동 로그인 성공", Toast.LENGTH_SHORT).show();
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = fAuth.getCurrentUser();
        userEmail = currentUser.getEmail();
        System.out.println(userEmail);


        signOut = findViewById(R.id.imgSignOut);
        viewWeater = findViewById(R.id.weather);

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
        new DaumThread().execute();

        tab=findViewById(R.id.tab);
        pager=findViewById(R.id.pager);

        tab.addTab(tab.newTab().setText("배달 맛집"));
        tab.addTab(tab.newTab().setText("방문 맛집"));
        tab.addTab(tab.newTab().setText("맛집 검색"));


        tab.getTabAt(0).setIcon(R.drawable.ic_deliver);
        tab.getTabAt(1).setIcon(R.drawable.ic_food);
        tab.getTabAt(2).setIcon(R.drawable.ic_search_map);

        array=new ArrayList<>();
        array.add(new DeliverFragment());
        array.add(new RestaurantFragment());
        array.add(new SearchFragment());

        ad = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(ad);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    //Daum Weather Thread
    class DaumThread extends AsyncTask<String,String,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            BackThread backThread = new BackThread();
            backThread.setDaemon(true);
            backThread.start();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc= Jsoup.connect("https://search.daum.net/search?w=tot&q=%EB%82%A0%EC%94%A8&DA=TMN").get();
                HashMap<String, String> map=new HashMap<String, String>();
                Elements elements=doc.select(".info_detail");
                map.put("part", elements.select(".tit_info").text());
                elements=doc.select(".wrap_today");
                map.put("temper", elements.select(".txt_temp").text());
                System.out.println(elements.select(".txt_temp").text() + " : 출력 여부 확인");
                map.put("ico", elements.select(".txt_weather").text());
                arrayDaum.add(map);

            }catch(Exception e) {
                System.out.println(e.toString());
            }
            return null;
        }
    }

    class BackThread extends Thread{
        @Override
        public void run() {
            super.run();
            index=0;
            while(true){
                handler.sendEmptyMessage(0);
                index++;
                if(index==arrayDaum.size()){
                    index=0;
                }
                try{Thread.sleep(2000);}catch(Exception e){}
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String part=arrayDaum.get(index).get("part");
            String temper=arrayDaum.get(index).get("temper");
            String ico=arrayDaum.get(index).get("ico");
            viewWeater.setText(part +"\n온도 : "+ temper+" / 날씨 : " + ico);
        }
    };

    class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return array.get(position);
        }

        @Override
        public int getCount() {
            return array.size();
        }
    }
}