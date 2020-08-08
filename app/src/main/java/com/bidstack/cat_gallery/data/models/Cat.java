package com.bidstack.cat_gallery.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Cat implements Parcelable {

    private String id;
    private String url;
    private int width;
    private int height;

    protected Cat(Parcel in) {
        id = in.readString();
        url = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<Cat> CREATOR = new Creator<Cat>() {
        @Override
        public Cat createFromParcel(Parcel in) {
            return new Cat(in);
        }

        @Override
        public Cat[] newArray(int size) {
            return new Cat[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeInt(width);
        parcel.writeInt(height);
    }
}