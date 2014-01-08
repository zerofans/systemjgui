

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.datatransfer.Transferable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.TransferHandler;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.view.mxGraph;

public class DualHelloWorld extends JFrame
{

    private final static String DROPALLOWED_KEY = "DropAllowed";

    private static final long serialVersionUID = -2707712944901661771L;

    public DualHelloWorld()
    {
        super("Hello, World!");

        // left graph
        mxGraph graph1 = new mxGraph();
        Object parent1 = graph1.getDefaultParent();

        graph1.getModel().beginUpdate();
        try
        {
            Object v1 = graph1.insertVertex(parent1, null, "Hello", 20, 20, 80,
                    30);
            Object v2 = graph1.insertVertex(parent1, null, "World!", 240, 150,
                    80, 30);
            graph1.insertEdge(parent1, null, "Edge", v1, v2);
        }
        finally
        {
            graph1.getModel().endUpdate();
        }

            // check the drag source
        mxGraphComponent graphComponent1 = new mxGraphComponent(graph1) {

            protected TransferHandler createTransferHandler()
            {
                return new mxGraphTransferHandler() {

                    public boolean importData(JComponent c, Transferable t)
                    {
                        if( c instanceof mxGraphComponent) {

                            Object obj = ((mxGraphComponent) c).getClientProperty(DROPALLOWED_KEY);
                            boolean allowDrop = obj != null && ((Boolean) obj) == true;

                            if( !allowDrop) {
                                System.out.println( "Drop not allowed here!");
                                return false;
                            }

                        }

                        return super.importData(c, t);

                    }

                };
            }

        };
            // drop from bottom to top panel NOT allowed
        graphComponent1.putClientProperty(DROPALLOWED_KEY, Boolean.FALSE);

        graphComponent1.setBorder( BorderFactory.createLineBorder(Color.red));

        mxGraph graph2 = new mxGraph();
        Object parent2 = graph2.getDefaultParent();

        graph2.getModel().beginUpdate();
        try
        {
            Object v1 = graph2.insertVertex(parent2, null, "Hello", 20, 20, 80,
                    30);
            Object v2 = graph2.insertVertex(parent2, null, "World!", 240, 150,
                    80, 30);
            graph2.insertEdge(parent2, null, "Edge", v1, v2);
        }
        finally
        {
            graph2.getModel().endUpdate();
        }

        mxGraphComponent graphComponent2 = new mxGraphComponent(graph2);

            // drop from top to bottom panel allowed
        graphComponent2.putClientProperty(DROPALLOWED_KEY, Boolean.TRUE);

        getContentPane().setLayout( new GridLayout( 2,1));
        getContentPane().add(graphComponent1);
        getContentPane().add(graphComponent2);

    }

    public static void main(String[] args)
    {
        DualHelloWorld frame = new DualHelloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

}