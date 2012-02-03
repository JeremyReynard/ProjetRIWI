package scores;

import index.Index;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class Bm25FElements extends ScoreElements{

    private double k1;
    private double b;
    
    private String precision;
    
    /**
     * alphas(0) : article
     * alphas(1) : header
     * alphas(2) : title
     * alphas(3) : body
     * alphas(4) : sec
     * alphas(5) : p
     * 
     */
    private ArrayList<Double> alphas;
    
    /**
     * the constructor
     * @param request the query
     * @param index the index
     * @param k1 the parameter k1 for BM25 ranking's function
     * @param b  the parameter b for BM25 ranking's function
     * @param precision the granulary of the ranking's function
     * @param alphas used for the alpha
     */
     public Bm25FElements(String request, Index index, double k1, double b, String precision, ArrayList<Double> alphas) {
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
        PathsCouple pathsCouple;
        double score = 0;

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

        double wtd = ((alpha*tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / avdl)) + alpha*tf)) * Math.log((N - df + 0.5) / (df + 0.5));

        return wtd;
    }
    
    /**
     * Generate all the PathsCouple from a list of paths
     * @param paths the list of paths
     * @return the PathsCouple
     */
    private PathsCouple generatePathsList(ArrayList<String> paths) {

        boolean isInArticle = false;
        boolean isInHeader = false;
        boolean isInTitle = false;
        boolean isInBody = false;

        PathsCouple pathsCouple = new PathsCouple();

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
        
}
