package f.ox.kr;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

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
	
	private String getParameter(String parameter) {
		Map<String,String> parameterMap = new HashMap<>();
		
		StringTokenizer st = new StringTokenizer(parameter);
		String[] param = {};
		while(st.hasMoreTokens()) {
			st.nextToken("&");
			
			
//			param = st.nextToken("&").split("=");
//			parameterMap.putIfAbsent(param[0], param.length > 0 ? param[1] : "");
		}

		parameterMap.keySet().forEach(parameterKey->{
			log.info("{} : {}", parameterKey, parameterMap.get(parameterKey));
		});
		
		parameterMap.compute("password", (key, val)  -> val.toUpperCase());         

		parameterMap.keySet().forEach(parameterKey->{
			log.info("{} : {}", parameterKey, parameterMap.get(parameterKey));
		});
		
		return "";
	}
	
	public void run() {
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			DataOutputStream dos = new DataOutputStream(out);
			
			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			String method = st.nextToken();
			String originPath = st.nextToken();
			String path = originPath;
			String protocol = st.nextToken();
			String parameter = "";

			int idx = path.indexOf("?");
			if( idx > 0 ) {
				parameter = getParameter(path.substring(idx+1));
				path = path.substring(0, idx);	
			}
						
			log.info("{} {} {} {}", method, path, parameter ,protocol);
			
			String line = null;
			Map<String,String> headers = new HashMap<String,String>();
			while((line = br.readLine()).length() > 0) {
				StringTokenizer header = new StringTokenizer(line, ":");
				headers.put(header.nextToken().trim(), header.nextToken().trim());
			}

			if( !originPath.contentEquals("/favicon.ico") ) {
				headers.keySet().forEach(key->{ log.info("{} : {}", key, headers.get(key)); });	
			}
			
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
