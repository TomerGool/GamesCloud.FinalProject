package com.example.android1finalproject.models;

import java.util.ArrayList;

public class SearchUsersModel {
    private String username;
    private String userId;
    private String email;
    private String phone;
    private ArrayList<String> favoriteGames;
    private ArrayList<String> genres;
    private  ArrayList<String> platforms;
    private SearchUsersModel forUser;
    private SearchUsersModel forGame;

    public SearchUsersModel(SearchUsersModel forUser, SearchUsersModel forGame) {
        this.forUser = forUser;
        this.forGame = forGame;
    }

    public SearchUsersModel getForUser() {
        return forUser;
    }

    public void setForUser(SearchUsersModel forUser) {
        this.forUser = forUser;
    }

    public SearchUsersModel getForGame() {
        return forGame;
    }

    public void setForGame(SearchUsersModel forGame) {
        this.forGame = forGame;
    }

    public SearchUsersModel(String username, String userId, String email, String phone) {
        this.username = username;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
    }

    public SearchUsersModel(ArrayList<String> favoriteGames, ArrayList<String> genres, ArrayList<String> platforms) {
        this.favoriteGames = favoriteGames;
        this.genres = genres;
        this.platforms = platforms;
    }

    public SearchUsersModel(String username, String userId, String email, String phone,
                            ArrayList<String> favoriteGames, ArrayList<String> genres, ArrayList<String> platforms) {
        this.username = username;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.favoriteGames = favoriteGames;
        this.genres = genres;
        this.platforms = platforms;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(ArrayList<String> favoriteGames) {
        this.favoriteGames = favoriteGames;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(ArrayList<String> platforms) {
        this.platforms = platforms;
    }
}
