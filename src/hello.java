import java.awt.Dimension;

import javax.swing.JApplet;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;


public class hello extends JApplet {

	JGraphXAdapter<Integer, DefaultEdge> jgxAdapter;
	
	public static void main(String[] args) {
		new hello();
	}
	
	public hello() {

		//0110001010011100100000010100001101000011010000111001110000111100
		//010100100011000110101011011100010100
		

		
		graph G1=new graph(0, "0110101100101010010000110000100001001001001000000");
		System.out.println(G1.adjacencyMatrix());
		//new lowerBound(G1).simpleBound();
		
		displayGraph(G1.G);
		//hello1();
	}

	public void hello1() {
		System.out.println("hx");

		 SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		 G.addVertex(1);
		 G.addVertex(2);
		 G.addVertex(3);
		 
		 G.addEdge(1,2);
		 G.addEdge(2,1);
		 
			
		 displayGraph((SimpleGraph<Integer, DefaultEdge>) G);
	}
	
	public void displayGraph(SimpleGraph<Integer,DefaultEdge> G) {
		
		jgxAdapter = new JGraphXAdapter<Integer, DefaultEdge>(G);
		getContentPane().add(new mxGraphComponent(jgxAdapter));
		resize(new Dimension(640, 480));
	        
		mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
		layout.execute(jgxAdapter.getDefaultParent());
	}
}
