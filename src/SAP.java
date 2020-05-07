import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class SAP {

    private Digraph g;

    private Iterable<Integer> vCached;
    private Iterable<Integer> wCached;
    private AncestorData ancestorDataCached;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Argument can't be null");

        g = new Digraph(G);
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

//        System.out.println("ansestor = " + sap.ancestor(13, 4));
//        System.out.println("len = " + sap.length(13, 4));
//        System.out.println("ansestor = " + sap.ancestor(13, 1));
//        System.out.println("len = " + sap.length(13, 1));

        //System.out.println("ansestor = " + sap.ancestor(34252, 29893));
        System.out.println("len = " + sap.length(5, 6));

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
