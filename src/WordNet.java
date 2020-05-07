import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordNet {

    // TODO maybe change to a simple List
    // Map containing synset nouns aa a key and a list of synset ids where this noun appears
    private HashMap<String, List<Integer>> nounToId;

    // Map containing synset ids as a key and a list of nouns associated with this id
    private HashMap<Integer, String> synsetIdToWord;
    private Digraph wordNetGraph;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("Argument can't be null");
        parseSynsets(new In(synsets));
        parseHypernyms(new In(hypernyms));

        // TODO check if wordNetGraph is a rooted DAG
        if (!new Topological(wordNetGraph).hasOrder()) throw new IllegalArgumentException("WordNet graph is not a DAG");

        sap = new SAP(wordNetGraph);
    }

    private void parseSynsets(In synsetsInput) {
        nounToId = new HashMap<>();
        synsetIdToWord = new HashMap<>();


        while (synsetsInput.hasNextLine()) {
            String line = synsetsInput.readLine();
            // Split synset line
            String[] synsetSplit = line.split(",");

            // Get ID
            Integer synsetId = Integer.valueOf(synsetSplit[0]);

            // Get sysnset word that consist ofmultiple nouns
            String synsetWord = synsetSplit[1];

            // Split nouns from synset word
            String[] nouns = synsetWord.split(" ");

            for (String noun : nouns) {
                // Add each noun in the HashMap where noun is a key and synset id is a value
                // If there's no such noun in the map, create <noun, synsetIds> entry
                if (!nounToId.containsKey(noun)) {
                    List<Integer> synsetIds = new ArrayList<>();
                    synsetIds.add(synsetId);
                    nounToId.put(noun, synsetIds);
                } else {
                    List<Integer> synsetIds = nounToId.get(noun);
                    synsetIds.add(synsetId);
                }
            }
            // Add entry to synsetIdToWord
            synsetIdToWord.put(synsetId, synsetWord);
        }
    }

    private void parseHypernyms(In hypernymsInput) {
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        int vertices = 0;


        while (hypernymsInput.hasNextLine()) {
            String line = hypernymsInput.readLine();
            String[] ids = line.split(",");
            int id = Integer.parseInt(ids[0]);
            for (int i = 1; i < ids.length; i++) {
                a.add(id);
                b.add(Integer.parseInt(ids[i]));
            }
            vertices++;
        }

        wordNetGraph = new Digraph(vertices);
        for (int i = 0; i < a.size(); i++) {
            wordNetGraph.addEdge(a.get(i), b.get(i));
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("Argument can't be null");

        return nounToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Argumant is not a WordNet noun");

        List<Integer> aIds = nounToId.get(nounA);
        List<Integer> bIds = nounToId.get(nounB);
        return sap.length(aIds, bIds);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Argument is not a WordNet noun");

        List<Integer> aIds = nounToId.get(nounA);
        List<Integer> bIds = nounToId.get(nounB);
        return synsetIdToWord.get(sap.ancestor(aIds, bIds));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("lib\\synsets.txt", "lib\\hypernyms.txt");
        wordNet.isNoun("word");
        String sap = wordNet.sap("worm", "bird");
        System.out.println(sap);
        System.out.println(wordNet.isNoun("zebra"));
        System.out.println(wordNet.isNoun("table"));

        int distance = wordNet.distance("zebra", "table");
        System.out.println(distance);
    }
}