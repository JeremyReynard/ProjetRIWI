/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;

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

        return this.index.getCollectionData().get(word).get(documentTitle);

            }

    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word){

        return this.index.getCollectionData().get(word).size();
        }
    
    
    //Getters 

    public Index getIndex() {
        return this.index;
    }

    public String getRequest() {
        return this.request;
}
    
}
