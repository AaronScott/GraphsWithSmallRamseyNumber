import java.util.ArrayList;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class lowerBound {
	
	public graph g;
	public SimpleGraph<Integer, DefaultEdge> G;
	public int vertices;

	public lowerBound(graph g) {
		this.g = g;
		G = g.G;
		vertices = G.vertexSet().size();
		g.lowerBound = Math.max(vertices, g.lowerBound);
	}	
	
	// try to improve the lower bound by searching for a construction via randomized greedy search
	public void randomizedGreedyBound(int numberOfTrials, int maxFailedMutations) {
		//simpleBound();
		int n = g.lowerBound;
		int n0 = n;
		int leastCopies = n*n*n*n*n;
		
		for(int trials=0;trials < numberOfTrials + (n-n0); trials++) {
			//if(trials % 10 == 0) System.out.println("starting trial #" +(trials+1) +" with " +n +" vertices");
			SimpleGraph<Integer, DefaultEdge> H=graph.randomGraph(n,0.5);
			SimpleGraph<Integer, DefaultEdge> H_inv=graph.inverseGraph(H);
			
			int copies=subgraphIsomorphism.allSubgraphIsomorphismsVF2(H,G).size()+subgraphIsomorphism.allSubgraphIsomorphismsVF2(H_inv,G).size();
			
			//System.out.println(copies);
			
			int failedMutations = 0;
			while(copies > 0 && failedMutations < maxFailedMutations) {
			//for(int i=0;i<100000 && copies > 0;i++) {
				int e1=(int)Math.round(Math.random()*(n-1));
				int e2=e1;
				while(e2==e1) e2=(int)Math.round(Math.random()*(n-1));
				int z=Math.max(e1,e2);
				e1=Math.min(e1, e2);
				e2=z;
				
				/*System.out.println(H.toString());
				System.out.println(graph.adjacencyMatrix(H));
				System.out.println("considering edge " +e1 +"," +e2);*/
				int delta=subgraphDelta(H,e1,e2)+subgraphDelta(H_inv,e1,e2);
				//System.out.println("would change by " +delta +"=" +subgraphDelta(H,e1,e2) +"+" +subgraphDelta(H_inv,e1,e2));
				
				if(delta <= 0) {
					graph.flipEdge(H,e1,e2);
					graph.flipEdge(H_inv,e1,e2);
					copies+=delta;
					
					if(delta < 0) {
						failedMutations=0;
						//System.out.println(copies);
					}
				}
				if(delta >= 0) failedMutations++;
			}
			
			if(copies==0) {
				System.out.println("found a counterexample on " +n +" vertices: " +graph.adjacencyMatrix(H));
				n++;
				g.lowerBoundConstruction = graph.adjacencyMatrix(H);
				g.lowerBound = n;
				leastCopies = n*n*n*n*n;
				if(n > graph.largestColoringWeWillEverConsider) break;
			}
			else if(copies < leastCopies){
				leastCopies = copies;
				System.out.println("failed trial but found coloring with only " +copies +" copies (" +graph.adjacencyMatrix(H) +")");
				g.lowerBoundConstruction = graph.adjacencyMatrix(H);
			}
		}
	}
	
	// returns how many more labeled m.c. copies of smallGraph appear in the two-coloring bigGraph when flipping the edge e in bigGraph
	public int subgraphDelta(SimpleGraph<Integer, DefaultEdge> twoColoring, int e1, int e2) {
		
		// first of all, for code readability further down, we permute our two coloring such that e is between v0 and v1
		SimpleGraph<Integer, DefaultEdge> h = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		int s=twoColoring.vertexSet().size();
		int[] vxs=new int[s];
		for(int a=0;a < s;a++) {
			h.addVertex(a);
			vxs[a]=a;
		}
		if(e1==1) {vxs[0]=e2;vxs[e2]=0;}
		else {
			vxs[0]=e1;
			vxs[e1]=0;
			int m=vxs[1];
			vxs[1]=e2;
			vxs[e2]=m;
		}
		for(int a=0;a < s;a++) {
			for(int b=a+1;b < s;b++) {
				if(twoColoring.containsEdge(a,b)) h.addEdge(vxs[a],vxs[b]);
			}
		}
		
		twoColoring = h;
		
		// if the edge is present in this graph, then every copy of G containing this edge gets removed. hence we subtract the number of copies.
		// if the edge is not present, then every copy of G containing this edge gets added. hence we add the number.
		// since this is a two-coloring, we sum over the delta of both colors. one will have a negative sign.
		int signMultiplier = 0;
		if(twoColoring.containsEdge(0,1)) signMultiplier=-1;
		else signMultiplier = 1;
		
		int sum=0;
		
		// note that the number of subgraph isomorphisms is overcounting the number of copies of G in the coloring,
		// as distinct isomorphisms can yield the same unlabeled embedding.
		// however, counting the exact number of copies becomes infeasible for graphs with 8 or more vertices,
		// and for smaller graphs we are fine with the greedy algorithm sometimes making bad choices in exchange for running much faster.
		
		int edgeExistenceConstant = 0;
		if(!twoColoring.containsEdge(0,1)) edgeExistenceConstant = 1;
		// check every ordered pair of vertices in G for feasibility w.r.t assigning them to vxs 0,1
		// if they are a feasible choice, start VF2 with appropriate parameters
		for(int v0=0;v0 < vertices;v0++) {
			if((twoColoring.degreeOf(0)+edgeExistenceConstant) >= G.degreeOf(v0)) {
				for(int v1=0;v1 < vertices;v1++) {
					// if v0 and v1 are not adjacent, the edge has no influence over that embedding, thus we ignore.
					if(G.containsEdge(v0,v1) && (twoColoring.degreeOf(1)+edgeExistenceConstant) >= G.degreeOf(v1)) {
						ArrayList<Integer> matching = new ArrayList<Integer>();
						for(int a=0;a < G.vertexSet().size();a++) matching.add(-1);							
						boolean[] isUnmatchedBigGraphNeighbor = new boolean[twoColoring.vertexSet().size()];
						boolean[] isUnmatchedSmallGraphNeighbor = new boolean[G.vertexSet().size()];
						
						matching.set(v0,0);
						matching.set(v1,1);
						
						for(int c=0;c < G.vertexSet().size();c++) {
							if(c!= v0 && c!=v1 && (G.containsEdge(c,v0) || G.containsEdge(c,v1))) {
								isUnmatchedSmallGraphNeighbor[c] = true;
							}
						}
						for(int c=2;c < twoColoring.vertexSet().size();c++) {
							if(twoColoring.containsEdge(c,0) || twoColoring.containsEdge(c,1)) {
								isUnmatchedBigGraphNeighbor[c] = true;
							}
						}							
						
						sum += subgraphIsomorphism.allSubgraphIsomorphismsVF2(twoColoring,G,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor).size();
					}
				}
			}
		}		
		return signMultiplier*sum;		
	}
}
