
/*
*	Alice Gibbons
*	Fall 2015
*/

import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

class StronglyConnectedComponents{
	private static Stack <Integer> st;
	private static int index;
	private static Digraph graph;
	private static List<List<Integer>> sccList;

	public static int electionResult(){
		int electionOutcome = 1;
		int counter = 0;
        int vertex = -1;
        for(List<Integer> scc : sccList){
        	counter++;
       		for(int i = 0; i < scc.size() - 1 ; i++){
       			Integer v = (Integer) scc.get(i);

       			if(v%2 == 0){		//negative 
        			vertex = (v + 2) / 2;
        			if(scc.subList(i+1, scc.size()).contains(v+1)){	//if a vertex and its negation in a scc then outcome = 0
        				electionOutcome = 0; 
    					return(electionOutcome);
        			}

        		} else {			//positive
        			vertex = (v+1) / 2;
        			if(scc.subList(i+1, scc.size()).contains(v-1)){	//if a vertex and its negation in a scc then outcome = 0
        				electionOutcome = 0; 
    					return(electionOutcome);
        			}
        		}
       		}
		}
		return(electionOutcome);
	}

	//based on <https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm>
	public static void tarjan(){	
  		st = new Stack <Integer>();
		index = 0;
		sccList = new ArrayList<List<Integer>>();				
	  	for(int v = 0; v < graph.V; v++){
	  		if(graph.index[v] < 0){			//is "undefined" (-1)
	  			strongConnect(v);
	  		} 
	  	}
	  }

	  public static void strongConnect(int v){	// Set the depth index for v to the smallest unused index
	  	graph.index[v] = index;
	  	graph.lowlink[v] = index;
	  	index = index + 1;
	  	st.push((Integer)v);
	  	graph.onStack[v] = true;
		Iterator<Integer> itr = graph.adj(v);

		// Consider successors of v
		while(itr.hasNext()){
		//for(Integer w : adj){
			Integer w = (Integer) itr.next();
			if(graph.index[w] < 0){				// Successor w has not yet been visited; recurse on it
				strongConnect(w);
				graph.lowlink[v] = Math.min(graph.lowlink[v], graph.lowlink[w]);
			} else if(graph.onStack[w]) {		// Successor w is in stack S and hence in the current SCC
				graph.lowlink[v] = Math.min(graph.lowlink[v], graph.index[w]);
			}
		}

		Integer w;
		if(graph.lowlink[v] == graph.index[v]){		// If v is a root node, pop the stack and generate an SCC
			//start a new strongly connected component
			List<Integer> scc = new ArrayList<Integer>();

			do{
				w = (Integer) st.pop();
				graph.onStack[w] = false;
				//add w to scc
				scc.add(w);

			} while (v != w);		//can compare directly because of unboxing
			//output scc
			sccList.add(scc);
		}
	  }

	public static void createGraph(int numPairs, int highestNum, Scanner scan){
		int pair[] = new int [2];
		int signs[] = new int[2];
		int oppSigns[] = new int[2];

		//create adj list array
		graph = new Digraph(2*highestNum);

		for(int i = 0; i < numPairs; i++){

			for(int j = 0; j < 2; j++){	//get two numbers and their signs
				String temp = scan.next();
				if(temp.matches("\\+\\d+")){	//positive int 
					pair[j] = Integer.parseInt(temp.replaceAll("[\\D]", ""));
					signs[j] = 1;
					oppSigns[j] = 2;
				} else {		//negative int
					pair[j] = Integer.parseInt(temp.replaceAll("[\\D]", ""));
					signs[j] = 2;
					oppSigns[j] = 1;
				}
			}
			//for vertices +i, +j, add an edge from +i -> -j, +j -> -i
			graph.addEdge(2*pair[0]-signs[0], 2*pair[1]-oppSigns[1]);
			graph.addEdge(2*pair[1]-signs[1], 2*pair[0]-oppSigns[0]);
		}
	}

	public static void main(String args[]) throws IOException{
		final long startTime = System.currentTimeMillis();
		String filename;
		if(args[0] != null){
			filename = args[0];
		} else {
			System.out.println("Usage: StronglyConnectedComponents inputfile.txt");
			return;
		}

		Scanner scan = null;
		int numPairs = -1;
		int highestNum = -1;
		int electionOutcome = -1;

		//read file in line by line
	    try {
	        scan = new Scanner(new BufferedReader(new FileReader(filename)));
			while (scan.hasNextInt()) {
				if(numPairs < 0){
		        	numPairs = Integer.parseInt(scan.next());
		        	highestNum = Integer.parseInt(scan.next());
		        }

		        //create graph using data
		        createGraph(highestNum, numPairs, scan);
		        //call scc alg
		        tarjan();

		       	//determine election output
		       	electionOutcome = electionResult();
		       	System.out.println(electionOutcome);

		        //do next graph
		        numPairs = -1;
		        highestNum = -1;
	    	}

	    } finally {
	        if (scan != null) {
	            scan.close();
	        }
	    }
	    final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime) );
	}
}

class Digraph {
	public int V;
    private List<List<Integer>> adj;    // adj[v] = adjacency list for vertex v
	public int[] index;				//for tarjans
	public int[] lowlink;
	public boolean[] onStack;
	
	public Digraph(int V) {
		this.V = V;
		index = new int[V];		
		lowlink = new int[V];
		onStack = new boolean[V];
        adj = new ArrayList<List<Integer>>();
		for (int v = 0; v < V; v++){
			List<Integer> l = new ArrayList<Integer>();
            adj.add(l);
            index[v] = -1;
            onStack[v] = false;
		}
	}

	//adds edge from v-> w
	public void addEdge(int v, int w) {
		adj.get(v).add(w);
	}

    //list of vertices vertex v points to
	public Iterator<Integer> adj(int v) {
		return adj.get(v).iterator();
	}
}