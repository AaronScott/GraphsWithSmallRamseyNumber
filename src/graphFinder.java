import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


//this class's functions continuously extend the poset of unlabeled graphs without isolated vertices.
// 

// n | # of graphs without isolated vertices
// 2	1
// 3	2
// 4	7
// 5	23
// 6	122
// 7	888
// 8	

// what graphs on n vertices can't have ramsey number <= 11?
// probabilistic method: E(# m.c. copies) <= 11*10*...*(11-n+1)*2*2^{-e(G)}
// => 11*10*...*(11-n+1) >= 2^{e(G)-1}
//
// vertices | max edges s.t. r can be <= 11
// 8			23
// 9			25
// 10			26
// 11			26
// 12			0

public class graphFinder {
	
	int max_id = 0;
	public ArrayList<graph> graphs[][] = new ArrayList[graph.largestColoringWeWillEverConsider+1][];
	public ArrayList<graph> allGraphs = new ArrayList<graph>();
	
	public static void main(String[] args) {
		new graphFinder();
	}
	
	public graphFinder() {
		//findAllGraphs();
		
		//writeGraphs();
		//readGraphs();
	}
	
	public void findAllGraphsUpToSevenVertices() {
		int id = 1;
		// no graph with id 0
		allGraphs.add(new graph(""));
		graphs[1] = new ArrayList[1];
		graphs[1][0] = new ArrayList<graph>();
		graphs[2] = new ArrayList[2];
		graphs[2][0] = new ArrayList<graph>();
		graphs[2][1] = new ArrayList<graph>(); 
		SimpleGraph<Integer,DefaultEdge> G1 = new SimpleGraph<Integer,DefaultEdge>(DefaultEdge.class);
		G1.addVertex(0);
		G1.addVertex(1);
		G1.addEdge(0,1);
		graph g1 = new graph(G1);
		g1.id = id;
		id++;
		graphs[2][1].add(g1);
		allGraphs.add(g1);
		for(int vxs=3;vxs < 8;vxs++) {
			graphs[vxs] = new ArrayList[vxs*(vxs-1)/2+1];
			for(int edges=0;edges <= vxs*(vxs-1)/2;edges++) {
				graphs[vxs][edges]=new ArrayList<graph>();
				if(edges > 0) {
					// add an edge to every graph with the same number vertices but one less edge
					for(graph g : graphs[vxs][edges-1]) {
						SimpleGraph<Integer,DefaultEdge> Graph = g.G;
						for(int v1=0;v1 < vxs;v1++) {
							for(int v2=0;v2 < v1;v2++) {
								if(!Graph.containsEdge(v1,v2)) {
									SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
									G.addEdge(v1,v2);
									boolean isNew = true;
									for(graph otherGraph : graphs[vxs][edges]) {									
										if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
											if(!g.supergraphs.contains(otherGraph)) {
												otherGraph.subgraphs.add(g);
												g.supergraphs.add(otherGraph);
											}
											isNew=false;
										}
									}
									if(isNew) {
										graph newg = new graph(graph.orderVerticesByDegree(G));
										newg.id=id;
										id++;
										g.supergraphs.add(newg);
										newg.subgraphs.add(g);
										graphs[vxs][edges].add(newg);
										allGraphs.add(newg);
									}
								}
							}
						}
					}
					
					// add a pendent edge to every graph on one less vertex with one less edge
					if(edges-1 <= (vxs-1)*(vxs-2)/2) {
						for(graph g : graphs[vxs-1][edges-1]) {
							SimpleGraph<Integer,DefaultEdge> Graph = g.G;
							for(int v=0;v < vxs-1;v++) {
								SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
								G.addVertex(vxs-1);
								G.addEdge(v,vxs-1);
								boolean isNew = true;
								for(graph otherGraph : graphs[vxs][edges]) {
									if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
										if(!g.supergraphs.contains(otherGraph)) {
											otherGraph.subgraphs.add(g);
											g.supergraphs.add(otherGraph);
										}
										isNew=false;
									}
								}
								if(isNew) {
									graph newg = new graph(graph.orderVerticesByDegree(G));
									newg.id=id;
									id++;
									g.supergraphs.add(newg);
									newg.subgraphs.add(g);
									graphs[vxs][edges].add(newg);
									allGraphs.add(newg);
								}
							}
						}
					}

					// add a K2 to every graph on two less vertices with one less edge
					if(edges-1 <= (vxs-2)*(vxs-3)/2) {
						for(graph g : graphs[vxs-2][edges-1]) {
							SimpleGraph<Integer,DefaultEdge> Graph = g.G;
							SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
							G.addVertex(vxs-2);
							G.addVertex(vxs-1);
							G.addEdge(vxs-2,vxs-1);
							boolean isNew = true;
							for(graph otherGraph : graphs[vxs][edges]) {
								if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
									if(!g.supergraphs.contains(otherGraph)) {
										otherGraph.subgraphs.add(g);
										g.supergraphs.add(otherGraph);
									}
									isNew=false;
								}
							}
							if(isNew) {
								graph newg = new graph(graph.orderVerticesByDegree(G));
								newg.id=id;
								id++;
								g.supergraphs.add(newg);
								newg.subgraphs.add(g);
								graphs[vxs][edges].add(newg);
								allGraphs.add(newg);
							}
						}
					}
				}
			}
		}
	}
	
	public void findNewGraphs(int vxs, int edges) {		
		if(graphs[vxs] == null) graphs[vxs] = new ArrayList[vxs*(vxs-1)/2+1];
    	if(graphs[vxs][edges] != null) {
    		System.out.println("graphs on " +vxs +" vertices, " +edges +" edges were already searched! aborting for danger of overwriting data.");
    		return;
    	}
    	else {
    		graphs[vxs][edges] = new ArrayList<graph>();
    		
    		if(edges > 0) {
				// add an edge to every graph with the same number vertices but one less edge
    			if(edges-1 >= Math.ceil((double)vxs/2)) {
    				if(graphs[vxs][edges-1] == null) {
    					System.out.println("graphs on " +vxs +" vertices, " +(edges-1) +" edges were not yet searched. aborting since continuing would result in incomplete data");
    					return;
    				}
    				else {
						for(graph g : graphs[vxs][edges-1]) {
							if(g.lowerBound <= graph.largestColoringWeWillEverConsider) {
								SimpleGraph<Integer,DefaultEdge> Graph = g.G;
								for(int v1=0;v1 < vxs;v1++) {
									for(int v2=0;v2 < v1;v2++) {
										if(!Graph.containsEdge(v1,v2)) {
											SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
											G.addEdge(v1,v2);
											boolean isNew = true;
											for(graph otherGraph : graphs[vxs][edges]) {										
												if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
													if(!g.supergraphs.contains(otherGraph)) {
														otherGraph.subgraphs.add(g);
														g.supergraphs.add(otherGraph);
													}
													isNew=false;
												}
											}
											if(isNew) {
												graph newg = new graph(graph.orderVerticesByDegree(G));
												max_id++;
												newg.id=max_id;
												newg.lowerBound = g.lowerBound;
												newg.lowerBoundConstruction = g.lowerBoundConstruction;
												g.supergraphs.add(newg);
												newg.subgraphs.add(g);
												graphs[vxs][edges].add(newg);
												allGraphs.add(newg);
											}
										}
									}
								}
							}
						}
    				}
    			}
				
				// add a pendent edge to every graph on one less vertex with one less edge
				if(edges-1 <= (vxs-1)*(vxs-2)/2 && edges-1 >= Math.ceil(((double)vxs-1)/2)) {
					if(graphs[vxs-1][edges-1] == null) {
    					System.out.println("graphs on " +(vxs-1) +" vertices, " +(edges-1) +" edges were not yet searched. aborting since continuing would result in incomplete data");
    					return;
    				}
    				else {
						for(graph g : graphs[vxs-1][edges-1]) {
							if(g.lowerBound <= graph.largestColoringWeWillEverConsider) {
								SimpleGraph<Integer,DefaultEdge> Graph = g.G;
								for(int v=0;v < vxs-1;v++) {
									SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
									G.addVertex(vxs-1);
									G.addEdge(v,vxs-1);
									boolean isNew = true;
									for(graph otherGraph : graphs[vxs][edges]) {
										if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
											if(!g.supergraphs.contains(otherGraph)) {
												otherGraph.subgraphs.add(g);
												g.supergraphs.add(otherGraph);
											}
											isNew=false;
										}
									}
									if(isNew) {
										graph newg = new graph(graph.orderVerticesByDegree(G));
										max_id++;
										newg.id=max_id;
										newg.lowerBound = g.lowerBound;
										newg.lowerBoundConstruction = g.lowerBoundConstruction;
										g.supergraphs.add(newg);
										newg.subgraphs.add(g);
										graphs[vxs][edges].add(newg);
										allGraphs.add(newg);
									}
								}
							}
						}
    				}
				}

				// add a K2 to every graph on two less vertices with one less edge
				if(edges-1 <= (vxs-2)*(vxs-3)/2 && edges-1 >= Math.ceil(((double)vxs-2)/2)) {
					if(graphs[vxs-2][edges-1] == null) {
    					System.out.println("graphs on " +(vxs-2) +" vertices, " +(edges-1) +" edges were not yet searched. aborting since continuing would result in incomplete data");
    					return;
    				}
    				else {
						for(graph g : graphs[vxs-2][edges-1]) {
							if(g.lowerBound <= graph.largestColoringWeWillEverConsider) {
								SimpleGraph<Integer,DefaultEdge> Graph = g.G;
								SimpleGraph<Integer,DefaultEdge> G = (SimpleGraph<Integer, DefaultEdge>) Graph.clone();
								G.addVertex(vxs-2);
								G.addVertex(vxs-1);
								G.addEdge(vxs-2,vxs-1);
								boolean isNew = true;
								for(graph otherGraph : graphs[vxs][edges]) {
									if(subgraphIsomorphism.VF2(G,otherGraph.G)) {
										if(!g.supergraphs.contains(otherGraph)) {
											otherGraph.subgraphs.add(g);
											g.supergraphs.add(otherGraph);
										}
										isNew=false;
									}
								}
								if(isNew) {
									graph newg = new graph(graph.orderVerticesByDegree(G));
									max_id++;
									newg.id=max_id;
									newg.lowerBound = g.lowerBound;
									newg.lowerBoundConstruction = g.lowerBoundConstruction;
									g.supergraphs.add(newg);
									newg.subgraphs.add(g);
									graphs[vxs][edges].add(newg);
									allGraphs.add(newg);
								}
							}
						}
    				}
				}
			}
    	}
    	System.out.println("found " +graphs[vxs][edges].size() +" graphs on " +vxs +" vertices, " +edges +" edges");
	}
	
	// save graphs
	public void writeGraphs() {
		/*	structure of graphs.txt
		*	
		*	graph_id#number_of_vertices_#number_of_edges
		*	adjacency matrix with rows concatenated
		*	lower bound on r
		*	adjacency matrix of lower bound construction
		*	upper bound on r
		*   id of super graph with equal r, or 0 if bound was determined via brute force
		*   subgraph_id_1#subgraph_id_2#...#subgraph_id_last		[subgraphs with exactly one less edge]
		*   supergraph_id_1#supergraph_id_2#...#supergraph_id_last	[supergraphs with exactly one more edge]
		*   
		*/ 	
		PrintWriter writer;
		try {
			//writer = new PrintWriter("graphs_" +System.currentTimeMillis() +".txt", "UTF-8");			
			writer = new PrintWriter("graphs.txt", "UTF-8");
			for(int a=2;a < graphs.length;a++) {
				if(graphs[a] != null) {
					for(int b=0;b <= a*(a-1)/2;b++) {
						if(graphs[a][b] != null) {
							for(graph g : graphs[a][b]) {
								writer.println(g.graphInfo());
								writer.println(g.adjacencyMatrix());
								writer.println(g.lowerBound);
								writer.println(g.lowerBoundConstruction);
								writer.println(g.upperBound);
								writer.println(g.upperBoundReason);
								writer.println(g.subgraphIds());
								writer.println(g.supergraphIds());
							}
						}
					}		
				}
			}	
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//load graphs from file
	public void readGraphs() {
		try (BufferedReader file = new BufferedReader(new FileReader("graphs.txt"))) {
		    String line;
		    // no graph with id 0
		    allGraphs.add(new graph(""));
		    while ((line = file.readLine()) != null) {
		    	int id = Integer.parseInt(line.split("#")[0]);
		    	max_id = Math.max(id,max_id);
		    	int vxs = Integer.parseInt(line.split("#")[1]);
		    	int edges = Integer.parseInt(line.split("#")[2]);
		    	if(graphs[vxs] == null) graphs[vxs] = new ArrayList[vxs*(vxs-1)/2+1];
		    	if(graphs[vxs][edges] == null) graphs[vxs][edges] = new ArrayList<graph>();
		    	graph g = new graph(id,file.readLine());
		    	g.lowerBound = Integer.parseInt(file.readLine());
		    	g.lowerBoundConstruction = file.readLine();
		    	g.upperBound = Integer.parseInt(file.readLine());
		    	g.upperBoundReason = Integer.parseInt(file.readLine());
		    	String[] subgraphIds = file.readLine().split("_");
		    	if(!subgraphIds[0].isEmpty()) {
		    		for(String s : subgraphIds) {
		    			graph subGraph = allGraphs.get(Integer.parseInt(s));
		    			g.subgraphs.add(subGraph);
		    			subGraph.supergraphs.add(g);
		    		}
		    	}
		    	String[] supergraphIds = file.readLine().split("_");
		    	//if(!supergraphIds[0].isEmpty()) {for(String s : supergraphIds) g.supergraphIds.add(Integer.parseInt(s));}
		    	graphs[vxs][edges].add(g);
		    	allGraphs.add(g);
		    }
		    
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
