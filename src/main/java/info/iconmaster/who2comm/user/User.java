package info.iconmaster.who2comm.user;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import info.iconmaster.who2comm.ConnectionManager;
import info.iconmaster.who2comm.Utils;
import info.iconmaster.who2comm.user.ResultReason.ReasonKind;
import info.iconmaster.who2comm.user.ResultReason.ReasonType;

/**
 * User represents an FA user. It contains data about open/closed for comms status,
 * reasons for this determination, and other relevant user data.
 * 
 * @author iconmaster
 *
 */
public class User {
	/**
	 * An enum representing open/closed for comms status.
	 * 
	 * @author iconmaster
	 *
	 */
	public static enum Status {
		OPEN,
		CLOSED,
		INVALID,
		UNKNOWN,
	}
	
	/**
	 * The FA username of the person to query.
	 */
	public String name;
	
	/**
	 * Whether or not the user is open or closed for commissions.
	 * Not set until findIfCommsOpen() is called.
	 */
	public Status status;
	
	/**
	 * A list of reasons the user's status is the way it is.
	 * Not set until findIfCommsOpen() is called.
	 */
	public ArrayList<ResultReason> reasons = new ArrayList<>();
	
	/**
	 * A list of possible links to the user's terms of service.
	 * Not set until findIfCommsOpen() is called.
	 */
	public ArrayList<ResultReason> tosLinks = new ArrayList<>();
	
	/**
	 * A list of possible links to the user's price guide.
	 * Not set until findIfCommsOpen() is called.
	 */
	public ArrayList<ResultReason> pricesLinks = new ArrayList<>();

	/**
	 * Creates a new User.
	 * Once constructed, call findIfCommsOpen() to scrape FA and find status.
	 * 
	 * @param name the FA username
	 */
	public User(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the URL of the user's FA homepage.
	 * 
	 * @return FA homepage URL
	 */
	public String getUserPageUrl() {
		return "http://www.furaffinity.net/user/" + name + "/";
	}
	
	/**
	 * This returns the DOM element of the user's front page.
	 * Used during findIfCommsOpen().
	 * 
	 * @return The user's front page.
	 */
	public Document getUserPage() {
		return ConnectionManager.get(getUserPageUrl());
	}
	
	/**
	 * Determines if this user is open or closed for commissions.
	 * This function grabs Internet data through ConnectionManager.
	 */
	public void findIfCommsOpen() {
		Document userpage = getUserPage();
		ResultReason tempRes;
		
		//check to see if we're not able to see the page
		Element isPageBad = userpage.select("table.maintable tbody tr td.alt1").first();
		if (!isPageBad.text().equals(" ")) { //that's a NBSP in that string, so be careful!
			ResultReason res = new ResultReason();
			res.type = ReasonType.ERROR;
			res.kind = ReasonKind.INVALID;
			res.source = isPageBad.html();
			res.link = getUserPageUrl();
			
			if (userpage.text().contains("voluntarily disabled")) {
				res.desc = "This user has disabled thier account.";
			} else if (userpage.text().contains("user cannot be found")) {
				res.desc = "This user does not exist.";
			} else if (userpage.text().contains("registered users only")) {
				res.desc = "This user requires you to register to see this account.";
			} else {
				res.desc = "An unknown error occured.";
			}
			
			reasons.add(res);
			
			status = Status.INVALID;
			userpage = null;
			return;
		}
		
		//look at the featured journal
		Element featJournal = userpage.select("div.journal-body").first();
		ReasonKind jres1 = ReasonKind.UNKNOWN;
		if (featJournal != null) {
			tempRes = findEvidence(featJournal, ReasonType.JOURNAL);
			reasons.add(tempRes);
			jres1 = tempRes.kind;
		}
		
		//look at the journal header; common across all journals
		Element header = userpage.select("div.journal-header").first();
		ReasonKind jres2 = ReasonKind.UNKNOWN;
		if (header != null) {
			tempRes = findEvidence(header, ReasonType.JOURNAL_HEADER);
			reasons.add(tempRes);
			jres2 = tempRes.kind;
		}
		
		//look at the journal footer; common across all journals
		Element footer = userpage.select("div.journal-header").first();
		ReasonKind jres3 = ReasonKind.UNKNOWN;
		if (footer != null) {
			tempRes = findEvidence(footer, ReasonType.JOURNAL_FOOTER);
			reasons.add(tempRes);
			jres3 = tempRes.kind;
		}
		
		if (jres1 == ReasonKind.UNKNOWN && jres2 == ReasonKind.UNKNOWN && jres3 == ReasonKind.UNKNOWN) {
			//if nothing is in featured journal, load up journals page and sift through them
			//we want to do this as a last resort, as it involves loading another page
			// TODO
		}
		
		//look at the user profile
		Element profile = userpage.select("td.alt1.addpad table tbody tr td.ldot").first();
		reasons.add(findEvidence(profile, ReasonType.PROFILE));
		
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
			} else if (res.kind == ReasonKind.INVALID) {
				status = Status.INVALID;
				break;
			}
		}
		
		if (status != Status.INVALID) {
			if (pos == 0 && neg == 0) {
				status = Status.UNKNOWN;
			} else if (pos >= neg) {
				status = Status.OPEN;
			} else {
				status = Status.CLOSED;
			}
		}
	}
	
	/**
	 * This function is used for a section of a page (journals, profile, etc.).
	 * It finds indicitave words, and returns the reason with a result.
	 * 
	 * @param input The node that may contain the text of interest.
	 * @param type Where this node came from, e.g. profile, journal, etc.
	 * @return The result of the investigation.
	 */
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
		// TODO
		
		// return the result
		return res;
	}
	
	/**
	 * Given a paragraph of text, see if there's a positive word that can go with the word 'comm'.
	 * 
	 * @param s The paragraph to check.
	 * @return Whether or not it contains a positive word, e.g. "open" or "yes".
	 */
	public boolean positiveWord(String s) {
		return s.contains("open") || s.contains("yes") || s.contains("yep");
	}
	
	/**
	 * Given a paragraph of text, see if there's a negative word that can go with the word 'comm'.
	 * 
	 * @param s The paragraph to check.
	 * @return Whether or not it contains a negative word, e.g. "closed" or "no".
	 */
	public boolean negativeWord(String s) {
		return s.contains("close") || (s.contains("not") && !s.contains("note")) || s.contains("nope") || (s.contains(" no") && !s.contains("note")) || s.contains("no ");
	}
	
	/**
	 * The toString method. Produces pretty output good for a console.
	 */
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
