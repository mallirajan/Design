package com.malli.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service implements AppConstants {

    private final IBinder mBinder = new MyBinder();

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected void start(DownloadFile file) {
        new FileDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
    }

    protected void sendStatus(int downloadID, int downloaded, int total) {
        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        intent.putExtra(DOWNLOAD_ID, downloadID);
        intent.putExtra(TOTAL_BYTES, total);
        intent.putExtra(DOWNLOADED_BYTES, downloaded);
        intent.putExtra(RESULT, RESULT_INPROGRESS);
        sendBroadcast(intent);
    }

    protected void sendResult(DownloadFile file, int id) {
        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        intent.putExtra(DOWNLOAD_ID, id);
        if (file == null) {
            intent.putExtra(RESULT, RESULT_FAILURE);
        } else {
            intent.putExtra(DOWNLOAD_ID, file.mId);
            intent.putExtra(DOWNLOAD_FILE_NAME, file.mFilePath);
            intent.putExtra(RESULT, RESULT_SUCCESS);
        }
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }

    public class FileDownloader extends AsyncTask<DownloadFile, Integer, DownloadFile> {

        private int id, count;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            count = 0;
        }

        @Override
        protected DownloadFile doInBackground(DownloadFile... file) {
            id = file[0].mId;
            String link = file[0].mDownloadUrl;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            boolean isCancelled = false;
            try {
                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                int fileLength = connection.getContentLength();
                connection.setConnectTimeout(TIMEOUT);
                if (fileLength > 0)
                    Log.d("", "Malli:" + fileLength);
                input = connection.getInputStream();
                output = new FileOutputStream(file[0].mFilePath, false);

                byte data[] = new byte[FILE_CHUNK_SIZE];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // cancel
                    if (isCancelled) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publish the progress....
                    if (fileLength > 0) {
                        publishProgress(file[0].mId, (int) total, fileLength);
                    }
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return file[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            sendStatus(values[0], values[1], values[2]);
        }

        @Override
        protected void onPostExecute(DownloadFile file) {

            sendResult(file, id);
        }
    }
}
