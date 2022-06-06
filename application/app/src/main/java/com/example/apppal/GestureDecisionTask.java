package com.example.apppal;

import android.os.AsyncTask;

public class GestureDecisionTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        ServerConnection.checkConnection();
        return null;
    }
}
