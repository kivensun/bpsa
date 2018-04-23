package com.sl.kiven.bpsa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sl.kiven.bpsa.utils.X5WebView;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private X5WebView mWebView;
    private Context mContext = null;


    private ProgressBar mPageLoadingProgressBar = null;
    private static final String mHomeUrl = "http://www.myfalcon.top:8200";
    private ViewGroup mViewParent;
    private boolean mNeedTestPage = false;

    private URL mIntentUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mContext = this;
        Intent intent = getIntent();
        if (intent != null) {
            try {
                mIntentUrl = new URL(intent.getData().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (Exception e) {
            }
        }
        //
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
        setContentView(R.layout.activity_main);
        mViewParent = (ViewGroup) findViewById(R.id.webView1);

        //mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
        init();

    }
    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
        //ProgressBar(getApplicationContext(),
        // null,
        // android.R.attr.progressBarStyleHorizontal);
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }

    private void init() {

        mWebView = new X5WebView(this, null);

        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        initProgressBar();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 50);// 5s?
                /* mWebView.showLog("test Log"); */
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;



            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);
                if (newProgress == 100) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                } else {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                    mPageLoadingProgressBar.setProgress(newProgress);//设置加载进度
                }
            }
        });


        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        if (mIntentUrl == null) {
            mWebView.loadUrl(mHomeUrl);
        } else {
            mWebView.loadUrl(mIntentUrl.toString());
        }
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.tbsSuiteExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void tbsSuiteExit() {
        // exit TbsSuite?
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("提示");
        dialog.setPositiveButton("OK", new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Process.killProcess(Process.myPid());
            }
        });
        dialog.setMessage("现在就退出吗?");
        dialog.create().show();
    }

    public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private int mCurrentUrl = mUrlStartNum;
    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;
                case MSG_INIT_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
