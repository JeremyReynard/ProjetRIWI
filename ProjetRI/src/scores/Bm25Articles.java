/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import serialization.IndexDeserialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Articles {
    
    Index index;
    String[] request;
    
    public Bm25Articles(String request, Index index){
        this.index = index;
        this.request = request.split("\\W");
    }
    
    public float getWordScore(String word){
        
        int tf =  0;
        int N  =  0;
        int df =  0;
                
        
        float score = (float) (Math.log(1+tf)*N/df);
        
        return 0;
    }
    
    public static void main(String[] args){
        Index index = IndexDeserialization.deserialize("fileSerialization/index.serial");
        
        
        
    }
   
}
