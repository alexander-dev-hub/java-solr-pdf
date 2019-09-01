package org.solr.index.pdf.jakub;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class IOStreamUtil {

	/**
	 * 
	 * 
	*/
	public static void downloadResource(InputStream is, String filename, String dir) {
		try {
			File downfile = new File(dir + File.separator + filename);
			FileOutputStream fos = new FileOutputStream(downfile);

			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("null")
	public static OutputStream dumpFile(File readFile) {
		OutputStream outputStream = null;
		byte readByte[] = new byte[4096];
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(readFile));
			int i;
			while ((i = inputStream.read(readByte, 0, 4096)) != -1) {
				outputStream.write(readByte, 0, i);
			}
			inputStream.close();
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}

		return outputStream;
	}

	/**
	 * 
	 */
	public static byte[] getBytesFromInputStream(InputStream is, long length) {

		byte[] bytes = new byte[(int) length];

		try {
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file ");
			}

			// Close the input stream and return bytes
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}

	/**
	 * 
	 */
	public static String convertAsciiToString(byte[] resByte) {
		String res = "";
		int len = resByte.length;
		for (int i = 0; i < len; i++) {
			byte b = resByte[i];
			StringBuilder sb = new StringBuilder(String.valueOf(res));
			String str = new String(Character.toChars(b));
			res = sb.append(str).toString();
		}
		return res;
	}

	/**
	 * 
	 */
	public static StringBuilder streamToStringBuffer(InputStream inputStream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb;
	}

	/**
	 * 
	 */
	public static String streamToString(InputStream inputStream) {

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			
			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public static InputStream getResponseFromUrl(String url) {

		InputStream is = null;

		try {
			// ------------------------------------------------------------>
			BasicCookieStore cookieStore = new BasicCookieStore();
			CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

			HttpGet request = new HttpGet(url);

			request.setHeader("Connection", "keep-alive");
			request.setHeader("Cache-Control", "max-age=0");
			request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			request.setHeader("Upgrade-Insecure-Requests", "1");
			request.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
			request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,zh-TW;q=0.2");
			request.setHeader("Cookie",
					"ezproxy=ixti75KLBwlKZE7; __gads=ID=971c8ab7109e95d9:T=1485545118:S=ALNI_MZ-hedjs7wwmrO4vl_9bsySQBMWrA; wt3_eid=%3B935649882378213%7C2148560274500118039; _gat=1; _gali=browse-volumes-and-issues; _ga=GA1.2.781345050.1485602738; ki_t=1485602747534%3B1485602747534%3B1485604947534%3B1%3B13; ki_r=; wt3_sid=%3B935649882378213");

			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();

			// get cookie
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				System.out.println(cookie.getName() + "=" + cookie.getValue());
			}

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			is = response.getEntity().getContent();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return is;
	}
}