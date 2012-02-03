package scores;

import index.Index;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class Bm25Articles extends Score implements CommonsScoreInterface {
    
    double k1;
    double b;
    int N;
    double avdl;
    /**
     * the constructor
     * @param request the query
     * @param index the index
     * @param k1 the parameter k1 for BM25 ranking's function
     * @param b  the parameter b for BM25 ranking's function
     */
    public Bm25Articles(String request, Index index, double k1, double b) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
        this.N = index.getN().get("/article");
        this.avdl = index.getAvdl("/article");
    }
    
    @Override
    public Map<String, Double> getScores() {
        
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
        
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
        
    }
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        int dl = index.getDl(documentNumber, "/article");
        
        for (String word : this.request.split("\\W")) {
            score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl, N, avdl);
        }
        
        return score;
    }
    
    public double getDocumentWordScore(String word, float tf, int dl, int N, double avdl) {
        
        int df = getDocumentFrequency(index, word);
        
        if (df == -1) {
            return 0;
        }
        
        double wtd = ((tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / avdl)) + tf)) * Math.log((N - df + 0.5) / (df + 0.5));
        
        return wtd;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        
        return this.getDocumentWordScore(word, termFrequency, documentLength, this.N, this.avdl);
    }

}
