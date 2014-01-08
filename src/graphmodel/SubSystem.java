package graphmodel;

import graphelements.jgraphx.DrawComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Sub System construct in the SystemJ language. It logically partitions the Clock Domains 
 * in the system to allow it to be deployed on a distributed platform.
 * It contains a list of the ClockDomain objects that belongs to it, and the name of the class that contains the implementation for the scheduler
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class SubSystem extends Component {
	
  private List<ClockDomain> clockDomains;
  private String schedulerClass;
  private boolean local=false;
	
  public SubSystem(String name, String schedulerClass, String local) {
    super(name);
    clockDomains = new ArrayList<ClockDomain>();
    this.schedulerClass = schedulerClass;
    this.local = (Boolean.valueOf(local)).booleanValue();
  }
	
  /**
   * Add a Clock Domain to this Sub System
   * @param c
   */
  protected void addClockDomain(ClockDomain c) {
    clockDomains.add(c);
  }
	
  /**
   * Remove the given Clock Domain from this Sub System
   * @param c
   */
  protected void removeClockDomain(ClockDomain c) {
    clockDomains.remove(c);
  }
	
  /**
   * This method checks to see if the given ClockDomain object is contained within this SubSystem object
   * @param cd
   * @return true if it is, false if not
   */
  public boolean containsClockDomain(ClockDomain cd) {
    return clockDomains.contains(cd);
  }
	
  /**
   * If a ClockDomain object has been edited, then a new object will be created representing a new ClockDomain.
   * In order to preserve that change, replace the old Clock Domain with the new one
   * @param oldCD
   * @param newCD
   */
  protected void replaceClockDomain(ClockDomain oldCD, ClockDomain newCD) {
    if (containsClockDomain(oldCD)) {
      clockDomains.remove(oldCD);
      clockDomains.add(newCD);
    }
  }
	
  /**
   * @return the number of Clock Domains in this SubSystem
   */
  public int getNumClockDomains() {
    return clockDomains.size();
  }
	
  /**
   * This method returns the ClockDomain object with the given name. If no such ClockDomain object exists, return null
   * @param name
   * @return
   */
  public ClockDomain getClockDomain(String name) {
    for (ClockDomain cd : clockDomains) {
      if (name.equals(cd.name)) {
	return cd;
      }
    }
		
    return null;
  }
	
  /**
   * @return String of the class name of the scheduler. "" if no scheduler class was specified
   */
  public String getSchedulerClass() {
    return schedulerClass;
  }
	
  public String getLocal() {
    return Boolean.toString(local);
  }
  public List<ClockDomain> getClockDomains() {
    return clockDomains;
  }
	
  /**
   * Method inherited from Component. This method implements the XML generation per Sub System
   * The fields are ordered:
   * - Name of the Sub System
   * - Scheduler class, if provided.
   * 
   * It then iterates through all the ClockDomain objects in its list and appends that to its XML
   */
  @Override
  public String generateXML() {
    StringBuilder result;
    if (local)
      result = new StringBuilder("<SubSystem Name=\"" + this.name + "\" Local=\"" + this.local + "\">\n");
    else result = new StringBuilder("<SubSystem Name=\"" + this.name + "\">\n");
    // If a scheduler class has been specified, then include its tag. Else, do not.
    if (!this.schedulerClass.equals("")) {
      result.append("    <Scheduler Class=\"" + this.schedulerClass + "\">\n");
    }
    // Iterate through all the CLock Domains and append their generated XML
    for (Component c : clockDomains) {
      result.append("        " + c.generateXML() + "\n");
    }
    // If there is a scheduler, then add its closing tag
    if (!this.schedulerClass.equals("")) {
      result.append("    </Scheduler>\n");
    }
    result.append("    </SubSystem>\n");
		
    return result.toString();
  }
	
  @Override
  public String getTypeAsString() {
    return DrawComponent.SUBSYSTEM_NAME;
  }

}
