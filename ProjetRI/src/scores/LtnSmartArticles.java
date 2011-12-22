/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;

/**
 *LtnSmart Algorithm
 */
public class LtnSmartArticles {
    
    
    public Integer getTermFrequency(Index index, String word, String documentTitle){
        
        return index.getCollectionData().get(word).get(documentTitle);

    }
    
    public Integer getDocumentFrequency(Index index, String word, String document){
        
        //DF The number of document where word is 
        return index.getCollectionData().get(word).size();
    }
}
