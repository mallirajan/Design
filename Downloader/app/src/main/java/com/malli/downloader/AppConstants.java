package com.malli.downloader;

/**
 * Created by Mallirajan on 12/22/2017.
 */

public interface AppConstants {
    // Intent Actions
    public static final String MY_ACTION = "my_action";
    // Intent Extras
    public static final String DOWNLOADED_BYTES = "downloadedbytes";
    public static final String TOTAL_BYTES = "totalbytes";
    public static final String DOWNLOAD_ID = "downloadid";
    public static final String DOWNLOAD_FILE_NAME = "filename";
    public static final String RESULT = "result";

    public static final String FILE_DOWNLOAD_DIRECTORY = "MyDemo";

    public static final int TIMEOUT = 5000;

    public static final int FILE_CHUNK_SIZE = 4096;

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILURE = 2;
    public static final int RESULT_INPROGRESS = 3;
}
