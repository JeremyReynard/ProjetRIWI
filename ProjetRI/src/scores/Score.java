/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.Map;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Score {
    
    protected Index index;
    protected String request;
    
    /*
     * Constructor
     * @Param String request
     * @Param Index index
     */
    public Score(String request, Index index) {
        this.index = index;
        this.request = request;
    }
    
    /*
     * @return the tf
     */
    public Integer getTermFrequency(Index index, String word, String documentTitle){
        
        Map<String,Integer> mapValue = this.index.getCollectionData().get(word);
        int integer =0;
        //If the word does not exist in the Map
        if(mapValue==null){
            return 0;
        }
        //If the word exist in the document
        if(mapValue.containsKey(documentTitle)){
            integer = mapValue.get(documentTitle);
        }
        return integer;
    }
    
    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word){
        
        Map<String,Integer> mapValue = this.index.getCollectionData().get(word);
        
        if(mapValue == null){
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        return mapValue.size();
    }
    
    
    //Getters
    
    public Index getIndex() {
        return this.index;
    }
    
    public String getRequest() {
        return this.request;
    }

}
