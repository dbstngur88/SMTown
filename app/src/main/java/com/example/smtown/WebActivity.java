package com.example.smtown;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebActivity extends AppCompatActivity {
    ProgressDialog progress;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        //액션바 안보이게 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        String strLink = intent.getStringExtra("place_url");


        WebView web = (WebView)findViewById(R.id.web);

        progress = new ProgressDialog(this);
        progress.setMessage("페이지를 불러오는 중입니다..");
        progress.show();
        webSettings=web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.setWebViewClient(new MyWebView());
        web.loadUrl(strLink);
    }

    public class MyWebView extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            System.out.println(url +"-----------------------테스트--------------------");
            return super.shouldOverrideUrlLoading(view,url);
        }
        public void onPageFinished(WebView view, String url){
            progress.dismiss();
            super.onPageFinished(view,url);
        }
    }
}