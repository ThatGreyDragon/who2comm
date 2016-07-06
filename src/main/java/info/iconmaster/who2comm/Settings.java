package info.iconmaster.who2comm;

/**
 * A static container class for global settings, such as login information or time between scrapes.
 * 
 * @author iconmaster
 *
 */
public class Settings {
	/**
	 * Whether or not to use AUTH_COOKIE to log in as a user.
	 */
	public static boolean USE_AUTH = false;
	
	/**
	 * A cookie, provided to FA to scrape pages as if you were logged in.
	 * 
	 * Execute get-cookie.js (in this repository) in your browser console, on
	 * an FA page while logged in, to get your FA cookie.
	 */
	public static String AUTH_COOKIE;
	
	/**
	 * Who2comm ensures that this number of milliseconds goes by at minimum
	 * between all FA page downloads.
	 */
	public static long MIN_DELAY = 100L;
}
