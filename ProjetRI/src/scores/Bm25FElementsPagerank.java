package scores;

import index.Index;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import serialization.IndexDeserialization;

/**
 * Class Bm25FElements
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25FElementsPagerank extends ScoreElements{

    private double k1;
    private double b;
    
    private String precision;
    
    /*
     * alphas(0) : article
     * alphas(1) : header
     * alphas(2) : title
     * alphas(3) : body
     * alphas(4) : sec
     * alphas(5) : p
     * 
     */
    private ArrayList<Double> alphas;
    
     public Bm25FElementsPagerank(String request, Index index, double k1, double b, String precision, ArrayList<Double> alphas) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
        this.precision = precision;
        this.alphas = alphas ;
    }

    public Map<String, Map<String, Double>> getScores() {

        Map<String, Map<String, Double>> scores = new HashMap<>();

        String documentNumber;

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }

        return scores;

    }

    public Map<String, Double> getRequestScore(String documentNumber) {
        Map<String, Double> pathsScores = new HashMap<>();
        Bm25FElementsPagerank.PathsCouple pathsCouple;
        double score = 0;
        
        if(this.index.getPagerank().get(documentNumber)==null)
            this.pagerank = 1;
        else
            this.pagerank = this.index.getPagerank().get(documentNumber).size();
        
        // optimisation
        int dl = 0;
        int N = 0;
        double avdl = 0;

        String p;
        //For each word of request
        for (String word : this.request.split("\\W")) {
            //System.out.println("word : "+word);
            //For each path of the current word
            if (index.getCollectionData().get(word) != null) {
                //If tf !=0
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    //Generate all compressed paths
                    pathsCouple = generatePathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (int i = 0; i < pathsCouple.compressedPaths.size(); i++) {
                        p = pathsCouple.compressedPaths.get(i);
                        try {
                            dl = index.getDl(documentNumber, p);

                        } catch (NullPointerException e) {
                            dl = 1;
                        }
                        N = index.getN(p);
                        avdl = index.getAvdl(p);
                        score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, pathsCouple.originalPaths.get(i)), dl, N, avdl, p);
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(pathsCouple.originalPaths.get(i), score);
                        }
                    }
                }
            }
        }
        return pathsScores;
    }

    public double getDocumentWordScore(String word, float tf, int dl, int N, double avdl, String path) {

        int df = getDocumentFrequency(index, word, precision);

        if (df == -1) {
            return 0;
        }
        
        double alpha = this.alphas.get(path.split("/").length-1);

        double wtd = ((alpha*tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / avdl)) + alpha*tf)) 
                * Math.log((N - df + 0.5) / (df + 0.5))
                * Math.log(1 + this.pagerank);

        return wtd;
    }

    private Bm25FElementsPagerank.PathsCouple generatePathsList(ArrayList<String> paths) {

        boolean isInArticle = false;
        boolean isInHeader = false;
        boolean isInTitle = false;
        boolean isInBody = false;

        Bm25FElementsPagerank.PathsCouple pathsCouple = new Bm25FElementsPagerank.PathsCouple();

        String[] splitedPath;
        String s;
        String sEnd;
        for (String p : paths) {
            splitedPath = p.split("/");
            isInArticle = false;
            isInHeader = false;
            isInBody = false;
            isInTitle = false;
            s = "";
            for (int i = 1; i < splitedPath.length; i++) {
                switch (splitedPath[i].replaceAll("\\[\\d+\\]", "")) {
                    case "article":
                        isInArticle = true;
                        break;
                    case "header":
                        if (!isInBody) {
                            isInHeader = true;
                        }
                        break;
                    case "bdy":
                        isInBody = true;
                        break;
                    case "title":
                        if (isInHeader) {
                            isInTitle = true;
                        }
                        break;
                }
                if (splitedPath[i].replaceAll("\\[\\d+\\]", "").equals("article")
                        || (isInArticle && (isInHeader || isInBody))) {
                    s = s + "/" + splitedPath[i];
                }
            }
            if (!pathsCouple.compressedPaths.contains("/article")) {
                pathsCouple.compressedPaths.add("/article");
                pathsCouple.originalPaths.add("/article[1]");
            }

            if (s.startsWith(this.precision)) {
                sEnd = s.split(precision.split("/")[precision.split("/").length - 1])[1].split("/")[0];
                s = this.precision;
                splitedPath = s.split("/");
                s = "";
                for (int j = 1; j < splitedPath.length; j++) {
                    if (!pathsCouple.compressedPaths.contains(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""))) {
                        pathsCouple.compressedPaths.add(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""));
                        if (j == splitedPath.length - 1) {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j] + sEnd);
                        } else {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j]);
                        }
                    }
                    s = s + "/" + splitedPath[j];
                }
            }
        }
        // System.out.println(pathsCouple.compressedPaths.toString());
        // System.out.println(pathsCouple.originalPaths.toString());
        return pathsCouple;


    }

    private static class PathsCouple {

        ArrayList<String> originalPaths;
        ArrayList<String> compressedPaths;

        public PathsCouple() {
            this.originalPaths = new ArrayList<>();
            this.compressedPaths = new ArrayList<>();
        }
    }
    
     public static void main(String[] args) {

        System.out.println("Begin of deserialization...");
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML1000.serial");
        System.out.println("End of deserialization.");

        System.out.println("dlMap : " + index.getDlMap());
        System.out.println(" index " + index.getCollectionData().size());

        ArrayList<Double> alphas = new ArrayList<>();
        
        alphas.add(0.9);
        alphas.add(1.1);
        alphas.add(1.5);
        alphas.add(1.0);
        
        
        Bm25FElements score = new Bm25FElements("olive oil health benefit", index, 1, 0.5, "/article[1]/header[1]/title",alphas);

        Map<String, Map<String, Double>> scores = score.getScores();

        System.out.println("Scores : " + scores);
    }
    
}