package gui.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import graphmodel.GlobalSystem;
import graphmodel.SubSystem;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class XMLView extends JPanel implements ActionListener{
	private JTextArea xmlArea;
	
	
	
	public XMLView() {
		super();
		this.setLayout(new BorderLayout());
		xmlArea = new JTextArea();
		xmlArea.setEditable(false);
		xmlArea.setLineWrap(true);
		
		JScrollPane xmlView = new JScrollPane(xmlArea);
		this.add(xmlView, BorderLayout.CENTER);
		GlobalSystem.getInstance().addListener(this);
	}
	
	public void setXML() {
		GlobalSystem system = GlobalSystem.getInstance();
		
		xmlArea.replaceRange(system.createXMLFile(), 0, xmlArea.getSelectionEnd());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setXML();
	}

	
	
}
