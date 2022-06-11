package com.example.apppal;

import android.util.Log;

import static com.example.apppal.Storage.GlobalState.is;
import static com.example.apppal.Storage.GlobalState.os;
import com.example.apppal.VO.CoordinateInfo;
import com.example.apppal.VO.SocketFunctionType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

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
            HashMap<SocketFunctionType, Object> req = new HashMap<>();
            req.put(SocketFunctionType.STRING, "Hello!");
            os.writeObject(req);
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

    public void sendHandCoordinatesToServer(ArrayList<CoordinateInfo> handCoorList) throws IOException {
        HashMap<SocketFunctionType, Object> req = new HashMap<>();
        req.put(SocketFunctionType.COORDINATE, handCoorList);
        os.writeObject(req);
        os.flush();
    }
}
