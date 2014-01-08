/**
 * This package encapsulates all library specific behaviour for JGraphX. It contains implementations of the
 * graph models as well as drawing specifications.
 */
package graphelements.jgraphx;

import graphmodel.Channel;
import graphmodel.ClockDomain;
import graphmodel.Component;
import graphmodel.GlobalSystem;
import graphmodel.IOType;
import graphmodel.Link;
import graphmodel.LinkGroup;
import graphmodel.Signal;
import graphmodel.SubSystem;

import java.awt.Color;
import java.util.Hashtable;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxConnectionConstraint;
import com.mxgraph.view.mxGraph;

/**
 * This class contains all logic to define the look and feel of every drawable component, as well as the implementation to draw it..
 * It contains the widths and heights of Clock Domains and Sub Systems, and style sheets for:
 * - Clock Domain
 * - Channel
 * - Signal
 * - Sub System
 * - Link
 * 
 * All members of this class are public and static, so no instance is needed when making calls to this class.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class DrawComponent {

	// SIZES OF COMPONENTS
	public static final int CLOCK_DOMAIN_WIDTH = 70;
	public static final int CLOCK_DOMAIN_HEIGHT = 90; 
	public static final int SUBSYSTEM_WIDTH = 200;
	public static final int SUBSYSTEM_HEIGHT = 200;
	public static final int SIGNAL_LENGTH = 50;
	
	// Size of the blue square at each end of the channel. The blue square is the default look of vertices
	// in JGraphX. If that look was to be changed, add a new Style Sheet
	public static final int CHANNEL_PORT_SIZE = 5; 

	// STYLE SHEETS
	public static final Hashtable<String, Object> CLOCK_DOMAIN_VIEW = new Hashtable<String, Object>();
	public static final Hashtable<String, Object> CHANNEL_VIEW = new Hashtable<String, Object>();
	public static final Hashtable<String, Object> SIGNAL_VIEW = new Hashtable<String, Object>();
	public static final Hashtable<String, Object> SUBSYSTEM_VIEW = new Hashtable<String, Object>();
	public static final Hashtable<String, Object> LINK_VIEW = new Hashtable<String, Object>();

	public static final Hashtable<String, Object> CHANNEL_LABEL = new Hashtable<String, Object>();

	// COMPONENT NAMES
	public static final String CLOCK_DOMAIN_NAME = "Clock Domain";
	public static final String VIEW_MODE_NAME = "View";
	public static final String SUBSYSTEM_NAME = "Sub System";
	public static final String CHANNEL_NAME = "Channel";
	public static final String SIGNAL_NAME = "Signal";
	public static final String LINK_NAME = "Link";
	public static final String INTERFACE_NAME = "Interface";


	// Style sheets initialised in a static code block, as an instance is not needed for them.
	// Defines how each of the components look.
	static {
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_STROKECOLOR,mxUtils.getHexColorString(Color.BLACK));
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		CLOCK_DOMAIN_VIEW.put(mxConstants.STYLE_FOLDABLE, true);

		CHANNEL_VIEW.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
		CHANNEL_VIEW.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
		CHANNEL_VIEW.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
		CHANNEL_VIEW.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
		CHANNEL_VIEW.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT);

		LINK_VIEW.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_TOPTOBOTTOM);
		LINK_VIEW.put(mxConstants.STYLE_STROKEWIDTH, 3.0);
		LINK_VIEW.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
		LINK_VIEW.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
		LINK_VIEW.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT);
		LINK_VIEW.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		LINK_VIEW.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);

		SIGNAL_VIEW.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);
		SIGNAL_VIEW.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
		SIGNAL_VIEW.put(mxConstants.STYLE_STROKECOLOR,mxUtils.getHexColorString(Color.BLACK));
		SIGNAL_VIEW.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));

		SUBSYSTEM_VIEW.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_FILLCOLOR, mxConstants.NONE);
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_ROUNDED, true);
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		SUBSYSTEM_VIEW.put(mxConstants.STYLE_FOLDABLE, false);	
	}

	/**
	 * Draws a Clock Domain onto the canvas, specified by the CLOCK_DOMAIN_VIEW Style Sheet defined above.
	 * This is drawn as a vertex onto the graph
	 * 
	 * TODO: Examine whether it's necessary to have the subParent parameter, as the Clock Domains are added
	 * to a separate graph to the Sub Systems
	 *
	 * @param graph - mxGraph to add the Clock Domain to
	 * @param subParent - The mxCell object that represents the SubSystem that this Clock Domain belongs to
	 * @param cd - Clock Domain model object
	 * @param x - x coordinates to draw the Clock Domain to
	 * @param y - y coordinates to draw the Clock Domain to
	 * @return mxCell object which represents the drawn Clock Domain
	 */
	public static mxCell drawClockDomain(mxGraph graph, mxCell subParent, ClockDomain cd, double x, double y) {
		mxCell vertex = (mxCell)graph.insertVertex(subParent, cd.toString(), cd, x, y, DrawComponent.CLOCK_DOMAIN_WIDTH, DrawComponent.CLOCK_DOMAIN_HEIGHT, DrawComponent.CLOCK_DOMAIN_NAME);
		
		return vertex;
	}
	
	// under working
	// robert

	
	public static void remvoeClockDomain(mxGraph graph, mxCell Clockdomian){
		System.out.println ("in removeclockdomain");
		Object[] removedcell = graph.removeCells();
		
		//Object[] removedcell = graph.getSelectionCells();
		
		for ( Object obj :  removedcell){
			if (obj instanceof mxCell){
				System.out.println(obj);
				mxCell rtemp = (mxCell)obj;
				System.out.println (rtemp.getAttribute("Name"));
				System.out.println (rtemp.getId());
				if (rtemp.getValue() instanceof Component) {
					Component selectedComponent = (Component) rtemp.getValue();
					System.out.println("component is " + selectedComponent);	
					if (selectedComponent instanceof ClockDomain) {
						ClockDomain cd = (ClockDomain)selectedComponent;
						System.out.println(cd.getClassName() );
						//System.out.println(cd. );
						// what if there is same cd name in 2 sub system
						GlobalSystem.getInstance().removeClockDomain(cd);
						//for ( SubSystem temp : allsubsys){
						//	GlobalSystem.getInstance().removeClockDomain(cd,temp);
							
							/*List<ClockDomain> allclcDomain = temp.getClockDomains();					
							for (ClockDomain tempclc : allclcDomain){
								if (tempclc == cd){
									System.out.print("found cd " );
									System.out.println(cd);
									System.out.println(temp);
								}
							}*/ // this should not need to be used
						//}
						List<SubSystem> allsubsysdisplay = GlobalSystem.getInstance().getSubSystems();
						for ( SubSystem temp : allsubsysdisplay){
							List<ClockDomain> allclcDomain = temp.getClockDomains();					
							for (ClockDomain tempclc : allclcDomain){
								
									System.out.print("found cd " );
									System.out.println(tempclc);
									System.out.println(temp);
								
							}
							
							
						}
						
						
					}
				}
				
			}
			else
			{
				System.out.println("find " +  obj);
			}
			
		}
		
		
		
		System.out.println ("this has been removed " + removedcell);
		
	}

	/**
	 * Draws a Channel onto the canvas, specified by the CHANNEL_VIEW Style Sheet defined above.
	 * This is drawn as an edge between two vertices on the graph
	 * 
	 * @param graph - mxGraph to add the Channel to
	 * @param ch - Channel model object
	 * @param cdFrom - mxCell representing the Clock Domain the Channel goes from
	 * @param cdTo - mxCell representing the Clock Domain the Channel goes to
	 * @param fromIndex - current number of Channels on the originating Clock Domain
	 * @param toIndex - current number of Channels on the destination Clock Domain
	 * @return mxCell object which represents the drawn Channel
	 */
	public static mxCell drawChannel(mxGraph graph, Channel ch, mxCell cdFrom, mxCell cdTo, int fromIndex, int toIndex) {

		// Ensures that the ports coming from the originating Clock Domain do not overlap each other
		int yOffset = fromIndex * 10;
		
		// Retrieve names for labelling
		String fromName = ch.getName();
		String toName = ch.getToName();
		
		mxCell edge;
		graph.getModel().beginUpdate();
		try {
			// port for Channel from the source Clock Domain
			mxGeometry geo1 = new mxGeometry(0, 0.5, CHANNEL_PORT_SIZE, CHANNEL_PORT_SIZE);
			
			// set the new origin of the port to be on the top right side of the Clock Domain, accounting for the size of the port and the yOffset
			geo1.setOffset(new mxPoint(CLOCK_DOMAIN_WIDTH - CHANNEL_PORT_SIZE, -CLOCK_DOMAIN_HEIGHT/2 + yOffset));
			geo1.setRelative(true);
			
			// Create the "from" port of the Channel and add it to the ClockDomain cell
			mxCell port1 = new mxCell(null, geo1,"shape=rectangle;perimeter=rectanglePerimeter");
			port1.setVertex(true);
			graph.addCell(port1, cdFrom);

			// port for Channel on the target Clock Domain
			// Ensures that the ports going to the destination Clock Domain do not overlap each other
			yOffset = toIndex * 10;
			mxGeometry geo2 = new mxGeometry(0, 0.5, CHANNEL_PORT_SIZE, CHANNEL_PORT_SIZE);
			
			// set the new origin of this port to be on the top left side of the Clock Domain, accounting for the yOffset
			geo2.setOffset(new mxPoint(0, -CLOCK_DOMAIN_HEIGHT/2 + yOffset));
			mxCell port2 = new mxCell(null, geo2, "shape=rectangle;perimeter=rectanglePerimeter");
			port2.setVertex(true);
			graph.addCell(port2, cdTo);

			// Insert the Channel between the two ports, using its name in the originating Clock Domain as its label
			edge = (mxCell) graph.insertEdge(graph.getDefaultParent(), fromName, ch, port1, port2, CHANNEL_NAME);
			edge.getGeometry().setRelative(true);
			// Set the position of the label to be on its left side, towards the originating Clock Domain
			edge.getGeometry().setX(-0.90);
			edge.getGeometry().setY(5);

			// Create another cell representing the label on the destination Clock Domain side
			mxGeometry geo3 = new mxGeometry();
			mxCell toCDLabel = new mxCell(toName, geo3, "fontColor=#0000;width=20");
			toCDLabel.getGeometry().setRelative(true);
			toCDLabel.setVertex(true);
			toCDLabel.getGeometry().setX(0.90);
			toCDLabel.getGeometry().setY(5);
			graph.addCell(toCDLabel, edge);

			// Define direction of the edge and constraints
			graph.getModel().setTerminal(edge, port1, true);
			graph.getModel().setTerminal(edge, port2, false);
			graph.setConnectionConstraint(edge, port1, true, new mxConnectionConstraint(new mxPoint(1, 0.5), true));
			graph.setConnectionConstraint(edge, port2, false, new mxConnectionConstraint(new mxPoint(0, 0.5), true));

		} finally {
			graph.getModel().endUpdate();
		}

		return edge;
	}

	/**
	 * Draws a Sub System onto the canvas, specified by the SUBSYSTEM_VIEW Style Sheet defined above.
	 * This is drawn as a vertex on the given graph
	 * 
	 * @param graph - mxGraph to add the Sub System to
	 * @param ss - Sub System model object
	 * @param x - x coordinates to draw the Sub System to
	 * @param y - y coordinates to draw the Sub System to
	 * @return mxCell representing the drawn Sub System
	 */
	public static mxCell drawSubSystem(mxGraph graph, SubSystem ss, int x, int y) {
		graph.getModel().beginUpdate();
		mxCell sub;
		try {
			// Add Sub System cell to the graph's default parent, as it is the top level of the system
			sub = (mxCell) graph.insertVertex(graph.getDefaultParent(), ss.toString(), ss, x, y, SUBSYSTEM_WIDTH, SUBSYSTEM_HEIGHT, SUBSYSTEM_NAME);
		} finally {
			graph.getModel().endUpdate();
		}

		return sub;
	}

	/**
	 * Draws a group of Links onto the canvas, specified by the LINK_VIEW Style Sheet defined above.
	 * This is drawn as an edge between two vertices on the graph
	 * 
	 * TODO: Check the correctness of this implementation. This is on the understanding that everything in the <Link> group in the XML file are separate links
	 * but are related to each other, so a link between every Sub System mentioned in that group will be constructed.
	 * 
	 * @param graph - mxGraph to add the Links to
	 * @param links - group of Links to add
	 * @param subSystems - list of cells representing the Sub Systems that the links are between
	 */
	public static void drawLinkGroup(mxGraph graph, LinkGroup links, List<mxCell> subSystems) {
		List<Link> linkList = links.getLinks();
		
		// for every Link in a Link group, draw a connection between all the Sub Systems part of this LinkGroup
		for (int i = 0; i < linkList.size(); i++) {
			
			Link link1 = linkList.get(i);
			mxCell subCell1 = null;
			
			// Get the corresponding graph cell of the sub system present in this link
			for (mxCell sub : subSystems) {
				SubSystem s = (SubSystem) sub.getValue();
				if (s == link1.getSubSystem()) {
					subCell1 = sub;
					break;
				}
			}
			
			// Get the next Link interface in the group
			for (int j = i + 1; j < links.getLinks().size(); j++) {
				Link link2 = linkList.get(j);
				mxCell subCell2 = null;
				
				// Get the corresponding graph cell of the sub system
				for (mxCell sub : subSystems) {
					SubSystem s = (SubSystem) sub.getValue();
					if (s == link2.getSubSystem()) {
						subCell2 = sub;
						break;
					}
				}
				
				graph.getModel().beginUpdate();

				// align the link arrows appropriately on both the involved sub systems
				// TODO: Not sure if the Links are aligned properly. Investigate
				int xOffset1 = 20 + subCell1.getEdgeCount() * 20;
				int xOffset2 = 20 + subCell2.getEdgeCount() * 20;

				// Set port cell for originating Sub System
				mxGeometry geo1 = new mxGeometry(0, 0.5, 0, 0);
				geo1.setOffset(new mxPoint(xOffset1, DrawComponent.SUBSYSTEM_HEIGHT/2));
				geo1.setRelative(true);

				mxCell port1 = new mxCell(null, geo1,"shape=rectangle;perimeter=rectanglePerimeter;fontColor=#0000;width=20");
				port1.setValue(link1);
				port1.setVertex(true);

				mxCell edge;

				try {

					graph.addCell(port1, subCell1);
					
					// port for Link on the target Sub System
					mxGeometry geo2 = new mxGeometry(0, 0.5, 0, 0);
					geo2.setOffset(new mxPoint(xOffset2, DrawComponent.SUBSYSTEM_HEIGHT/2));
					geo2.setRelative(true);
					mxCell port2 = new mxCell(null, geo2, "shape=rectangle;perimeter=rectanglePerimeter;fontColor=#0000");
					port2.setVertex(true);
					
					// set labels of the links on the ports
					port2.setValue(link2);
					graph.addCell(port2, subCell2);

					edge = (mxCell) graph.insertEdge(graph.getDefaultParent(), link1.toString(), links, port1, port2, LINK_NAME);
					edge.getGeometry().setRelative(true);
					edge.getGeometry().setX(-0.90);
					edge.getGeometry().setY(5);


					graph.getModel().setTerminal(edge, port1, true);
					graph.getModel().setTerminal(edge, port2, false);
					graph.setConnectionConstraint(edge, port1, true, new mxConnectionConstraint(new mxPoint(0, 0), true));
					graph.setConnectionConstraint(edge, port2, false, new mxConnectionConstraint(new mxPoint(0, 0), true));

				} finally {
					graph.getModel().endUpdate();
				}
			}

		}

	}

	/**
	 * Draws a Signal onto the canvas, specified by the Signal_VIEW Style Sheet defined above.
	 * This is represented as drawing an edge going from/to the Clock Domain (depending if it is an Input or Output Signal)
	 * The edge is drawn between the Clock Domain and an empty cell.
	 * 
	 * @param graph
	 * @param s
	 * @param clockDomainCell
	 * @param index
	 * @return
	 */
	public static mxCell drawSignal(mxGraph graph, Signal s, mxCell clockDomainCell, int index) {

		// Pixel offset of the Signal on the Clock Domain. If further spacing is desired, then increase the number it should be multiplied by
		int xOffset = index * 10;

		mxCell edge;
		graph.getModel().beginUpdate();
		try {
			mxGeometry geo = new mxGeometry(0, 0.5, 1, 1);

			// If the Signal is an input signal, place the offset on the bottom of the Clock Domain.
			// If the Signal is an output signal, place the offset on the top of the Clokc Domain
			if (s.getType() == IOType.INPUT) {
				geo.setOffset(new mxPoint(xOffset, DrawComponent.CLOCK_DOMAIN_HEIGHT/2));
			} else {
				geo.setOffset(new mxPoint(xOffset, -DrawComponent.CLOCK_DOMAIN_HEIGHT/2));
			}

			// Add the port to the Clock Domain cell
			geo.setRelative(true);
			mxCell port = new mxCell(null, geo, "shape=rectangle;perimeter=rectanglePerimeter");
			port.setVertex(true);
			graph.addCell(port, clockDomainCell);

			// Create the Signal edge
			edge = (mxCell)graph.insertEdge(clockDomainCell, s.toString(), s, null, null, SIGNAL_NAME);
			mxGeometry geo1 = graph.getCellGeometry(edge);
			geo1.setRelative(true);

			if (s.getType() == IOType.INPUT) {
				// Set the port of the Signal to the length of the Signal below the Clock Domain
				geo1.setTerminalPoint(new mxPoint(xOffset, DrawComponent.CLOCK_DOMAIN_HEIGHT + SIGNAL_LENGTH), true);
				// set it to come from the port
				graph.getModel().setTerminal(edge, port, false);
			} else {
				// Set the port of the Signal to be above the Clock Domain
				geo1.setTerminalPoint(new mxPoint(xOffset, -SIGNAL_LENGTH), false);
				// set it to go to the port
				graph.getModel().setTerminal(edge, port, true);
			}

		} finally {
			graph.getModel().endUpdate();
		}

		return edge;
	}
}
