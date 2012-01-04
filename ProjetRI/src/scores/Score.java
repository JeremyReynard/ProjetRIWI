/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

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
    public Integer getTermFrequency(Index index, String word, String documentTitle) {

        Map<String, Integer> mapValue = this.index.getCollectionData().get(word);
        int integer = 0;
        //If the word does not exist in the Map
        if (mapValue == null) {
            return 0;
        }
        //If the word exist in the document
        if (mapValue.containsKey(documentTitle)) {
            integer = mapValue.get(documentTitle);
        }
        return integer;
    }

    /*
     * @return the df
     */
    public Integer getDocumentFrequency(Index index, String word) {

        Map<String, Integer> mapValue = this.index.getCollectionData().get(word);

        if (mapValue == null) {
            // -1 because 0 make a divide by 0 error
            return -1;
        }
        return mapValue.size();
    }

    /*
     * Methods allowed to sort the score map
     */
    public Map sortMap(Map unsortedMap) {
        Map sortedMap = new HashMap();
        TreeSet set = new TreeSet(new Comparator() {

            public int compare(Object obj, Object obj1) {
                Double val1 = (Double) ((Map.Entry) obj).getValue();
                Double val2 = (Double) ((Map.Entry) obj1).getValue();

                return val1.compareTo(val2);
            }
        });

        set.addAll(unsortedMap.entrySet());

        for (Iterator it = set.iterator(); it.hasNext();) {
            Map.Entry myMapEntry = (Map.Entry) it.next();
            sortedMap.put(myMapEntry.getKey(), myMapEntry.getValue());
        }

        return sortedMap;
    }

    //Getters
    public Index getIndex() {
        return this.index;
    }

    public String getRequest() {
        return this.request;
    }
}
