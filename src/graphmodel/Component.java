package graphmodel;

import java.io.Serializable;

/**
 * This class defines all common behaviour of all the SystemJ construct objects. It allows for some polymorphism, and 
 * ensures that all objects implement their own XML logic.
 * 
 * Any new constructs that need to be added need to inherit from this class.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public abstract class Component implements Serializable {
	
	protected String name;
	
	public Component(String name) {
		this.name = name;
	}

	public Component(){
		
	}
	public Component(Component in){
		this.name = in.name;
		
	}
	
	/**
	 * Method for sub classes to implement: their own XML generation logic
	 * @return String with XML representation contained
	 */
	public abstract String generateXML();
	
	/**
	 * Method for each class to return the name of the construct they are representing
	 * @return
	 */
	public abstract String getTypeAsString();
	
	/**
	 * Overrides String's method toString. 
	 * Prints out the name of this Component instead of the object ID.
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * This method gets the name defined for this Component
	 * @return String representing its name
	 */
	public String getName() {
		return name;
	}
	
	
	
	

}
