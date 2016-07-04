package info.iconmaster.who2comm.user;

public class ResultReason {
	public static enum ReasonType {
		PROFILE,
		OPEN_STATUS_BOX,
		JOURNAL,
		JORNAL_HEADER,
		JORUNAL_FOOTER,
		TOS,
		PRICES,
		OTHER,
	}
	
	public static enum ReasonKind {
		POSITIVE,
		NEGATIVE,
		UNKNOWN,
	}
	
	public ReasonType type;
	public ReasonKind kind;
	public String desc;
	public String source;
	public String link;
}
