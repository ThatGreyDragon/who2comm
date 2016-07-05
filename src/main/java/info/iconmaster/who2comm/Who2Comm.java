package info.iconmaster.who2comm;

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
		User u = new User("rukis");
		u.findIfCommsOpen();
		System.out.println(u);
	}
}
