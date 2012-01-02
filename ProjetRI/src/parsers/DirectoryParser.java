/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Index;
import java.util.Map;
import javax.swing.JProgressBar;

/**
 *
 * @author mlh
 */
public abstract class DirectoryParser {
    
    protected long extractionTime;
    protected Index index;
    protected String directoryPath;
    
    public DirectoryParser(String dirPath){
        
        this.directoryPath = dirPath;
        this.index = new Index();        
    }
    
    public abstract Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal);    
            
    public String showResults(){
        long sec = (this.extractionTime + 500) / 1000;
        String result = "";
        result += "Indexation time " + sec + "sec\n";
        result += "N : " + index.getN() + "\n";
        result += "avdl : " + index.getAvdl();
               
        return result;
    }
    
    public String showResults(String word) {
        Map<String, Integer> map = this.index.getCollectionData().get(word);
        int nbOccurs = 0;
        
        if (map != null){
            for (Integer i : map.values()) {
                nbOccurs += i;
            }
        }
        
        String result = "Occurences : " + nbOccurs + "\n";
        return result;
    }
    
    public Index getIndex(){
        return this.index;
    }   
}
