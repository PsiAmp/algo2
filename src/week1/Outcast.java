package week1;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int dist[] = new int[nouns.length];

        for (int i = 0; i < nouns.length; i++) {
            for (int j = i+1; j < nouns.length; j++) {
                int distance = wordnet.distance(nouns[i], nouns[j]);
                dist[i] += distance;
                dist[j] += distance;
            }
        }

        int max = 0;
        for (int i = 1; i < dist.length; i++) {
            if (dist[i] > dist[max])
                max = i;
        }

        return nouns[max];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);

        // String[] nouns = {"horse", "zebra", "cat", "bear", "table"};
        // System.out.println(outcast.outcast(nouns));

        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
