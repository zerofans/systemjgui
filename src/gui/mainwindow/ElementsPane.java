package gui.mainwindow;

import graphelements.jgraphx.DrawComponent;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class represents the JPanel where the buttons used for selecting a component to draw will be housed.
 * The buttons are single selectable, so only one can be active at any one time, and the buttons available will change depending on which tab is active.
 * 
 * If more tabs are added that require corresponding buttons, then add an additional panel to the main panel, add the tab as a listener and modify the stateChanged() method to add it in.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class ElementsPane extends JPanel implements ChangeListener {
	
	private JPanel buttonPanel;
	private static ButtonGroup buttonGroup;
	
	
	public ElementsPane(ActionListener[] canvas) {
		super();
		
		buttonPanel = new JPanel();
		// Card layout allows for multiple different panels to be switched into view, which is ideal for changing the viewed buttons with the tabs
		buttonPanel.setLayout(new CardLayout());
		this.add(buttonPanel);
		// ButtonGroups allow for one button being selected at a time, similar to Radio Button functionality
		buttonGroup = new ButtonGroup();
		
		// Passed in Canvas objects so that can register the canvases as Action Listeners to the button selection
		buttonPanel.add(createCDViewPanel(canvas[0]), "Clock Domain Elements");
		buttonPanel.add(createSSViewPanel(canvas[1]), "Sub System Elements");
		
	}
	
	/**
	 * This method creates a panel for displaying the buttons relating to the Clock Domain View.
	 * The buttons available in this view are:
	 * - Clock Domain
	 * - Signal
	 * - Channel
	 * 
	 * @param canvas - object representing the Clock Domain canvas
	 * @return JPanel to add to Card Layout
	 */
	private JPanel createCDViewPanel(ActionListener canvas) {
		JPanel clockDomainElementsPanel = new JPanel();
		
		clockDomainElementsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		// Add buttons. JToggleButton allows for a selected state, so button remains pressed when clicked once
		// Add the Clock Domain button
		JToggleButton clockDomainButtonCD = new JToggleButton(DrawComponent.CLOCK_DOMAIN_NAME);
		clockDomainButtonCD.setHorizontalAlignment(SwingConstants.LEFT);
		clockDomainElementsPanel.add(clockDomainButtonCD);
		clockDomainButtonCD.addActionListener(canvas);
		buttonGroup.add(clockDomainButtonCD);
		
		// Add the Channel button
		JToggleButton channelButtonCD = new JToggleButton(DrawComponent.CHANNEL_NAME);
		clockDomainElementsPanel.add(channelButtonCD);
		channelButtonCD.addActionListener(canvas);
		buttonGroup.add(channelButtonCD);
		
		// Add the Signal button
		JToggleButton signalButtonCD = new JToggleButton(DrawComponent.SIGNAL_NAME);
		clockDomainElementsPanel.add(signalButtonCD);
		signalButtonCD.addActionListener(canvas);
		buttonGroup.add(signalButtonCD);
		
		// Add the View button
		JToggleButton Viewbutton = new JToggleButton(DrawComponent.VIEW_MODE_NAME);
		clockDomainElementsPanel.add(Viewbutton);
		Viewbutton.addActionListener(canvas);
		buttonGroup.add(Viewbutton);
		
		return clockDomainElementsPanel;
	}
	
	/**
	 * This method creates a panel for displaying the buttons relating to the Sub System View.
	 * The buttons available in this view are:
	 * - Sub System
	 * - Link
	 * 
	 * @param canvas - object representing the Sub System canvas
	 * @return JPanel to add to Card Layout
	 */
	private JPanel createSSViewPanel(ActionListener canvas) {
		JPanel subSystemElementsPanel = new JPanel();
		
		subSystemElementsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		// Add the Sub System button
		JToggleButton subSystemButtonCD = new JToggleButton(DrawComponent.SUBSYSTEM_NAME);
		subSystemButtonCD.setHorizontalAlignment(SwingConstants.LEFT);
		subSystemElementsPanel.add(subSystemButtonCD);
		subSystemButtonCD.addActionListener(canvas);
		buttonGroup.add(subSystemButtonCD);
		
		// Add the Link button
		JToggleButton linkButtonCD = new JToggleButton(DrawComponent.LINK_NAME);
		subSystemElementsPanel.add(linkButtonCD);
		linkButtonCD.addActionListener(canvas);
		buttonGroup.add(linkButtonCD);
		
		JToggleButton Viewbutton = new JToggleButton(DrawComponent.VIEW_MODE_NAME);
		subSystemElementsPanel.add(Viewbutton);
		Viewbutton.addActionListener(canvas);
		buttonGroup.add(Viewbutton);
		
		
		return subSystemElementsPanel;
	}

	/**
	 * This method is fired whenever the active tab in the main tabbed view is changed.
	 * The panel with the corresponding buttons will then be switched in, according to which of the drawing tabs ia active.
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		buttonGroup.clearSelection();
		JTabbedPane pane = (JTabbedPane) event.getSource();
		CardLayout cardLayout = (CardLayout) buttonPanel.getLayout();
		
		// if the Clock Domain view is selected, show the Clock Domain view buttons
		if (pane.getTitleAt(pane.getSelectedIndex()).contains(DrawComponent.CLOCK_DOMAIN_NAME)) {
			cardLayout.show(buttonPanel, "Clock Domain Elements");
			
		// if the Sub System view is selected, show the Sub System view buttons
		}  else if (pane.getTitleAt(pane.getSelectedIndex()).contains(DrawComponent.SUBSYSTEM_NAME)) {
			cardLayout.show(buttonPanel, "Sub System Elements");
		}
		
	}
	
	/**
	 * Clears the current selection in the buttons.
	 */
	public static void deselectButtons() {
		buttonGroup.clearSelection();
	}


}
