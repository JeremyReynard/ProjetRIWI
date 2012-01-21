/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JProgressBar;
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
public class ArticlesDirectoryXMLParser extends ArticlesDirectoryParser {

    public ArticlesDirectoryXMLParser(String dirPath) {

        super(dirPath);
    }

    @Override
    public Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal) {
        //System.out.println("Beginning of indexing.\n");

        File[] files = null;
        File directoryToScan = new File(this.directoryPath);
        files = directoryToScan.listFiles();

        // JProgressBar
        int nbFiles = files.length;
        int currentFileNumber = 0;
        int currentWordNumber = 0;
        int deltaPBGlobal = 0;
        int percent = 0;
        // ---

        Map<String, ArrayList<String>> valueMap = null;
        String currentDocNum = "";
        String[] words = null;
        String w;
        ArrayList<String> paths = null;
        String p;
        ArrayList<String> pathsList;
        String tag;
        SAXParserFactory factory = null;
        SAXParser saxParser;
        ArticleXMLParser articleParser = null;
        int numberOfWords = 0;

        long startTime = System.currentTimeMillis();
        for (File f : files) {

            // JProgressBar
            jpBarFile.setString(f.getName());
            jpBarGlobal.setString("Global : " + (currentFileNumber + 1) + " / " + (nbFiles + 1));
            deltaPBGlobal = 100 * currentFileNumber / nbFiles;
            currentWordNumber = 0;
            currentFileNumber++;
            // ---

            factory = SAXParserFactory.newInstance();
            try {
                saxParser = factory.newSAXParser();
                articleParser = new ArticleXMLParser();
                saxParser.parse(f.getAbsolutePath(), articleParser);

                currentDocNum = articleParser.getId().toString();

                words = (articleParser.getText().toString()).split("[\\W]+");
                paths = articleParser.getPaths();

                for (Iterator i = articleParser.getN().keySet().iterator(); i.hasNext();) {
                    tag = i.next().toString();
                    if (index.getN().containsKey(tag)) {
                        index.getN().put(tag, index.getN().get(tag) + articleParser.getN().get(tag));
                    } 
                    else {
                       index.getN().put(tag, articleParser.getN().get(tag)); 
                    }
                }
                index.getN().putAll(articleParser.getN());

                numberOfWords = 0;

                for (int i = 1; i < words.length; i++) {;
                    w = words[i];
                    p = paths.get(i - 1);

                    // JProgressBar
                    percent = (100 * currentWordNumber) / words.length;
                    jpBarFile.setValue(percent);
                    jpBarGlobal.setValue(deltaPBGlobal + (percent / (nbFiles + 1)));
                    currentWordNumber++;
                    //--

                    w = w.toLowerCase();
                    if (!w.isEmpty() && (!Stopwords.isStopword(w))) {
                        numberOfWords++;
                        valueMap = index.getCollectionData().get(w);

                        // the word is already in the collection
                        if (valueMap != null) {
                            // the word has been already found in the current document
                            if (valueMap.containsKey(currentDocNum)) {
                                valueMap.get(currentDocNum).add(p);
                                valueMap.put(currentDocNum, valueMap.get(currentDocNum));
                            } else {
                                //first occurrence of the word in this document
                                pathsList = new ArrayList<>();
                                pathsList.add(p);
                                valueMap.put(currentDocNum, pathsList);
                            }
                        } // first occurrence of the word : add it to the collection
                        else {
                            index.getCollectionData().put(w, new HashMap<String, ArrayList<String>>());
                            pathsList = new ArrayList<>();
                            pathsList.add(p);
                            index.getCollectionData().get(w).put(currentDocNum, pathsList);
                        }
                    }
                    this.index.getDlMap().put(currentDocNum, numberOfWords);
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }

        this.extractionTime = System.currentTimeMillis() - startTime;
        System.out.println("End of indexing.\n");
        return index;

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        JProgressBar jp1 = new JProgressBar();
        JProgressBar jp2 = new JProgressBar();

        Index index = new ArticlesDirectoryXMLParser("../../coll10").parseDirectory(jp1, jp2);

        IndexSerialization.serialize(index, "fileSerialization/indexXML1.serial");

        index = IndexDeserialization.deserialize("fileSerialization/indexXML1.serial");

        System.out.println("N : " + index.getN());
        System.out.println("avdl : " + index.getAvdl());
        System.out.println("dl : " + index.getDlMap().toString());
        //System.out.println("\n" + index.toString());

    }
}
