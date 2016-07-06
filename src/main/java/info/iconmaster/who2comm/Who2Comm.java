package info.iconmaster.who2comm;

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
		String in;
		
		// parse settings here. See usage() for what these flags do.
		
		if (cla.containsKey("auth")) {
			Settings.USE_AUTH = true;
			Settings.AUTH_COOKIE = cla.get("auth");
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
			System.exit(0);
		}
		
		//see if we're in command-line mode or not
		if (cla.unmatched.length == 0) {
			//TODO: make this open the GUI instead
			System.out.print("Enter a FA username to look up: ");
			in = new Scanner(System.in).nextLine();
		} else if (cla.unmatched.length == 1) {
			in = cla.unmatched[0];
		} else {
			usage();
			return;
		}
		
		User u = new User(in);
		u.findIfCommsOpen();
		System.out.println(u);
	}
	
	/**
	 * Prints usage information and exits.
	 */
	public static void usage() {
		System.out.println("Usage: who2comm [user] [options...]");
		System.out.println("Options available are:");
		System.out.println("	-auth 'cookie' :       Provides an authorization cookie to allow this program to scrape as you.");
		System.out.println("	-wlist 'name' :        Prints the watchlist of the given user and exits.");
		System.exit(0);
	}
}
