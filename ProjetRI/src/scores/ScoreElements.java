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
                    if (p.startsWith(originalPath)) {
                        tf++;
                    }
                }
            }
        }
        return tf;
    }

    /*
     * @return the df
     */
    /*public Integer getDocumentFrequency(Index index, String word, String path) {

        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);

        String documentNumber;
        int df = 0;
        int splitedPathIndex, splitedWordIndex, pathsDocumentIndex;
        boolean isFound;

        String wordPath = "";

        String[] splitedPath = path.split("/");
        String[] splitedWordPath;

        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }

        for (Iterator i = mapValue.keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            isFound = false;
            if (mapValue.get(documentNumber).contains(path)) {
                df++;
            } else {
                pathsDocumentIndex = 0;
                //System.out.println(mapValue.get(documentNumber));
                while (pathsDocumentIndex < mapValue.get(documentNumber).size() && !isFound) {
                    wordPath = mapValue.get(documentNumber).get(pathsDocumentIndex);
                    splitedPathIndex = 1;
                    splitedWordIndex = 1;
                    splitedWordPath = wordPath.split("/");

                    while (splitedWordIndex < splitedWordPath.length && !isFound) {
                        //System.out.println(splitedWordPath[splitedWordIndex] + " " + splitedPath[splitedPathIndex]);
                        if (splitedPathIndex == splitedPath.length - 1) {
                            if (splitedWordPath[splitedWordIndex].replaceAll("\\[\\d+\\]", "").equals(splitedPath[splitedPathIndex])) {
                                splitedPathIndex++;
                            }
                        } else if (splitedWordPath[splitedWordIndex].equals(splitedPath[splitedPathIndex])) {
                            splitedPathIndex++;
                        }
                        if (splitedPathIndex == splitedPath.length) {
                            isFound = true;
                            df++;
                        }
                        splitedWordIndex++;
                    }
                    pathsDocumentIndex++;
                }
            }
        }
        return df;
    }*/
    public Integer getDocumentFrequency(Index index, String word, String path) {

        Map<String, ArrayList<String>> mapValue = this.index.getCollectionData().get(word);
        
        String documentNumber;
        int df = 0;
        int k;
        boolean isFound;
        String p = "";

        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }

        for (Iterator i = mapValue.keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            isFound = false;
            if (mapValue.get(documentNumber).contains(path)) {
                df++;
            } else {
                k = 0;
                while(k < mapValue.get(documentNumber).size() && !isFound) {
                    p = mapValue.get(documentNumber).get(k);
                    if (p.startsWith(path) && !isFound) {
                        df++;
                        isFound = true;
                    }
                    k++;
                }
            }
        }
        return df;
    }

    //Getters
    public Index getIndex() {
        return this.index;
    }

    public String getRequest() {
        return this.request;
    }
}
