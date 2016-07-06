package info.iconmaster.who2comm;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Miscellaneous utility functions.
 * 
 * @author iconmaster
 *
 */
public class Utils {
	
	/**
	 * Splits a String with HTML in it into paragrpahs, based on the &lt;br&gt; tags found.
	 * Discards empty lines.
	 * 
	 * @param text The HTML String to pass in. (hint: use JSoup's .html() method).
	 * @return A list of paragrpahs in the text. HTML is preserved.
	 */
	public static String[] splitBreaks(String text) {
		//split on <br> tags
		String[] subs = text.split("<br>");
		ArrayList<String> a = new ArrayList<>();
		
		//trim empty lines
		for (String sub : subs) {
			if (!sub.trim().isEmpty()) {
				a.add(sub);
			}
		}
		
		return a.toArray(new String[0]);
	}
	
	/**
	 * Returns all the names in a given user's watchlist.
	 * It uses ConnectionManager to download an FA page.
	 * 
	 * @param user
	 * @return
	 */
	public static String[] getWatchlist(String user) {
		Document doc = ConnectionManager.get("http://www.furaffinity.net/watchlist/by/" + user + "/");
		if (doc.text().contains("username not found in the database")) {
			// return nothing if the user doesn't exist
			return null;
		}
		Elements users = doc.select("table#userpage-budlist tr td");
		ArrayList<String> a = new ArrayList<>();
		
		for (Element watcher : users) {
			// split the entry into prefix and username
			String prefixedName = watcher.text();
			Matcher m = Pattern.compile("([\\~\\!])\\s*(.*)").matcher(prefixedName);
			if (m.find()) {
				String prefix = m.group(1);
				
				// a "~" means the user is active. "!" means the user is banned, and probably isn't open for comms on FA.
				if (prefix.equals("~")) {
					String name = m.group(2);
					a.add(name);
				}
			}
		}
		
		return a.toArray(new String[0]);
	}
}
