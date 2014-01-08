package graphmodel;

import graphelements.jgraphx.DrawComponent;

import java.util.ArrayList;
import java.util.List;


/**
 * This object represents the Clock Domain construct in the SystemJ language.
 * It contains all the input/output Signals, and input/output Channels associated with this particular Clock Domain.
 * 
 * Each Clock Domain has a name, and a class that implements its behaviour.
 *  * TODO: Add error checking for empty naming, and checking if the Clock Domain name is unique in the chosen Sub System
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class ClockDomain extends Component {
	
	// Signals and Channels are separated by input and output for correct grouping and XML generation
	private List<Signal> inputSignals;
	private List<Signal> outputSignals;
	private List<Channel> inputChannels;
	private List<Channel> outputChannels;

	private String className;
	
	public ClockDomain(String name, String className) {
		super(name);
		inputSignals = new ArrayList<Signal>();
		outputSignals = new ArrayList<Signal>();
		
		inputChannels = new ArrayList<Channel>();
		outputChannels = new ArrayList<Channel>();
		
		this.className = className;
	}
	
	// ADDING AND REMOVING INPUT/OUTPUT SIGNALS AND CHANNELS
	
	protected void addInputSignal(Signal s) {
		inputSignals.add(s);
	}
	
	protected void removeInputSignal(Signal s) {
		inputSignals.remove(s);
	}
	
	protected void addOutputSignal(Signal s) {
		outputSignals.add(s);
	}
	
	protected void removeOutputSignal(Signal s) {
		outputSignals.remove(s);
	}
	
	protected void addInputChannel(Channel c) {
		inputChannels.add(c);
	}
	
	protected void removeInputChannel(Channel ch) {
		inputChannels.remove(ch);
	}
	
	protected void addOutputChannel(Channel c) {
		outputChannels.add(c);
	}
	
	protected void removeOutputChannel(Channel c) {
		outputChannels.remove(c);
	}
	
	
	// GETTERS OF THE NUMBER OF INPUT/OUTPUT SIGNALS AND CHANNELS
	public int getNumInputSignals() {
		return inputSignals.size();
	}
	
	public int getNumOutputSignals() {
		return outputSignals.size();
	}
	
	public int getNumInputChannels() {
		return inputChannels.size();
	}
	
	public int getNumOutputChannels() {
		return outputChannels.size();
	}
	
	// GETTERS OF THE LISTS OF INPUT/OUTPUT SIGNALS AND CHANNELS
	public List<Signal> getInputSignals() {
		return inputSignals;
	}
	
	public List<Signal> getOutputSignals() {
		return outputSignals;
	}
	
	public List<Channel> getInputChannels() {
		return inputChannels;
	}
	
	public List<Channel> getOutputChannels() {
		return outputChannels;
	}

	/**
	 * This method returns the name of the class given that contains this Clock Domain's behaviour
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Method inherited from Component. This method implements the XML generation per Clock Domain
	 * The fields are ordered:
	 * - Input Signals
	 * - Output Signals
	 * - Input Channels
	 * - Output Channels
	 */
	@Override
	public String generateXML() {
		StringBuilder result = new StringBuilder("<ClockDomain Name=\"" + this.name + "\" Class=\"" + this.className + "\">\n");
		
			for (Signal s : inputSignals) {
				result.append("            " + s.generateXML() + "\n");
			}
		
			for (Signal s : outputSignals) {
				result.append("            " + s.generateXML() + "\n");
			}
			
			for (Channel ch : inputChannels) {
				result.append("            <iChannel Name=\"" + ch.getToName() + "\" From=\"" + ch.getFromClockDomain().name + "." + ch.name  + "\" />\n");
			}
		
			for (Channel ch: outputChannels) {
				result.append("            <oChannel Name=\"" + ch.name + "\" To=\"" + ch.getToClockDomain().name + "." + ch.getToName()  + "\" />\n");
			}
		
		
		result.append("        </ClockDomain>");
		return result.toString();
		
	}
	
	/**
	 * Method inherited from Component. It returns the type of Component object this is, which is a Clock Domain
	 * Actual definition of this String is in the DrawComponent class.
	 */
	@Override
	public String getTypeAsString() {
		return DrawComponent.CLOCK_DOMAIN_NAME;
	}
	

	

}
