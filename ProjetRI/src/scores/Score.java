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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Score {
    
    protected Index index;
    protected String request;
    protected int pageRank;
    
    /*
     * Constructor
     * @Param String request
     * @Param Index index
     */
    public Score(String request, Index index) {
        this.index = index;
        this.request = request.toLowerCase();
        
    }
    
    /*
     * @return the tf
     */
    public Integer getTermFrequency(Index index, String word, String documentTitle) {
        
        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        int integer = 0;
        //If the word does not exist in the Map
        if (mapValue == null) {
            return 0;
        }
        //If the word exist in the document
        if (mapValue.containsKey(documentTitle)) {
            integer = mapValue.get(documentTitle).size();
        }
        return integer;
    }
    
    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word) {
        
        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        
        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        return mapValue.entrySet().size();
    }

    public void createRunFile(String fileName, String requestNumber, String pathElement,Map<String, Double> scores, int runNumber){
        
        double maxValue;
        String docNumber = "";
        String next;
        String runs = " ";
        String separator = " ";
        
        for ( int runIndice = 1; runIndice <= runNumber; runIndice++ ) {
            maxValue = Double.MIN_VALUE;
            for (Iterator j = scores.keySet().iterator(); j.hasNext();) {
                next = (String) j.next();
                if (scores.get(next) > maxValue) {
                    docNumber = next;
                    maxValue = scores.get(next);
                }
            }
            scores.remove(docNumber);
            
            runs += requestNumber + separator
                    + "Q0" + separator
                    + docNumber + separator
                    + runIndice + separator
                    + (runNumber - runIndice + 1) + separator
                    + "MichaelJeremyMickael" + separator
                    + pathElement + "\n";
        }
        
        Path runPath = Paths.get("Runs/"+fileName+".txt");
        
        try (BufferedWriter writer = Files.newBufferedWriter(runPath, Charset.forName("UTF8"),StandardOpenOption.CREATE)) {
            writer.write(runs);
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
    
    /*public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        
        LtnSmartArticles score = new LtnSmartArticles("states", index);
        
        Map<String, Double> scores = score.getScores();
        score.createRunFile("runtest", "articleNumb", "/article[1]", scores, 1500);
        
        //System.out.println("[main][scores]"+scores.toString());
        
        
    }*/

}
