package info.iconmaster.who2comm;

import java.util.ArrayList;

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
}
