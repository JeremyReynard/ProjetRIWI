/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Index Class that contains the data collection
 * @author Jérémy Reynard
 */
public class Index implements Serializable {

    /**
     * The data collection 
     */
    private Map<String, Map<String, ArrayList<String>>> collectionData;
    /*
     * Size of the collection
     */
    private Map<String, Integer> N;
    /*
     * List of documents' lengths
     */
    private Map<String, Integer> dlMap;

    /**
     * The constructor 
     */
    public Index(Map<String, Map<String, ArrayList<String>>> collectionData) {
        this.collectionData = collectionData;
    }

    public Index() {
        this.collectionData = new HashMap<>();
        this.dlMap = new HashMap<>();
        this.N = new HashMap<>();
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

    public Map<String, Integer> getDlMap() {
        return dlMap;
    }

    public void setDlMap(Map<String, Integer> dlMap) {
        this.dlMap = dlMap;
    }

    public double getAvdl() {

        double dlSum = 0;
        Integer dl = 0;

        for (Iterator i = dlMap.values().iterator(); i.hasNext();) {
            dl = (Integer) i.next();
            dlSum += dl.intValue();
        }

        return (dlSum / dlMap.size());
    }

    public int getNumberOfWords() {
        int nbWords = 0;
        Integer dl = 0;

        for (Iterator i = dlMap.values().iterator(); i.hasNext();) {
            dl = (Integer) i.next();
            nbWords += dl.intValue();
        }

        return nbWords;
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
}
