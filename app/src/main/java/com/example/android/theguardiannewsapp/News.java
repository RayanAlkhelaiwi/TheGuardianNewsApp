package com.example.android.theguardiannewsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rean on 10/13/2017.
 */

public class News implements Parcelable{

    private String mTitle;
    private String mSectionName;
    private String mDate;
    private String mUrl;

    public News(String title, String sectionName, String date, String url) {

        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
        mUrl = url;
    }

    protected News(Parcel in) {
        mTitle = in.readString();
        mSectionName = in.readString();
        mDate = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDate() {
        return mDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mSectionName);
        dest.writeString(mDate);
        dest.writeString(mUrl);
    }
}