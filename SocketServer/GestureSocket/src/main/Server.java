package main;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Server {

	private static final Logger logger = Logger.getGlobal();
	private static final int PORT_NUMBER = 4000;

	public Server() {
		// Set RSA Key pair
		try {
			ServerDatabase.SERVER_ADDRESS = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger.info(":::------------------------------------------------:::");
		logger.info(":::       Socket Application  Process Start        :::");
		logger.info(":::       SERVER IP ADDRESS: " + ServerDatabase.SERVER_ADDRESS + "          :::");
		logger.info(":::       PORT NUMBER: " + PORT_NUMBER + "                        :::");
		logger.info(":::------------------------------------------------:::");

		try (ServerSocket server = new ServerSocket(PORT_NUMBER)) {
			while (true) {
				Socket connection = server.accept();
				String clientIp = connection.getInetAddress().getHostAddress();
				System.out.println("client has been connected :: " + clientIp);
				Thread unitThread = new UnitThread(connection);
				unitThread.start();
			}
		} catch (IOException e) {
			logger.severe(e.toString());
		}
	}
}
