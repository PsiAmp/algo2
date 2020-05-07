package week1;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class BFS {

    private boolean[] visited;
    private int[] pathTo;
    private int[] distTo;

    public BFS(Digraph g, int vertex) {
        visited = new boolean[g.V()];
        pathTo = new int[g.V()];
        distTo = new int[g.V()];
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }

        bfs(g, vertex);
    }

    public BFS(Digraph g, Iterable<Integer> vertices) {
        visited = new boolean[g.V()];
        pathTo = new int[g.V()];
        distTo = new int[g.V()];
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }

        bfs(g, vertices);
    }

    private void bfs(Digraph g, int vertex) {
        Queue<Integer> q = new Queue<>();

        q.enqueue(vertex);
        distTo[vertex] = 0;
        visited[vertex] = true;

        while (!q.isEmpty()) {
            Integer v = q.dequeue();
            Iterable<Integer> adjacentVertices = g.adj(v);

            for (Integer w : adjacentVertices) {
                if (!visited[w]) {
                    pathTo[w] = v;
                    distTo[w] = distTo[v]+1;
                    visited[w] = true;
                    q.enqueue(w);
                }
            }
        }
    }

    private void bfs(Digraph g, Iterable<Integer> vertices) {
        Queue<Integer> q = new Queue<>();

        for (Integer vertex : vertices) {
            q.enqueue(vertex);
            distTo[vertex] = 0;
            visited[vertex] = true;
        }

        while (!q.isEmpty()) {
            Integer v = q.dequeue();
            Iterable<Integer> adjacentVertices = g.adj(v);

            for (Integer w : adjacentVertices) {
                if (!visited[w]) {
                    pathTo[w] = v;
                    distTo[w] = distTo[v]+1;
                    visited[w] = true;
                    q.enqueue(w);
                }
            }
        }
    }

    public boolean hasPathTo(Integer v) {
        return visited[v];
    }

    public int distTo(int v) {
        return distTo[v];
    }

    public int[] pathTo(int v) {
        if (!hasPathTo(v)) return null;

        int[] path = new int[distTo[v]+1];

        for (int i = 0; i < path.length; i++) {
            path[i] = v;
            v = pathTo[v];
        }
        path[path.length-1] = v;

        return path;
    }

    public int[] pathToReversed(int v) {
        if (!hasPathTo(v)) return null;

        int[] path = new int[distTo[v]+1];

        for (int i = path.length - 1; i >= 0; i--) {
            path[i] = v;
            v = pathTo[v];
        }
        path[path.length-1] = v;

        return path;
    }

    public boolean[] getVisited() {
        return visited;
    }

    public int[] getPathTo() {
        return pathTo;
    }

    public int[] getDistTo() {
        return distTo;
    }

    public static void main(String[] args) {

    }
}
