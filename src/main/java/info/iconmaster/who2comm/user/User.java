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
		res.desc = "No indicitave information found.";
		
		// first, search for the words that indicate comms are open or not
		String raw = input.html().toLowerCase();
		String[] paragraphs = Utils.splitBreaks(raw);
		for (String p : paragraphs) {
			boolean found = p.contains("comm");
			
			if (found) {
				res.kind = ReasonKind.POSITIVE;
				res.desc = "The word 'comm' was found, and not 'closed.'";
				res.source = p;
				
				found = p.contains("closed");
				if (found) {
					res.desc = "The word 'comm' was found, as well as 'closed.'";
					res.kind = ReasonKind.NEGATIVE;
					break;
				}
				
				found = p.contains("open");
				if (found) {
					res.desc = "The word 'comm' was found, as well as 'open.'";
					break;
				}
			}
		}
		
		// then, scrape the HTML for links to TOS or prices
		
		reasons.add(res);
	}
}
