package hr.tt.addcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Util {
	
	private static final String userAgent = PropertiesCache.getInstance().getProperty("userAgent");
	
	private static final String cookieNjuskalo = PropertiesCache.getInstance().getProperty("cookieNjuskalo"); 
	
	public static final Document getDOMDocumentFromUrl(String url) throws IOException {
		URLConnection connection = null;
		if (PageGeter.useProxy) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PageGeter.proxyUrl, 8080));
			connection =  new URL(url).openConnection(proxy);
		} else {
			connection =  new URL(url).openConnection();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
			response.append(inputLine);

		in.close();
		Document doc = Jsoup.parse(new String (response.toString().getBytes(), "UTF-8"));
		return doc;
	}
	
	public static final Document getDOMDocumentFromUrlNjuskalo(String url) throws IOException {
		Document doc = Jsoup.connect(url)
				  .userAgent(userAgent)    
				  .header("cookie",cookieNjuskalo)
				  .timeout(0)
				  .get();
		return doc;
	}
	
	public static Date parseDateTime(String dateString) throws ParseException {
	    if (dateString == null) return null;
	    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	    if (dateString.contains("T")) dateString = dateString.replace('T', ' ');
	    if (dateString.contains("Z")) dateString = dateString.replace("Z", "+0000");
	    else
	        dateString = dateString.substring(0, dateString.lastIndexOf(':')) + dateString.substring(dateString.lastIndexOf(':')+1);
	    return fmt.parse(dateString);
	}
}
