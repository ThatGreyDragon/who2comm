package info.iconmaster.who2comm;

import java.util.ArrayList;

public class Utils {
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
