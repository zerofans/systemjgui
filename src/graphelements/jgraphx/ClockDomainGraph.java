package graphelements.jgraphx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Hashtable;

import graphmodel.Channel;
import graphmodel.ClockDomain;
import graphmodel.Component;
import graphmodel.GlobalSystem;
import graphmodel.Signal;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * This class represents the mxGraph object that will be used for the Clock Domain view.
 * The components that are allowed in this view are:
 * - Clock Domains
 * - Input/Output Signals
 * - Channels
 * 
 * Inherited from mxGraph so that specific constraints can be added, and to set the cell styles of all the
 * components possible in this view.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class ClockDomainGraph extends mxGraph implements ActionListener {

	public ClockDomainGraph() {
		super();

		// Set constraints on the mxGraph
		this.setAllowDanglingEdges(true);
		this.setMultigraph(true);
		this.setDisconnectOnMove(false);
		this.setCellsEditable(false);

		// Set styles of all the components
		mxStylesheet styleSheet = getStylesheet();
		styleSheet.putCellStyle(DrawComponent.CLOCK_DOMAIN_NAME, DrawComponent.CLOCK_DOMAIN_VIEW);
		styleSheet.putCellStyle(DrawComponent.CHANNEL_NAME, DrawComponent.CHANNEL_VIEW);
		styleSheet.putCellStyle(DrawComponent.SIGNAL_NAME, DrawComponent.SIGNAL_VIEW);
		setStylesheet(styleSheet);
		
		// Add this graph as a listener to the global object model
		GlobalSystem.getInstance().addListener(this);

	}

	/**
	 * This method clears the existing configuration in the graph, and recreates it based on the
	 * current state of the global object model.
	 * It will only add the existing Clock Domains, Channels and Signals to this graph.
	 * 
	 * TODO: Placement of the components is hard coded, so will be arranged in a grid. Investigate automatic
	 * layout. There are libraries within JGraphX that look to deal with this, but need to be investigated more thoroughly.
	 * 
	 */
	public void createGraph() {
		// Clear the existing model
		GlobalSystem system = GlobalSystem.getInstance();
		mxGraphModel m = (mxGraphModel)this.getModel();
		m.clear();

		this.getModel().beginUpdate();

		try {

			// Placement coordinates are hard coded
			double x = 50;
			double y = 50;
			
			// Insert all clock domains
			for (ClockDomain cd : system.getAllClockDomains()) {
				mxCell clockDomain = DrawComponent.drawClockDomain(this, (mxCell) this.getDefaultParent(), cd, x, y);
				
				// Place the next Clock Domain one Clock Domain width away
				x += 2 * DrawComponent.CLOCK_DOMAIN_WIDTH;

				// If the x coordinate reaches 500, start another row
				if (x >= 500) {
					x = 50;
					y += 2 * DrawComponent.CLOCK_DOMAIN_HEIGHT;
				}

				// Insert input signals
				int xOffset = 0;
				for (Signal sig : cd.getInputSignals()) {
					DrawComponent.drawSignal(this, sig, clockDomain, xOffset);
					xOffset++;
				}

				// Insert output signals
				xOffset = 0;
				for (Signal sig : cd.getOutputSignals()) {
					DrawComponent.drawSignal(this, sig, clockDomain, xOffset);
					xOffset++;

				}


			}

			// put in Channels once Clock Domains have been created
			
			// Get all Clock Domain cells
			Hashtable<ClockDomain, mxCell> clockDomainCells = new Hashtable<ClockDomain, mxCell>();

			for (Object obj : getChildVertices(getDefaultParent())) {
				mxCell cdCell = (mxCell)obj;
				clockDomainCells.put((ClockDomain)cdCell.getValue(), cdCell);
			}

			// Iterate through all the Clock Domain cells
			for (mxCell cdCell : clockDomainCells.values()) {
				ClockDomain cdFrom = (ClockDomain)cdCell.getValue();
				//Get all output channels from this Clock Domain. 
				// Only output Channels needed as it contains where the Channels are going to
				Collection<Channel> channels = cdFrom.getOutputChannels();

				// Indexes the Channels so the ports don't overlap
				int yOffset = 0;
				for (Channel ch : channels) {
					// Get destination Clock Domain cell
					mxCell cdToCell = clockDomainCells.get(ch.getToClockDomain());	
					ClockDomain cdTo = (ClockDomain)cdToCell.getValue();
					DrawComponent.drawChannel(this, ch, cdCell, cdToCell, yOffset, cdTo.getNumInputChannels());
					
					yOffset++;
				}
			}

		} finally {


			this.getModel().endUpdate();
			
			// TODO: Put layout code here. Experiment with layout
			mxFastOrganicLayout layout = new mxFastOrganicLayout(this);
			layout.execute(getDefaultParent());
		}
		refresh();
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
	 * It exchanges the cell value with the new component on the specified graph view
	 * 
	 * @param compCell - the graph cell to change
	 * @param comp - the object model component to change the cell's value to
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

	/**
	 * This method listens to the state of the global object model. If a new model has been created, then
	 * create a new graph in sync with the model.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals(GlobalSystem.MODEL_CREATED_EVENT)) {
			createGraph();
		} else if (command.equals(GlobalSystem.MODEL_CLEARED_EVENT)) {
			// TODO: Clear the model 
		}
		
	}



}
