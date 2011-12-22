/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import serialization.IndexDeserialization;
import serialization.IndexSerialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class ArticlesDirectoryXMLParser {
    
    public Index parseDirectory(String directoryPath) {
        System.out.println("Beginning of indexing.\n");
        
        File[] files = null;
        File directoryToScan = new File(directoryPath);
        files = directoryToScan.listFiles();
        
        Index index = new Index();
        Map<String,Integer> valueMap = null;
        String currentDocNum = "";
        String[] words = null;
        SAXParserFactory factory = null;
        SAXParser saxParser;
        
        for (File f : files) {
            factory = SAXParserFactory.newInstance();
            try {
                saxParser = factory.newSAXParser();
                ArticleXMLParser articleParser = new ArticleXMLParser();
                saxParser.parse(f.getAbsolutePath(), articleParser);
                
                currentDocNum = articleParser.getId().toString();
                
                words = (articleParser.getText().toString()).split("\\W");
                
                for (String w : words) {
                    if (!w.isEmpty()) {
                        valueMap = index.getCollectionData().get(w);
                        boolean isTermFrequencyFound = false;
                        // the word is already in the collection
                        if (valueMap != null) {
                            for (int i = 0; i < valueMap.size(); i++) {
                                // the word has been already found in the current document
                                if (valueMap.containsKey(currentDocNum) && !isTermFrequencyFound) {
                                    valueMap.put(currentDocNum,valueMap.get(currentDocNum)+1);
                                    isTermFrequencyFound = true;
                                }
                            }
                            if (!isTermFrequencyFound) {
                                //first occurrence of the word in this document
                                valueMap.put(currentDocNum, 1);
                            }
                        } // first occurrence of the word : add it to the collection
                        else {
                            index.getCollectionData().put(w, new HashMap<String,Integer>());
                            index.getCollectionData().get(w).put(currentDocNum, 1);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("End of indexing.\n");
        return index;
        
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        Index index = new ArticlesDirectoryXMLParser().parseDirectory("../coll");
        IndexSerialization.serialize(index,"fileSerialization/index.serial");
