package com.malli.downloader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mallirajan on 12/24/2017.
 */

public class DownloadFile implements Parcelable {

    String mDownloadUrl;
    String mFilePath;
    int mId;

    protected DownloadFile(Parcel in) {
        mDownloadUrl = in.readString();
        mFilePath = in.readString();
        mId = in.readInt();
    }

    public DownloadFile(String mDownloadUrl, String mFilePath, int mId) {
        this.mDownloadUrl = mDownloadUrl;
        this.mFilePath = mFilePath;
        this.mId = mId;
    }

    public static final Creator<DownloadFile> CREATOR = new Creator<DownloadFile>() {
        @Override
        public DownloadFile createFromParcel(Parcel in) {
            return new DownloadFile(in);
        }

        @Override
        public DownloadFile[] newArray(int size) {
            return new DownloadFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mDownloadUrl);
        parcel.writeString(mFilePath);
        parcel.writeInt(mId);
    }
}
