import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.Arrays;
import java.util.Iterator;

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

        System.out.println("HAS ORDR = " + new Topological(g).hasOrder());
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
        if (reversedGraphBFS != null)
            return findAncestorData(v, w).distance;
        return findAncestorData(Arrays.asList(v), Arrays.asList(w)).distance;
    }

    public int ancestor(int v, int w) {
        if (reversedGraphBFS != null)
            return findAncestorData(v, w).vertex;
        return findAncestorData(Arrays.asList(v), Arrays.asList(w)).vertex;
    }

    private int vC = -1;
    private int wC = -1;
    private AncestorData ancestorDataC;

    private AncestorData findAncestorData(int v, int w) {
        if (v == vC && w == wC) return ancestorDataC;

        reversedGraphBFS.validateVertex(v);
        reversedGraphBFS.validateVertex(w);

        if (v == w) return new AncestorData(v, 0);

        AncestorData ancestorData = new AncestorData(-1, -1);

        int[] pathToV = reversedGraphBFS.pathToReversed(v);
        int[] pathToW = reversedGraphBFS.pathToReversed(w);

        int minLen = Math.min(pathToV.length, pathToW.length);

        for (int i = 0; i < minLen && pathToV[i] == pathToW[i]; i++) {
            ancestorData.vertex = pathToV[i];
        }

        ancestorData.distance = reversedGraphBFS.distTo(v) + reversedGraphBFS.distTo(w) - reversedGraphBFS.distTo[ancestorData.vertex] * 2;

        ancestorDataC = ancestorData;
        vC = v;
        wC = w;

        return ancestorData;
    }

    private AncestorData findAncestorData(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Argument can't be null");

        if (v == vCached && w == wCached) return ancestorDataCached;

        AncestorData ancestorData = new AncestorData(-1, Integer.MAX_VALUE);

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

        if (ancestorData.vertex == -1) {
            ancestorData.distance = -1;
        }

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

        System.out.println("ansestor = " + sap.ancestor(13, 4));
        System.out.println("len = " + sap.length(13, 4));
        System.out.println("ansestor = " + sap.ancestor(13, 1));
        System.out.println("len = " + sap.length(13, 1));

//        List<Integer> v = Arrays.asList(0, 7, 9, 12, -1);
//        List<Integer> w = Arrays.asList(1, 2, 4, 5, 10);
//        System.out.println("__ansestor = " + sap.ancestor(v, w));
        //System.out.println("__a_length = " + sap.length(v, w));

//        System.out.println("a = " + sap.ancestor(2,6));
//        System.out.println("d = " + sap.length(2,6));


//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            int w = StdIn.readInt();
//            int length   = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }
    }
}
