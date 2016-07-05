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
		
		//check to see if we're not able to see the page
		Element isPageBad = userpage.select("table.maintable tbody tr td.alt1").first();
		if (!isPageBad.text().equals(" ")) {
			ResultReason res = new ResultReason();
			res.type = ReasonType.OTHER;
			res.kind = ReasonKind.UNKNOWN;
			res.desc = "This user requires you to register to see this account.";
			res.source = isPageBad.html();
			res.link = getUserPageUrl();
			reasons.add(res);
			return;
		}
		
		//look at the featured journal
		Element featJournal = userpage.select("div.journal-body").first();
		ReasonKind jres1 = findEvidence(featJournal, ReasonType.JOURNAL).kind;
		
		//look at the journal header; common across all journals
		Element header = userpage.select("div.journal-header").first();
		ReasonKind jres2 = ReasonKind.UNKNOWN;
		if (header != null) {
			jres2 = findEvidence(header, ReasonType.JOURNAL_HEADER).kind;
		}
		
		//look at the journal footer; common across all journals
		Element footer = userpage.select("div.journal-header").first();
		ReasonKind jres3 = ReasonKind.UNKNOWN;
		if (footer != null) {
			jres3 = findEvidence(footer, ReasonType.JOURNAL_FOOTER).kind;
		}
		
		if (jres1 == ReasonKind.UNKNOWN && jres2 == ReasonKind.UNKNOWN && jres3 == ReasonKind.UNKNOWN) {
			//if nothing is in featured journal, load up journals page and sift through them
			//we want to do this as a last resort, as it involves loading another page
			
		}
		
		//look at the user profile
		Element profile = userpage.select("td.alt1.addpad table tbody tr td.ldot").first();
		findEvidence(profile, ReasonType.PROFILE);
		
		// look at the "Accepting Commissions" box at the side of the profile
		// Not many artists use this, so don't count it much
		Element commBox = userpage.select("span.option-yes").first();
		ResultReason cbres = new ResultReason();
		cbres.type = ReasonType.OPEN_STATUS_BOX;
		cbres.link = getUserPageUrl();
		if (commBox != null && commBox.text().equals("Accepting Commissions")) {
			cbres.kind = ReasonKind.POSITIVE;
			cbres.desc = "'Accepting Commissions' is listed next to the profile.";
		} else {
			cbres.kind = ReasonKind.UNKNOWN;
			cbres.desc = "'Accepting Commissions' is not listed next to the profile.";
		}
		reasons.add(cbres);
		
		//All scraping done. Now, let's determine a result
		status = Status.UNKNOWN;
		int pos = 0, neg = 0;
		for (ResultReason res : reasons) {
			if (res.kind == ReasonKind.POSITIVE) {
				pos++;
			} else if (res.kind == ReasonKind.NEGATIVE) {
				neg++;
			}
		}
		if (pos >= neg) {
			status = Status.OPEN;
		} else {
			status = Status.CLOSED;
		}
		
		//clean up
		userpage = null;
	}
	
	public ResultReason findEvidence(Element input, ReasonType type) {
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
		return res;
	}
	
	public boolean positiveWord(String s) {
		return s.contains("open") || s.contains("yes") || s.contains("yep");
	}
	
	public boolean negativeWord(String s) {
		return s.contains("close") || s.contains("not") || s.contains("nope") || (s.contains(" no") && !s.contains("note")) || s.contains("no ");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("User ");
		sb.append(name);
		sb.append(" is...\n");
		sb.append(status);
		sb.append("\nReasons:");
		for (ResultReason res : reasons) {
			sb.append("\n\t");
			sb.append(res.toString().replace("\n", "\n\t"));
		}
		sb.append('\n');
		return sb.toString();
	}
}
