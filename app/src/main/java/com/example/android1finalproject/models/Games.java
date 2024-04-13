package com.example.android1finalproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Games implements Parcelable {
    private String name;

    private String added;
    private ArrayList<String> genres;
    private ArrayList<String> platform;
    private String image;





    public Games(String name, String added, ArrayList<String> genres, ArrayList<String> platform, String image) {
        this.name = name;
        this.added = added;
        this.genres = genres;
        this.platform = platform;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(added);
        parcel.writeStringList(platform);
        parcel.writeStringList(genres);
    }

    public static final Parcelable.Creator<Games> CREATOR = new Parcelable.Creator<Games>() {
        @Override
        public Games createFromParcel(Parcel in) {
            return new Games(in);
        }

        @Override
        public Games[] newArray(int size) {
            return new Games[size];
        }
    };


    protected Games(Parcel in) {
        name = in.readString();
        image = in.readString();
        added = in.readString();
        in.readStringList(platform);
        in.readStringList(genres);
        // Read other fields as needed
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getPlatform() {
        return platform;
    }

    public void setPlatform(ArrayList<String> platform) {
        this.platform = platform;
    }
}
