package graphmodel;

import graphelements.jgraphx.DrawComponent;

/**
 * This class represents an individual Link interface of a Sub System.
 * This is represented in the XML by the <Interface> tag. 
 * 
 * It contains the Sub System it relates to, the class for its implementation, the type of interface it uses, and the arguments it needs.
 * 
 * TODO: Change "linkInterface" type to enum to constrain number of options. Could be USB, TCP/IP, etc.
 * Get list of possible options
 * 
 * TODO: Add error checking for including Links with the same Sub System, and empty parameters
 * 
 * @author Chanisha
 *
 */
public class Link extends Component {
	
	private SubSystem subSystem;
	private String className;
	private String linkInterface;
	private String args;

	public Link(SubSystem subSystem, String fullClassName, String linkInterface, String args) {
		// This name is for the labelling
		super(linkInterface + ", " + args);
		this.subSystem = subSystem;
		this.className = fullClassName;
		this.linkInterface = linkInterface;
		this.args = args;
	}
	
	public SubSystem getSubSystem() {
		return subSystem;
	}
	
	public String getLinkInterface() {
		return linkInterface;
	}
	
	public String getArguments() {
		return args;
	}
	
	public String getClassName() {
		return className;
	}

	/**
	 * Method inherited from Component. This method implements the XML generation per Link
	 * The fields are ordered:
	 * - Sub System
	 * - Implementing Class
	 * - Interface type
	 * - Arguments needed
	 */
	@Override
	public String generateXML() {
		
		String result = "<Interface SubSystem=\"" + subSystem.name + "\" Class=\"" + className
				+ "\" Interface=\"" + linkInterface + "\" Args=\"" + args + "\"/>"; 
		
		return result;
	}

	/**
	 * This method checks if the passed in SubSystem is the same as its contained SubSystem
	 * @param s
	 * @return
	 */
	public boolean hasSubSystem(SubSystem s) {
		return s == subSystem;
	}
	
	@Override
	public String getTypeAsString() {
		return DrawComponent.INTERFACE_NAME;
	}

}
