package info.iconmaster.who2comm.user;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import info.iconmaster.who2comm.ConnectionManager;
import info.iconmaster.who2comm.Utils;
import info.iconmaster.who2comm.user.ResultReason.ReasonKind;
import info.iconmaster.who2comm.user.ResultReason.ReasonType;

public class User {
	public static enum Status {
		OPEN,
		CLOSED,
		INACTIVE,
		UNKNOWN,
	}
	
	public String name;
	
	protected Document userpage;
	
	public Status status;
	public ArrayList<ResultReason> reasons = new ArrayList<>();
	public ArrayList<ResultReason> tosLinks = new ArrayList<>();
	public ArrayList<ResultReason> pricesLinks = new ArrayList<>();

	public User(String name) {
		this.name = name;
	}
	
	public String getUserPageUrl() {
		return "http://www.furaffinity.net/user/" + name + "/";
	}
	
	public Document getUserPage() {
		if (userpage != null) {
			return userpage;
		}
		
		userpage = ConnectionManager.get(getUserPageUrl());
		
		return userpage;
	}
	
	public void findIfCommsOpen() {
		getUserPage();
		
		Element isPageBad = userpage.select("table.maintable tbody tr td.alt1").first();
		if (!isPageBad.text().equals(" ")) {
			ResultReason res = new ResultReason();
			res.type = ReasonType.OTHER;
			res.kind = ReasonKind.UNKNOWN;
			res.desc = "This user requires you to register to see this account.";
			res.source = isPageBad.html();
			reasons.add(res);
			return;
		}
		
		Element profile = userpage.select("td.alt1.addpad table tbody tr td.ldot").first();
		findEvidence(profile, ReasonType.PROFILE);
	}
	
	public void findEvidence(Element input, ReasonType type) {
		ResultReason res = new ResultReason();
		res.type = type;
		res.kind = ReasonKind.UNKNOWN;
		res.source = input.html();
		res.link = getUserPageUrl();
		res.desc = "No indicitave information found.";
		
		// first, search for the words that indicate comms are open or not
		boolean fallback = true;
		String raw = input.html().toLowerCase();
		String[] paragraphs = Utils.splitBreaks(raw);
		
		for (String p : paragraphs) { // look for words on same line
			boolean found = p.contains("comm");
			
			if (found) {
				res.source = p;
				
				if (negativeWord(p)) {
					res.desc = "The word 'comm' was found, as well as 'closed.'";
					res.kind = ReasonKind.NEGATIVE;
					fallback = false;
					break;
				}
				
				if (positiveWord(p)) {
					res.desc = "The word 'comm' was found, as well as 'open.'";
					res.kind = ReasonKind.POSITIVE;
					fallback = false;
					break;
				}
			}
		}
		
		if (fallback) { // look for words on two consucutive line
			for (int i = 0; i < paragraphs.length - 1; i++) {
				res.source = paragraphs[i] + "<br>" + paragraphs[i+1];
				
				boolean found = paragraphs[i].contains("comm");
				if (found) {
					if (negativeWord(paragraphs[i+1])) {
						res.desc = "The word 'comm' was found, as well as 'closed.'";
						res.kind = ReasonKind.NEGATIVE;
						fallback = false;
						break;
					}
					
					if (positiveWord(paragraphs[i+1])) {
						res.desc = "The word 'comm' was found, as well as 'open.'";
						res.kind = ReasonKind.POSITIVE;
						fallback = false;
						break;
					}
				}
			}
		}
		
		if (fallback && raw.contains("comm")) { //comm was there, but no other useful words
			res.desc = "The word 'comm' was found, and not 'closed.'";
			res.kind = ReasonKind.POSITIVE;
		} else if (fallback && raw.contains("closed")) { //closed was there, but no other useful words
			res.desc = "The word 'closed.' was found, but not 'comm'.";
			res.kind = ReasonKind.NEGATIVE;
		}
		
		// then, scrape the HTML for links to TOS or prices
		
		reasons.add(res);
	}
	
	public boolean positiveWord(String s) {
		return s.contains("open") || s.contains("yes") || s.contains("yep");
	}
	
	public boolean negativeWord(String s) {
		return s.contains("close") || s.contains("not") || s.contains("nope") || (s.contains("no") && !s.contains("note"));
	}
}
