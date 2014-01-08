package gui.mainwindow;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;

/**
 * This class represents the  file structure in which existing Clock Domain files can be dragged and dropped into the canvas.
 * 
 * TODO: Currently, this functionality has not been implemented. This class is merely a place holder in the GUI to show where it goes.
 * 		 The tree used contains default data, but should be used to represent the file structure.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */


public class PackagesPanel extends JPanel {
	
	private JTree packageTree;
	
	public PackagesPanel() {
		super();
		setLayout(new BorderLayout(0,0));
		packageTree = new JTree();
		//add(packageTree, BorderLayout.CENTER); // disabled by robert . called from MainApllication, (also commented)
	}

}
