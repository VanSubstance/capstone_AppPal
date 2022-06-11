package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.example.apppal.VO.CoordinateInfo;
import com.example.apppal.VO.SocketFunctionType;

public class UnitThread extends Thread {
	private Socket socket;
	HashMap<SocketFunctionType, Object> incomingData;
	private ObjectInputStream incomingBuffer;

	public UnitThread(Socket socket) {
		System.out.println("Message transfer thread on load.");
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			incomingBuffer = new ObjectInputStream(socket.getInputStream());
		} catch (SocketException e1) {
			System.out.println("Socket errer.\n" + e1);
			System.out.println("Client disconnected.");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.interrupt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.interrupt();
		}
		while (true) {
			try {
				incomingData = (HashMap<SocketFunctionType, Object>) incomingBuffer.readObject();
				for (Entry<SocketFunctionType, Object> item : incomingData.entrySet()) {
					Object value = item.getValue();
					switch (item.getKey()) {
					case COORDINATE:
						System.out.println("Received Coordinates List for single hand :: " + (ArrayList<CoordinateInfo>) value);
						break;
					case NOTICE:
						break;
					case STRING:
						System.out.println("Incomming string :: " + (String) value);
						break;
					default:
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		this.interrupt();
	}
}