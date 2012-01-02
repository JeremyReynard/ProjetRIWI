/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import java.util.Map;

/**
 *
 * @author Dje
 */
public interface CommonsScoreInterface {
    
    //TODO : javadoc
    /**
     * 
     * @param X
     * @return 
     */
    public Map<String, Double> getXBestScore(int X);
    
    /**
     * Get the request Score for the document
     * @param documentNumber the document Number
     * @return the score
     */
    public double getRequestScore(String documentNumber);
    
    /**
     * Get the document word score
     * @param word a Word
     * @param termFrequency the Term Frequency
     * @param documentLength the document Length
     * @return the document word score
     */
    public double getDocumentWordScore(String word, float termFrequency, int documentLength);
    
    
    
}
