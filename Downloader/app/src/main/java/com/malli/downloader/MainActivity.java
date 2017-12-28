package com.malli.downloader;

/**
 * Created by Mallirajan on 12/22/2017.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ServiceConnection, AppConstants {

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 111;
    private DownloadService mDownloadService;
    private DownloadUpdatReceiver mDownloadUpdatReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    public void onDownload(View v) {
        findViewById(R.id.testView).setVisibility(View.INVISIBLE);
        findViewById(R.id.view).setVisibility(View.INVISIBLE);
        switch (v.getId()) {
            case R.id.testDownload:
                TextView textView = findViewById(R.id.textView);
                String testUrl = textView.getText().toString();
                if (testUrl.length() == 0) {
                    textView.setError("Enter valid URL to Download");
                } else {
                    DownloadFile testFile = new DownloadFile(testUrl, getAbsolutePath(String.valueOf(R.id.textView), "jpg"), R.id.textView);
                    downloadFileFromNetwork(testFile);
                }
                break;
            case R.id.download1:
                EditText editText1 = findViewById(R.id.editText1);
                String url = editText1.getText().toString();
                EditText editText2 = findViewById(R.id.editText2);
                String extension = editText2.getText().toString();
                if (url.length() == 0) {
                    editText1.setError("Enter valid URL to Download");
                } else {
                    DownloadFile file = new DownloadFile(url, getAbsolutePath(String.valueOf(R.id.editText1), extension), R.id.editText1);
                    downloadFileFromNetwork(file);
                }
                break;
        }
    }

    private void updateUI(String status, int downloaded, int total, int id) {
        switch (id) {
            case R.id.textView:
                ((TextView) findViewById(R.id.status)).setText(status);
                break;
            case R.id.editText1:
                ((TextView) findViewById(R.id.status1)).setText(status);
                break;
        }
    }

    private void downloadFileFromNetwork(DownloadFile file) {
        if (mDownloadService != null) {
            mDownloadService.start(file);
            switch (file.mId) {
                case R.id.textView:
                    Button button = findViewById(R.id.testDownload);
                    button.setEnabled(false);
                    button.setClickable(false);
                    break;
                case R.id.editText1:
                    findViewById(R.id.download1).setEnabled(false);
                    findViewById(R.id.download1).setClickable(false);
                    break;
            }
        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        DownloadService.MyBinder b = (DownloadService.MyBinder) iBinder;
        mDownloadService = b.getService();
        Toast.makeText(this, "Service connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mDownloadService = null;
    }

    private class DownloadUpdatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int downloaded = intent.getIntExtra(DOWNLOADED_BYTES, 0);
            int total = intent.getIntExtra(TOTAL_BYTES, 0);
            int downloadId = intent.getIntExtra(DOWNLOAD_ID, 0);
            int result = intent.getIntExtra(RESULT, 0);
            switch (result) {
                case RESULT_INPROGRESS:
                    updateUI((downloaded / 1024) + " kb/" + (total / 1024) + " kb", downloaded, total, downloadId);
                    break;
                case RESULT_SUCCESS:
                    String filepath = intent.getStringExtra(DOWNLOAD_FILE_NAME);
                    setViewResult(filepath, downloadId);
                    break;
                case RESULT_FAILURE:
                    updateUI("Download Failure", 0, 0, downloadId);
                    findViewById(R.id.download1).setEnabled(true);
                    findViewById(R.id.download1).setClickable(true);
                    break;

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDownloadUpdatReceiver = new DownloadUpdatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(mDownloadUpdatReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mDownloadUpdatReceiver);
    }

    private String getAbsolutePath(String filename, String extension) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_DOWNLOAD_DIRECTORY);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created");
        }
        return file.getAbsolutePath() + File.separator + filename + "." + extension;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Please grant permission to use this App", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            break;
        }
    }

    private void setViewResult(final String filepath, final int downloadId) {
        ImageButton btn = null;
        switch (downloadId) {
            case R.id.textView:
                btn = findViewById(R.id.testView);
                findViewById(R.id.testDownload).setEnabled(true);
                findViewById(R.id.testDownload).setClickable(true);
                break;
            case R.id.editText1:
                btn = findViewById(R.id.view);
                findViewById(R.id.download1).setEnabled(true);
                findViewById(R.id.download1).setClickable(true);
                break;
        }
        if (btn != null) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downloadId == R.id.textView) {
                        File file = new File(filepath);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + file), "image/jpeg");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "file path:Download\\MyDemo\\", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}