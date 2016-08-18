import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class graph {
		public int id = -1;
		public int vertices = -1;
		public int edges = -1;
		public int lowerBound = 0;
		public int upperBound = -1;
		public int upperBoundReason = 0;
		public String lowerBoundConstruction = "";
		public static int largestColoringWeWillEverConsider = 20;
		public SimpleGraph<Integer,DefaultEdge> G = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		//public ArrayList<Integer> subgraphIds = new ArrayList<Integer>();
		//public ArrayList<Integer> supergraphIds = new ArrayList<Integer>();
		public ArrayList<graph> subgraphs = new ArrayList<graph>();
		public ArrayList<graph> supergraphs = new ArrayList<graph>();
		public graph(SimpleGraph<Integer,DefaultEdge> G) {
			this.G = G;
			this.vertices = G.vertexSet().size();
			this.edges = G.edgeSet().size();
		}
		public graph(int id, String adjacency) {
			this.id=id;
			this.vertices=(int)Math.pow(adjacency.length(),0.5);
			for(int a=0;a<vertices;a++) G.addVertex(a);
			for(int a=0;a<adjacency.length();a++) {
				if(adjacency.charAt(a)=='1') {
					G.addEdge(a/vertices,a % vertices);
				}
			}
			this.edges=G.edgeSet().size();
		}
		public graph(String adjacency) {
			this.vertices=(int)Math.pow(adjacency.length(),0.5);
			for(int a=0;a<vertices;a++) G.addVertex(a);
			for(int a=0;a<adjacency.length();a++) {
				if(adjacency.charAt(a)=='1') {
					G.addEdge(a/vertices,a % vertices);
				}
			}
			this.edges=G.edgeSet().size();
		}
		public String graphInfo() {
			return id +"#" +vertices +"#" +edges;
		}
		public String subgraphIds() {
			String s = "";
			for(graph g : subgraphs) {
				s+=g.id +"_";
			}
			if(s.length() > 0) s=s.substring(0,s.length()-1);
			return s;
		}
		public String supergraphIds() {
			String s = "";
			for(graph g : supergraphs) {
				s+=g.id +"_";
			}
			if(s.length() > 0) s=s.substring(0,s.length()-1);
			return s;
		}
		public String adjacencyMatrix() {
			String o="";
			for(int a=0;a<vertices;a++) {
				for(int b=0;b<vertices;b++) {
					if(G.getEdge(a,b) != null) o+="1";
					else o+="0";
				}
			}
			return o;
		}
		
		public static String adjacencyMatrix(SimpleGraph<Integer,DefaultEdge> G) {
			String o="";
			int vertices=G.vertexSet().size();
			for(int a=0;a<vertices;a++) {
				for(int b=0;b<vertices;b++) {
					if(G.getEdge(a,b) != null) o+="1";
					else o+="0";
				}
			}
			return o;
		}
		
		public static String adjacencyMatrixNotConcatenated(SimpleGraph<Integer,DefaultEdge> G) {
			String o="";
			int vertices=G.vertexSet().size();
			for(int a=0;a<vertices;a++) {
				for(int b=0;b<vertices;b++) {
					if(G.getEdge(a,b) != null) o+="1";
					else o+="0";
				}
				o+="\n";
			}
			return o;
		}
		
		// order vertices decreasing by degree
		public static SimpleGraph<Integer,DefaultEdge> orderVerticesByDegree(SimpleGraph<Integer,DefaultEdge> G) {
			ArrayList<Integer> ordering = new ArrayList<Integer>();
			for(int a=0;a < G.vertexSet().size();a++) {
				int indexOfMaxDegree = 0;
				for(int b=0;b < G.vertexSet().size();b++) {
					if(ordering.contains(indexOfMaxDegree) || (!ordering.contains(b) && G.degreeOf(b) > G.degreeOf(indexOfMaxDegree))) indexOfMaxDegree = b;
				}
				ordering.add(indexOfMaxDegree);
			}
			
			SimpleGraph<Integer,DefaultEdge> G2 = new SimpleGraph<Integer,DefaultEdge>(DefaultEdge.class);
			for(int a=0;a < G.vertexSet().size();a++) G2.addVertex(a);
			for(int a=0;a < G.vertexSet().size();a++) {
				for(int b=0;b < a;b++) {
					if(G.containsEdge(a,b)) G2.addEdge(ordering.indexOf(a),ordering.indexOf(b));
				}
			}
			return G2;
		}
		
		// order vertices randomly
		public static SimpleGraph<Integer,DefaultEdge> orderVerticesRandomly(SimpleGraph<Integer,DefaultEdge> G) {
			ArrayList<Integer> ordering = new ArrayList<Integer>();
			for(int a=0;a < G.vertexSet().size();a++) ordering.add(a);
			Collections.shuffle(ordering);
			
			SimpleGraph<Integer,DefaultEdge> G2 = new SimpleGraph<Integer,DefaultEdge>(DefaultEdge.class);
			for(int a=0;a < G.vertexSet().size();a++) G2.addVertex(a);
			for(int a=0;a < G.vertexSet().size();a++) {
				for(int b=0;b < a;b++) {
					if(G.containsEdge(a,b)) G2.addEdge(ordering.indexOf(a),ordering.indexOf(b));
				}
			}
			return G2;
		}
		
		// returns a random graph on n vertices with edge probability p
		public static SimpleGraph<Integer, DefaultEdge> randomGraph(int n, double p) {
			SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
			for(int a=0;a < n;a++) G.addVertex(a);
			for(int a=0;a < n;a++) {
				for(int b=a+1;b < n;b++) {
					if(Math.random() <= p) {
						G.addEdge(a,b);
					}
				}
			}
			return G;
		}
		
		// return max degree of a graph
		public static int maxDegree(SimpleGraph<Integer,DefaultEdge> G) {
			int Delta = 0;
			for(int v : G.vertexSet()) {
				Delta = Math.max(G.degreeOf(v),Delta);
			}
			return Delta;
		}

		
		// returns the inverse of a graph G
		public static SimpleGraph<Integer, DefaultEdge> inverseGraph(SimpleGraph<Integer, DefaultEdge> G) {
			SimpleGraph<Integer, DefaultEdge> G_inv = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
			int n=G.vertexSet().size();
			for(int a=0;a < n;a++) G_inv.addVertex(a);
			for(int a=0;a < n;a++) {
				for(int b=a+1;b < n;b++) {
					if(!G.containsEdge(a,b)) G_inv.addEdge(a,b); 
				}
			}
			return G_inv;
		}
		
		// flip edge in given graph
		public static void flipEdge(SimpleGraph<Integer, DefaultEdge> G, int e1, int e2) {
			if(G.containsEdge(e1,e2)) G.removeEdge(e1,e2);
			else G.addEdge(e1,e2);
		}
	}