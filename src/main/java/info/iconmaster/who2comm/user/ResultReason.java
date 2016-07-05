package info.iconmaster.who2comm.user;

public class ResultReason {
	public static enum ReasonType {
		PROFILE,
		OPEN_STATUS_BOX,
		JOURNAL,
		JOURNAL_HEADER,
		JOURNAL_FOOTER,
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
	
	@Override
	public String toString() {
		return "ResultReason [type=" + type + ", kind=" + kind + ", desc=" + desc + ", source=" + source + ", link="
				+ link + "]";
	}
}
