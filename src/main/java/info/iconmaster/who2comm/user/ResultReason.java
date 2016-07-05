package info.iconmaster.who2comm.user;

/**
 * ResultReason represents a sub-reason for the determination of a User's status.
 * This contains info for the end user to see, in order to make a decision.
 * 
 * @author iconmaster
 *
 */
public class ResultReason {
	/**
	 * An enum representing a few things, like the section where a result came from, or
	 * the type of link that was found.
	 * 
	 * @author iconmaster
	 *
	 */
	public static enum ReasonType {
		PROFILE,
		OPEN_STATUS_BOX,
		JOURNAL,
		JOURNAL_HEADER,
		JOURNAL_FOOTER,
		TOS,
		PRICES,
		ERROR,
		OTHER,
	}
	
	/**
	 * An enum represnting if this reason counts towards the user being opened or closed.
	 * 
	 * @author iconmaster
	 *
	 */
	public static enum ReasonKind {
		POSITIVE,
		NEGATIVE,
		UNKNOWN,
		INVALID,
	}
	
	/**
	 * The type of result. See ReasonType for more info.
	 */
	public ReasonType type;
	
	/**
	 * The kind of result. See ReasonKind for more info.
	 */
	public ReasonKind kind;
	
	/**
	 * A short description, to succiently show to the user why the result was the way it was.
	 */
	public String desc;
	
	/**
	 * The raw HTML where the result was found. The user can see this if they need more info.
	 */
	public String source;
	
	/**
	 * A link to the URL where this info was found. The user can naviagte to this link.
	 */
	public String link;
	
	/**
	 * The toString method. Produces pretty output good for a console.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(type.toString());
		sb.append(" : ");
		sb.append(kind);
		sb.append("\n\t");
		sb.append(desc);
		return sb.toString();
	}
}
