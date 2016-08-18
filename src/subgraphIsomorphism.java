import java.util.ArrayList;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class subgraphIsomorphism {
	public static void main(String[] args) {
		new subgraphIsomorphism();
	}
	
	public subgraphIsomorphism() {
		
		for(int a=0;a < 10000;a++) {
			SimpleGraph<Integer, DefaultEdge> G1 = graph.randomGraph(20, 0.3);
			SimpleGraph<Integer, DefaultEdge> G2 = graph.randomGraph(10, 0.45);
			
			
			//lowerBound lb = new lowerBound(new graph(0,graph.adjacencyMatrix(G2)),true);
			
			System.out.println(VF2(G1,G2));
			/*if(VF2(G1,G2) != lb.isSubgraph(G1)) {
				System.out.println(graph.adjacencyMatrix(G1));System.out.println(graph.adjacencyMatrix(G2));
				break;
			}*/
		}
		System.out.println("done");
		
	}
	
	// determine if smallGraph is contained in bigGraph via VF2 algorithm as described in [An Improved Algorithm for Matching Large Graphs]
	public static boolean VF2(SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph) {
		// ordering the vertices by degree is hugely beneficial for most graphs, since we abort once we find the first isomorphism
		// we expect smallGraph to have ordered vertices already
		bigGraph = graph.orderVerticesByDegree(bigGraph);
		
		// init empty matching function
		ArrayList<Integer> matching = new ArrayList<Integer>();
		for(int a=0;a < smallGraph.vertexSet().size();a++) matching.add(-1);
		
		// init empty neighbor set
		boolean[] isUnmatchedBigGraphNeighbor = new boolean[bigGraph.vertexSet().size()];
		boolean[] isUnmatchedSmallGraphNeighbor = new boolean[smallGraph.vertexSet().size()];
		
		return VF2(bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor);
	}
	
	// VF2 function called with proper parameters from main VF2
	public static boolean VF2(SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph, ArrayList<Integer> matching, boolean[] isUnmatchedBigGraphNeighbor, boolean[] isUnmatchedSmallGraphNeighbor) {
		// if matching is complete, we are done
		if(!matching.contains(-1)) return true;
		else {
			// let m the first unmatched vertex in smallGraph that has an already matched neighbor 
			int m = 0;
			boolean bothNonEmpty = false;
			while(m < isUnmatchedSmallGraphNeighbor.length && !isUnmatchedSmallGraphNeighbor[m]) m++;
			// if m exists, go through all unmatched vertices in bigGraph that have an already matched neighbor
			if(m < isUnmatchedSmallGraphNeighbor.length) {
				for(int n=0;n < isUnmatchedBigGraphNeighbor.length;n++) {
					if(isUnmatchedBigGraphNeighbor[n]) {
						bothNonEmpty = true;
						if(VF2_exploreMatch(m,n,bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor)) return true;
					}
					
				}
			}
			// if one of the sets was empty, pick the first unmatched vertex in smallGraph and go through all unmatched vertices in bigGraph
			if(!bothNonEmpty) {
				// find an unmatched vertex in smallGraph
				m = 0;
				while(matching.get(m) >= 0) m++;
				
				// go through all unmatched vertices in bigGraph
				for(int n=0;n < bigGraph.vertexSet().size();n++) {
					if(!matching.contains(n)) {
						if(VF2_exploreMatch(m,n,bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor)) return true;
					}
				}
			}
			
		}
		return false;
	}
	
	// check if m -> n can be added to the matching. if yes, explore
	public static boolean VF2_exploreMatch(int m, int n, SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph, ArrayList<Integer> matching, boolean[] isUnmatchedBigGraphNeighbor, boolean[] isUnmatchedSmallGraphNeighbor) {
		boolean feasibleMatch = true;		
		int neighborsOfmNotMatched = 0;
		int neighborsOfnNotMatched = 0;
		// check if m -> n is a feasible matching
		for(int b=0;b < smallGraph.vertexSet().size() && feasibleMatch;b++) {
			if(smallGraph.containsEdge(m,b)) {
				// every matched neighbor of m has to map to a neighbor of n
				if(matching.get(b) >= 0) {
					if(!bigGraph.containsEdge(n,matching.get(b))) feasibleMatch = false;
				}
				// count how many unmatched neighbors m has
				else neighborsOfmNotMatched++;
			} 
		}
		
		// count how many unmatched neighbors n has
		for(int b=0;b < bigGraph.vertexSet().size() && neighborsOfnNotMatched < neighborsOfmNotMatched && feasibleMatch;b++) {
			if(bigGraph.containsEdge(n,b) && !matching.contains(b)) neighborsOfnNotMatched++;
		}
		// n needs at least as many unmatched neighbors as m
		if(neighborsOfnNotMatched < neighborsOfmNotMatched) feasibleMatch = false;		
		
		// if n,m are a feasible match, explore this extended matching
		if(feasibleMatch) {
			// make a hard copy of the current match and extend it with appropriate parameters
			ArrayList<Integer> matching_extended = (ArrayList<Integer>) matching.clone();
			boolean[] isUnmatchedBigGraphNeighbor_extended = isUnmatchedBigGraphNeighbor.clone();
			boolean[] isUnmatchedSmallGraphNeighbor_extended = isUnmatchedSmallGraphNeighbor.clone();
			
			matching_extended.set(m,n);
			isUnmatchedBigGraphNeighbor_extended[n] = false;
			isUnmatchedSmallGraphNeighbor_extended[m] = false;
			
			for(int a=0;a < smallGraph.vertexSet().size();a++) {
				if(matching.get(a)==-1 && smallGraph.containsEdge(m,a)) isUnmatchedSmallGraphNeighbor_extended[a] = true;
			}
			for(int a=0;a < bigGraph.vertexSet().size();a++) {
				if(!matching.contains(a) && bigGraph.containsEdge(n,a)) isUnmatchedBigGraphNeighbor_extended[a] = true;
			}
			
			// explore the extended matching
			if(VF2(bigGraph,smallGraph,matching_extended,isUnmatchedBigGraphNeighbor_extended,isUnmatchedSmallGraphNeighbor_extended)) return true;
		}
		
		return false;
	}

	// find all subgraph isomorphisms between two graphs using VF2
	public static ArrayList<ArrayList<Integer>> allSubgraphIsomorphismsVF2(SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph) {
		
		ArrayList<Integer> matching = new ArrayList<Integer>();
		for(int a=0;a < smallGraph.vertexSet().size();a++) matching.add(-1);
		
		boolean[] isUnmatchedBigGraphNeighbor = new boolean[bigGraph.vertexSet().size()];
		boolean[] isUnmatchedSmallGraphNeighbor = new boolean[smallGraph.vertexSet().size()];
		
		return allSubgraphIsomorphismsVF2(bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor);
	}
	
	public static ArrayList<ArrayList<Integer>> allSubgraphIsomorphismsVF2(SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph, ArrayList<Integer> matching, boolean[] isUnmatchedBigGraphNeighbor, boolean[] isUnmatchedSmallGraphNeighbor) {
		ArrayList<ArrayList<Integer>> listOfMatchings = new ArrayList<ArrayList<Integer>>();
		if(!matching.contains(-1)) {
			listOfMatchings.add(matching);
		}
		else {
			// let m the first unmatched vertex in smallGraph that has an already matched neighbor 
			int m = 0;
			boolean bothNonEmpty = false;
			while(m < isUnmatchedSmallGraphNeighbor.length && !isUnmatchedSmallGraphNeighbor[m]) m++;
			if(m < isUnmatchedSmallGraphNeighbor.length) {
				// go through all unmatched vertices in bigGraph that have an already matched neighbor
				for(int n=0;n < isUnmatchedBigGraphNeighbor.length;n++) {
					if(isUnmatchedBigGraphNeighbor[n]) {
						bothNonEmpty = true;
						ArrayList<ArrayList<Integer>> newMatchings = allSubgraphIsomorphismsVF2_exploreMatch(m,n,bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor);
						for(ArrayList<Integer> M : newMatchings) listOfMatchings.add(M);
					}
					
				}
			}
			// if one of the sets was empty, pick the first unmatched vertex in smallGraph and go through all unmatched vertices in bigGraph
			if(!bothNonEmpty) {
				// find an unmatched vertex in smallGraph
				m = 0;
				while(matching.get(m) >= 0) m++;
				
				// go through all unmatched vertices in bigGraph
				for(int n=0;n < bigGraph.vertexSet().size();n++) {
					if(!matching.contains(n)) {
						ArrayList<ArrayList<Integer>> newMatchings = allSubgraphIsomorphismsVF2_exploreMatch(m,n,bigGraph,smallGraph,matching,isUnmatchedBigGraphNeighbor,isUnmatchedSmallGraphNeighbor);
						for(ArrayList<Integer> M : newMatchings) listOfMatchings.add(M);
					}
				}
			}
			
		}
		return listOfMatchings;
	}
	
	public static ArrayList<ArrayList<Integer>> allSubgraphIsomorphismsVF2_exploreMatch(int m, int n, SimpleGraph<Integer, DefaultEdge> bigGraph, SimpleGraph<Integer, DefaultEdge> smallGraph, ArrayList<Integer> matching, boolean[] isUnmatchedBigGraphNeighbor, boolean[] isUnmatchedSmallGraphNeighbor) {
		ArrayList<ArrayList<Integer>> listOfMatchings = new ArrayList<ArrayList<Integer>>();
		boolean feasibleMatch = true;		
		int neighborsOfmNotMatched = 0;
		int neighborsOfnNotMatched = 0;
		// check if n,m is a feasible matching
		for(int b=0;b < smallGraph.vertexSet().size() && feasibleMatch;b++) {
			if(smallGraph.containsEdge(m,b)) {
				// every matched neighbor of m has to map to a neighbor of n
				if(matching.get(b) >= 0) {
					if(!bigGraph.containsEdge(n,matching.get(b))) feasibleMatch = false;
				}
				// count how many unmatched neighbors m has
				else neighborsOfmNotMatched++;
			} 
		}
		
		// count how many unmatched neighbors n has
		for(int b=0;b < bigGraph.vertexSet().size() && neighborsOfnNotMatched < neighborsOfmNotMatched && feasibleMatch;b++) {
			if(bigGraph.containsEdge(n,b) && !matching.contains(b)) neighborsOfnNotMatched++;
		}
		if(neighborsOfnNotMatched < neighborsOfmNotMatched) feasibleMatch = false;		
		
		// if n,m are a feasible match, explore this extended matching
		if(feasibleMatch) {
			ArrayList<Integer> matching_extended = (ArrayList<Integer>) matching.clone();
			boolean[] isUnmatchedBigGraphNeighbor_extended = isUnmatchedBigGraphNeighbor.clone();
			boolean[] isUnmatchedSmallGraphNeighbor_extended = isUnmatchedSmallGraphNeighbor.clone();
			
			matching_extended.set(m,n);
			isUnmatchedBigGraphNeighbor_extended[n] = false;
			isUnmatchedSmallGraphNeighbor_extended[m] = false;
			
			for(int a=0;a < smallGraph.vertexSet().size();a++) {
				if(matching.get(a)==-1 && smallGraph.containsEdge(m,a)) isUnmatchedSmallGraphNeighbor_extended[a] = true;
			}
			for(int a=0;a < bigGraph.vertexSet().size();a++) {
				if(!matching.contains(a) && bigGraph.containsEdge(n,a)) isUnmatchedBigGraphNeighbor_extended[a] = true;
			}
			
			ArrayList<ArrayList<Integer>> newMatchings = allSubgraphIsomorphismsVF2(bigGraph,smallGraph,matching_extended,isUnmatchedBigGraphNeighbor_extended,isUnmatchedSmallGraphNeighbor_extended);
			for(ArrayList<Integer> M : newMatchings) listOfMatchings.add(M);
		}
		
		return listOfMatchings;
	}	
}
