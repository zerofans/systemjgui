package gui.mainwindow;

import gui.tabs.ClockDomainViewCanvas;
import gui.tabs.SubSystemViewCanvas;
import gui.tabs.XMLView;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.mxgraph.util.mxEvent;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that contains the main method that will run the application.
 * It contains all the layout of the different components in the window. Placement of components can either be modified in the code, or can switch to the Design view, which
 * uses the WYSIWYG editor in Eclipse. NOTE: This plug in must be installed before it can be utilised.
 * 
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class MainApplication extends JFrame {

  private JPanel contentPane;
  private PropertiesPanel propertiesPanel;

  private TopMenuBar menuBar;
  private ClockDomainViewCanvas clockDomainCanvas;
  private SubSystemViewCanvas subSystemCanvas;
  private XMLView xmlView;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
	public void run() {
	  try {
	    MainApplication frame = new MainApplication();
	    frame.setVisible(true);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
      });
  }

  /**
   * Create the frame, and arrange all the components.
   */
  //@SuppressWarnings("deprecation")
public MainApplication() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 900, 600);

    // Add the menu bar at the top. All options and fields can be specified in the TopMenuBar class
    menuBar = new TopMenuBar();
    setJMenuBar(menuBar);
    
    // Set the main content pane of the window.Gridbag Layout used, but for ease of placing components, use the Design view
    contentPane = new JPanel();
    setContentPane(contentPane);
    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.rowHeights = new int[]{25, 20, 28, 30, 163, 31, 0};
    //gbl_contentPane.columnWidths = new int[]{200,200,200};
    gbl_contentPane.columnWeights = new double[]{0.0, 3.0,0.0};
    gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0};
    contentPane.setLayout(gbl_contentPane);
//gbl_contentPane.
    // Create the canvases for the Clock Domain and Sub System views.
    clockDomainCanvas = new ClockDomainViewCanvas();
    subSystemCanvas = new SubSystemViewCanvas();

    // Create list of panels to be used in the tabs, so they can be passed into the TabPanel object

    // Create and place the labels
    JLabel elementsLabel = new JLabel("Elements");
    GridBagConstraints gbc_elementsLabel = new GridBagConstraints();
    gbc_elementsLabel.insets = new Insets(0, 0, 5, 5);
    gbc_elementsLabel.gridx = 0;
    gbc_elementsLabel.gridy = 0;
    contentPane.add(elementsLabel, gbc_elementsLabel);

    JLabel propertiesLabel = new JLabel("Properties");
    propertiesLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_propertiesLabel = new GridBagConstraints();
    gbc_propertiesLabel.fill = GridBagConstraints.BOTH;
    gbc_propertiesLabel.insets = new Insets(0, 5, 5, 5);
    gbc_propertiesLabel.gridx = 2;
    gbc_propertiesLabel.gridy = 0;
    contentPane.add(propertiesLabel, gbc_propertiesLabel);

    // Add canvases to to ElementsPane as listeners to button selection
    // Create ElementsPane and add
    ActionListener[] canvases = {clockDomainCanvas, subSystemCanvas};
    ElementsPane elements = new ElementsPane(canvases);
    // Place ElementsPane on the window
    GridBagConstraints gbc_elements = new GridBagConstraints();
    gbc_elements.gridheight = 2;
    gbc_elements.insets = new Insets(0, 0, 2, 1);
    gbc_elements.gridx = 0;
    gbc_elements.gridy = 1;
    contentPane.add(elements, gbc_elements);

    // Create PropertiesPanel and add it as a listener to the canvases for selection
    propertiesPanel = new PropertiesPanel();
    clockDomainCanvas.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, propertiesPanel);
    subSystemCanvas.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, propertiesPanel);

    // Place Properties table on the window
    GridBagConstraints gbc_propertiesPanel = new GridBagConstraints();
    //gbc_propertiesPanel.
    gbc_propertiesPanel.weightx = 0.7;
    gbc_propertiesPanel.gridheight = 4;
    gbc_propertiesPanel.gridwidth =1;
    gbc_propertiesPanel.fill = GridBagConstraints.BOTH;
    gbc_propertiesPanel.insets = new Insets(0, 0, 5, 5);
    gbc_propertiesPanel.gridx = 2;
    gbc_propertiesPanel.gridy = 1;
    //gbc_propertiesPanel.
    contentPane.add(propertiesPanel, gbc_propertiesPanel);


    // XML view
    xmlView = new XMLView();

    // Tab names for the TabPanel
    List<String> tabNames = new ArrayList<String>();
    tabNames.add("Clock Domain View");
    tabNames.add("Sub System View");
    tabNames.add("XML View");

    // Initialise the TabPanel with all the intended tabs and their names
    List<JComponent> tabs = new ArrayList<JComponent>();
    tabs.add(clockDomainCanvas);
    tabs.add(subSystemCanvas);
    tabs.add(xmlView);
    TabPanel tabPanel = new TabPanel(tabs, tabNames);
    tabPanel.addListener(elements);

    // Place tab Panel on the window
    GridBagConstraints gbc_tabPanel = new GridBagConstraints();
    gbc_tabPanel.insets = new Insets(0, 0, 0, 5);
    gbc_tabPanel.gridheight = 6;
    gbc_tabPanel.fill = GridBagConstraints.BOTH;
    gbc_tabPanel.gridx = 1;
    gbc_tabPanel.gridy = 1;
    contentPane.add(tabPanel, gbc_tabPanel);

    // Create and place label from structure tree
    JLabel structureLabel = new JLabel("System Structure");
    GridBagConstraints gbc_structureLabel = new GridBagConstraints();
    gbc_structureLabel.insets = new Insets(0, 0, 5, 5);
    gbc_structureLabel.gridx = 0;
    gbc_structureLabel.gridy = 3;
    contentPane.add(structureLabel, gbc_structureLabel);

    // Initialise and place structure tree panel
    StructurePanel structurePanel = new StructurePanel();
    GridBagConstraints gbc_structurePanel = new GridBagConstraints();
    gbc_structurePanel.insets = new Insets(0, 0, 5, 5);
    gbc_structurePanel.fill = GridBagConstraints.BOTH;
    gbc_structurePanel.gridx = 0;
    gbc_structurePanel.gridy = 4;
    contentPane.add(structurePanel, gbc_structurePanel);

     //Create and place label for package panel
    JLabel existingClockDomainsLabel = new JLabel("SystemJ Existing Clock Domains");
    GridBagConstraints gbc_existingClockDomains = new GridBagConstraints();
    gbc_existingClockDomains.insets = new Insets(0, 0, 5, 5);
    gbc_existingClockDomains.gridx = 0;
    gbc_existingClockDomains.gridy = 5;
    contentPane.add(existingClockDomainsLabel, gbc_existingClockDomains);

    // Initialise and place package panel
    PackagesPanel packagesPanel = new PackagesPanel();
    GridBagConstraints gbc_packagesPanel = new GridBagConstraints();
    gbc_packagesPanel.insets = new Insets(0, 0, 5, 5);
    gbc_packagesPanel.fill = GridBagConstraints.BOTH;
    gbc_packagesPanel.gridx = 0;
    gbc_packagesPanel.gridy = 6;
    contentPane.add(packagesPanel, gbc_packagesPanel);
    
    //resize(900, 601);
    
   // this.pack();
   // this.repaint(); 
    
  }

}
