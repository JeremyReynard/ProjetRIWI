package scores;

import index.Index;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class ScoreElements {

    protected Index index;
    protected String request;
    protected int pagerank;

    /*
     * Constructor
     * @Param String request
     * @Param Index index
     */
    public ScoreElements(String request, Index index) {
        this.index = index;
        this.request = request.toLowerCase();
    }

    /*
     * @return the tf
     */
    public Integer getTermFrequency(Index index, String word, String documentTitle, String originalPath) {
        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        int tf = 0;
        //If the word does not exist in the Map
        if (mapValue == null) {
            return 0;
        }
        //If the word exist in the document  
        if (mapValue.containsKey(documentTitle)) {
            if (mapValue.get(documentTitle).contains(originalPath)) {
                tf++;
            } else {
                for (String p : mapValue.get(documentTitle)) {
                    //BUGGE
                    if (p.startsWith(originalPath)) {
                        tf++;
                    }
                }
            }
        }
        //System.out.println("TF : "+word+" "+tf);
        return tf;
    }

    /*
     * Return the df 
     */
    public Integer getDocumentFrequency(Index index, String word, String compressedPath) {

        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);

        String documentNumber;
        int dfArticle, dfHeader, dfTitle, dfSec, dfBdy;
        boolean isInArticle;
        boolean isInHeader;
        boolean isInTitle;
        boolean isInBdy;
        boolean isInSec;
        
        dfArticle = 0;
        dfHeader = 0;
        dfBdy = 0;
        dfTitle = 0;
        dfSec = 0;

        String[] splitedCompressedPath = compressedPath.split("/");
        String lastTag = splitedCompressedPath[splitedCompressedPath.length - 1];

        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }

        for (Iterator i = mapValue.keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            isInArticle = false;
            isInBdy = false;
            isInHeader = false;
            isInTitle = false;
            isInSec = false ;
            for (String p : mapValue.get(documentNumber)) {
                if (p.contains("article") && !isInArticle) {
                    isInArticle = true;
                    dfArticle++;
                } else if (p.contains("header") && !isInHeader && !lastTag.equals("bdy") && !lastTag.equals("article")) {
                    isInHeader = true;
                    dfHeader++;
                } else if (p.contains("bdy") && !isInBdy && !lastTag.equals("header") && !lastTag.equals("article")) {
                    isInBdy = true;
                    dfBdy++;
                } else if (p.contains("title") && isInHeader) {
                    isInTitle = true;
                    dfTitle++;
                } else if (p.contains("sec") && isInBdy) {
                    isInSec = true;
                    dfSec++;
                }
            }
        }
        // System.out.println("DF de "+word+" "+(dfArticle+dfHeader+dfBdy));
        return dfArticle + dfHeader + dfTitle + dfBdy + dfSec;
    }

    //Getters
    public Index getIndex() {
        return this.index;
    }

    public String getRequest() {
        return this.request;
    }
}
