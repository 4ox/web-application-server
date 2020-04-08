package f.ox.kr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	private final String DEFALT_ENCODING = "UTF-8";
	
	private String method;
	private String originPath;
	private String path;
	private String protocol;
	private Map<String,String> parameter = new HashMap<String,String>();
	private Map<String,String> headers = new HashMap<String,String>();
	
	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	private String getPath(String filePath) {
		if( filePath.contentEquals("") || filePath.contentEquals("/") ) {
			filePath = "/index.html";
		}
		return Paths.get("").toAbsolutePath().toString() + "/webapp" + filePath;
	}
	
	private Map<String,String> getParameter(String parameter) throws UnsupportedEncodingException {
		Map<String,String> parameterMap = new HashMap<>();
		StringTokenizer st = new StringTokenizer(parameter);
		String[] param = {};
		while(st.hasMoreTokens()) {
			param = st.nextToken("&").split("=");
			parameterMap.putIfAbsent(param[0], param.length > 0 ? param[1] : "");
		}
		return parameterMap;
	}
	
	private void setInfo(BufferedReader br) {
		try {

			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			method = st.nextToken().trim();
			originPath = URLDecoder.decode(st.nextToken(),DEFALT_ENCODING);
			path = originPath;
			protocol = st.nextToken();
			
			String line = null;
			String k = "";
			String v = "";
			StringTokenizer header = null;
			int idxx = -1;
			
			int idx = path.indexOf("?");
			if( idx > 0 ) {
				parameter = getParameter(path.substring(idx+1));
				path = path.substring(0, idx);	
			}
			
			if( method.contentEquals("POST")) {
				System.out.println(method);
				while((line = br.readLine()).length() > 0) {
					header = new StringTokenizer(line, ":");
					k = header.nextToken().trim();
					v = header.nextToken().trim();
					String m = "multipart/form-data;";
					idxx = v.indexOf(m);
					if( k.contentEquals("Content-Type") && idxx >= 0 ) {
						v = v.substring(idxx+m.length()).trim();
						log.info(v);
					}
					headers.put(k, v);
				}					
				
				
				log.info("------------------------------------------------------------------------------------------");
				log.info(originPath);
				
				//parameter
				if( parameter != null && parameter.size() > 0 ) {
					parameter.forEach((key,val)-> log.info("{} : {}", key,val));
				}
				
				if( headers.size() > 0 ) {
					headers.forEach((key,val)-> log.info("{} : {}", key, val));
				}
			}
			else if( method.contentEquals("GET")) {
				while((line = br.readLine()).length() > 0) {
					header = new StringTokenizer(line, ":");
					k = header.nextToken().trim();
					v = header.nextToken().trim();
					headers.put(k, v);
				}	

			}
			
			

			
//			//path
//			if( !path.contentEquals("/favicon.ico") ) {

//				
//			}


			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in,DEFALT_ENCODING));
			DataOutputStream dos = new DataOutputStream(out);
			
			//정보저장
			setInfo(br);
			
			//등록 URL 
			List<String> urls = new LinkedList<String>();
			urls.add("/user/create");
			
			//파일 패스설정
			Path filePath = Paths.get( getPath(path) );
			
			//등록 URL이 있으면 동적처리
			if( urls.contains(path)) {
				if( path.contentEquals("/user/create") ) {					
					byte[] content = "Hello world".getBytes();
					responseHeader(dos, 200, content.length);
					responseBody(dos, content);					
				}
			}
			else if( Files.exists(filePath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }) ) {
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
