package week1;

import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class SAP {

    private class AncestorData {
        int distance;
        int vertex;
    }

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
        //
        // // Check if graph has a single root
        // if (!reversedGraphBFS.hasPathTo(tail))
        //     throw new IllegalArgumentException("Graph is not a rooted DAG");
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == w) return 0;

        int ancestor = ancestor(v, w);
        if (ancestor == -1) return -1;

        int distToAncestor = reversedGraphBFS.distTo(ancestor);
        int distToV = reversedGraphBFS.distTo(v);
        int distToW = reversedGraphBFS.distTo(w);

        int length = distToV + distToW - 2*distToAncestor;

        return length;
    }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!reversedGraphBFS.hasPathTo(v) || !reversedGraphBFS.hasPathTo(w))
            return -1;

        if (v == w)
            return v;

        int[] vPath = reversedGraphBFS.pathToReversed(v);
        int[] wPath = reversedGraphBFS.pathToReversed(w);
        int commonAncestor = -1;
        int minPathLen = Math.min(vPath.length, wPath.length);

        for (int i = 0; i < minPathLen && vPath[i] == wPath[i]; i++) {
            commonAncestor = vPath[i];
        }

        return commonAncestor;
    }

    private AncestorData findAncestorData(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Argument can't be null");

        if (v == vCached && w == wCached) return ancestorDataCached;

        AncestorData ancestorData = new AncestorData();

        boolean[] visitedV = new boolean[g.V()];
        boolean[] visitedW = new boolean[g.V()];
        boolean[][] visited = new boolean[2][];
        visited[0] = visitedV;
        visited[1] = visitedW;

        int[] distToV = new int[g.V()];
        int[] distToW = new int[g.V()];
        int[][] distTo = new int[2][];
        distTo[0] = distToV;
        distTo[1] = distToW;

        for (int i = 0; i < g.V(); i++) {
            distToV[i] = Integer.MAX_VALUE;
            distToW[i] = Integer.MAX_VALUE;
        }

        Queue<Integer> qV = new Queue<>();
        Iterator<Integer> iterator = v.iterator();
        while (iterator.hasNext()) {
            Integer n = iterator.next();
            distToV[n] = 0;
            visitedV[n] = true;
            qV.enqueue(n);
        }

        Queue<Integer> qW = new Queue<>();
        iterator = w.iterator();
        while (iterator.hasNext()) {
            Integer n = iterator.next();

            // Ancestor is the vertex itself
            if (visitedV[n]) {
                ancestorData.vertex = n;
                ancestorData.distance = distToV[n];
                return ancestorData;
            }

            distToW[n] = 0;
            visitedW[n] = true;
            qW.enqueue(n);
        }

        Queue<Queue<Integer>> qq = new Queue<>();
        qq.enqueue(qV);
        qq.enqueue(qW);

        int ancestor = -1;
        int ancestorDist = Integer.MAX_VALUE;

        Queue<Integer> aq = new Queue<>();
        Queue<Integer> bq = new Queue<>();
        aq.enqueue(0);
        aq.enqueue(1);
        bq.enqueue(1);
        bq.enqueue(0);

        while (!qq.isEmpty()) {
            Queue<Integer> q = qq.dequeue();

            int a = aq.dequeue();
            int b = bq.dequeue();

            Queue<Integer> nQ = new Queue<>();
            while (!q.isEmpty()) {
                Integer vertex = q.dequeue();
                Iterable<Integer> adj = g.adj(vertex);


                for (Integer i : adj) {
                    if (!visited[a][i]) {
                        //if (!visited[a][i]) {
                        visited[a][i] = true;
                        distTo[a][i] = distTo[a][vertex] + 1;
                        nQ.enqueue(i);

                        // Check if in parallel bfs we found ancestor
                        if (visited[b][i]) {
                            // Calculate dist
                            int d = distTo[a][i] + distTo[b][i];
                            // Check if shortest ancestor found
                            if (d < ancestorDist) {
                                ancestor = i;
                                ancestorDist = d;
                            }
                        }
                    }
                }
            }
            if (!nQ.isEmpty()) {
                qq.enqueue(nQ);
                aq.enqueue(a);
                bq.enqueue(b);
            }
        }

        ancestorData.distance = ancestorDist;
        ancestorData.vertex = ancestor;

        vCached = v;
        wCached = w;
        ancestorDataCached = ancestorData;

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
        // List<Integer> v = Arrays.asList(13, 23, 24);
        // List<Integer> w = Arrays.asList(6, 16, 17);
        // System.out.println("__ansestor = " + sap.ancestor(v, w));
        // System.out.println("__a_length = " + sap.length(v, w));



        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
