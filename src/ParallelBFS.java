import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

// TODO can be implemented a more generalized cyclic structure with a 'ParallelBFS next' field
public class ParallelBFS extends BFS {

    private Digraph g;
    private AncestorData ancestorData;
    private Queue<Integer> queue;
    private int width;

    public ParallelBFS(Digraph g, Iterable<Integer> vertices, AncestorData ancestorData) {
        this.g = g;
        this.ancestorData = ancestorData;
        init(g);

        queue = new Queue<>();
        width = 0;

        for (Integer vertex : vertices) {
            validateVertex(vertex);
            visited[vertex] = true;
            distTo[vertex] = 0;
            queue.enqueue(vertex);
            width++;
        }
    }

    public void recursiveDoubleBFS(ParallelBFS parallelBFS) {
        int i = 0;

        while (width > 0) {
            Integer v = queue.dequeue();
            Iterable<Integer> adj = g.adj(v);
            for (Integer w : adj) {
                if (!visited[w]) {
                    distTo[w] = distTo[v] + 1;
                    visited[w] = true;
                    queue.enqueue(w);
                    i++;

                    if (parallelBFS.getVisited()[w]) {
                        // Calculate dist
                        int d = distTo[w] + parallelBFS.getDistTo()[w];

                        // Check if shortest ancestor found
                        if (d < ancestorData.distance) {
                            ancestorData.distance = d;
                            ancestorData.vertex = w;
                        }
                    }
                }
            }
            width--;
        }

        width = i;

        if (parallelBFS.getQueue().isEmpty()) {
            if (queue.isEmpty())
                return;
            recursiveDoubleBFS(parallelBFS);
        }

        parallelBFS.recursiveDoubleBFS(this);
    }

    public Integer checkVisited(Iterable<Integer> vertices) {
        for (Integer vertex : vertices) {
            if (visited[vertex]) return vertex;
        }
        return null;
    }

    public Queue<Integer> getQueue() {
        return queue;
    }
}
