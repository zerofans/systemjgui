package gui.tabs.popups;

import graphelements.jgraphx.DrawComponent;
import graphmodel.Channel;
import graphmodel.ClockDomain;
import graphmodel.Component;
import graphmodel.GlobalSystem;
import graphmodel.IOType;
import graphmodel.Link;
import graphmodel.LinkGroup;
import graphmodel.LinkType;
import graphmodel.Signal;
import graphmodel.SubSystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JSeparator;

/**
 * This class contains the implementation of the popup that appears when
 * a user attempts to create a new Component or edit an existing
 * Component on the canvas.  The fields that appear in the window for
 * the user to fill out will change depending on what Component they are
 * trying to create.  Once the information has been entered by the user,
 * a new Component object will be created with that information, which
 * can then be accessed by the canvases and drawn.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class ComponentPopup extends JDialog implements ActionListener {
  private static final String CREATE_TITLE = "Create new ";
  private static final String EDIT_TITLE = "Edit ";
  private String componentType;
  private Component componentCreated;

  private JPanel contentPanel;
	
  // For all constructs
  private JTextField nameTextField;
  private JTextField localTextField;
	
  // For Clock Domains and Signals
  private JTextField classTextField;
	
  // For Clock Domains
  private JComboBox subSystemBox;
	
  // For Signals only
  private JComboBox<IOType> ioTypes;
  private List<JTextField> keys;
  private List<JTextField> values;
  private int parameterRowOffset;
	
  // For Channels only
  private JComboBox<Object> clockDomainFrom;
  private JComboBox<Object> clockDomainTo;
  private JTextField channelFromName;
  private JTextField channelToName;
	
	
  // For Links only
  private JComboBox<LinkType> linkTypes;
  private List<JComboBox<Object>> subSystemComboBoxes;
  private List<JTextField> classTextBoxes;
  private List<JTextField> interfaceTextBoxes;
  private List<JTextField> argumentsTextBoxes;

  /**
   * Create the dialog.
   */
  private void constructPopup(String componentType, int x, int y) {
		
    this.setBounds(100, 100, 370, 325);
    this.setLocation(x, y);
    this.getContentPane().setLayout(new BorderLayout());

    nameTextField = new JTextField();
    localTextField = new JTextField();
    classTextField = new JTextField();
    parameterRowOffset = 4;
		
    this.componentType = componentType;
		
    // Create the appropriate panel corresponding to the type of object the user is trying to make
    if (componentType.equals(DrawComponent.CLOCK_DOMAIN_NAME)) {
      this.contentPanel = createClockDomainPanel();
    } else if (componentType.equals(DrawComponent.CHANNEL_NAME)) {
      this.contentPanel = createChannelPanel();
    } else if (componentType.equals(DrawComponent.SIGNAL_NAME)){
      this.contentPanel = createSignalPanel();
    } else if (componentType.equals(DrawComponent.SUBSYSTEM_NAME)){
      this.contentPanel = createSubSystemPanel();
    } else {
      this.contentPanel = createLinkPanel();
    }

    // Once the panel is created, set it in the dialog box
    this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.NORTH);
		
    // No matter which of the panels are created, there will always be "OK" and "Cancel" buttons at the bottom.
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    // OK Button
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    okButton.addActionListener(this);
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);

    // Cancel Button
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    cancelButton.addActionListener(this);
    buttonPane.add(cancelButton);

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }
	
	
  /**
   * @wbp.parser.constructor
   * This constructor is for creating a new Component
   * @param jFrame - parent window, which should be MainApplication
   * @param componentType - String representing the type of Component that is being generated
   * @param x - x coordinate to place popup
   * @param y - y coordinate to place popup
   * 
   */
  public ComponentPopup(JFrame jFrame, String componentType, int x, int y) {
    super(jFrame, true);
    this.constructPopup(componentType, x, y);
    this.setTitle(CREATE_TITLE + componentType);
    setVisible(true);
  }
	
  /**
   * This constructor is for editing an existing Component.
   * @param jFrame - the parent window, which should be MainApplication
   * @param comp - the Component object that will be edited
   * @param x - x coordinate to place popup
   * @param y - y coordinate to place popup
   */
  public ComponentPopup(JFrame jFrame, Component comp, int x, int y) {
    super(jFrame, true);
    this.constructPopup(comp.getTypeAsString(), x, y);
    this.setTitle(EDIT_TITLE + comp.getTypeAsString());
    populateFields(comp);
    setVisible(true);   
		
  }

  /**
   * This method is called when the "OK" and "Cancel" Buttons are pressed.
   * If it is "OK", then the populated fields will be used to create new Components.
   * If "Cancel", then the window is disposed with nothing happening.
   * 
   * TODO: There is no error checking in component creation. Should be error messages if crucial fields are empty, duplicate objects, etc.
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getActionCommand().equals("OK")) {
      // Depending on the type of Component created, extract values from the fields and create a new Component object
      // Clock Domain
      if (componentType.equals(DrawComponent.CLOCK_DOMAIN_NAME)) {
		String name = nameTextField.getText();
		String className = classTextField.getText();
		componentCreated = new ClockDomain(name, className);
				
	// Signal
      } else if (componentType.equals(DrawComponent.SIGNAL_NAME)) {
	// put parameters into a hashtable
		Hashtable<String, String> params = new Hashtable<String, String>();
		for (int i = 0; i < keys.size(); i++) {
		  // Both fields in a parameter must be filled in order to be added
		  if (keys.get(i) != null) {
		    if (values.get(i) != null) {
		      params.put(keys.get(i).getText(), values.get(i).getText());
		    }
		  }
		}
		componentCreated = new Signal(nameTextField.getText(), classTextField.getText(), (IOType)ioTypes.getSelectedItem(), params);
				
	// Channel
      } else if (componentType.equals(DrawComponent.CHANNEL_NAME)) {
	// TODO: Maybe add dropdowns to select the Clock Domains it goes to and from, instead of relying on the clicks.
	componentCreated = new Channel(channelFromName.getText());
	Channel ch = (Channel)componentCreated;
	ch.setToName(getChannelToName());
				
	// Sub System
      } else if (componentType.equals(DrawComponent.SUBSYSTEM_NAME)) {
	componentCreated = new SubSystem(nameTextField.getText(), classTextField.getText(), localTextField.getText());
				
	// Link
      } else if (componentType.equals(DrawComponent.LINK_NAME)) {
				
		LinkGroup link = new LinkGroup((LinkType)linkTypes.getSelectedItem());
					
		for (int j = 0; j < subSystemComboBoxes.size(); j++) {
		  SubSystem s = (SubSystem)subSystemComboBoxes.get(j).getSelectedItem();
		  Link i = new Link(s, classTextBoxes.get(j).getText(), interfaceTextBoxes.get(j).getText(), argumentsTextBoxes.get(j).getText());
		  link.addLink(i);
		}
					
		componentCreated = link;
      }
      // Once Component is created, close the window
      this.dispose();
			
      // This event fires in the Signal creation panel, when the "+" button is pressed to add additional parameters to the Signal
    } else if (event.getActionCommand().equals("Add Signal Parameters")) {
      addAnotherSignalParameter();
			
      // This event fires in the Link creation panel, when the "+" button is pressed to add an additional Link interface
    } else if (event.getActionCommand().equals("Add Interface")) {
      addNewInterface(contentPanel);
      // Adding an additional Link interface adds quite a few components, so the window must be resized to fit them
      this.setSize(this.getWidth(), this.getHeight() + 70);
			
      // refresh the panel
      contentPanel.revalidate();
			
    } else if (event.getActionCommand().equals("Cancel")) {
      this.dispose();
    }

  }
	
  /**
   * @return the Sub System selected in the drop down menu.
   */
  public SubSystem getSelectedSubSystem() {
    return (SubSystem)subSystemBox.getSelectedItem();
  }
	
  /**
   * This method adds an additional two text fields for the user to enter the property name and its value.
   * The property name is the left text field, and the value is the right text field.
   */
  private void addAnotherSignalParameter() {
		
    // Text field for key
    JTextField key1 = new JTextField();
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 0, 5);
    gbc_textField_1.gridx = 0;
    gbc_textField_1.gridy = parameterRowOffset; // keep track of how many rows there are, so text fields don't overlap
    contentPanel.add(key1, gbc_textField_1);
    key1.setColumns(10);
    // Add the new text field to list of key text fields created, to retrieve value from later
    keys.add(key1);
		
    // Text field for value
    JTextField value1 = new JTextField();
    GridBagConstraints gbc_textField_2 = new GridBagConstraints();
    gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_2.gridx = 1;
    gbc_textField_2.gridy = parameterRowOffset;
    contentPanel.add(value1, gbc_textField_2);
    value1.setColumns(10);
    // Add the new text field to list of value text fields created, to retrieve value from later
    values.add(value1);

    // Increase size of panel to account for new text fields
    this.setSize(this.getWidth(), this.getHeight() + 20);
    contentPanel.revalidate();
		
    // Increment row counter
    parameterRowOffset++;
  }
	
  /**
   * This method creates the Clock Domain panel.
   * 
   * It contains fields for:
   * - The name of the Clock Domain
   * - The implementation class name
   * - The Sub System this Clock Domain is part of
   * 
   * @return JPanel with fields
   */
  public JPanel createClockDomainPanel() {
    JPanel clockDomainPanel = new JPanel();
    GridBagLayout gbl_clockDomainPanel = new GridBagLayout();
    gbl_clockDomainPanel.columnWidths = new int[]{110, 221, 0};
    gbl_clockDomainPanel.rowHeights = new int[]{30, 33, 40, 0};
    gbl_clockDomainPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    gbl_clockDomainPanel.rowWeights = new double[]{0.0, 0.0, 0, Double.MIN_VALUE};
    clockDomainPanel.setLayout(gbl_clockDomainPanel);

    // "Name" label and text field
    JLabel nameLabel = new JLabel("Name: ");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_nameLabel = new GridBagConstraints();
    gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_nameLabel.gridx = 0;
    gbc_nameLabel.gridy = 0;
    clockDomainPanel.add(nameLabel, gbc_nameLabel);

    GridBagConstraints gbc_nameTextField = new GridBagConstraints();
    gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
    gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_nameTextField.gridx = 1;
    gbc_nameTextField.gridy = 0;
    clockDomainPanel.add(nameTextField, gbc_nameTextField);
    nameTextField.setColumns(10);

    // "Class" label and text field
    JLabel classLabel = new JLabel("Class: ");
    GridBagConstraints gbc_classLabel = new GridBagConstraints();
    gbc_classLabel.insets = new Insets(0, 0, 0, 5);
    gbc_classLabel.gridx = 0;
    gbc_classLabel.gridy = 1;
    clockDomainPanel.add(classLabel, gbc_classLabel);

    GridBagConstraints gbc_classTextField = new GridBagConstraints();
    gbc_classTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_classTextField.gridx = 1;
    gbc_classTextField.gridy = 1;
    clockDomainPanel.add(classTextField, gbc_classTextField);
    classTextField.setColumns(10);
		
    // "Sub System" label and combo box
    JLabel subSystemLabel = new JLabel("Sub System: ");
    GridBagConstraints gbc_subSystemLabel = new GridBagConstraints();
    gbc_subSystemLabel.insets = new Insets(0, 0, 0, 5);
    gbc_subSystemLabel.gridx = 0;
    gbc_subSystemLabel.gridy = 2;
    clockDomainPanel.add(subSystemLabel, gbc_subSystemLabel);
		
    subSystemBox = new JComboBox(GlobalSystem.getInstance().getSubSystems().toArray());
    GridBagConstraints gbc_subSystemBox = new GridBagConstraints();
    gbc_subSystemBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_subSystemBox.gridx = 1;
    gbc_subSystemBox.gridy = 2;
    clockDomainPanel.add(subSystemBox, gbc_subSystemBox);
		
    return clockDomainPanel;
  }
	
  /**
   * This method creates the Channel panel.
   * 
   * It contains fields for:
   * - The name of the Channel on the source Clock Domain
   * - The name of the Channel on the destination Clock Domain
   * 
   * TODO: Possibly include combo boxes to specify the Clock Domains? Partially implemented by commented out
   * 
   * @return JPanel with fields
   */
  public JPanel createChannelPanel() {
    setBounds(100, 100, 430, 280);
		
    JPanel channelPanel = new JPanel();
    GridBagLayout gbl_clockDomainPanel = new GridBagLayout();
    gbl_clockDomainPanel.columnWidths = new int[]{110, 221};
    gbl_clockDomainPanel.rowHeights = new int[]{30, 26, 22, 0, 0, 0};
    gbl_clockDomainPanel.columnWeights = new double[]{1.0, 1.0};
    gbl_clockDomainPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    channelPanel.setLayout(gbl_clockDomainPanel);
		
    GlobalSystem system = GlobalSystem.getInstance();
    clockDomainFrom = new JComboBox<Object>(system.getAllClockDomains().toArray()); // UNUSED NOW
		
    channelFromName = new JTextField();
    channelToName = new JTextField();
		
    // CODE FOR CLOCK DOMAIN COMBO BOXES. NEED TO COMPLETE/EVALUATE WHETHER IT IS A GOOD IDEA

    /*JLabel fromLabel = new JLabel("From Clock Domain: ");
      fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
      GridBagConstraints gbc_fromLabel = new GridBagConstraints();
      gbc_fromLabel.insets = new Insets(0, 0, 5, 5);
      gbc_fromLabel.gridx = 0;
      gbc_fromLabel.gridy = 0;
      channelPanel.add(fromLabel, gbc_fromLabel);
		

      GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
      gbc_fromComboBox.insets = new Insets(0, 0, 5, 0);
      gbc_fromComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_fromComboBox.gridx = 1;
      gbc_fromComboBox.gridy = 0;
      channelPanel.add(clockDomainFrom, gbc_fromComboBox);*/
    //nameTextField.setColumns(10);
		
    // Source Clock Domain name, label and text field
    JLabel fromNameLabel = new JLabel("Label in \"From\" Clock Domain: ");
    fromNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_fromNameLabel = new GridBagConstraints();
    gbc_fromNameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_fromNameLabel.gridx = 0;
    gbc_fromNameLabel.gridy = 1;
    channelPanel.add(fromNameLabel, gbc_fromNameLabel);
		
    GridBagConstraints gbc_fromNameTextField = new GridBagConstraints();
    gbc_fromNameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_fromNameTextField.insets = new Insets(0, 0, 5, 0);
    gbc_fromNameTextField.gridx = 1;
    gbc_fromNameTextField.gridy = 1;
    channelPanel.add(channelFromName, gbc_fromNameTextField);
    channelFromName.setColumns(10);
		
		
    /*JLabel toLabel = new JLabel("To Clock Domain: ");
      toLabel.setHorizontalAlignment(SwingConstants.CENTER);
      GridBagConstraints gbc_toLabel = new GridBagConstraints();
      gbc_toLabel.insets = new Insets(0, 0, 5, 5);
      gbc_toLabel.gridx = 0;
      gbc_toLabel.gridy = 3;
      channelPanel.add(toLabel, gbc_toLabel);
      clockDomainTo = new JComboBox<Object>(system.getAllClockDomains().toArray());
		
      GridBagConstraints gbc_toComboBox = new GridBagConstraints();
      gbc_toComboBox.insets = new Insets(0, 0, 5, 0);
      gbc_toComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_toComboBox.gridx = 1;
      gbc_toComboBox.gridy = 3;
      channelPanel.add(clockDomainTo, gbc_toComboBox);*/
		
    // Destination Clock Domain, label and text field
    JLabel toNameLabel = new JLabel("Label in \"To\" Clock Domain: ");
    toNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_fromToLabel = new GridBagConstraints();
    gbc_fromToLabel.insets = new Insets(0, 0, 0, 5);
    gbc_fromToLabel.gridx = 0;
    gbc_fromToLabel.gridy = 4;
    channelPanel.add(toNameLabel, gbc_fromToLabel);
    channelToName.setColumns(10);
		
    GridBagConstraints gbc_toNameTextField = new GridBagConstraints();
    gbc_toNameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_toNameTextField.gridx = 1;
    gbc_toNameTextField.gridy = 4;
    channelPanel.add(channelToName, gbc_toNameTextField);

    return channelPanel;	
  }
	
  /**
   * This method creates the Link panel.
   * 
   * It contains fields for:
   * - Type of Link Group (Local/Destination)
   * - group of interfaces for the Sub Systems
   * 
   * @return JPanel with fields
   */
  public JPanel createLinkPanel() {
    JPanel linkPanel = new JPanel();
		
    setBounds(100, 100, 550, 280);
		
    GridBagLayout gbl_linkPanel = new GridBagLayout();
    gbl_linkPanel.columnWidths = new int[]{50, 84, 77, 107, 56, 57};
    gbl_linkPanel.rowHeights = new int[]{18, 24, 0, 0, 0, 0, 0, 0};
    gbl_linkPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    gbl_linkPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    linkPanel.setLayout(gbl_linkPanel);

    // Type label and combo box
    JLabel typeLabel = new JLabel("Type: ");
    typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_nameLabel = new GridBagConstraints();
    gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_nameLabel.gridx = 0;
    gbc_nameLabel.gridy = 0;
    linkPanel.add(typeLabel, gbc_nameLabel);

    linkTypes = new JComboBox<LinkType>(LinkType.values());
    GridBagConstraints gbc_linkTypeComboBox = new GridBagConstraints();
    gbc_linkTypeComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_linkTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_linkTypeComboBox.gridx = 1;
    gbc_linkTypeComboBox.gridy = 0;
    linkPanel.add(linkTypes, gbc_linkTypeComboBox);
		
    // Interface label
    JLabel interfaceLabel = new JLabel("Interfaces: ");
    GridBagConstraints gbc_interfaceLabel = new GridBagConstraints();
    gbc_interfaceLabel.insets = new Insets(0, 0, 5, 5);
    gbc_interfaceLabel.gridx = 0;
    gbc_interfaceLabel.gridy = 1;
    linkPanel.add(interfaceLabel, gbc_interfaceLabel);
		
    // Button to add additional Sub System interfaces
    JButton button = new JButton("+");
    button.setActionCommand("Add Interface");
    button.addActionListener(this);
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.insets = new Insets(0, 0, 5, 5);
    gbc_button.anchor = GridBagConstraints.WEST;
    gbc_button.gridx = 1;
    gbc_button.gridy = 1;
    linkPanel.add(button, gbc_button);
		
    // Separator between interface groups
    JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
    separator.setPreferredSize(new Dimension(linkPanel.getWidth(), 1));
    GridBagConstraints gbc_separator = new GridBagConstraints();
    gbc_separator.weighty = 1;
    gbc_separator.gridwidth = 6;
    gbc_separator.fill = GridBagConstraints.HORIZONTAL;
    gbc_separator.insets = new Insets(0, 0, 5, 0);
    gbc_separator.gridx = 0;
    gbc_separator.gridy = 2;
    linkPanel.add(separator, gbc_separator);
		
    //		GridBagConstraints gbc_comboBox = new GridBagConstraints();
    //		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
    //		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    //		gbc_comboBox.gridx = 1;
    //		gbc_comboBox.gridy = 2;
    //		linkPanel.add(ioTypes, gbc_comboBox);
		
    // Array of different fields to track all values of all interfaces
    argumentsTextBoxes = new ArrayList<JTextField>();
    subSystemComboBoxes = new ArrayList<JComboBox<Object>>();
    classTextBoxes = new ArrayList<JTextField>();
    interfaceTextBoxes = new ArrayList<JTextField>();
		
    // Start with 2 interfaces by default
    linkPanel = addNewInterface(linkPanel);
    linkPanel = addNewInterface(linkPanel);
		
    return linkPanel;
		
  }
	
  /**
   * This method adds an additional interface parameter to the Link panel.
   * 
   * @param linkPanel
   * @return
   */
  public JPanel addNewInterface(JPanel linkPanel) {

    GlobalSystem system = GlobalSystem.getInstance();
		
    // "Sub System" label and combo box
    JLabel subSystemLabel = new JLabel("Sub System");
    GridBagConstraints gbc_linkType = new GridBagConstraints();
    gbc_linkType.insets = new Insets(0, 0, 5, 5);
    gbc_linkType.gridx = 0;
    gbc_linkType.gridy = parameterRowOffset;
    linkPanel.add(subSystemLabel, gbc_linkType);
    JComboBox<Object> sub1 = new JComboBox<Object>(system.getSubSystems().toArray());
		
    GridBagConstraints gbc_subSystemComboBox = new GridBagConstraints();
    gbc_subSystemComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_subSystemComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_subSystemComboBox.gridx = 1;
    gbc_subSystemComboBox.gridy = parameterRowOffset;
    linkPanel.add(sub1, gbc_subSystemComboBox);
    subSystemComboBoxes.add(sub1);

    // "Class" label and text field
    JLabel lblClass_1 = new JLabel("Class: ");
    GridBagConstraints gbc_lblClass_1 = new GridBagConstraints();
    gbc_lblClass_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblClass_1.gridx = 2;
    gbc_lblClass_1.gridy = parameterRowOffset;
    linkPanel.add(lblClass_1, gbc_lblClass_1);
		
    JTextField classField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 5);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 3;
    gbc_textField.gridy = parameterRowOffset;
    linkPanel.add(classField, gbc_textField);
    classTextBoxes.add(classField);
    classField.setColumns(10);
		
    // "Interface" label and text field
    // TODO: Change text field to a combo box with enumerated values: USB, TCP/IP, etc
    JLabel lblInterface = new JLabel("Interface");
    GridBagConstraints gbc_lblInterface = new GridBagConstraints();
    gbc_lblInterface.insets = new Insets(0, 0, 5, 5);
    gbc_lblInterface.gridx = 4;
    gbc_lblInterface.gridy = parameterRowOffset;
    linkPanel.add(lblInterface, gbc_lblInterface);
		
    JTextField interfaceBox = new JTextField();
    GridBagConstraints gbc_textField_3 = new GridBagConstraints();
    gbc_textField_3.insets = new Insets(0, 0, 5, 0);
    gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_3.gridx = 5;
    gbc_textField_3.gridy = parameterRowOffset;
    linkPanel.add(interfaceBox, gbc_textField_3);
    interfaceBox.setColumns(10);
    interfaceTextBoxes.add(interfaceBox);
		
    // "Arguments" label and text field
    JLabel lblArguments = new JLabel("Arguments");
    GridBagConstraints gbc_lblArguments = new GridBagConstraints();
    gbc_lblArguments.insets = new Insets(0, 0, 5, 5);
    gbc_lblArguments.gridx = 2;
    gbc_lblArguments.gridy = parameterRowOffset + 1;
    linkPanel.add(lblArguments, gbc_lblArguments);

		
    JTextField argumentsTextField = new JTextField();
    GridBagConstraints gbc_keyTextField = new GridBagConstraints();
    gbc_keyTextField.gridwidth = 3;
    gbc_keyTextField.insets = new Insets(0, 0, 5, 0);
    gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_keyTextField.gridx = 3;
    gbc_keyTextField.gridy = parameterRowOffset + 1;
    linkPanel.add(argumentsTextField, gbc_keyTextField);
    argumentsTextField.setColumns(10);
    argumentsTextBoxes.add(argumentsTextField);		
		
    // Separator between Interfaces
    JSeparator separator_1 = new JSeparator(JSeparator.HORIZONTAL);
    GridBagConstraints gbc_separator_1 = new GridBagConstraints();
    gbc_separator_1.insets = new Insets(0, 0, 5, 0);
    gbc_separator_1.weighty = 1.0;
    gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_separator_1.gridwidth = 6;
    gbc_separator_1.gridx = 0;
    gbc_separator_1.gridy = parameterRowOffset + 2;
    linkPanel.add(separator_1, gbc_separator_1);
		
    // Every Interface adds 3 more rows to the panel
    parameterRowOffset += 3;
		
    return linkPanel;
  }
	
  /**
   * This method creates the Sub System panel.
   * 
   * It contains fields for:
   * - Name of the Sub System
   * - Name of the class implementing the Scheduler
   * 
   * @return JPanel with fields
   */
  public JPanel createSubSystemPanel() {
    JPanel subSystemPanel = new JPanel();
		
    GridBagLayout gbl_subSystemPanel = new GridBagLayout();
    gbl_subSystemPanel.columnWidths = new int[]{110, 221, 0};
    gbl_subSystemPanel.rowHeights = new int[]{30, 33, 0};
    gbl_subSystemPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    gbl_subSystemPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    subSystemPanel.setLayout(gbl_subSystemPanel);

    // "Name" label and text field
    JLabel nameLabel = new JLabel("Name: ");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_nameLabel = new GridBagConstraints();
    gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_nameLabel.gridx = 0;
    gbc_nameLabel.gridy = 0;
    subSystemPanel.add(nameLabel, gbc_nameLabel);

    GridBagConstraints gbc_nameTextField = new GridBagConstraints();
    gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
    gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_nameTextField.gridx = 1;
    gbc_nameTextField.gridy = 0;
    subSystemPanel.add(nameTextField, gbc_nameTextField);
    nameTextField.setColumns(10);

    JLabel localLabel = new JLabel("Local: ");
    localLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_localLabel = new GridBagConstraints();
    gbc_localLabel.insets = new Insets(0, 0, 5, 5);
    gbc_localLabel.gridx = 0;
    gbc_localLabel.gridy = 2;
    subSystemPanel.add(localLabel, gbc_localLabel);

    GridBagConstraints gbc_localTextField = new GridBagConstraints();
    gbc_localTextField.insets = new Insets(0, 0, 5, 0);
    gbc_localTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_localTextField.gridx = 1;
    gbc_localTextField.gridy = 2;
    subSystemPanel.add(localTextField, gbc_localTextField);
    localTextField.setColumns(10);

    // "Scheduler" label and text field
    JLabel schedulerLabel = new JLabel("Scheduler: ");
    GridBagConstraints gbc_schedulerLabel = new GridBagConstraints();
    gbc_schedulerLabel.insets = new Insets(0, 0, 0, 5);
    gbc_schedulerLabel.gridx = 0;
    gbc_schedulerLabel.gridy = 1;
    subSystemPanel.add(schedulerLabel, gbc_schedulerLabel);

    GridBagConstraints gbc_schedulerTextField = new GridBagConstraints();
    gbc_schedulerTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_schedulerTextField.gridx = 1;
    gbc_schedulerTextField.gridy = 1;
    subSystemPanel.add(classTextField, gbc_schedulerTextField);
    classTextField.setColumns(10);
		
    return subSystemPanel;
  }
	
  /**
   * This method creates the Signal panel.
   * 
   * It contains fields for:
   * - name of the Signal
   * - name of implementation class
   * - type of Signal (Input/Output)
   * - parameters for the Signal in pairs of text fields (additional pairs can be added)
   * 
   * @return JPanel with fields
   */
  public JPanel createSignalPanel() {
    JPanel signalPanel = new JPanel();
    GridBagLayout gbl_clockDomainPanel = new GridBagLayout();
    gbl_clockDomainPanel.columnWidths = new int[]{110, 221, 0};
    gbl_clockDomainPanel.rowHeights = new int[]{30, 33, 0, 0};
    gbl_clockDomainPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
    gbl_clockDomainPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
    signalPanel.setLayout(gbl_clockDomainPanel);

    // "Name" label and text field
    JLabel nameLabel = new JLabel("Name: ");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_nameLabel = new GridBagConstraints();
    gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_nameLabel.gridx = 0;
    gbc_nameLabel.gridy = 0;
    signalPanel.add(nameLabel, gbc_nameLabel);

    GridBagConstraints gbc_nameTextField = new GridBagConstraints();
    gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
    gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_nameTextField.gridx = 1;
    gbc_nameTextField.gridy = 0;
    signalPanel.add(nameTextField, gbc_nameTextField);
    nameTextField.setColumns(10);
		
    // "Class" label and text field
    JLabel lblClass = new JLabel("Class: ");
    GridBagConstraints gbc_lblClass = new GridBagConstraints();
    gbc_lblClass.insets = new Insets(0, 0, 5, 5);
    gbc_lblClass.gridx = 0;
    gbc_lblClass.gridy = 1;
    signalPanel.add(lblClass, gbc_lblClass);
		
    classTextField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 1;
    signalPanel.add(classTextField, gbc_textField);
    classTextField.setColumns(10);
		
    // "IO Type" label and combo box
    JLabel lblIoType = new JLabel("IO Type");
    GridBagConstraints gbc_lblIoType = new GridBagConstraints();
    gbc_lblIoType.insets = new Insets(0, 0, 5, 5);
    gbc_lblIoType.gridx = 0;
    gbc_lblIoType.gridy = 2;
    signalPanel.add(lblIoType, gbc_lblIoType);
		
		
    ioTypes = new JComboBox<IOType>(IOType.values());
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.insets = new Insets(0, 0, 5, 0);
    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_comboBox.gridx = 1;
    gbc_comboBox.gridy = 2;
    signalPanel.add(ioTypes, gbc_comboBox);
		
    // "Parameters" label
    JLabel lblParameters = new JLabel("Parameters");
    GridBagConstraints gbc_lblParameters = new GridBagConstraints();
    gbc_lblParameters.insets = new Insets(0, 0, 5, 5);
    gbc_lblParameters.gridx = 0;
    gbc_lblParameters.gridy = 3;
    signalPanel.add(lblParameters, gbc_lblParameters);
		
    // "+" button for adding additional parameters
    JButton addParametersButton = new JButton("+");
    addParametersButton.setActionCommand("Add Signal Parameters");
    addParametersButton.addActionListener(this);
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.insets = new Insets(0, 0, 5, 0);
    gbc_button.anchor = GridBagConstraints.WEST;
    gbc_button.gridx = 1;
    gbc_button.gridy = 3;
    signalPanel.add(addParametersButton, gbc_button);
		
    // Arrays to keep track of all the values of the parameters
    keys = new ArrayList<JTextField>();
    values = new ArrayList<JTextField>();
		
    // Add one pair of key and value fields by default
    JTextField textField_1 = new JTextField();
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 0, 5);
    gbc_textField_1.gridx = 0;
    gbc_textField_1.gridy = parameterRowOffset;
    signalPanel.add(textField_1, gbc_textField_1);
    textField_1.setColumns(10);
    keys.add(textField_1);
		
    JTextField textField_2 = new JTextField();
    GridBagConstraints gbc_textField_2 = new GridBagConstraints();
    gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_2.gridx = 1;
    gbc_textField_2.gridy = parameterRowOffset;
    signalPanel.add(textField_2, gbc_textField_2);
    textField_2.setColumns(10);
    values.add(textField_2);
		
    parameterRowOffset++;
		
    return signalPanel;
  }

  /**
   * @return  Component that was created by the popup
   */
  public Component getComponent() {
    return componentCreated;
  }
	
	
  public String getChannelFromName() {
    return channelFromName.getText();
  }
	
  public String getChannelToName() {
    return channelToName.getText();
  }
	
  /**
   * Given a Component, populate the relevant fields with its configuration
   * @param comp
   */
  public void populateFields(Component comp) {
    // Most components have names, so set the name field accordingly
    nameTextField.setText(comp.toString());
		
    // Fill the appropriate fields depending on the Component type
    if (comp.getTypeAsString().equals(DrawComponent.CLOCK_DOMAIN_NAME)) {
      ClockDomain cd = (ClockDomain)comp;
      classTextField.setText(cd.getClassName());
			
    } else if (comp.getTypeAsString().equals(DrawComponent.SIGNAL_NAME)) {
      Signal s = (Signal)comp;
      classTextField.setText(s.getClassName());
      ioTypes.setSelectedItem(s.getType());
			
      for (String key : s.getParameters().keySet()) {
	// Class and Name get filled in special fields. The rest get filled in the parameter fields
	if (!key.equals("Class") && !key.equals("Name")) {
	  keys.get(keys.size() - 1).setText(key);
	  values.get(keys.size() - 1).setText(s.getParameters().get(key));
	  addAnotherSignalParameter();
	}
      }
			
    } else if (comp.getTypeAsString().equals(DrawComponent.SUBSYSTEM_NAME)) {
      SubSystem s = (SubSystem)comp;
      classTextField.setText(s.getSchedulerClass());
      localTextField.setText(s.getLocal());
			
    } else if (comp.getTypeAsString().equals(DrawComponent.CHANNEL_NAME)) {
      Channel ch = (Channel)comp;
			
      // TODO: FOR POSSIBLE COMBO BOX INCLUSION
      ClockDomain fromClockDomain = ch.getFromClockDomain();
      ClockDomain toClockDomain = ch.getToClockDomain();
			
      channelFromName.setText(ch.getName());
      channelToName.setText(ch.getToName());

    } else if (comp.getTypeAsString().equals(DrawComponent.LINK_NAME)) {
      LinkGroup l =  (LinkGroup)comp;
			
      linkTypes.setSelectedItem(l.getLinkType());
      List<Link> interfaces = l.getLinks();
			
      // Add additional interface parameters if there is more than 2 in this Link
      if (interfaces.size() > 2) {
	for (int i = 2; i < interfaces.size(); i++) {
	  addNewInterface(contentPanel);
	}
      }
			
      for (int i = 0; i < interfaces.size(); i++) {
	Link in = interfaces.get(i);
	subSystemComboBoxes.get(i).setSelectedItem(in.getSubSystem());
	classTextBoxes.get(i).setText(in.getClassName());
	interfaceTextBoxes.get(i).setText(in.getLinkInterface());
	argumentsTextBoxes.get(i).setText(in.getArguments());
      }
			
			
    }
  }

}
