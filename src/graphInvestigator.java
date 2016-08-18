import java.util.ArrayList;


public class graphInvestigator {
	
	public graphFinder graphInfo;
	
	public static void main(String[] args) {
		new graphInvestigator();
	}
	
	public graphInvestigator() {
		
		graphInfo = new graphFinder();
		graphInfo.readGraphs();
		
		
		System.out.println("hi");
		//proveUpperBounds(4,4);
		//findLowerBounds(4,4);
		

		//System.out.println(findLargestGuaranteedGraph(10).adjacencyMatrix());
		//System.out.println(findLargestGuaranteedGraph(10).edges);
		
		//proveUpperBounds(10);
		while(true) {
			for(int a=10;a <= 20;a++) improveLowerBounds(a);
		}
		
		//graphInfo.findAllGraphsUpToSevenVertices();
		/*for(int a=9; a <= (7*6)/2;a++) {
			findLowerBounds(7,a);
			graphInfo.writeGraphs();
		}*/
		//proveUpperBounds(3,3);
		
//		for(int a=5;a < 10;a++) graphInfo.findNewGraphs(8,a);
		
		/*System.out.println("saving graphs");
		graphInfo.writeGraphs();
		System.out.println("done");*/
	}
	
	// find lower bounds for all graphs on given number of vertices and edges
	public void findLowerBounds(int vxs, int edges) {
		if(graphInfo.graphs[vxs][edges] == null) {
			System.out.println("mistakes were made! graphs on " +vxs +" vertices, " +edges +" edges have not yet been initialized.");
			return;
		}
		else {
			for(graph g : graphInfo.graphs[vxs][edges]) {
				if(g.lowerBound <= graph.largestColoringWeWillEverConsider && (g.lowerBound < g.upperBound || g.upperBound == -1)) {
					System.out.println("graph #" +g.id +" with current lower bd " +g.lowerBound +" and adjacency matrix " +g.adjacencyMatrix());
					int oldLowerBound = g.lowerBound;
					lowerBound lb = new lowerBound(g);
					lb.randomizedGreedyBound(1,50*edges);
					if(oldLowerBound < g.lowerBound) {
						System.out.println("determined a lower bound of " +g.lowerBound +" by constructing " +g.lowerBoundConstruction);
						updateLowerBoundsByInclusion(g);
						graphInfo.writeGraphs();
					}
					else {
						System.out.println("could not improve bound");
					}
				}
			}
		}
	}
	
	// prove upper bounds for all graphs on given number of vertices and edges via brute force, or find a counterexample
	public void proveUpperBounds(int vxs, int edges) {
		if(graphInfo.graphs[vxs][edges] == null) {
			System.out.println("mistakes were made! graphs on " +vxs +" vertices, " +edges +" edges have not yet been initialized.");
			return;
		}
		else {
			for(graph g : graphInfo.graphs[vxs][edges]) {
				while(g.lowerBound <= graph.largestColoringWeWillEverConsider && (g.lowerBound < g.upperBound || g.upperBound == -1)) {
					long t = System.currentTimeMillis();
					System.out.println("graph #" +g.id +" with lower bound of " +g.lowerBound +" and adjacency matrix " +g.adjacencyMatrix());
					int oldLowerBound = g.lowerBound;
					upperBound ub = new upperBound(g);
					ub.proveBound();
					if(oldLowerBound < g.lowerBound) {
						System.out.println("found a counterexample on " +(g.lowerBound-1) +" vertices after " +(System.currentTimeMillis()-t)/1000 +"s @ " +g.lowerBoundConstruction);
						if(g.lowerBound <= graph.largestColoringWeWillEverConsider) System.out.println("trying again with " +g.lowerBound +" vertices");
						else System.out.println("discontinuing investigation of this graph as r > " +graph.largestColoringWeWillEverConsider);
						updateLowerBoundsByInclusion(g);
						graphInfo.writeGraphs();
					}
					else {
						System.out.println("proved upper bound of " +g.upperBound +" by brute force in " +(System.currentTimeMillis()-t)/1000 +"s");
						updateUpperBoundsByInclusion(g);
						graphInfo.writeGraphs();
					}
				}
			}
		}
	}
	
