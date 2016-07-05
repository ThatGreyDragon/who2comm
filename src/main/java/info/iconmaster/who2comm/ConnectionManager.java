package info.iconmaster.who2comm;

import java.io.IOException;

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
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
