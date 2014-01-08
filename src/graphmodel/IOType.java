package graphmodel;

/**
 * This enum is used to define the possible types of Signals
 * @author Chanisha Somatilaka, rsom024
 *
 */
public enum IOType {
	INPUT ("i"), OUTPUT("o");
	
	private final String code;
	
	IOType(String s) {
		code = s;
	}
	
	public String getCode() {
		return code;
	}
	
	/**
	 * This method returns the IOType based on the input String.
	 * @param code - this must either be "i" or "o"
	 * @return INPUT or OUTPUT depending on the passed in String
	 */
	public static IOType getIOType(String code) {
		if (code.equals("i")) {
			return IOType.INPUT;
		} else {
			return IOType.OUTPUT;
		}
	}

}
