package gui.tabs;

import graphelements.jgraphx.ClockDomainGraph;
import graphelements.jgraphx.DrawComponent;
import graphmodel.*;
import gui.mainwindow.ElementsPane;
import gui.tabs.popups.ComponentPopup;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;

/**
 * This class represents the Clock Domain view. It inherits from
 * mxGraphComponent, which is JGraphX's visualisation component of the created
 * mxGraph. It is placed in the tabs package so that the tabs can be grouped
 * together. If another diagram library is used, then this class needs to be
 * replaced. This component renders: - Clock Domains - Signals - Channels
 * 
 * TODO: ClockDomainViewCanvas and SubSystemViewCanvas have a lot of similar
 * functionality. Maybe investigate a common abstract base class?
 * 
 * @author Chanisha Somatilaka, rsom024
 * 
 */
public class ClockDomainViewCanvas extends mxGraphComponent implements
		ActionListener {
	private mxCell selectedComponent;
	private String selectedMode;

	public ClockDomainViewCanvas() {
		super(new ClockDomainGraph());

		// Set the background of the canvas to white
		this.getViewport().setOpaque(true);
		this.getViewport().setBackground(Color.WHITE);

		// Handle mouse events
		this.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				int clickCount = event.getClickCount();
				System.out
				.println("aaaaa");

				// If the user double clicks, then trigger the edit event
				if (clickCount == 2) {
					mxCell comp = getSelectedComponent();

					if (comp != null) {
						editComponent(comp);
					} else {
						System.out
								.println("You have double clicked somewhere, but no component has been selected");

					}
				} else {
					handleMouseEvent(event);
				}
			}

		});
		
		this.addKeyListener( new KeyAdapter(){ //.graph.addListener(eventName, listener);	
			
			@Override
			public void keyPressed(KeyEvent e){
				System.out.println(e.getExtendedKeyCode());
				System.out.println(e.getExtendedKeyCodeForChar(0));
				System.out.println("test test");
				if (e.getExtendedKeyCode()==127)// delete key
				{
					handleDeletefunction(e);
				}
				
			}			
		}
		
				
		);
		
		
		
		//mxKeyboardHandler tempname  = new mxKeyboardHandler( this );
		
		// This disables the previewing of edges by dragging from the centre of
		// the vertex. It creates its own edges and undermines the specific
		// components that have been
		// laid out, so this needed to be disabled
		this.connectionHandler.setEnabled(false);
	}

	protected void handleDeletefunction(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(getSelectedComponent());
		graphelements.jgraphx.DrawComponent.remvoeClockDomain(getGraph(), getSelectedComponent());
		
	}

	/**
	 * This method is responsible for handling the single click mouse events on
	 * the canvas. If the click has been made on spare canvas space, and one of
	 * the drawing buttons have been selected, then draw the specified component
	 * on the canvas. Once it has been clicked, then a popup window asking for
	 * the configuration of that Component will appear, where the user sets its
	 * values. Once that has been accepted, the Component will then be rendered
	 * onto the canvas.
	 * 
	 * NOTE: For Channels, two different Clock Domains have to be chosen
	 * sequentially, and then the popup will be opened. TODO: Maybe add Clock
	 * Domains as dropdowns in the box, or add a label for hinting to help with
	 * usability
	 * 
	 * @param event
	 *            - mouse event representing the click on the canvas
	 */
	public void handleMouseEvent(MouseEvent event) {

		// If one of the drawing buttons has been selected
		if (selectedMode != null) {
			// Depending on the type of Component selected, create that
			// Component and draw it
			if (selectedMode.equals(DrawComponent.CLOCK_DOMAIN_NAME)) {
				// Display the popup for Clock Domain creation
				ComponentPopup popup = new ComponentPopup(
						(JFrame) SwingUtilities.getWindowAncestor(this),
						selectedMode, event.getX(), event.getY());

				// Create a new ClockDomain object
				ClockDomain cd = (ClockDomain) popup.getComponent();
				SubSystem sub = popup.getSelectedSubSystem();

				// This check is here to check if the popup was cancelled, so
				// not to add a new Clock Domain if it was
				if (cd != null) {

					// Add the Clock Domain at the same place where the user
					// clicked
					addClockDomain(cd, event.getX(), event.getY(), sub);
				}
			} else if (selectedMode.equals(DrawComponent.CHANNEL_NAME)) {
				// Channels need 2 clicks to create. If the first click was on a
				// Component, then save the first click.
				// TODO: Maybe change the 2 click system.
				if (selectedComponent == null) {
					selectedComponent = getSelectedComponent();

					// If a component has been selected, and it is a Clock
					// Domain, then get the currently selected Component.
				} else if (selectedComponent != null
						&& selectedComponent.getValue() instanceof ClockDomain) {
					mxCell c = getSelectedComponent();
					// If they are both Clock Domains, proceed
					if (c != null && c.getValue() instanceof ClockDomain
							&& !c.equals(selectedComponent)) {
						ComponentPopup popup = new ComponentPopup(
								(JFrame) SwingUtilities.getWindowAncestor(this),
								selectedMode, event.getX(), event.getY());
						Channel ch = (Channel) popup.getComponent();

						// change here made by Robert: if clicked cancel all
						// return will be null;
						if (ch != null) {
							// Set labels of Channel according to user
							// specified.
							String channelFromName = popup.getChannelFromName();
							String channelToName = popup.getChannelToName();

							addChannel(ch, selectedComponent, c,
									channelFromName, channelToName);
							// Reset fields to process new events
							selectedComponent = null;
						} else {
						}
						graph.clearSelection();
					}
				} else {
					// reset selection field
					selectedComponent = null;
				}
			} else if (selectedMode.contains(DrawComponent.SIGNAL_NAME)) {
				// Selected Component will be the ClockDomain object to add the
				// Signal to.
				selectedComponent = getSelectedComponent();

				if (selectedComponent != null
						&& selectedComponent.getValue() instanceof ClockDomain) {
					// Display Signal creation popup window
					ComponentPopup popup = new ComponentPopup(
							(JFrame) SwingUtilities.getWindowAncestor(this),
							selectedMode, event.getX(), event.getY());
					Signal s = (Signal) popup.getComponent();

					if (s != null) {
						// Add the Signal to the selected Clock Domain
						addSignal(s, selectedComponent);
					} else {
					}

				}
			} else if (selectedMode.contains(DrawComponent.VIEW_MODE_NAME)){
				System.out.println("VIEW_MODE_NAME has been clicked");
			}
		}
	}

	/**
	 * This method adds the Signal to the model and renders it on the canvas. It
	 * calculates the position of where the Signal should be drawn, and then
	 * passes itself to the DrawComponent class, which handles all the
	 * rendering.
	 * 
	 * @param s
	 * @param clockDomain
	 */
	public void addSignal(Signal s, mxCell clockDomain) {
		ClockDomain cd = (ClockDomain) clockDomain.getValue();

		IOType type = s.getType();
		int index;
		// Add the Signal to the global object model
		GlobalSystem.getInstance().addSignal(s, cd);

		// Index the Signal so that it is rendered in a place that will not
		// overlap the other Signals
		if (type == IOType.INPUT) {
			index = cd.getNumInputSignals();
		} else {
			index = cd.getNumOutputSignals();
		}

		// After adding the Signal to the ClockDomain object, set it back as the
		// Clock Domain cell's value
		clockDomain.setValue(cd);

		// Draw the Signal onto the canvas
		DrawComponent.drawSignal(graph, s, clockDomain, index);

		// Reset the selection on ElementsPane after the object has been drawn
		resetCompSelection();
	}

	/**
	 * This method returns the currently selected Component on the canvas, If no
	 * Component is selected, then return null.
	 * 
	 * @return currently selected mxCell
	 */
	public mxCell getSelectedComponent() {
		mxCell cell;
		cell = (mxCell) this.graph.getSelectionModel().getCell();

		return cell;

	}

	/**
	 * This method adds the given Clock Domain to the given Sub System. It
	 * renders the Clock Domain at the given coordinates
	 * 
	 * @param cd
	 *            - Clock Domain to add to the model
	 * @param x
	 *            - x coordinate to draw the Clock Domain
	 * @param y
	 *            - y coordinate to draw the Clock Domain
	 * @param sub
	 *            - Sub System to add the Clock Domain to
	 */
	public void addClockDomain(ClockDomain cd, int x, int y, SubSystem sub) {
		// Add the Clock Domain to the object model
		GlobalSystem.getInstance().addClockDomain(cd, sub);

		// Draw it onto the canvas
		DrawComponent.drawClockDomain(this.graph,
				(mxCell) this.graph.getDefaultParent(), cd, x, y);
		resetCompSelection();
	}

	/**
	 * Whenever a button is selected in the ElementsPane, the selected button's
	 * action command is saved. This is how the canvas knows which component to
	 * draw on the canvas. It also clears the current selection so that a new
	 * selection can be made.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		selectedMode = event.getActionCommand();
		graph.clearSelection();
	}

	/**
	 * This method adds a new Channel to the object model and the canvas.
	 * 
	 * @param ch
	 *            - Channel object to add
	 * @param cdFrom
	 *            - Source Clock Domain cell of the Channel
	 * @param cdTo
	 *            - Destination Clock Domain cell of Channel
	 * @param fromName
	 *            - Name of the Channel on the source Clock Domain side
	 * @param toName
	 *            - Name of the Channel on the destination Clock Domain side
	 */
	public void addChannel(Channel ch, mxCell cdFrom, mxCell cdTo,
			String fromName, String toName) {
		// add the Channel in between the two Clock Domain objects
		ClockDomain cd1 = (ClockDomain) cdFrom.getValue();
		cdFrom.setValue(cd1);

		ClockDomain cd2 = (ClockDomain) cdTo.getValue();
		cdTo.setValue(cd2);

		// Add the Channel to the model, and sraw it onto the canvas
		GlobalSystem.getInstance().addChannel(ch, cd1, cd2, fromName, toName);
		DrawComponent.drawChannel(this.graph, ch, cdFrom, cdTo,
				cd1.getNumOutputChannels(), cd2.getNumInputChannels());

	}

	/**
	 * This method resets the selection in ElementsPane, and the saved drawing
	 * mode.
	 */
	public void resetCompSelection() {
		ElementsPane.deselectButtons();
		selectedMode = null;
	}

	/**
	 * This method opens up an existing Component on the canvas for editing. A
	 * popup window will be displayed featuring the existing values of those
	 * components in the fields. These fields can then be changed by the user.
	 * Upon selecting OK, a new Component will be created using those
	 * configurations and it will replace the old Component.
	 * 
	 * @param selectedComponent
	 *            - Component selected for editing.
	 */
	public void editComponent(mxCell selectedComponent) {

		// patch here
		System.out.println(selectedComponent.getId());
		System.out.println(selectedComponent.getClass());
		System.out.println(selectedComponent.getValue());
		Component comp = null;
		if (selectedComponent.getValue() instanceof Component) {
			comp = (Component) selectedComponent.getValue();
		}

		else {
			System.out.println("you have clicked somthing I can not find");
			return; // fix me
		}
  
		// Open popup passing in the Component that is selected. This will
		// populate the fields with its existing values.
		ComponentPopup popup = new ComponentPopup(
				(JFrame) SwingUtilities.getWindowAncestor(this), comp,
				(int) selectedComponent.getGeometry().getCenterX(),
				(int) selectedComponent.getGeometry().getCenterY());
		// Once popup is closed, retrieve the created Component object

		// ################## what if the popup selected cancell with blank fill
		// #####################

		Component newComp = popup.getComponent();

		if (newComp != null) {
			// If the object is a Channel, then need to modify the label cell on
			// the destination Clock Domain side.
			if (newComp instanceof Channel) {
				mxCell label = (mxCell) selectedComponent.getChildAt(0);
				label.setValue(popup.getChannelToName());
			}

			// Replace the old Component with the new Component in both the
			// object model and the graph object
			GlobalSystem system = GlobalSystem.getInstance();
			system.changeComponent(comp, newComp);

			ClockDomainGraph g = (ClockDomainGraph) this.graph;
			g.changeComponent(selectedComponent, newComp);
		}
	}
}
