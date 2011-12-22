package index;

import java.io.Serializable;

/**
 * Couple class that contains the document's number and the occurence of a term in this document
 */
public class Couple implements Serializable{
    
    /**
     * The number of the document
     */
    private Integer documentNumber;
    
    /**
     * The number of current term occurence 
     */
    private Integer termFrequency;

    public Couple(Integer documentNumber, Integer termFrequency) {
        this.documentNumber = documentNumber;
        this.termFrequency = termFrequency;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Integer numberOccurence) {
        this.termFrequency = numberOccurence;
    }
    
    @Override
    public String toString() {
        return "docNum=" + documentNumber + ", tf=" + termFrequency + "";
    }
}
