package gui.mainwindow;

import graphmodel.GlobalSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

/**
 * This class encapsulates the menu bar that is placed at the very top of the window.
 * Currently, there is only one option, and that is "File", which contains:
 * - "New" - (yet to be implemented)
 * - "Load existing file"
 * - "Generate XML file"
 * 
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class TopMenuBar extends JMenuBar implements ActionListener {
	
	private JMenu fileMenu;
	
	private static final String NEW_OPTION = "New";
	private static final String LOAD_OPTION = "Load Existing File";
	private static final String SAVE_OPTION = "Save XML File";
	
	
	public TopMenuBar() {
		// Add file option to the Menu Bar
		fileMenu = new JMenu("File");
		add(fileMenu);
		
		// Add menu options to "File"
		JMenuItem menuOptionNew = new JMenuItem(NEW_OPTION);
		menuOptionNew.setActionCommand(NEW_OPTION);
		menuOptionNew.addActionListener(this);
		fileMenu.add(menuOptionNew);
		
		JMenuItem menuOptionLoad = new JMenuItem(LOAD_OPTION);
		menuOptionLoad.setActionCommand(LOAD_OPTION);
		menuOptionLoad.addActionListener(this);
		fileMenu.add(menuOptionLoad);
		
		JMenuItem menuOptionSave = new JMenuItem(SAVE_OPTION);
		menuOptionSave.addActionListener(this);
		menuOptionSave.setActionCommand(SAVE_OPTION);
		fileMenu.add(menuOptionSave);
	}
	
	/**
	 * This method is executed when the "Save" option has been selected. It opens the standard "Save As" dialog box and prompts the user to choose a location and name for this file.
	 * The "Save As" dialog uses a filter so that only XML files can be displayed and saved.
	 * Once a location is obtained, then the full XML String generated from the object model will be written to file.
	 */
	public void saveAsXML() {
		// Open the "Save As" dialog box
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new XMLFilter());
		int returnVal = fc.showSaveDialog(this.getParent());
		
		// If the user chooses "OK", then retrieve the file name the user set and use it to create the XML file of the current system.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// append .xml to the end if not specified by the user to ensure it is an XML file that is saved
			if (!file.getName().endsWith(".xml")) {
				File f2 = new File(file.getAbsolutePath() + ".xml");
				file = f2;
			}
			GlobalSystem system = GlobalSystem.getInstance();
			try{
				  // Create file 
				  FileWriter fstream = new FileWriter(file);
				  BufferedWriter out = new BufferedWriter(fstream);
				  // Write the generated XML to the file
				  out.write(system.createXMLFile());
				  
				  //Close the output stream
				  out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * This method is called when the "Load from existing file" option is selected. It opens a dialog box for the user to load an existing file, with a filter so that it only
	 * shows XML files.
	 * Once a file is selected, a model is then constructed from this XML file, and then displayed.
	 */
	public void loadXMLFile() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new XMLFilter());
		// Show "Open" dialog box
		int returnVal = fc.showOpenDialog(this.getParent());
		
		// when the user selects a file and chooses "OK", load the chosen file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			GlobalSystem system = GlobalSystem.getInstance();
			system.constructModel(file);

		}
	}

	/**
	 * This method is called when an option in the menu is selected.
	 * It executes the corresponding action, whether it is New (yet to be implemented), Save or Load.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		// Based on the chosen menu option, do the corresponding action
		if (command.equals(SAVE_OPTION)) {
			saveAsXML();
		} else if (command.equals(LOAD_OPTION)) {
			loadXMLFile();
		} else {
			//TODO: implement correctly. This is supposed to be the "New" option
			GlobalSystem.getInstance().clearModel();
		}
		
	}
	
	/**
	 * This class is just a filter to ensure that only XML files are displayed when opening/saving from the file directory.
	 * @author Chanisha Somatilaka, rsom024
	 *
	 */
	private class XMLFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			// Include directories as well as XML files
			if (f.isDirectory()) {
				return true;
			}
			
			return f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "XML files";
		}
		
	}

}


