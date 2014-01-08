package gui.tabs.popups;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ErrorBlankInputpopup extends JDialog implements ActionListener {
	private JPanel contentPanel;
	private JTextField errorTextField;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.dispose();
		
	}
	
	
	void constructPopup(String Blankslot){
		
		this.setBounds(100, 100,100, 100);
		this.setLayout(new BorderLayout());
		errorTextField = new JTextField();
		
		errorTextField.setText(Blankslot);
	    //this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	    //getContentPane().add(contentPanel, BorderLayout.NORTH);
		JLabel nameLabel = new JLabel("please fill " + Blankslot);	
		getContentPane().add(nameLabel);
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
		
	}
	
	  public ErrorBlankInputpopup(JFrame jFrame, String componentType, int x, int y) {
		    super(jFrame, true);
		    this.constructPopup(componentType);
		    this.setTitle("title is here");
		    setVisible(true);
		  }

}
