package practice;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.MinPQ;

import java.util.LinkedList;
import java.util.Queue;

public class Prims {

    private boolean[] visited;
    private Queue<Edge> edges;
    private MinPQ<Edge> pq;

    public Prims(EdgeWeightedGraph g) {
        visited = new boolean[g.V()];
        edges = new LinkedList<>();
        pq = new MinPQ<>();

        visit(g, 0);

        //while (edges.size() < g.V()-1) {
        while (!pq.isEmpty()) {
            Edge edge = pq.delMin();
            int v = edge.either();
            int w = edge.other(v);

            if (!visited[v] || !visited[w]) {
                edges.add(edge);

                if (!visited[v]) visit(g, v);
                if (!visited[w]) visit(g, w);
            }
        }
    }

    private void visit(EdgeWeightedGraph g, int v) {
        visited[v]  = true;

        Iterable<Edge> adj = g.adj(v);
        for (Edge edge : adj) {
            if (!visited[edge.other(v)])
                pq.insert(edge);
        }
    }

}
