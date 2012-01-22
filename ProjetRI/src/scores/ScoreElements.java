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
import parsers.Stemmer;
import serialization.IndexDeserialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class ScoreElements {

    protected Index index;
    protected String request;

    /*
     * Constructor
     * @Param String request
     * @Param Index index
     */
    public ScoreElements(String request, Index index) {
        this.index = index;
        this.request = Stemmer.lemmeWord(request.toLowerCase());
    }

    /*
     * @return the tf
     */
    public Integer getTermFrequency(Index index, String word, String documentTitle, String path) {

        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        int tf = 0;
        //If the word does not exist in the Map
        if (mapValue == null) {
            return 0;
        }
        //If the word exist in the document
        if (mapValue.containsKey(documentTitle)) {
            if (mapValue.get(documentTitle).contains(path)) {
                tf++;
            } else {
                for (String p : mapValue.get(documentTitle)) {
                    //System.out.println(p);
                    if (p.startsWith(path)) {
                        tf++;
                    }
                }
            }
        }
        return tf;
    }

    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word, String path) {

        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);

        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        String documentNumber;
        int df = 0;
        for (Iterator i = mapValue.keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            if (mapValue.get(documentNumber).contains(path)) {
                df++;
            } else {
                for (String p : mapValue.get(documentNumber)) {
                    //System.out.println(p);
                    if (p.startsWith(path)) {
                        df++;
                    }
                }
            }
        }
        return df;
    }

    public void createRunFile(String fileName, String requestNumber, Map<String, Map<String, Double>> scores, int runNumber) {

        double maxValue;
        String maxValuePath = "";
        String docNumber = "";
        String nextDoc = "";
        String nextPath;
        String runs = " ";
        String separator = " ";

        for (int runIndice = 1; runIndice <= runNumber; runIndice++) {
            maxValue = Double.MIN_VALUE;
            for (Iterator j = scores.keySet().iterator(); j.hasNext();) {
                nextDoc = j.next().toString();
                for (Iterator k = scores.get(nextDoc).keySet().iterator(); k.hasNext();) {
                    nextPath  = k.next().toString();
                    if (scores.get(nextDoc).get(nextPath) > maxValue) {
                        docNumber = nextDoc;
                        maxValue = scores.get(nextDoc).get(nextPath);
                        maxValuePath = nextPath;
                    }
                }
            }
            scores.get(docNumber).remove(maxValuePath);

            runs += requestNumber + separator
                    + "Q0" + separator
                    + docNumber + separator
                    + runIndice + separator
                    + (runNumber - runIndice + 1) + separator
                    + "MichaelJeremyMickael" + separator
                    + maxValuePath + "\n";
        }

        Path runPath = Paths.get("Runs/" + fileName + ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(runPath, Charset.forName("UTF8"), StandardOpenOption.CREATE)) {
            writer.write(runs);
            writer.close();
        } catch (IOException e) {
            System.out.println("[Score][createRunFile] " + e);
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
    Index index = IndexDeserialization.deserialize("fileSerialization/indexXML1.serial");
    
    LtnSmartElements score = new LtnSmartElements("Gottschalk", index);
    
    Map<String, Map<String, Double>> scores = score.getScores();
    score.createRunFile("runtest", "articleNumb", scores, 1500);
    
    //System.out.println("[main][scores]"+scores.toString());
    
    
    }
}
