package com.gyl.rtmpplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gyl.view.ProgressWebView;

public class webActivity extends Activity {
    ProgressWebView wb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityweb);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        wb = (ProgressWebView)findViewById(R.id.webview);

        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        wb.getSettings().setAppCacheEnabled(false);
        wb.getSettings().setAllowFileAccess(true);
        wb.getSettings().setSupportZoom(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setAppCacheEnabled(true);

        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);

        wb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ;
            }
        });
        String url = "http://dvr.dragra.com";
        wb.loadUrl(url);

    }
}
