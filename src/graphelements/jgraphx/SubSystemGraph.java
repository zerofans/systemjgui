package graphelements.jgraphx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphmodel.ClockDomain;
import graphmodel.Component;
import graphmodel.GlobalSystem;
import graphmodel.LinkGroup;
import graphmodel.SubSystem;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * This class represents the mxGraph object that will be used for the Sub System
 * View. The components that are allowed in this view are: - Sub Systems - Links
 * 
 * An additional element is added to each Sub System denoting how many Clock
 * Domains are contained within it.
 * 
 * Inherited from mxGraph so that specific constraints can be added, and to set
 * the cell styles of all the components possible in this view.
 * 
 * TODO: This and ClockDomainViewGraph have some similar functionality. Abstract
 * base class maybe?
 * 
 * @author Chanisha Somatilaka, rsom024
 * 
 */
public class SubSystemGraph extends mxGraph implements ActionListener {

	// Label on the cell added to the Sub System denoting how many Clock Domains
	// it has.
	private static final String CLOCK_DOMAIN_NUM = "Num. CD's: ";
	private static int[] oldclockdomiansize = new int[10] ; // this might need to be a int array;
	Map<String, Integer> oldclockdomain = new HashMap<String, Integer>();
	public SubSystemGraph() {
		super();

		// Set constraints on the mxGraph
		this.setAllowDanglingEdges(true);
		this.setMultigraph(true);
		this.setDisconnectOnMove(false);
		this.setCellsEditable(false);

		// Set styles of all the components
		mxStylesheet styleSheet = getStylesheet();
		styleSheet.putCellStyle(DrawComponent.SUBSYSTEM_NAME,
				DrawComponent.SUBSYSTEM_VIEW);
		styleSheet.putCellStyle(DrawComponent.LINK_NAME,
				DrawComponent.LINK_VIEW);
		// The cell denoting number of Clock Domains has been set to look the
		// same as a Clock Domain
		styleSheet.putCellStyle(SubSystemGraph.CLOCK_DOMAIN_NUM,
				DrawComponent.CLOCK_DOMAIN_VIEW);
		setStylesheet(styleSheet);

		// By default, there is one Sub System. Add this to the graph
		// TODO: Give default Sub System name SS1? Something to think about
		SubSystem s = GlobalSystem.getInstance().getSubSystem("SS1");
		GlobalSystem.getInstance().addListener(this);

		this.insertVertex(getDefaultParent(), s.toString(), s, 10, 10,
				DrawComponent.SUBSYSTEM_WIDTH, DrawComponent.SUBSYSTEM_HEIGHT,
				DrawComponent.SUBSYSTEM_NAME);

	}

	/**
	 * Determines that any cell that is a vertex can be moved, but not edges.
	 */
	@Override
	public boolean isCellMovable(Object cell) {
		return !getModel().isEdge(cell);
	}

	/**
	 * This method is called whenever a component on the graph is being edited.
	 * It exchanges the cell value with the new component on the specified graph
	 * view
	 * 
	 * @param compCell
	 *            - the graph cell to change
	 * @param comp
	 *            - the object model component to change the cell's value to
	 */
	public void changeComponent(mxCell compCell, Component comp) {
		this.getModel().beginUpdate();

		try {
			// set the cell's value to the new Component
			compCell.setValue(comp);
		} finally {
			this.getModel().endUpdate();
		}
		refresh();

	}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	//need to fix
	// you should not add vertex subsystem if there is not sub system has been added.
	
