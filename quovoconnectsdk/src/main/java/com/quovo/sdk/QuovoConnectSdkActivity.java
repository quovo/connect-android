package com.quovo.sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovo.sdk.listeners.BroadCastManager;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity launches Webview with custom controls
 */
public class QuovoConnectSdkActivity extends AppCompatActivity implements View.OnClickListener {

    protected WebView webView;
    protected String url;
    protected TextView mTitleView;
    protected String titleText;
    protected Boolean isAllowAutoImageLoad = true;
    protected Boolean isAllowContentAccess;
    protected Boolean isAllowFileAccess;
    protected ImageButton mBtnClose;
    protected int animationCloseEnter;
    protected int animationCloseExit;
    private ProgressBar progressBar;
    private final String REGEX_RESPONSE_CHECK = "post-message:\\/\\/([^=]+)(={0,1})(\\S*)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quovo_web_view);
        initViews();
        initializeOptions();
    }

    /**
     * Initialize views with ids and listners
     */
    protected void initViews() {
        webView = findViewById(R.id.quovo_web_view);
        webView.setWebViewClient(new QuovoWebClient());
        webView.setWebChromeClient(new QuovoChromeClient());
        mTitleView = findViewById(R.id.title);
        mBtnClose = findViewById(R.id.close);
        mBtnClose.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * This methods takes care the data coming from builder and place it in to controller
     */
    protected void initializeOptions() {
        Intent intent = getIntent();
        if (intent == null) return;

        QuovoConnectSdk.Builder builder = (QuovoConnectSdk.Builder) intent.getSerializableExtra("builder");

        // set theme before resolving attributes depending on those
        setTheme(builder.theme != null ? builder.theme : 0);

        url = builder.url;
        titleText = builder.titleDefault;
        isAllowAutoImageLoad = builder.isAllowAutoImageLoad;
        isAllowContentAccess = builder.isAllowContentAccess;
        isAllowFileAccess = builder.isAllowFileAccess;

        animationCloseEnter = builder.animationCloseEnter != null ? builder.animationCloseEnter
                : R.anim.modal_activity_close_enter;
        animationCloseExit = builder.animationCloseExit != null ? builder.animationCloseExit
                : R.anim.modal_activity_close_exit;

        if (url != null)
            webView.loadUrl(url);
        Log.d("url:", url);
        mTitleView.setText(titleText);
        WebSettings webSettings = webView.getSettings();

        if (isAllowFileAccess != null) webSettings.setAllowFileAccess(isAllowFileAccess);
        if (isAllowContentAccess != null) webSettings.setAllowContentAccess(isAllowContentAccess);
        if (isAllowAutoImageLoad != null)
            webSettings.setLoadsImagesAutomatically(isAllowAutoImageLoad);
        webSettings.setJavaScriptEnabled(true);

        { // ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            float progressBarHeight = getResources().getDimension(R.dimen.defaultProgressBarHeight);
            progressBar.setMinimumHeight((int) progressBarHeight);
            CoordinatorLayout.LayoutParams params =
                    new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) progressBarHeight);
            float toolbarHeight = getResources().getDimension(R.dimen.toolbarHeight);
            params.setMargins(0, (int) toolbarHeight, 0, 0);
            progressBar.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            exitActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Exit activity with custom animations
     */
    protected void exitActivity() {
        super.onBackPressed();
        overridePendingTransition(animationCloseEnter, animationCloseExit);
    }

    /**
     * Custom webchrome client to support webview actions
     */
    private class QuovoChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress == 100) newProgress = 0;
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    /**
     * Custom webview client to support webview actions
     */
    private class QuovoWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            webClientResponseParsing(request.getUrl());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }


    }

    /**
     * Parsing the response coming from webview after onload
     *
     * @param url
     */
    private void webClientResponseParsing(Uri url) {
        Pattern callbackPattern = Pattern.compile(REGEX_RESPONSE_CHECK);
        Matcher match = callbackPattern.matcher(url.toString());
        if (match.matches()) {
            try {
                String command = match.group(1);
                String payloadJsonData = java.net.URLDecoder.decode(match.group(3), "UTF-8");
                if (!payloadJsonData.isEmpty())
                    BroadCastManager.onComplete(QuovoConnectSdkActivity.this, command, payloadJsonData);

                Log.d("result", payloadJsonData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadCastManager.unregister(QuovoConnectSdkActivity.this);
        if (webView == null) return;
        webView.onPause();
        destroyWebView();
    }

    // Wait for zoom control to fade away
    private void destroyWebView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webView != null) webView.destroy();
            }
        }, ViewConfiguration.getZoomControlsTimeout() + 1000L);
    }

}
