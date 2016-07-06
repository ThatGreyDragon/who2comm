package info.iconmaster.who2comm;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
	 * The timer used to implement the minimum delay.
	 */
	private static Timer timer;
	
	/**
	 * Gets the HTML source of a web page.
	 * 
	 * @param url The URL to scrape.
	 * @return A JSoup Document representing the page scraped.
	 */
	public static Document get(String url) {
		try {
			// before downloading the page, wait for the min delay to pass
			if (timer == null) {
				updateMinDelay();
			} else {
				synchronized (timer) {
					timer.wait();
				}
			}
			
			Connection conn = Jsoup.connect(url);
			
			if (Settings.USE_AUTH && Settings.AUTH_COOKIE != null) {
				// to log in, we need two cookies from the user, called "a" and "b" by FA.
				
				//find cookie A
				Matcher m = Pattern.compile("a=([a-fA-F0-9\\-]+)").matcher(Settings.AUTH_COOKIE);
				if (m.find()) {
					String a = m.group(1);
					conn = conn.cookie("a", a);
				}
				
				//find cookie B
				m = Pattern.compile("b=([a-fA-F0-9\\-]+)").matcher(Settings.AUTH_COOKIE);
				if (m.find()) {
					String b = m.group(1);
					conn = conn.cookie("b", b);
				}
			}
			return conn.get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * If MIN_DELAY is changed, call this function to reset the timer to the new setting.
	 */
	public static void updateMinDelay() {
		// delete an old timer if it exists
		if (timer != null) {
			timer.cancel();
		}
		
		// create the timer, and make it allow 1 page to download every MIN_DELAY ms
		timer = new Timer(true); // make sure it's on a daemon thread, so the program eventually exits...
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				synchronized (timer) {
					timer.notify();
				}
			}
		}, Settings.MIN_DELAY, Settings.MIN_DELAY);
	}
}
