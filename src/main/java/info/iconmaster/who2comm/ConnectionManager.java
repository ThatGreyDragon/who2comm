package info.iconmaster.who2comm;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * ConnectionManager assists the scraping of FA pages.
 * All Internet operations are contained here, so things like delay times and
 * authorization can take place in a consistent manner.
 * 
 * @author iconmaster
 *
 */
public class ConnectionManager {
	/**
	 * Gets the HTML source of a web page.
	 * 
	 * @param url The URL to scrape.
	 * @return A JSoup Document representing the page scraped.
	 */
	public static Document get(String url) {
		try {
			Connection conn = Jsoup.connect(url);
			if (Settings.USE_AUTH && Settings.AUTH_COOKIE != null) {
				//find cookie A
				Matcher m = Pattern.compile("a=([a-fA-F0-9\\-]+)").matcher(Settings.AUTH_COOKIE);
				if (m.find()) {
					String a = m.group(1); System.out.println(a);
					conn = conn.cookie("a", a);
				}
				
				//find cookie B
				m = Pattern.compile("b=([a-fA-F0-9\\-]+)").matcher(Settings.AUTH_COOKIE);
				if (m.find()) {
					String b = m.group(1); System.out.println(b);
					conn = conn.cookie("b", b);
				}
			}
			return conn.get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
