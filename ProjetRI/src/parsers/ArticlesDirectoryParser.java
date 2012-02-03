/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Index;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JProgressBar;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public abstract class ArticlesDirectoryParser {
    
    protected long extractionTime;
    protected Index index;
    protected String directoryPath;
    
    /**
     * a Contructor
     * @param dirPath the directory path where search
     */
    public ArticlesDirectoryParser(String dirPath){
        
        this.directoryPath = dirPath;
        this.index = new Index();        
    }
    
    /**
     * a Constructor
     * @param index the index to parse
     */
    public ArticlesDirectoryParser(Index index){
        
        this.index = index;
        this.directoryPath = "";
    }
    
    /**
     * Used for JProgress Bar 
     */
    public abstract Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal);    
            
    /**
     * Show the result of the indexation
     * @return the String containing all the informations
     */
    public String showResults(){
        long sec = (this.extractionTime + 500) / 1000;
        String result = "";
        result += "Indexation time " + sec + "sec \n";
        result += "N(articles) : " + index.getN().get("article") + "\n";
        result += "avdl : " + index.getAvdl("/article") + "\n";
        result += "index size : " + index.getSize();
                 
        return result;
    }
    
    /**
     * Get the number of occurence of a term
     * @param word the term
     * @return the String contaning the occurence's term
     */
    public String showResults(String word) {
        
        String result = "Occurences : " + getOccurences(word) + "\n";
        return result;
    }
    /**
     * get the occurence of a term
     * @param word the term
     * @return the occurence's term
     */
    private int getOccurences(String word) {
        
        Map<String, ArrayList<String>> map = this.index.getCollectionData().get(word);
        int nbOccurs = 0;
        
        if (map != null){
            for (ArrayList<String> l : map.values()) {
                nbOccurs += l.size();
            }
        }
        
        return nbOccurs;        
    }
        
    public Index getIndex(){
        return this.index;
    }  
}
