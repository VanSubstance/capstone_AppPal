package com.example.apppal;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {
    public static void checkConnection() {
        StringBuilder checkRes = new StringBuilder();
        try {
            URL url = null;
            url = new URL(Utils.PYTHON_SERVER_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null) {
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    String temp = null;
                    while (true) {
                        temp = rd.readLine();
                        if (temp == null) {
                            break;
                        }
                        checkRes.append(Utils.PYTHON_SERVER_URL + " :: " + temp);
                    }
                    rd.close();
                    connection.disconnect();
                } else {
                    Log.i("i", Utils.PYTHON_SERVER_URL + " :: " + connection.getResponseCode());
                }
            }
            Log.i("python-con", checkRes.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decideGesture() {
        String result = null;
        try {
            URL url = null;
            url = new URL(Utils.PYTHON_SERVER_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null) {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    String temp = null;
                    while (true) {
                        temp = rd.readLine();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
