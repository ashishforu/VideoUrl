package com.moneyhawk.videobaseapp.model;

import java.util.ArrayList;

public class ListModel {
    public boolean status;
    public String message;
    public ArrayList<AppName> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<AppName> getData() {
        return data;
    }

    public void setData(ArrayList<AppName> data) {
        this.data = data;
    }
}
