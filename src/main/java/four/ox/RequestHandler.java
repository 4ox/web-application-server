package four.ox;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	private String getPath(String filePath) {
		if( filePath.contentEquals("") || filePath.contentEquals("/") ) {
			filePath = "/index.html";
		}
		return Paths.get("").toAbsolutePath().toString() + "/webapp" + filePath;
	}
	
	public void run() {
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			String method = st.nextToken();
			String path = st.nextToken();
			String protocol = st.nextToken();
			
			DataOutputStream dos = new DataOutputStream(out);
			Path filePath = Paths.get( getPath(path) );
			if( Files.exists(filePath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }) ) {
				byte[] content = Files.readAllBytes(filePath);
				responseHeader(dos, 200, content.length);
				responseBody(dos, content);
			}
			else {
				byte[] content = "No no".getBytes();
				responseHeader(dos, 404, content.length);
				responseBody(dos, content);				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseHeader(DataOutputStream dos, int httpStatus, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 "+ String.valueOf(httpStatus) +" \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
