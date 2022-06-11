package com.example.apppal;

import android.util.Log;

import static com.example.apppal.Storage.GlobalState.is;
import static com.example.apppal.Storage.GlobalState.os;
import com.example.apppal.VO.CoordinateInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class GestureRecognitionSocket extends Thread {
    private static Socket socket;
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


    public void sendCoordinateServer(float x, float y, float z, float visibility) throws IOException {
        CoordinateInfo send = new CoordinateInfo(x,y,z,visibility);
        String msg = "X : " + x + "Y : " + y + "Z : " + z + "VISIBILITY : " + visibility;
        Log.i("check",Charset.defaultCharset().toString());
        Log.i("SOCKET", msg);
        os.writeObject(send);
        os.flush();
    }

//    public void sendHelloToServer() {
//        byte[] byteArr = null;
//        String msg = "Hello Server";
//        try {
//            byteArr = msg.getBytes("utf-8");
//            os.writeObject(byteArr);
//            os.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
