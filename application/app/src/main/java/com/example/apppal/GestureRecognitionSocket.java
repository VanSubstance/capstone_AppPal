package com.example.apppal;

import android.util.Log;

import static com.example.apppal.Storage.GlobalState.is;
import static com.example.apppal.Storage.GlobalState.os;

import com.example.apppal.Handler.SocketReceiveHandler;
import com.example.apppal.VO.CoordinateInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

public class GestureRecognitionSocket extends Thread {
    private static Socket socket;
    private static SocketReceiveHandler socketReceiveHandler;
    public void run() {
        connect();
    }
    public void connect() {
        try {
            Log.i("checkpoint", "socket connecting...");
            socket = new Socket(Utils.PYTHON_SERVER_URL, Utils.GESTURE_SOCKET_PORT);

            os = new OutputStreamWriter(socket.getOutputStream());


            sendDataToServer("check", "Connection Test");
            is = new InputStreamReader(socket.getInputStream());

            socketReceiveHandler = new SocketReceiveHandler();

        } catch (IOException  e) {
            Log.e("socket", e.toString());
            e.printStackTrace();
        }
    }

    public void sendHandCoordinatesToServer(ArrayList<CoordinateInfo> handCoorList) {
        ArrayList<String> jointList = new ArrayList<>();
        for (CoordinateInfo coor : handCoorList) {
            jointList.add(coor.toStringForJson());
        }
//        Log.e("joint lists", jointList.toString());
        sendDataToServer("gesture", jointList);
    }

    private static void sendDataToServer(String function, Object data) {
        JSONObject req = new JSONObject();
        try {
            req.put("function", function);
            req.put("data", data);
            os.write(req.toString());
            os.flush();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
