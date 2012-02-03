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
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
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
    
    /**
     * Get the term fraquency of a term
     * @param index the index
     * @param word the term
     * @param documentTitle the current document
     * @return the term frequency
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
    
    /**
     * get the document frequency
     * @param index the index
     * @param word the term
     * @return the document frequency
     */
    public Integer getDocumentFrequency(Index index, String word) {
        
        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        
        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        return mapValue.entrySet().size();
    }

    /**
     * Create a run file
     * @param fileName the futur run file name
     * @param requestNumber the number of the request
     * @param pathElement the element path
     * @param scores the document scores
     * @param runNumber the number of runs
     */
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
    
    /**
     * Get the index
     * @return the index
     */
    public Index getIndex() {
        return this.index;
    }
    /**
     * Get the request
     * @return the request
     */
    public String getRequest() {
        return this.request;
    }


}
