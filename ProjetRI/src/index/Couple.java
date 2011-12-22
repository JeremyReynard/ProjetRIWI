package index;

import java.io.Serializable;

/**
 * Couple class that contains the document's number and the occurence of a term in this document
 */
public class Couple implements Serializable{
    
    /**
     * The document's title
     */
    private String documentTitle;
    
    /**
     * The number of current term occurence 
     */
    private Integer termFrequency;

    public Couple(String documentTitle, Integer termFrequency) {
        this.documentTitle = documentTitle;
        this.termFrequency = termFrequency;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }


    public Integer getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Integer numberOccurence) {
        this.termFrequency = numberOccurence;
    }
    
    @Override
    public String toString() {
        return "docTitle=" + documentTitle + ", tf=" + termFrequency + "";
    }
}
