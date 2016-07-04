package info.iconmaster.who2comm;

import org.jsoup.nodes.Element;

import info.iconmaster.who2comm.user.ResultReason;

public class Utils {
	public static String[] splitBreaks(String text) {
		return text.split("<br>");
	}
	
	public static ResultReason findEvidence(Element input) {
		ResultReason res = new ResultReason();
		
		
		
		return res;
	}
}
