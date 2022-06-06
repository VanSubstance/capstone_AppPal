package com.example.apppal;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestureRecognitionSocket extends Thread {
    private static Socket socket;
    private static ObjectInputStream is;
    private static ObjectOutputStream os;
    public void run() {
        connect();
    }
    public void connect() {
        try {
            Log.i("checkpoint", "socket connecting...");
            socket = new Socket(Utils.PYTHON_SERVER_URL, Utils.GESTURE_SOCKET_PORT);

            os = new ObjectOutputStream(socket.getOutputStream());
            os.writeObject("Hello!");
            os.flush();
            Log.d("ClientStream", "Sent to server.");

            is = new ObjectInputStream(socket.getInputStream());
            Object input = is.readObject();
            Log.d("ClientThread", "Received data: " + input);

        } catch (IOException | ClassNotFoundException e) {
            Log.e("socket", e.toString());
            e.printStackTrace();
        }
    }

    public void sendHelloToServer() {
        byte[] byteArr = null;
        String msg = "Hello Server";
        try {
            byteArr = msg.getBytes("utf-8");
            os.writeObject(byteArr);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
