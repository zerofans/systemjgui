package gui.tabs.popups;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import gui.tabs.popups.ErrorBlankInputpopup;

public class Testuseonly {
	//private static ArrayList<int[]> myArray;
	private static ArrayList<int[]> myArray = new ArrayList<int[]>();
	private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DialogDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
        //Create and set up the content pane.
       // DialogDemo newContentPane = new DialogDemo(frame);
      //  newContentPane.setOpaque(true); //content panes must be opaque
       // frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        
        
        ErrorBlankInputpopup newR = new ErrorBlankInputpopup(frame,"working" , 12 ,10);
        newR.constructPopup("working");
    }
 
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			
			public void run(){
				//createAndShowGUI();
			}
		});
		//ArrayList<int[]> myArray = new ArrayList<int[]>();
		
		int[] a = {1,2};
		int[] b = {1,2,3};
		myArray.add( a );
		myArray.add( b );
		
		System.out.println(((int[]) myArray.get(0))[1]);
		System.out.println(((int[]) myArray.get(0))[0]);
		
	}
	
}
