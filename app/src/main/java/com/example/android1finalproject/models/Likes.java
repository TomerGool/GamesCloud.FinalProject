package com.example.android1finalproject.models;

import com.google.firebase.firestore.FieldValue;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Likes {
    private String username;
    private String game;
    private FieldValue Timestamp;
    private String userId;
    private Boolean isLiked;
    private String image;
    private String added;
    private ArrayList<String> genres;
    private ArrayList<String> platform;

    public Likes(String username, String game, FieldValue Timestamp, String userId, Boolean isLiked,
                 String image, String added, ArrayList<String> genres, ArrayList<String> platform)
    {
        this.username = username;
        this.game = game;
        this.Timestamp = Timestamp;
        this.userId = userId;
        this.isLiked = isLiked;
        this.image = image;
        this.added = added;
        this.genres = genres;
        this.platform = platform;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public ArrayList<String> getArrGenres() {
        return genres;
    }

    public void setArrGenres(ArrayList<String> arrGenres) {
        this.genres = arrGenres;
    }

    public ArrayList<String> getArrPlatform() {
        return platform;
    }

    public void setArrPlatform(ArrayList<String> arrPlatform) {
        this.platform = arrPlatform;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Likes(String username,String userId)
    {
        this.username = username;
        this.userId = userId;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameName() {
        return game;
    }

    public void setGameName(String gameName) {
        this.game = gameName;
    }

    public FieldValue getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(FieldValue timestamp) {
        this.Timestamp = timestamp;
    }
}
