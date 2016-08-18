import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class upperBound {

	public graph g;
	public SimpleGraph<Integer, DefaultEdge> G;
	public int vertices;
	public SimpleGraph<Integer, DefaultEdge> guaranteedSubcolorings[];
	
	public upperBound(graph g) {
		this.g = g;
		g.G=graph.orderVerticesByDegree(g.G);
		this.G = g.G;
		this.vertices = G.vertexSet().size();
		initSubcolorings();		
	}
	
	// prove g.lowerBound is tight by brute force, or produce a counter example
	public void proveBound() {
		if(g.lowerBound > graph.largestColoringWeWillEverConsider) {
			System.out.println("r > " +graph.largestColoringWeWillEverConsider +". we do not want to prove this bound.");
			return;
		}
		SimpleGraph<Integer, DefaultEdge> redColoring = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[g.lowerBound].clone();
		SimpleGraph<Integer, DefaultEdge> blueColoring = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		for(int a=0;a < g.lowerBound;a++) blueColoring.addVertex(a);
		if(iterateColorings(redColoring,blueColoring,guaranteedSubcolorings[g.lowerBound],0)) {
			//System.out.println("every coloring contains a m.c. G");
			g.upperBound = g.lowerBound;
			g.upperBoundReason = 0;
		}
		else {
			g.lowerBound++;
		}		
	}
	
	// iterate all two colorings but fix the largest guaranteed m.c. structure to save time
	public boolean iterateColorings(SimpleGraph<Integer, DefaultEdge> redColoring, SimpleGraph<Integer, DefaultEdge> blueColoring, SimpleGraph<Integer, DefaultEdge> guaranteedSubcoloring, int edgeIndex) {
		int n=redColoring.vertexSet().size();
		
		// if the two coloring is complete, check for m.c. G
		if(edgeIndex >= n*n) {
			if(subgraphIsomorphism.VF2(redColoring,G) || subgraphIsomorphism.VF2(blueColoring,G)) return true;
			else {
				//System.out.println("found counterexample @ " +graph.adjacencyMatrix(redColoring) +"\n" +graph.adjacencyMatrixNotConcatenated(redColoring));
				g.lowerBoundConstruction = graph.adjacencyMatrix(redColoring);
				return false;
			}
		}
		// only change edges if they are not part of the guaranteed m.c. substructure. 
		// also, for code readability, we go through all ordered pairs of vertices, but we ignore duplicates
		else if(edgeIndex / n >= edgeIndex % n || guaranteedSubcoloring.containsEdge(edgeIndex / n,edgeIndex % n)) {
			return iterateColorings(redColoring,blueColoring,guaranteedSubcoloring,edgeIndex+1);
		}
		else {
			// color the given edge both red and blue and explore what happens.
			// if the new edge already induces a m.c. G, we don't need to color any more edges for this choice of previous edge-colorings
			SimpleGraph<Integer, DefaultEdge> redColoring_extended = (SimpleGraph<Integer, DefaultEdge>) redColoring.clone();
			SimpleGraph<Integer, DefaultEdge> blueColoring_extended = (SimpleGraph<Integer, DefaultEdge>) blueColoring.clone();
			redColoring_extended.addEdge(edgeIndex / n, edgeIndex % n);
			blueColoring_extended.addEdge(edgeIndex / n, edgeIndex % n);
			if(!subgraphIsomorphism.VF2(redColoring_extended,G) && !iterateColorings(redColoring_extended, blueColoring, guaranteedSubcoloring, edgeIndex+1)) return false;
			if(!subgraphIsomorphism.VF2(blueColoring_extended,G) && !iterateColorings(redColoring, blueColoring_extended, guaranteedSubcoloring, edgeIndex+1)) return false;
		}
		
		//if(edgeIndex == 7) System.out.println("~1% progress");
			
		return true;
	}
	
	// set up guaranteed m.c. substructures found in any two coloring on a vertices.
	// calling this function every time we generate a new instance of upperBound is unnecessary, but it has no impact on computation time
	public void initSubcolorings() {
		guaranteedSubcolorings = new SimpleGraph[graph.largestColoringWeWillEverConsider+1];
		for(int a=1;a < graph.largestColoringWeWillEverConsider+1;a++) {
			guaranteedSubcolorings[a] = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
			for(int b=0;b < a;b++) {
				guaranteedSubcolorings[a].addVertex(b);
			}
		}
		
		// path of length 3 (3 edges)
		guaranteedSubcolorings[5] = new graph("0110100110000100").G;
		
		// c4 with pendent edge (5 edges)
		guaranteedSubcolorings[6] = new graph("0110110010100100110010000").G;
		guaranteedSubcolorings[6].addVertex(5);
		
		
		// c4 with two pendent edges from same vertex (6 edges)
		guaranteedSubcolorings[7] = new graph("011011100100100100011000100000100000").G;
		guaranteedSubcolorings[7].addVertex(6);
		
		// c4 and k3 joined at a common edge (6 edges)
		guaranteedSubcolorings[8] = new graph("011010100101100100011000100001010010").G;
		guaranteedSubcolorings[8].addVertex(6);
		guaranteedSubcolorings[8].addVertex(7);
		
		// two c4's joined at a common edge with one pendent edge from a common vertex (8 edges)
		guaranteedSubcolorings[9] = new graph("0110101100101010010000110000100001001001001000000").G;
		guaranteedSubcolorings[9].addVertex(7);
		guaranteedSubcolorings[9].addVertex(8);
		
		// 
		guaranteedSubcolorings[10] = new graph("0110101100101010010000110000100001001001001000000").G;
		guaranteedSubcolorings[10].addVertex(7);
		guaranteedSubcolorings[10].addVertex(8);
		guaranteedSubcolorings[10].addVertex(9);
		
		// c8 with 4 edges added, see unsaturation paper
		guaranteedSubcolorings[11].addEdge(0,1);
		guaranteedSubcolorings[11].addEdge(1,2);
		guaranteedSubcolorings[11].addEdge(2,3);
		guaranteedSubcolorings[11].addEdge(3,4);
		guaranteedSubcolorings[11].addEdge(4,5);
		guaranteedSubcolorings[11].addEdge(5,6);
		guaranteedSubcolorings[11].addEdge(6,7);
		guaranteedSubcolorings[11].addEdge(7,0);
		guaranteedSubcolorings[11].addEdge(0,3);
		guaranteedSubcolorings[11].addEdge(0,5);
		guaranteedSubcolorings[11].addEdge(2,7);
		guaranteedSubcolorings[11].addEdge(1,6);
		
		guaranteedSubcolorings[12] = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[11].clone();
		guaranteedSubcolorings[12].addVertex(11);
		
		guaranteedSubcolorings[13] = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[12].clone();
		guaranteedSubcolorings[13].addVertex(12);
		
		guaranteedSubcolorings[14] = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[13].clone();
		guaranteedSubcolorings[14].addVertex(13);
		
		guaranteedSubcolorings[15] = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[14].clone();
		guaranteedSubcolorings[15].addVertex(14);
		
		guaranteedSubcolorings[20] = (SimpleGraph<Integer, DefaultEdge>) guaranteedSubcolorings[11].clone();
		guaranteedSubcolorings[20].addVertex(11);
		guaranteedSubcolorings[20].addVertex(12);
		guaranteedSubcolorings[20].addVertex(13);
		guaranteedSubcolorings[20].addVertex(14);
		guaranteedSubcolorings[20].addVertex(15);
		guaranteedSubcolorings[20].addVertex(16);
		guaranteedSubcolorings[20].addVertex(17);
		guaranteedSubcolorings[20].addVertex(18);
		guaranteedSubcolorings[20].addVertex(19);
		
	}

}
