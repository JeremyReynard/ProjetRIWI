package extractor;

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
    private Integer numberOccurence;

    public Couple(Integer documentNumber, Integer numberOccurence) {
        this.documentNumber = documentNumber;
        this.numberOccurence = numberOccurence;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getNumberOccurence() {
        return numberOccurence;
    }

    public void setNumberOccurence(Integer numberOccurence) {
        this.numberOccurence = numberOccurence;
    }
    
    @Override
    public String toString() {
        return "docNum=" + documentNumber + ", nbOcc=" + numberOccurence + "\n";
    }
}
