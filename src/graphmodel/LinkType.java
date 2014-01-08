package graphmodel;

public enum LinkType {
	LOCAL("Local"), DESTINATION("Destination");
	
	String code;
	
	LinkType(String c) {
		this.code = c;
	}
	
	public static LinkType getLinkType(String type) {
		if (type.equals("Local")) {
			return LOCAL;
		} else if (type.equals("Destination")) {
			return DESTINATION;
		} else {
			return null;
		}
	}
	
	public String getCode() {
		return this.code;
	}

}
