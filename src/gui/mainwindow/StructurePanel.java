package gui.mainwindow;

import graphmodel.Channel;
import graphmodel.ClockDomain;
import graphmodel.GlobalSystem;
import graphmodel.Link;
import graphmodel.LinkGroup;
import graphmodel.Signal;
import graphmodel.SubSystem;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * This class encapsulates the JPanel that contains the tree object representing the current structure of the created system.
 * It provides a view of the entire system so far.
 * For every change that is made in the object model, the change is reflected in this tree.
 * It displays Signals within Clock Domain nodes, and CLock Domain nodes within their Sub System nodes.
 * Links and Channels are displayed in separate nodes, as they represent connections between objects and are not objects themselves.
 * NOTE: Channels will be displayed according to their name, which is their name in the source Clock Domain
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class StructurePanel extends JPanel implements ActionListener {
	
	private JTree structureTree;
	private TreeModel structureModel;
	
	public StructurePanel() {
		super();
		setLayout(new BorderLayout(0,0));
		
		structureModel = createTree();
		structureTree = new JTree(structureModel);
		JScrollPane treeView = new JScrollPane(structureTree);
		add(treeView, BorderLayout.CENTER);
		
		// Add this tree as a listener so it is notified of changes
		GlobalSystem.getInstance().addListener(this);
	}
	
	/**
	 * This method builds a new tree from scratch depending on the current state of the object model.
	 * @return TreeModel representing the current object model structure
	 */
	public TreeModel createTree() {
		// top level node
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Global Configuration");
		GlobalSystem system = GlobalSystem.getInstance();
		
		// Iterate through all Sub Systems and add them to the tree.
		for (SubSystem s : system.getSubSystems()) {
			DefaultMutableTreeNode sub = new DefaultMutableTreeNode(s);
			// Add all Clock Domains to the tree
			for (ClockDomain cd : s.getClockDomains()) {
				DefaultMutableTreeNode clockDomain = new DefaultMutableTreeNode(cd);
				
				//Add both input and output Signals onto te tree, as they apply to a single Clock Domain, so can be nested in the tree structure
				for (Signal input : cd.getInputSignals()) {
					DefaultMutableTreeNode in = new DefaultMutableTreeNode(input);
					clockDomain.add(in);
				}
				
				for (Signal output : cd.getOutputSignals()) {
					DefaultMutableTreeNode out = new DefaultMutableTreeNode(output);
					clockDomain.add(out);
				}
				sub.add(clockDomain);
			}
			top.add(sub);
		}
		
		// Add Links and Channels separately, as they concern connections between Sub Systems
		// TODO: if there was a way to add different symbols in a tree, then can add them into the hierarchical structure with symbols differentiating them from Clock Domains and Signals
		
		// Add all Links. Nest the Link interfaces inside the Link groups
		DefaultMutableTreeNode linkGroups = new DefaultMutableTreeNode("Links");
		for (LinkGroup l : system.getLinks()) {
			DefaultMutableTreeNode lg = new DefaultMutableTreeNode(l);
			for (Link link : l.getLinks()) {
				DefaultMutableTreeNode linkNode = new DefaultMutableTreeNode(link);
				lg.add(linkNode);
			}
			linkGroups.add(lg);
		}
		top.add(linkGroups);
		
		// Add Channels
		DefaultMutableTreeNode channels = new DefaultMutableTreeNode("Channels");
		for (ClockDomain cd : system.getAllClockDomains()) {
			for (Channel ch : cd.getOutputChannels()) {
				DefaultMutableTreeNode chNode = new DefaultMutableTreeNode(ch);
				channels.add(chNode);
			}
		}
		top.add(channels);
		
		TreeModel model = new DefaultTreeModel(top);
		return model;
		
		
	}


	/**
	 * This method is fired every time there is a change in the object model.
	 * TODO: currently, this reconstructs the whole tree for every change made in the model. Maybe implement a more efficient method?
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		structureTree.setModel(createTree());
		
	}

}
