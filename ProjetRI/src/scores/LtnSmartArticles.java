/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Couple;
import index.Index;
import java.util.Iterator;
import java.util.List;

/**
 *LtnSmart Algorithm
 */
public class LtnSmartArticles {
    
    
    public Integer getTermFrequency(Index index, String word, String documentTitle){
        
        List<Couple> listCouple = index.getCollectionData().get(word);
        
        Iterator iteratorCouple = listCouple.iterator();
        
        while(((Couple)iteratorCouple.next()).getDocumentTitle().equals(documentTitle)){
            
        }
        
        
        return null;
    }
    
    public Integer getDocumentFrequency(Index index, String word, String document){
        
        //DF The number of document where word is 
        return index.getCollectionData().get(word).size();
    }
}
