package graphmodel;

import graphelements.jgraphx.DrawComponent;

import java.util.Hashtable;

/**
 * This class represents the Signal construct in the SystemJ language.
 * This object contains:
 * - the name of the Signal
 * - the class that houses its implementation
 * - the type of Signal (input or output)
 * - a Hashtable containing all the parameters for this Signal
 * 
 * TODO: Add a error checking for empty fields, and maybe even possibly checking if the fields entered are correct
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class Signal extends Component {

	private Hashtable<String, String> parameters;
	
	public Signal (String name, String className, IOType type,Hashtable<String, String> params) {
		super(name);
		parameters = params;
		// TODO: Is it necessary putting these fields in the hashtable? Or better to have them as separate fields?
		parameters.put("Class", className);
		parameters.put("Type", type.getCode());
	}
	
	/**
	 * This method returns the type of this Signal as an IOType object
	 * @return
	 */
	public IOType getType() {
		String code = parameters.get("Type");
		// As the type is saved as a String, can't return directly
		if (code.equals("i")) {
			return IOType.INPUT;
		} else {
			return IOType.OUTPUT;
		}
	}
	
	/**
	 * Add in another parameter to the signal, given its key and value
	 * @param key 
	 * @param value
	 */
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	/**
	 * Change an existing parameter contained within the Signal. It will replace the value currently stored under the
	 * given key with the new value provided
	 * 
	 * @param key - the key of the value that is wanting to be changed
	 * @param value - the new value to store in the given key
	 */
	public void changeExistingParameter(String key, String value) {
		// remove existing value
		parameters.remove(key);
		//replace with new value
		parameters.put(key, value);
	}
	
	public String getClassName() {
		return parameters.get("Class");
	}
	
	/**
	 * 
	 * @return a Hashtable containing all the parameters of the Signal.
	 */
	public Hashtable<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Method inherited from Component. This method implements the XML generation per Clock Domain
	 * The fields are ordered:
	 * - name of the Signal
	 * - name of the class with the implementation
	 * - all the parameters of the Signal
	 * 
	 * The input/output type is declared by the prefix of the <Signal> tag. It can be <oSignal> or <iSignal>
	 */
	@Override
	public String generateXML() {
		StringBuilder result = new StringBuilder("<" + parameters.get("Type") + "Signal ");
		result.append("Name=\"" + this.name + "\" ");
		for (String key : parameters.keySet()) {
			if (!key.equals("Type")) {
				result.append(" " + key + "=" + "\"" + parameters.get(key) + "\"");
			}
		}
		result.append("/>");
		
		return result.toString();
		
	}
	
	@Override
	public String getTypeAsString() {
		return DrawComponent.SIGNAL_NAME;
	}


}
