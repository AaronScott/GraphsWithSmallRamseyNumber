import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class tree {
	
	ArrayList<SimpleGraph<Integer, DefaultEdge>> trees[] = new ArrayList[11];
	
	public static void main(String[] args) {
		new tree();
	}
	public tree() {
		// we are investigating whether there is a tree for which the bound
		//		r(T) >= max(2a-1,a+2b-1,r(K_{1,Delta(T)})) for bipart sizes a >= b
		// is not tight
		
		// we have checked every tree on <= 8 vertices
		
		// n | # of trees
		// 0	1
		// 1	1
		// 2	1
		// 3	1
		// 4	2
		// 5	3
		// 6	6
		// 7	11
		// 8	23
		// 9	47
		// 10	106
		// .. , 235, 551, 1301, 3159, 7741, 19320, 48629, 123867, 317955,

		findAllTrees();
		for(int a=0;a < trees.length;a++) {
			//System.out.println(trees[a].toString());
			System.out.println(trees[a].size());
			Collections.shuffle(trees[a]);
		}
		
		int n = 7;
		for(int a=0;a < trees[n].size();a++) {
			SimpleGraph<Integer, DefaultEdge> T = trees[n].get(a);
			System.out.println("tree #" +(a+1) +":" +graph.adjacencyMatrix(T));
			//SimpleGraph<Integer, DefaultEdge> T = graph.orderVerticesByDegree(generateRandomTree(n));
			int expectedRamseyNumber = expectedRamseyNumberOfTree(T);
			graph g = new graph(0,graph.adjacencyMatrix(T));
			g.lowerBound = expectedRamseyNumber;
			
			//System.out.println(g.adjacencyMatrix());
			System.out.println("expecting r=" +expectedRamseyNumber);
			
			/*lowerBound lb = new lowerBound(g,true);
			lb.randomizedGreedyBound();*/
			//System.out.println("starting upper bound proof");
			long t = System.currentTimeMillis();
			upperBound ub = new upperBound(g);
			ub.proveBound();
			System.out.println("proving the bound took " +(System.currentTimeMillis()-t)/1000 +"s");
			
			//System.out.println("r=" +g.lowerBound);
			//System.out.println("expected " +expectedRamseyNumber +", got " +g.lowerBound);
			if(expectedRamseyNumber < g.lowerBound) {
				System.out.println("#########################");
				break;
			}
		}
		System.out.println("done");
		
	}
	
	public SimpleGraph<Integer, DefaultEdge> generateRandomTree(int vertices) {
		SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		for(int a=0;a < vertices;a++) G.addVertex(a);
		
		while(G.edgeSet().size() < vertices-1) {
			ConnectivityInspector<Integer, DefaultEdge> C = new ConnectivityInspector<Integer, DefaultEdge>(G);
			int a = (int)Math.round(Math.random()*(vertices-1));
			int b = (int)Math.round(Math.random()*(vertices-1));
			if(a != b && !C.pathExists(a,b)) {G.addEdge(a,b);}
		}
		
		return G;
	}
	
	public SimpleGraph<Integer, DefaultEdge> generateRandomTree2(int vertices) {
		SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		
		for(int a=0;a < vertices;a++) {
			G.addVertex(a);
			if(a > 0) {G.addEdge(a,(int)Math.round(Math.random()*(a-1)));}
		}
		
		return G;
	}
	
	public int expectedRamseyNumberOfTree(SimpleGraph<Integer, DefaultEdge> T) {
		int r = 0;
		
		// r(T) >= r(K_{1,Delta(T)})
		int maxDegree = 0;
		for(int v : T.vertexSet()) {
			maxDegree = Math.max(T.degreeOf(v),maxDegree);
		}
		if(maxDegree % 2 == 0) r = 2*maxDegree-1;
		else r = 2*maxDegree;
		
		int a = bipartiteSize(T);
		int b = T.vertexSet().size()-a;
		
		r = Math.max(2*a-1,r);
		r = Math.max(a+2*b-1,r);
		
		return r;
	}
	
	// returns the size of the larger bipart of a tree T
	public int bipartiteSize(SimpleGraph<Integer, DefaultEdge> T) {
		int a=0;
		for(int v : T.vertexSet()) {
			if((int)new DijkstraShortestPath<Integer, DefaultEdge>(T,0,v).getPathLength() % 2 == 0) a++;
		}
		return Math.max(a,T.vertexSet().size()-a);
	}
	
	public void findAllTrees() {
		trees[0] = new ArrayList<SimpleGraph<Integer, DefaultEdge>>();
		trees[0].add(new SimpleGraph<Integer,DefaultEdge>(DefaultEdge.class));
		trees[1] = new ArrayList<SimpleGraph<Integer, DefaultEdge>>();
		SimpleGraph<Integer,DefaultEdge> G1 = new SimpleGraph<Integer,DefaultEdge>(DefaultEdge.class);
		G1.addVertex(0);
		trees[1].add(G1);
		for(int a=2;a < trees.length;a++) {
			trees[a] = new ArrayList<SimpleGraph<Integer, DefaultEdge>>();
			for(SimpleGraph<Integer,DefaultEdge> Tree : trees[a-1]) {
				for(int b=0;b < a-1;b++) {
					SimpleGraph<Integer,DefaultEdge> T = (SimpleGraph<Integer, DefaultEdge>) Tree.clone();
					T.addVertex(a-1);
					T.addEdge(a-1,b);
					boolean isNew = true;
					for(SimpleGraph<Integer,DefaultEdge> otherTree : trees[a]) {
						if(subgraphIsomorphism.VF2(T,otherTree)) {
							isNew=false;
							break;
						}
					}
					if(isNew) trees[a].add(graph.orderVerticesByDegree(T));
				}
			}
		}
	}
	
	public boolean boundIsDominatedByLargeEvenDegree(SimpleGraph<Integer, DefaultEdge> T) {
		return 2*bipartiteSize(T)-1==expectedRamseyNumberOfTree(T);		
	}
}
