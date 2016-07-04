package info.iconmaster.who2comm.user;

import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import info.iconmaster.who2comm.ConnectionManager;
import info.iconmaster.who2comm.Utils;

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
		
		Element profile = userpage.select("td.alt1.addpad table tbody tr td.ldot").first();
		System.out.println(profile.text());
	}
}
