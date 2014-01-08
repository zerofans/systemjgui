package gui.mainwindow;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

/**
 * This class encapsulates the JPanel that contains the series of tab views that the user can cycle through.
 * Currently, this contains two graph views and a JTextArea displaying the XML view.
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class TabPanel extends JPanel {
	
	private JTabbedPane tabbedPane;
	
	/**
	 * Constructs the tabs using the list of Components as tabs, and the list of Strings as names of the tabs.
	 * NOTE: Size of both lists must be the same, or an error will be thrown.
	 * 
	 * @param tabs
	 * @param tabNames
	 */
	public TabPanel(List<JComponent> tabs, List<String> tabNames) {
		super();
		setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		if (tabs.size() != tabNames.size()) {
			throw new RuntimeException("Size of both lists must be the same!");
		} else {
			for (int i = 0; i < tabs.size(); i++) {
				tabbedPane.addTab(tabNames.get(i), null, tabs.get(i), null);
			}
		}	
		
	}
	
	/**
	 * Adds a listener to listen for changes in tab selection.
	 * Currently, it is only the ElementsPane object that is a listener, but others can be added as well.
	 * @param l
	 */
	public void addListener(ChangeListener l) {
		tabbedPane.addChangeListener(l);
	}

}