	/**
	 * When any change is made in the global object model, refresh this display.
	 * This method deals with the addition of other Clock Domains in the global
	 * object model, so updates the count for each Sub System
	 */
	@Override
	public void refresh() {
		List<SubSystem> subs = GlobalSystem.getInstance().getSubSystems();
		int oldsize =0 ;			
		for (SubSystem s : subs) {
			
			System.out.println(s.getClockDomains());
			//oldclockdomiansize //add at 19/12/2013
			try {
				oldsize = oldclockdomain.get(s.getName());
			}
			catch(NullPointerException e)
			{
				System.err.println(e.getMessage());
				System.out.println(" the old clock domain is null");
				
			}
			//new size of the subsystem
			int newsize = s.getClockDomains().size();
			
			if (newsize > oldsize) { //this is always bigger then 0??
				// add a cell updating the number
				mxCell c = (mxCell) insertVertex(getSubSystem(s),
						CLOCK_DOMAIN_NUM,
						CLOCK_DOMAIN_NUM + s.getNumClockDomains(), 0, 0,
						DrawComponent.CLOCK_DOMAIN_WIDTH,
						DrawComponent.CLOCK_DOMAIN_HEIGHT, CLOCK_DOMAIN_NUM);
				c.getGeometry().setRelative(true);
			}
			
			else if (newsize< oldsize){
				 List<ClockDomain> templist = s.getClockDomains();
				 System.out.println("printing clockdomain");
				 if (newsize != -1 ) //not empty				 
					 {
					 // System.out.println(newsize);
					 mxCell[] temparray = new mxCell[2];
					 temparray[0] = (mxCell) getSubSystem(s).getChildAt((newsize));
					 removeCells(temparray); // this one only work in a array
					//remove selection cell not working
					 /*System.out.println(getSubSystem(s).getChildCount());
					 System.out.println("printing child");
					 System.out.println(getSubSystem(s).getChildAt((newsize -1 ) ));
					 */
					 				   
				 }
				 else{
					 System.out.println(" there is no more ClockDomain left");
				 }
			
			}
			
			oldclockdomain.put(s.getName(),  s.getClockDomains().size() ); // TUDO check if this one may cause more problem when repeated name
		}
		super.refresh();
	}

	/**
	 * This method clears the existing configuration in the graph, and recreates
	 * it based on the current state of the global object model. It will only
	 * add the existing Sub Systems and Links to this graph.
	 * 
	 * TODO: Placement of the components is hard coded, so will be arranged in a
	 * grid. Investigate automatic layout. There are libraries within JGraphX
	 * that look to deal with this, but need to be investigated more thoroughly.
	 * 
	 */
	private void createGraph() {
		// Clear the existing model
		GlobalSystem system = GlobalSystem.getInstance();
		mxGraphModel m = (mxGraphModel) this.getModel();
		m.clear();

		m.beginUpdate();

		try {

			// Placement coordinates are hard coded
			int x = 50;
			int y = 50;

			// Add in all the Subsystems
			for (SubSystem s : system.getSubSystems()) {
				DrawComponent.drawSubSystem(this, s, x, y);
				// Place the next Sub System one Sub System + 50 width away
				x += DrawComponent.SUBSYSTEM_WIDTH + 50;

				// If the x coordinate reaches 500, start another row
				if (x >= 500) {
					x = 50;
					y += DrawComponent.SUBSYSTEM_HEIGHT + 20;
				}

			}

			// Add Links after adding all the Sub Systems
			for (LinkGroup l : GlobalSystem.getInstance().getLinks()) {
				// get the cells of the corresponding Sub Systems used in the
				// Link
				ArrayList<mxCell> subSystems = new ArrayList<mxCell>();

				// For all the Sub System cells, if the given Link contains it,
				// add the cell to a list to pass
				// into the drawLinkGroup method
				for (Object cell : getChildVertices(getDefaultParent())) {
					mxCell sub = (mxCell) cell;
					SubSystem s = (SubSystem) sub.getValue();

					if (l.containsSubSystem(s)) {
						subSystems.add(sub);
					}

				}

				// Don't need to draw Links that are less than 2
				// TODO: Is this correct?
				if (l.getLinks().size() >= 2) {
					DrawComponent.drawLinkGroup(this, l, subSystems);
				}
			}

		} finally {

			m.endUpdate();

			mxFastOrganicLayout layout = new mxFastOrganicLayout(this);
			// layout.setUseBoundingBox(true);
			// layout.setDisableEdgeStyle(false);
			layout.execute(getDefaultParent());
		}
		refresh();
	}

	/**
	 * This method retrieves the corresponding graph cell to the Sub System
	 * given
	 * 
	 * @param sub
	 *            - Sub System object from the object model
	 * @return mxcell encapsulating that object
	 */
	public mxCell getSubSystem(SubSystem sub) {
		mxCell cell = null;

		// Iterate through all the Sub System cells
		for (Object c : getChildVertices(getDefaultParent())) {
			cell = (mxCell) c;
			// retrieve the Sub System associated with that cell
			SubSystem cellSub = (SubSystem) cell.getValue();
			if (cellSub == sub) {
				break;
			}
		}
		return cell;
	}

	/**
	 * This method listens to the state of the global object model. If a new
	 * model has been created, then create a new graph in sync with the model.
	 * 
	 * But also, if any change is made to the model (ie if Clock Domains are
	 * added), then update the display.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(GlobalSystem.MODEL_CREATED_EVENT)) {
			System.out.println(event.getActionCommand());
			createGraph();
		}

		else {
			refresh();
		}

	}

}
