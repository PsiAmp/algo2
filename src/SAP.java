import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SAP {

    private Digraph g;
    private BFS reversedGraphBFS;

    private Iterable<Integer> vCached;
    private Iterable<Integer> wCached;
    private AncestorData ancestorDataCached;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Argument can't be null");

        g = new Digraph(G);
        bfs(g);
    }

    private void bfs(Digraph graph) {
        Digraph reverseGraph = graph.reverse();
        DepthFirstOrder dfo = new DepthFirstOrder(reverseGraph);

        // Find head using DFO. Last vertex in reverse post will be a head of the graph
        Iterator<Integer> iterator = dfo.reversePost().iterator();
        Integer head = iterator.next();
        Integer tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }

        // Do a BFS on a reversed graph to find distances and paths from head to vertices
        reversedGraphBFS = new BFS(reverseGraph, head);

        // Check if graph has a single root
        if (!reversedGraphBFS.hasPathTo(tail)) {
            reversedGraphBFS = null;
        }
    }

    public int length(int v, int w) {
        return findAncestorData(Arrays.asList(v), Arrays.asList(w)).distance;
    }

    public int ancestor(int v, int w) {
        return findAncestorData(Arrays.asList(v), Arrays.asList(w)).vertex;
    }

    private AncestorData findAncestorData(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Argument can't be null");

        if (v == vCached && w == wCached) return ancestorDataCached;

        AncestorData ancestorData = new AncestorData();
        ancestorData.vertex = -1;
        ancestorData.distance = Integer.MAX_VALUE;

        ancestorDataCached = ancestorData;
        vCached = v;
        wCached = w;

        ParallelBFS bfsV = new ParallelBFS(g, v, ancestorData);
        ParallelBFS bfsW = new ParallelBFS(g, w, ancestorData);

        Integer visitedVertex = bfsV.checkVisited(w);
        if (visitedVertex != null) {
            ancestorData.vertex = visitedVertex;
            ancestorData.distance = 0;
            return ancestorData;
        }

        bfsV.recursiveDoubleBFS(bfsW);

        return ancestorData;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findAncestorData(v, w).distance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findAncestorData(v, w).vertex;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        //sap.length()

        // System.out.println("ansestor = " + sap.ancestor(13, 1));
        // System.out.println("len = " + sap.length(13, 1));
        //
        List<Integer> v = Arrays.asList(13, 23, 24);
        List<Integer> w = Arrays.asList(16, 17, 6);
        System.out.println("__ansestor = " + sap.ancestor(v, w));
        System.out.println("__a_length = " + sap.length(v, w));

        System.out.println("a = " + sap.ancestor(13,18));
        System.out.println("d = " + sap.length(13,24));


//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            int w = StdIn.readInt();
//            int length   = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }
    }
}
