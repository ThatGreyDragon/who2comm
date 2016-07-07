package info.iconmaster.who2comm;

import java.util.Arrays;
import java.util.Scanner;

import info.iconmaster.who2comm.CLAHelper.CLA;
import info.iconmaster.who2comm.user.User;

/**
 * The main function. Starts up either the GUI or the console.
 * 
 * @author iconmaster
 *
 */
public class Who2Comm {
	/**
	 * The program start point.
	 * 
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		CLA cla = CLAHelper.getArgs(args);
		
		// parse settings here. See usage() for what these flags do.
		
		if (cla.containsKey("auth")) {
			Settings.USE_AUTH = true;
			Settings.AUTH_COOKIE = cla.get("auth");
			cla.remove("auth");
		}
		
		if (cla.containsKey("delay")) {
			String delay = cla.get("delay");
			try {
				Settings.MIN_DELAY = Long.parseUnsignedLong(delay);
			} catch (NumberFormatException e) {
				System.out.println("Error: Invalid delay amount \'" + delay + "\'.");
				usage();
				return;
			}
			cla.remove("delay");
		}
		
		if (cla.containsKey("q")) {
			Settings.QUIET = true;
			cla.remove("q");
		}
		
		if (cla.containsKey("wlist")) {
			String name = cla.get("wlist");
			String[] names = Utils.getWatchlist(name);
			if (name == null) {
				System.out.println("Error: User " + name + " does not exist.");
			} else {
				System.out.println("Watchlist of " + name + ":");
				System.out.print('\t');
				
				int i = 0;
				for (String watcher : names) {
					System.out.print(' ');
					System.out.print(watcher);
					if (i == 7) {
						System.out.println();
						System.out.print('\t');
					}
					i = (i + 1) % 8;
				}
			}
			return;
		}
		
		if (cla.containsKey("search")) {
			String name = cla.get("search");
			String[] names = Utils.getWatchlist(name);
			if (names == null) {
				System.out.println("Error: User " + name + " does not exist.");
				return;
			}
			int[] results = new int[User.Status.values().length];
			if (!Settings.QUIET) System.out.println("Looking up " + names.length + " users...");
			for (String username : names) {
				User u = new User(username);
				u.findIfCommsOpen();
				results[u.status.ordinal()]++;
				if (!Settings.QUIET) System.out.println(u);
			}
			if (!Settings.QUIET) System.out.println();
			System.out.println("TOTALS:");
			for (User.Status st : User.Status.values()) {
				System.out.println("\t" + st + ": " + results[st.ordinal()]);
			}
			return;
		}
		
		if (!cla.isEmpty()) {
			System.out.println("Error: Invalid option -" + cla.keySet().toArray()[0] + ".");
			usage();
			return;
		}
		
		//see if we're in command-line mode or not
		String[] in;
		if (cla.unmatched.length == 0) {
			//TODO: make this open the GUI instead
			System.out.print("Enter FA username(s) to look up: ");
			in = new Scanner(System.in).nextLine().split("\\s+");
			// sheesh, Java's functional stream support is really hard to read
			in = Arrays.asList(Arrays.stream(in).filter((String s)->s.matches("^\\S*$")).toArray()).toArray(new String[0]);
		} else {
			in = cla.unmatched;
		}
		
		for (String username: in) {
			User u = new User(username);
			u.findIfCommsOpen();
			System.out.println(u);
		}
	}
	
	/**
	 * Prints usage information and exits.
	 */
	public static void usage() {
		System.out.println("Usage: who2comm [user] [options...]");
		System.out.println("Options available are:");
		System.out.println("	-auth 'cookie' :    Provides an authorization cookie to allow this program to scrape as you.");
		System.out.println("	-wlist 'name'  :    Prints the watchlist of the given user and exits.");
		System.out.println("	-delay 'ms'    :    Specifies the minimum delay between FA page accesses.");
		System.out.println("	-search 'name' :    Does lookup on all users on this person's wachlist.");
		System.out.println("	-q             :    Quiet mode.");
		System.exit(0);
	}
}
