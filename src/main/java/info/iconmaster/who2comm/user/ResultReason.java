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
		StringBuilder sb = new StringBuilder(type.toString());
		sb.append(" : ");
		sb.append(kind);
		sb.append("\n\t");
		sb.append(desc);
		return sb.toString();
	}
}
