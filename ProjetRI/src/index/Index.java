/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Index Class thant contains the data collection
 * @author Jérémy Reynard
 */
public class Index implements Serializable {

    /**
     * The data collection 
     */
    private Map<String, List<Couple>> collectionData;

    /**
     * The constructor 
     */
    public Index(Map<String, List<Couple>> collectionData) {
        this.collectionData = collectionData;
    }

    public Index() {
        this.collectionData = new HashMap<>();
    }

    public Map<String, List<Couple>> getCollectionData() {
        return collectionData;
    }

    public void setCollectionData(Map<String, List<Couple>> collectionData) {
        this.collectionData = collectionData;
    }

    @Override
    public String toString() {
        String s = "Index {\n" + " collectionData = \n";

        for (Iterator i = collectionData.keySet().iterator(); i.hasNext();) {
            s+= i.next().toString();
        }

        return s;
    }
}
