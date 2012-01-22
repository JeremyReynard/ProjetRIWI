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
import parsers.Stemmer;
import serialization.IndexDeserialization;

/**
 *
 * @author Dje
 */
public class LtcSmartArticles  extends Score implements CommonsScoreInterface{
    
    public LtcSmartArticles(String request, Index index) {
        super(request, index);
    }
    
    @Override
    public Map<String, Double> getScores() {
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
        int ind=0;
        int size = index.getDlMap().size();
        //For all document
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            ind++;
            System.out.println("Document traité:"+ind+"/"+size);
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
    }
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        for (String word : this.request.split("[\\W]+")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            score += getDocumentWordScore(word,getTermFrequency(index, word, documentNumber) , dl,documentNumber);
        }
        
        return score;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public double getDocumentWordScore(String word,float termFrequency,int documentLength, String documentNumber){
        
        int documentFrequency = getDocumentFrequency(index, word);
        
        String termTemporary ="";
        int termTemporaryFrequency = 0;
        int termTemporaryDocumentFrequency = 0;
        int sommePonderations=0;
        //For all word in the index
        for(Iterator iteratorKeyMap = this.index.getCollectionData().keySet().iterator();iteratorKeyMap.hasNext();)
        {
            //Get an existing word
            termTemporary = iteratorKeyMap.next().toString();
            //Get the term frequency of an existing word
            termTemporaryFrequency = getTermFrequency(index, termTemporary, documentNumber);
            
            //Get the term frenquency of an existing word in a specific document
<<<<<<< HEAD
            termTemporaryDocumentFrequency = getTermFrequency(index, termTemporary,documentNumber);
=======
            termTemporaryDocumentFrequency = getDocumentFrequency(index, termTemporary);
>>>>>>> 057a7803314dc68cb64b3b1918351faea6b9f1d0
            
            //Formula
            sommePonderations+=Math.pow((Math.log(1+termTemporaryFrequency)*(index.getN().get("article") /termTemporaryDocumentFrequency)),2);
            
        }
        //Score formula
        Double result = Math.log(1+ termFrequency)/Math.sqrt(sommePonderations)* (index.getN().get("article") /documentFrequency );
        
        return result;
    }
    
    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        String runs = "";
        System.out.println("Deserialized");
        LtcSmartArticles score = new LtcSmartArticles(Stemmer.lemmeRequest("Algorithms for calculating variance"), index);
        System.out.println("score");
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
            
            runs += "001LTC" + separator
                    + "Q0" + separator
                    + docNumber + separator
                    + runIndice + separator
                    + (1500 - runIndice + 1) + separator
                    + "MichaelJeremyMickael" + separator
                    + "/article[1]" + "\n";
        }
        
        
        
        Path runPath = Paths.get("Runs/" + "runsMichaelJeremyMickaelLTC" + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(runPath, Charset.forName("UTF8"), StandardOpenOption.CREATE)) {
            writer.write(runs);
            writer.close();
        } catch (IOException e) {
            System.out.println("[Score][createRunFile] " + e);
        }
    }
    

}
