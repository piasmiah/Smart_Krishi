package com.trodev.smartkrishi.activity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.trodev.smartkrishi.R;

public class AllNewsActivity extends AppCompatActivity {

    private String url;
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_news);

        getSupportActionBar().setTitle("সংবাদ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webview= findViewById(R.id.newswebview);
        webview.setInitialScale(90);
        url = getIntent().getStringExtra("url");
        webview.loadUrl(url);

        WebSettings mywebsetting = webview.getSettings();
        mywebsetting.setJavaScriptEnabled(true);

        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setDatabaseEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebsetting.setDomStorageEnabled(true);
        mywebsetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mywebsetting.setUseWideViewPort(true);
        mywebsetting.setSavePassword(true);
        mywebsetting.setSaveFormData(true);
        mywebsetting.setEnableSmoothTransition(true);


        // ############################## Download Code is Here ####################################
        // Download any File in this website.
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //Checking runtime permission for devices above Marshmallow.
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AllNewsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    downloadDialog(url, userAgent, contentDisposition, mimetype);
                } else {
                    Toast.makeText(AllNewsActivity.this, "Permission granter", Toast.LENGTH_SHORT).show();
                    //requesting permissions.
                    ActivityCompat.requestPermissions(AllNewsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }

    public void downloadDialog(final String url, final String userAgent, String contentDisposition, String mimetype) {

        //getting filename from url.
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);

        //alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AllNewsActivity.this);

        //title of alertdialog
        builder.setTitle("Download File");

        //message of alertdialog
        builder.setMessage("Do you want to download " + filename);

        //if Yes button clicks.
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //DownloadManager.Request created with url.
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                //cookie
                String cookie = CookieManager.getInstance().getCookie(url);

                //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("User-Agent", userAgent);

                //file scanned by Media Scannar
                request.allowScanningByMediaScanner();

                //Download is visible and its progress, after completion too.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                //DownloadManager created
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                //Saving files in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                //download enqued
                downloadManager.enqueue(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //cancel the dialog if Cancel clicks
                dialog.cancel();
            }
        });

        //alertdialog shows.
        builder.create().show();
    }

    // One-BackPress Method is here....
    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {

            webview.goBack();
            webview.clearCache(true);

        } else {

            super.onBackPressed();

        }
    }
}