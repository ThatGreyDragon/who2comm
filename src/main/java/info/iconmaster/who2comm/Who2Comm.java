package info.iconmaster.who2comm;

import java.util.Scanner;

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
		String in;
		if (args.length == 0) {
			System.out.print("Enter a FA username to look up: ");
			in = new Scanner(System.in).nextLine();
		} else {
			in = args[0];
		}
		User u = new User(in);
		u.findIfCommsOpen();
		System.out.println(u);
	}
}
