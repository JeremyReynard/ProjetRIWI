/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Couple;
import index.Index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        for (File f : files) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser;
            try {
                saxParser = factory.newSAXParser();
                ArticleXMLParser articleParser = new ArticleXMLParser();
                saxParser.parse(f.getAbsolutePath(), articleParser);

                String currentDocNum = articleParser.getId().toString();

                String[] words = (articleParser.getText().toString()).split("\\W");

                for (String w : words) {
                    if (!w.isEmpty()) {
                        List<Couple> valueMap = index.getCollectionData().get(w);
                        boolean isTermFrequencyFound = false;
                        // the word is already in the collection
                        if (valueMap != null) {
                            for (int i = 0; i < valueMap.size(); i++) {
                                // the word has been already found in the current document
                                if (valueMap.get(i).getDocumentTitle().equals(currentDocNum) && !isTermFrequencyFound) {
                                    valueMap.get(i).setTermFrequency(valueMap.get(i).getTermFrequency() + 1);
                                    isTermFrequencyFound = true;
                                }
                            }
                            if (!isTermFrequencyFound) {
                                //first occurrence of the word in this document									
                                valueMap.add(0, new Couple(currentDocNum, 1));
                            }
                        } // first occurrence of the word : add it to the collection
                        else {
                            index.getCollectionData().put(w, new ArrayList<Couple>());
                            index.getCollectionData().get(w).add(0, new Couple(currentDocNum, 1));
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
        
        Index serializedIndex = IndexDeserialization.deserialize("fileSerialization/index.serial");
        
        System.out.println(serializedIndex.toString());
        
    }
}
