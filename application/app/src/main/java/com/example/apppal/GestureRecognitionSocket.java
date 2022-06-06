package com.example.apppal;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URLEncoder;

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
            Log.d("ClientStream", "Sent to server.");

            is = new ObjectInputStream(socket.getInputStream());
            Object input = is.readUTF();
            Log.d("ClientThread", "Received data: " + input);

        } catch (IOException e) {
            Log.e("socket", e.toString());
            e.printStackTrace();
        }
    }

    public void sendToServer(Bitmap gestureData) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        gestureData.compress( Bitmap.CompressFormat.JPEG, 30, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        try {
            os.write(byteArray);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