	// prove upper bounds for all graphs with lower bound of n, or produce a counterexample
	public void proveUpperBounds(int n) {
		ArrayList<graph> gs = allGraphsWithExpectedRamseyNumber(n);
		while(!gs.isEmpty()) {
			System.out.println(gs.size() +" graphs remaining with expected ramsey number " +n);
			graph g = maximalGraph(gs);
			
			long t = System.currentTimeMillis();
			System.out.println("graph #" +g.id +" with lower bound of " +g.lowerBound +" and adjacency matrix " +g.adjacencyMatrix());
			int oldLowerBound = g.lowerBound;
			upperBound ub = new upperBound(g);
			ub.proveBound();
			if(oldLowerBound < g.lowerBound) {
				System.out.println("found a counterexample on " +(g.lowerBound-1) +" vertices after " +(System.currentTimeMillis()-t)/1000 +"s @ " +g.lowerBoundConstruction);
				updateLowerBoundsByInclusion(g);
				graphInfo.writeGraphs();
			}
			else {
				System.out.println("proved upper bound of " +g.upperBound +" by brute force in " +(System.currentTimeMillis()-t)/1000 +"s");
				updateUpperBoundsByInclusion(g);
				graphInfo.writeGraphs();
			}			
			
			gs = allGraphsWithExpectedRamseyNumber(n);
		}
	}
	
	// take a random graph with expected ramsey number n and try to construct a counterexample
	public void improveLowerBounds(int n) {
		ArrayList<graph> gs = allGraphsWithExpectedRamseyNumber(n);
		System.out.println(n);
		if(gs.isEmpty()) {
			System.out.println("oh oh, there are no graphs with expected ramsey number of " +n);
		}
		else {
			graph g = allGraphsWithExpectedRamseyNumber(n).get((int)Math.round(Math.random()*(gs.size()-1)));
			System.out.println("graph #" +g.id +" with adjacency matrix " +g.adjacencyMatrix());
			lowerBound lb = new lowerBound(g);
			lb.randomizedGreedyBound(5,n*n);
			if(g.lowerBound == n) System.out.println("could not improve bound");
			else {
				updateLowerBoundsByInclusion(g);
				graphInfo.writeGraphs();
			}
		}
	}
	
	// when proving an upper bound, this function should be called to update all subgraphs
	public void updateUpperBoundsByInclusion(graph g) {
		for(graph smallerGraph : g.subgraphs) {
			if(smallerGraph.upperBound == -1 || smallerGraph.upperBound > g.upperBound) {
				System.out.println("updated upper bound of graph #" +smallerGraph.id);
				smallerGraph.upperBound = g.upperBound;
				smallerGraph.upperBoundReason = g.id;
				updateUpperBoundsByInclusion(smallerGraph);
			}
		}
	}
	
	// when proving a lower bound, this function should be called to update all supergraphs
	public void updateLowerBoundsByInclusion(graph g) {
		for(graph largerGraph : g.supergraphs){
			if(g.supergraphs.contains(largerGraph)) {
				if(largerGraph.lowerBound < g.lowerBound) {
					System.out.println("updated lower bound of graph #" +largerGraph.id);
					largerGraph.lowerBound = g.lowerBound;
					largerGraph.lowerBoundConstruction = g.lowerBoundConstruction;
					updateLowerBoundsByInclusion(largerGraph);
				}
			}
		}
	}
	
	// return the graph with the most edges known to have ramsey number at most n
	public graph findLargestGuaranteedGraph(int n) {
		int max = 0;
		graph gMax = new graph("");
		for(graph g : graphInfo.allGraphs) {
			if(g.upperBound != -1 && g.upperBound <= n && g.edges > max) {
				max = g.edges;
				gMax = g;
			}
		}
		return gMax;
	}
	
	// return all graphs with expected but unproven ramsey number n
	public ArrayList<graph> allGraphsWithExpectedRamseyNumber(int n) {
		if(n > graph.largestColoringWeWillEverConsider) {
			System.out.println("whoops, why are we interested in graphs with ramsey number > " +graph.largestColoringWeWillEverConsider +"? aborting..");
			return null;
		}
		ArrayList<graph> gs = new ArrayList<graph>();
		for(graph g : graphInfo.allGraphs) {
			if(g.lowerBound == n && g.upperBound != n) gs.add(g);
		}
		return gs;
	}
	
	// returns a maximal element from the poset
	public graph maximalGraph(ArrayList<graph> listOfGraphs) {
		graph g = listOfGraphs.get(0);
		boolean isMax = false;		
		while(!isMax) {
			isMax = true;
			for(graph supergraph : g.supergraphs) {
				if(listOfGraphs.contains(supergraph)) {
					g = supergraph;
					isMax = false;
					break;
				}
			}
		}
		return g;
	}
	
	// returns a minimal element from the poset
	public graph minimalGraph(ArrayList<graph> listOfGraphs) {
		graph g = listOfGraphs.get(0);
		boolean isMin = false;		
		while(!isMin) {
			isMin = true;
			for(graph subgraph : g.subgraphs) {
				if(listOfGraphs.contains(subgraph)) {
					g = subgraph;
					isMin = false;
					break;
				}
			}
		}
		return g;	
	}
	
}
