package info.iconmaster.who2comm;

import info.iconmaster.who2comm.user.User;

public class Who2Comm {
	public static void main(String[] args) {
		User u = new User("rukis");
		u.findIfCommsOpen();
	}
}
