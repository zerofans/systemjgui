package gui.mainwindow;

import graphmodel.ClockDomain;
import graphmodel.Component;
import graphmodel.IOType;
import graphmodel.Link;
import graphmodel.LinkGroup;
import graphmodel.Signal;
import graphmodel.SubSystem;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraphSelectionModel;

/**
 * This class encapsulates the JPanel that contains the table to view properties of an existing Component.
 * It listens to the canvas objects and upon selection of a Component, it displays the object value of the cell selected.
 * This object value will be one of the graphmodel objects.
 * 
 * NOTE: If a different diagram drawing library is used, then the class mxIEventListener must be changed to reflect the selection listener of the new library.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class PropertiesPanel extends JPanel implements mxIEventListener {

	private JTable propertiesTable;
	private DefaultTableModel tableModel;

	public PropertiesPanel() {
		super();
		setLayout(new BorderLayout(0,0));

		tableModel = new DefaultTableModel();
		// There are two columns in this table, one representing the property type, and the other with its value
		
		// TODO: Currently, this table is uneditable, but can make it so that any change in the table's values can change the object's value
		// Would need to save the selected object here and add event handlers in that case
		tableModel.addColumn("Property");
		tableModel.addColumn("Value");

		propertiesTable = new JTable(tableModel);
		JScrollPane table = new JScrollPane(propertiesTable);
		add(table);
	}

	/**
	 * This method fires whenever there is an event on the canvas object.
	 * It only acts on graph selection events, where it retrieves the selected object and displays its object configuration in the table.
	 * 
	 * NOTE: This would have to be changed if a different diagram library is used.
	 */
	@Override
	public void invoke(Object sender, mxEventObject event) {
		// clear the current table
		tableModel.setRowCount(0);

		// Only care about graph selection events
		if (sender instanceof mxGraphSelectionModel) {
			Map<String, Object> map = event.getProperties();

			// retrieve element that has been selected
			ArrayList list = (ArrayList)map.get("removed");
			
			// If there has been a selection, then retrieve the selected cell and its object
			if (list != null) {
				mxCell selectedCell = (mxCell)list.get(0);
				
				if (selectedCell.getValue() instanceof Component) {
					Component selectedComponent = (Component) selectedCell.getValue();
					// get name of the selected component
					String[] nameData = {"Name", selectedComponent.toString()};

					// Add name to the table first
					tableModel.addRow(nameData);

					// Each component will have different attributes to display depending on its object type.
					if (selectedComponent instanceof ClockDomain) {
						ClockDomain cd = (ClockDomain)selectedComponent;
						
						// Clock Domains only have a Class type as additional information
						// TODO: Possibly also include displaying which Sub System it is in too.
						
						// Add value as "Property", "Value"
						tableModel.addRow(new Vector(Arrays.asList("Class", cd.getClassName())));
					} else if (selectedComponent instanceof Signal) {
						Signal s = (Signal)selectedComponent;
						Hashtable<String, String> params = s.getParameters();
						
						// Signals can have a variable number of parameters, so loop through all its parameters and add it to the table
						for (String key : params.keySet()) {
							if (key.equals("Type")) {
								IOType type;
								if (params.get(key).equals("i")) {
									type = IOType.INPUT;
								} else {
									type = IOType.OUTPUT;
								}
								tableModel.addRow(new Vector(Arrays.asList(key, type)));
							} else {
								tableModel.addRow(new Vector(Arrays.asList(key, params.get(key))));
							}
						}	
						
					// If an individual Link is selected, the Link interfaces at both ends of the selection are displayed in the table.
					} else if (selectedComponent instanceof LinkGroup) {
						
						System.out.println( "a link group has been selected");
						LinkGroup l = (LinkGroup)selectedComponent;
						// remove the Name row as Links do not have names.
						tableModel.removeRow(0);
						tableModel.addRow(new Vector(Arrays.asList("Type", l.getLinkType())));
						
						// Get the port cells at either end of the connection to determine link interfaces, as the objects are stored in the ports.
						mxCell fromPort = (mxCell) selectedCell.getSource();
						mxCell toPort = (mxCell) selectedCell.getTarget();

						Link link1 = (Link) fromPort.getValue();
						Link link2 = (Link) toPort.getValue();

						// Add source interface
						tableModel.addRow(new Vector(Arrays.asList("From Sub System", link1.getSubSystem() )));
						tableModel.addRow(new Vector(Arrays.asList("Class", link1.getClass() )));
						tableModel.addRow(new Vector(Arrays.asList("Interface", link1.getLinkInterface())));
						tableModel.addRow(new Vector(Arrays.asList("Arguments", link1.getArguments() )));
						
						// add empty row to separate
						tableModel.addRow(new Vector(Arrays.asList("", "")));

						// Add destination interface
						tableModel.addRow(new Vector(Arrays.asList("From Sub System", link2.getSubSystem() )));
						tableModel.addRow(new Vector(Arrays.asList("Class", link2.getClass() )));
						tableModel.addRow(new Vector(Arrays.asList("Interface", link2.getLinkInterface())));
						tableModel.addRow(new Vector(Arrays.asList("Arguments", link2.getArguments() )));


					}
				} else {
					System.out.println("something else has been selected");
					
					String[] clockDomainName = {"Clock Domains", ""};
					tableModel.addRow(clockDomainName);
					
					//System.out.println(selectedCell.getId() + " has been selected");
					
					// if it isn't a Component object, then it will be the place holder object in the Sub System view which counts the number of Clock Domains
					mxCell subCell = (mxCell)selectedCell.getParent();
				//	System.out.println("printing sub system");
					//System.out.println(subCell);
					System.out.println("printing selected");
					System.out.println(selectedCell);
					
					
					SubSystem sub = null;
					
					if (subCell.getValue() instanceof SubSystem)
					{
						sub = (SubSystem)subCell.getValue();
					}
					
					else
					{
						System.out.println("you have clicked somthing I can not find");
						return; //fix me
					}
					
					
					// If this is the case, add all Clock Domain objects
					for (ClockDomain cd : sub.getClockDomains()) {
						tableModel.addRow(new Vector(Arrays.asList(cd.toString(), "")));
					}
					
				}
			}
		}

	}

}

