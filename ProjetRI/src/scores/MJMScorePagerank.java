/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import serialization.IndexDeserialization;

/**
 *
 * @author mlh
 */
public class MJMScorePagerank extends Score implements CommonsScoreInterface{

    private Bm25ArticlesPagerank scorer; 
    private final int k1 = 1;
    private final double b = 0.5;
    
    private double k;
        
    public MJMScorePagerank(String request, Index index, double k) {
        super(request, index);
        
        this.scorer = new Bm25ArticlesPagerank(request, index, k1, b);
        
        this.k = k;
    }

        
    @Override
    public Map<String, Double> getScores() {
        
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
      
        for (Iterator i = this.index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
    }

    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        double wordCoeff = 1.0;
        int dl = 0;
        
        for (String word : this.request.split("\\W")) {
            dl = index.getDl(documentNumber,"/article");
            
            score += wordCoeff * 
                    getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl);
            
            // update wordCoeff
            wordCoeff = wordCoeff / k;
        }

        return score;
    }

    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        if (getDocumentFrequency(index, word) == -1) {
            return 0;
        }
        return  (this.scorer.getDocumentWordScore(word, termFrequency, documentLength));
    }
    
    
       public static void main(String[] args) {
        
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        String runs = "";
        System.out.println("Deserialized");
        
        HashMap<String, String> requestsMap = new HashMap() {
            
            {
               // put("2009011", "olive oil health benefit");
                put("2009036", "notting hill film actors");
                put("2009067", "probabilistic models in information retrieval");
                put("2009073", "web link network analysis");
                put("2009074", "web ranking scoring algorithm");
                put("2009078", "supervised machine learning algorithm");
                put("2009085", "operating system +mutual +exclusion");
                
                
            }
        };
        for(Iterator iter = requestsMap.keySet().iterator();iter.hasNext();){
            
            String requestNum = (String)iter.next();
            MJMScorePagerank score = new MJMScorePagerank(requestsMap.get(requestNum), index,1.5);
            System.out.println("score: "+requestNum);
            Map<String, Double> scores = score.getScores();
            
            double maxValue;
            String docNumber = "";
            String next;
            
            String separator = " ";
            
            for (int runIndice = 1; runIndice <= 1500; runIndice++) {
                maxValue = Double.MIN_VALUE;
                for (Iterator j = scores.keySet().iterator(); j.hasNext();) {
                    next = (String) j.next();
                    if (scores.get(next) > maxValue) {
                        docNumber = next;
                        maxValue = scores.get(next);
                    }
                }
                scores.remove(docNumber);
                
                runs += requestNum + separator
                        + "Q0" + separator
                        + docNumber + separator
                        + runIndice + separator
                        + (1500 - runIndice + 1) + separator
                        + "MichaelJeremyMickael" + separator
                        + "/article[1]" + "\n";
            }
            
            
        }
        Path runPath = Paths.get("Runs/" + "runsMichaelJeremyMickaelMJMPagerank" + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(runPath, Charset.forName("UTF8"), StandardOpenOption.CREATE)) {
            writer.write(runs);
            writer.close();
        } catch (IOException e) {
            System.out.println("[Score][createRunFile] " + e);
        }
        
        /*  Index index = IndexDeserialization.deserialize("fileSerialization/indexXML10.serial");
         *
         * String request = "artificial";
         *
         * System.out.println(index.getCollectionData().get(request.toLowerCase()) + "\n");
         *
         * LtnSmartArticles score = new LtnSmartArticles(request, index);
         *
         * Map<String, Double> scores = score.getScores();
         *
         * System.out.println("[main][scores]" + scores.toString());
         */
    } 
    
    
    
    
    
}
