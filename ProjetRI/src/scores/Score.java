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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import serialization.IndexDeserialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Score {
    
    protected Index index;
    protected String request;
    
    /*
     * Constructor
     * @Param String request
     * @Param Index index
     */
    public Score(String request, Index index) {
        this.index = index;
        this.request = request;
    }
    
    /*
     * @return the tf
     */
    public Integer getTermFrequency(Index index, String word, String documentTitle) {
        
        Map<String, Integer> mapValue = this.index.getCollectionData().get(word);
        int integer = 0;
        //If the word does not exist in the Map
        if (mapValue == null) {
            return 0;
        }
        //If the word exist in the document
        if (mapValue.containsKey(documentTitle)) {
            integer = mapValue.get(documentTitle);
        }
        return integer;
    }
    
    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word) {
        
        Map<String, Integer> mapValue = this.index.getCollectionData().get(word);
        
        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        return mapValue.size();
    }
 
    
    public void createRunFile(String fileName, String requestNumber, String articleINEXNumber,String pathElement,Map<String, Double> xBestScore, int runNumber){

        int xBestScoresize = xBestScore.size();
        
        Path runPath = Paths.get("Runs/"+fileName+".txt");
                
        try (BufferedWriter writer = Files.newBufferedWriter(runPath, Charset.forName("UTF8"),StandardOpenOption.CREATE)) {
            String runLine;
            Double runScore;
            String separator=" ";
            for (Iterator iterBestscore = xBestScore.keySet().iterator() ; iterBestscore.hasNext() ; ){
                runScore = xBestScore.get((String)iterBestscore.next());
                runLine = requestNumber+separator+
                        "Q0"+separator
                        +articleINEXNumber+separator
                        +xBestScoresize+separator
                        +runScore+separator
                        +"MichaelJeremyMickael"+separator
                        +pathElement+"\n";
                writer.write(runLine);
                xBestScoresize--;
                
            }

            writer.close();
        }catch(IOException e){
            System.out.println("[Score][createRunFile] "+e);
        }

    }
    
    
    //Getters
    public Index getIndex() {
        return this.index;
    }
    
    public String getRequest() {
        return this.request;
    }
    
     public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        
        LtnSmartArticles score = new LtnSmartArticles("states", index);
        
        Map<String, Double> scores = score.getScores();
        score.createRunFile("runtest", "000", "articleNumb", "/article[1]", scores, 1500);
        
        //System.out.println("[main][scores]"+scores.toString());
        
        
    }
    
}
