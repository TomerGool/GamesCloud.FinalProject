package com.example.android1finalproject.models;

import java.util.ArrayList;

public class Request {
    private String name;
    private String status;
    private String userId;



    public Request(String name, String status, String userId) {
        this.name = name;
        this.status = status;
        this.userId = userId;

    }





    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
