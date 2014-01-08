package gui.tabs;

import graphelements.jgraphx.DrawComponent;
import graphelements.jgraphx.SubSystemGraph;
import graphmodel.*;
import gui.mainwindow.ElementsPane;
import gui.tabs.popups.ComponentPopup;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.SwingUtilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;

/**
 * This class represents the Sub System view. It inherits from mxGraphComponent, which is JGraphX's visualisation component of the created mxGraph.
 * It is placed in the tabs package so that the tabs can be grouped together. If another diagram library is used, then this class needs to be replaced.
 * This component renders:
 * - Sub Systems
 * - Links
 * - An additional cell within Sub Systems that keeps a count of the number of Clock Domains within that Sub System
 * 
 * TODO: ClockDomainViewCanvas and SubSystemViewCanvas have a lot of similar functionality. Maybe investigate a common abstract base class?
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class SubSystemViewCanvas extends mxGraphComponent implements ActionListener {
	private mxCell selectedComponent;
	private String selectedMode;

	public SubSystemViewCanvas() {
		super(new SubSystemGraph());	

		// Set the background of the canvas to white
		this.getViewport().setOpaque(true);
		this.getViewport().setBackground(Color.WHITE);

		// Handle mouse events
		this.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				int clickCount = event.getClickCount();

				// If the user double clicks, then trigger the edit event
				if (clickCount == 2) {
					mxCell comp = getSelectedComponent();
					editComponent(comp);
				} else {
					handleMouseEvent(event);
				}
			}

		});

		// This disables the previewing of edges by dragging from the centre of the vertex. It creates its own edges and undermines the specific components that have been
		// laid out, so this needed to be disabled
		this.connectionHandler.setEnabled(false);
	}


	/**
	 * This method is responsible for handling the single click mouse events on the canvas.
	 * If the click has been made on spare canvas space, and one of the drawing buttons have been selected, then draw the specified component on the canvas.
	 * Once it has been clicked, then a popup window asking for the configuration of that Component will appear, where the user sets its values. Once that has been accepted,
	 * the Component will then be rendered onto the canvas.
	 * 
	 * NOTE: For Links, click on any Sub System to display the Link group creation popup
	 * 
	 * 
	 * @param event - mouse event representing the click on the canvas
	 */
	public void handleMouseEvent(MouseEvent event) {

		// If one of the drawing buttons has been selected
		if (selectedMode != null) {
			// Depending on the type of Component selected, create that Component and draw it
			if (selectedMode.equals(DrawComponent.SUBSYSTEM_NAME)) {	
				mxCell c = getSelectedComponent();
				// If there is no Component selected, it is on a blank space, so it safe to draw a new one there
				if (c == null) {
					// Open the SubSystem creation popup
					ComponentPopup popup = new ComponentPopup((JFrame) SwingUtilities.getWindowAncestor(this), selectedMode, event.getX(), event.getY());
					SubSystem ss = (SubSystem)popup.getComponent();

					// If a SubSystem object was actually created, then add it at the position of the user's mouse coordinates
					if (ss != null) {
						addSubSystem(ss, event.getX(), event.getY());
					}
				}

			} else if (selectedMode.equals(DrawComponent.LINK_NAME)) {
				if (selectedComponent == null) {
					selectedComponent = getSelectedComponent();
				}

				// If a Sub System is selected, display the Link group creation popup.
				if (selectedComponent != null && selectedComponent.getValue() instanceof SubSystem) {
					ComponentPopup popup = new ComponentPopup((JFrame) SwingUtilities.getWindowAncestor(this), selectedMode, event.getX(), event.getY());
					LinkGroup l = (LinkGroup)popup.getComponent();
					if (l !=null){
					// Add the links
					addLinkGroup(l);
					graph.clearSelection();
					selectedComponent = null;
					}
					else{}
				}
				
			}else if (selectedMode.equals(DrawComponent.VIEW_MODE_NAME)){
				System.out.println("running VIEW_MODE_NAME");
				
			}
			
		}
	}

	/**
	 * This method returns the currently selected Component on the canvas,
	 * If no Component is selected, then return null.
	 * 
	 * @return currently selected mxCell
	 */
	public mxCell getSelectedComponent() {
		mxCell cell;
		cell = (mxCell)this.getGraph().getSelectionModel().getCell();

		return cell;

	}

	/**
	 * This method adds the given Sub System. It renders the Sub System at the given coordinates
	 * 
	 * @param ss - SubSystem object to add to the model
	 * @param x - x coordinate of where to draw the Sub System
	 * @param y - y coordinate of where to draw the Sub System
	 */
	public void addSubSystem(SubSystem ss, int x, int y) {
		// Add the Sub System to the object model
		GlobalSystem system = GlobalSystem.getInstance();
		system.addSubSystem(ss);

		// Draw it on the Canvas
		graph.insertVertex(graph.getDefaultParent(), ss.toString(), ss, x, y, DrawComponent.SUBSYSTEM_WIDTH, DrawComponent.SUBSYSTEM_HEIGHT, DrawComponent.SUBSYSTEM_NAME);
		ElementsPane.deselectButtons();

	}

	/**
	 * Whenever a button is selected in the ElementsPane, the selected button's action command is saved. This is how the canvas knows which component to draw on the canvas.
	 * It also clears the current selection so that a new selection can be made.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		selectedMode = event.getActionCommand();
		graph.clearSelection();
	}

	/**
	 * This method adds this group of Links to the object model and draws it onto the canvas.
	 * @param l
	 */
	public void addLinkGroup(LinkGroup l) {
		// Add links to the object model
		GlobalSystem.getInstance().addLinkGroup(l);

		// get the cells of the corresponding Sub Systems used in the link
		ArrayList<mxCell> subSystems = new ArrayList<mxCell>();
		for (Object cell : graph.getChildVertices(graph.getDefaultParent())) {
			mxCell sub = (mxCell)cell;
			SubSystem s = (SubSystem)sub.getValue();

			if (l.containsSubSystem(s)) {
				subSystems.add(sub);
			}

		}

		// Draw the Links onto the canvas
		DrawComponent.drawLinkGroup(this.graph, l, subSystems);

		ElementsPane.deselectButtons();


	}

	/**
	 * This method opens up an existing Component on the canvas for editing.
	 * A popup window will be displayed featuring the existing values of those components in the fields. These fields can then be changed by the user.
	 * Upon selecting OK, a new Component will be created using those configurations and it will replace the old Component.
	 * 
	 * 
	 * TODO: COMPLETE IMPLEMENTATION. Not quite complete with LinkGroups, as have to update the display. Underlying object is changing, but the display is not updating
	 * This is because when doubling clicking a Link, double click one particular connection. But the popup corresponds to all connections. 
	 * Either: Create another popup for editing one part of the Link
	 * 		   Find a way to update all the other connections, even though you only have access to one of the cells. Can do this by iterating through the edges and putting the
	 * 		   related edges in a list??
	 * 
	 * 
	 * @param selectedComponent - Component selected for editing.
	 */
	public void editComponent(mxCell selectedComponent) {
		Component comp = (Component) selectedComponent.getValue();

		// Open popup passing in the Component that is selected. This will populate the fields with its existing values.
		ComponentPopup popup = new ComponentPopup((JFrame) SwingUtilities.getWindowAncestor(this), comp, (int)selectedComponent.getGeometry().getCenterX(), (int)selectedComponent.getGeometry().getCenterY());
		// Once popup is closed, retrieve the created Component object
		Component newComp = popup.getComponent();

		
		if (newComp != null) {
			if (newComp instanceof LinkGroup) {
				// TODO: implement refreshing of Link labels
			}

			// Replace component in model and on the graph
			GlobalSystem system = GlobalSystem.getInstance();
			system.changeComponent(comp, newComp);

			SubSystemGraph g = (SubSystemGraph)graph;
			g.changeComponent(selectedComponent, newComp);
		}

	}
}
