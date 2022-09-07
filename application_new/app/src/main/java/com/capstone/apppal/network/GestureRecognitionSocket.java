package com.capstone.apppal.network;

import android.util.Log;

import com.capstone.apppal.VO.CoordinateInfo;
import com.capstone.apppal.network.handler.SocketReceiveHandler;
import com.capstone.apppal.utils.GlobalState;

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
            socket = new Socket(GlobalState.PYTHON_SERVER_URL, GlobalState.GESTURE_SOCKET_PORT);

            GlobalState.os = new OutputStreamWriter(socket.getOutputStream());


            sendDataToServer("check", "Connection Test");
            GlobalState.is = new InputStreamReader(socket.getInputStream());

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
            GlobalState.os.write(req.toString());
            GlobalState.os.flush();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
