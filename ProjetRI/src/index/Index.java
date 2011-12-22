/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Index Class thant contains the data collection
 * @author Jérémy Reynard
 */
public class Index implements Serializable {

    /**
     * The data collection 
     */
    private Map<String, Map<String,Integer>> collectionData;

    /**
     * The constructor 
     */
    public Index(Map<String, Map<String,Integer>> collectionData) {
        this.collectionData = collectionData;
    }

    public Index() {
        this.collectionData = new HashMap<>();
    }

    public Map<String, Map<String, Integer>> getCollectionData() {
        return collectionData;
    }

    public void setCollectionData(Map<String, Map<String, Integer>> collectionData) {
        this.collectionData = collectionData;
    }

    @Override
    public String toString() {
        String s = "Index {\n" + " collectionData = \n";

        for (Iterator i = collectionData.keySet().iterator(); i.hasNext();) {
            String term = i.next().toString();
            s += "[" + term + ":" + collectionData.get(term) + "],\n";
        }

        return s + "}";
    }
}
