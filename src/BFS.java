import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class BFS {

    boolean[] visited;
    int[] pathTo;
    int[] distTo;

    public BFS() {
    }

    public BFS(Digraph g, int vertex) {
        init(g);
        bfs(g, vertex);
    }

    public BFS(Digraph g, Iterable<Integer> vertices) {
        init(g);
        bfs(g, vertices);
    }

    public void init(Digraph g) {
        visited = new boolean[g.V()];
        pathTo = new int[g.V()];
        distTo = new int[g.V()];
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }
    }

    private void bfs(Digraph g, int vertex) {
        validateVertex(vertex);

        Queue<Integer> q = new Queue<>();

        q.enqueue(vertex);
        distTo[vertex] = 0;
        visited[vertex] = true;

        bfs(g, q);
    }

    private void bfs(Digraph g, Iterable<Integer> vertices) {
        Queue<Integer> q = new Queue<>();

        for (int vertex : vertices) {
            validateVertex(vertex);
            q.enqueue(vertex);
            distTo[vertex] = 0;
            visited[vertex] = true;
        }

        bfs(g, q);
    }

    private void bfs(Digraph g, Queue<Integer> q) {
        while (!q.isEmpty()) {
            int v = q.dequeue();
            Iterable<Integer> adjacentVertices = g.adj(v);

            for (int w : adjacentVertices) {
                if (!visited[w]) {
                    pathTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    visited[w] = true;
                    q.enqueue(w);
                }
            }
        }
    }

    public void validateVertex(Integer vertex) {
        if (vertex == null || vertex < 0 || vertex > visited.length - 1)
            throw new IllegalArgumentException("Vertex " + vertex + " is not in range of a Graph");
    }

    public boolean hasPathTo(int v) {
        return visited[v];
    }

    public int distTo(int v) {
        return distTo[v];
    }

    public int[] pathTo(int v) {
        if (!hasPathTo(v)) return null;

        int[] path = new int[distTo[v] + 1];

        for (int i = 0; i < path.length; i++) {
            path[i] = v;
            v = pathTo[v];
        }
        path[path.length - 1] = v;

        return path;
    }

    public int[] pathToReversed(int v) {
        if (!hasPathTo(v)) return null;

        int[] path = new int[distTo[v] + 1];

        for (int i = path.length - 1; i >= 0; i--) {
            path[i] = v;
            v = pathTo[v];
        }

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
