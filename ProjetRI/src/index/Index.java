/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.Serializable;
import java.util.*;

/**
 * Index Class that contains the data collection
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class Index implements Serializable {
    
    /**
     * The data collection
     */
    private Map<String, Map<String, ArrayList<String>>> collectionData;
    /**
     * Size of the collection
     */
    private Map<String, Integer> N;
    /**
     * List of documents' lengths
     */
    private Map<String, Map<String, Integer>> dlMap;
    /**
     * List of avdl
     */
    private Map<String, Double> avdlMap;
    /**
     * Map of links
     */
    private Map<String, List<String>> pagerank;
    
    /**
     * Vocabulary Map
     * DocId <-> Vocabulary of Doc
     */
    private Map<String, List<String>> vocabulary;
    
    /**
     * The constructor
     */
    public Index(Map<String, Map<String, ArrayList<String>>> collectionData) {
        this.collectionData = collectionData;
    }
    
    /**
     * the constructor
     */
    public Index() {
        this.collectionData = new HashMap<>();
        this.dlMap = new HashMap<>();
        this.N = new HashMap<>();
        this.pagerank = new HashMap<>();
        this.avdlMap = new HashMap<>();
        this.vocabulary = new HashMap<>();
    }
    
    public Map<String, Map<String, ArrayList<String>>> getCollectionData() {
        return collectionData;
    }
    
    public void setCollectionData(Map<String, Map<String, ArrayList<String>>> collectionData) {
        this.collectionData = collectionData;
    }
    
    public int getN(String tag) {
        return N.get(tag);
    }
    
    public Map<String, Integer> getN() {
        return N;
    }
    
    public void setN(Map<String, Integer> N) {
        this.N = N;
    }
    
    public int getDl(String documentId, String path) {
        return dlMap.get(documentId).get(path).intValue();
    }
    
    public Map<String, Map<String, Integer>> getDlMap() {
        return dlMap;
    }
    
    public void setDlMap(Map<String, Map<String, Integer>> dlMap) {
        this.dlMap = dlMap;
    }
    
    public double getAvdl(String path) {
        return avdlMap.get(path);
    }
    
    public Map<String, Double> getAvdlMap() {
        return avdlMap;
    }
    
    public void setAvdlMap(Map<String, Double> avdlMap) {
        this.avdlMap = avdlMap;
    }
    
    /**
     * get the number of words in the collection
     * @return the number of words in the collection
     */
    public int getNumberOfWords() {
        int nbWords = 0;
        Integer dl = 0;
        
        for (Iterator i = dlMap.values().iterator(); i.hasNext();) {
            dl = (Integer) i.next();
            nbWords += dl.intValue();
        }
        
        return nbWords;
    }
    /**
     * get the collection size
     * @return  the collection size
     */
    public int getSize() {
        
        return this.collectionData.size();
    }
    
    @Override
    public String toString() {
        String s = "Index {\n" + " collectionData = \n";
        String term = null;
        
        for (Iterator i = collectionData.keySet().iterator(); i.hasNext();) {
            term = i.next().toString();
            s += "[" + term + ":" + collectionData.get(term) + "],\n";
        }
        
        return s + "}";
    }
    
    public Map<String, List<String>> getPagerank() {
        return pagerank;
    }
    
    public void setPagerank(Map<String, List<String>> pagerank) {
        this.pagerank = pagerank;
    }
    
    public Map<String, List<String>> getVocabulary() {
        return vocabulary;
    }
    
    public void setVocabulary(Map<String, List<String>> vocabulary) {
        this.vocabulary = vocabulary;
    }
    
    

}
