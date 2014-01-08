package graphmodel;

import graphelements.jgraphx.DrawComponent;

/**
 * This object represents the Channel construct in the SystemJ language. It contains the Clock Domains it is connected to, and the names of channel on either end.
 * The name on the originating Clock Domain is stored in the "name" field contained within the Component abstract class.
 * The name on the destination Clock Domain is stored in the "toName" field in this class.
 * 
 * TODO: Add error checking for empty naming
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class Channel extends Component {
	
	private ClockDomain from;
	private ClockDomain to;
	
	// Name of the channel on the destination Clock Domain
	private String toName;
	
	public Channel(String fromName, String toName, ClockDomain from, ClockDomain to) {
		// passing in the fromName is only for graph labelling purporses
		super(fromName);
		this.from = from;
		this.to = to;
		this.toName = toName;
		 
	}
	
	public Channel(String fromName) {
		// passing in the fromName is only for graph labelling purporses
		super(fromName);
	}
	
	/**
	 * This method returns the label this Channel has on the destination Clock Domain.
	 * NOTE: If you want to get the name of this Channel on ths originating Clock Domain, then you just need to call the Component method getName()
	 * 
	 * @return the name of this Channel on the destination Clock Domain
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * This method retrieves the Clock Domain object representing the source of this Channel
	 * @return source Clock Domain
	 */
	public ClockDomain getFromClockDomain() {
		return from;
	}
	
	/**
	 * This method retrieves the Clock Domain object representing the destination of this Channel
	 * @return destination Clock Domain
	 */
	public ClockDomain getToClockDomain() {
		return to;
	}
	

	/**
	 * This method sets the Clock Domain object representing the source of this Channel
	 */
	protected void setFromClockDomain(ClockDomain cd) {
		from = cd;
	}
	
	/**
	 * This method sets the Clock Domain object representing the destination of this Channel
	 */
	protected void setToClockDomain(ClockDomain cd) {
		to = cd;
	}
	
	/**
	 * Stub method inherited from Component. Channel does not need to implement it as the Clock Domains generate it.
	 * This is so that all the Channel XML can be grouped correctly with each Clock Domain.
	 * 
	 * So this method doesn't do anything
	 */
	@Override
	public String generateXML() {
		// XML generation for the channels provided in the Clock Domain implementation
		return null;
	}

	/**
	 * Method inherited from Component. It returns the type of Component object this is, which is a Channel
	 * Actual definition of this String is in the DrawComponent class.
	 */
	@Override
	public String getTypeAsString() {
		return DrawComponent.CHANNEL_NAME;
	}
	
	/**
	 * Set the name of the Channel on the destination Clock Domain side
	 * @param to - name of Channel on destination Clock Domain side
	 */
	public void setToName(String to) {
		toName = to;
	}



}


