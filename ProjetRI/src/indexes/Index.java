/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexes;

import extractor.Couple;
import java.io.Serializable;
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
    
    public Map<String, List<Couple>> getCollectionData() {
        return collectionData;
    }

    public void setCollectionData(Map<String, List<Couple>> collectionData) {
        this.collectionData = collectionData;
    }   
    
    @Override
    public String toString() {
        return "Index{" + "collectionData=" + collectionData + '}';
    }
    
    
    
    
    

}
