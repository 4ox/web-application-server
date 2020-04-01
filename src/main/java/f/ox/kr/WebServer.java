package f.ox.kr;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	private static final int DEFAULT_PORT = 8080;
	
	public static void main(String[] args) throws IOException {
		try(ServerSocket listenSocket = new ServerSocket(DEFAULT_PORT)) {
			log.info("Web application Server start {} port.", DEFAULT_PORT);
			Socket connection;
			while((connection = listenSocket.accept()) != null ) {
				RequestHandler requestHandler = new RequestHandler(connection);
				requestHandler.start();
			}
		}
	}
}
