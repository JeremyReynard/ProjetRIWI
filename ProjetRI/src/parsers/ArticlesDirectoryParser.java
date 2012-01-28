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
 * @author mlh
 */
public abstract class ArticlesDirectoryParser {
    
    protected long extractionTime;
    protected Index index;
    protected String directoryPath;
    
    public ArticlesDirectoryParser(String dirPath){
        
        this.directoryPath = dirPath;
        this.index = new Index();        
    }
    
    public ArticlesDirectoryParser(Index index){
        
        this.index = index;
        this.directoryPath = "";
    }
    
    public abstract Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal);    
            
    public String showResults(){
        long sec = (this.extractionTime + 500) / 1000;
        String result = "";
        result += "Indexation time " + sec + "sec \n";
        result += "N(articles) : " + index.getN().get("article") + "\n";
        result += "avdl : " + index.getAvdl("/article") + "\n";
        result += "words : " + index.getNumberOfWords() + "\n";
        result += "index size : " + index.getSize();
                 
        return result;
    }
    
    public String showResults(String word) {
        
        String result = "Occurences : " + getOccurences(word) + "\n";
        return result;
    }
    
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
