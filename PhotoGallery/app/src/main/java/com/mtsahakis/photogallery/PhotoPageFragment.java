package com.mtsahakis.photogallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PhotoPageFragment extends VisibleFragment {

    public static final String ARG_URI = "ARG_URI";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Uri mUri;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_URI, uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mUri = bundle.getParcelable(ARG_URI);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // enable javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // set webviewclient
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!URLUtil.isNetworkUrl(url)) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                return false;
            }
        });
        // set webchromeclient
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setSubtitle(title);
                    }
                }
            }
        });
        // load url to webview
        mWebView.loadUrl(mUri.toString());

        return view;
    }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }
}
