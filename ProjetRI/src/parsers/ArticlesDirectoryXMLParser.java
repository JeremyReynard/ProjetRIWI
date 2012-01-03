/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import index.Index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
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

        Map<String, Integer> valueMap = null;
        String currentDocNum = "";
        String[] words = null;
        SAXParserFactory factory = null;
        SAXParser saxParser;
        ArticleXMLParser articleParser = null;

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
                this.index.setN(index.getN() + 1);
                saxParser = factory.newSAXParser();
                articleParser = new ArticleXMLParser();
                saxParser.parse(f.getAbsolutePath(), articleParser);

                currentDocNum = articleParser.getId().toString();

                words = (articleParser.getText().toString()).split("\\W");

                this.index.getDlMap().put(currentDocNum, words.length);

                for (String w : words) {

                    // JProgressBar
                    percent = (100 * currentWordNumber) / words.length;
                    jpBarFile.setValue(percent);
                    jpBarGlobal.setValue(deltaPBGlobal + (percent / (nbFiles + 1)));
                    currentWordNumber++;
                    //--

                    w = w.toLowerCase();
                    if (!w.isEmpty() && (!Stopwords.isStopword(w))) {
                        valueMap = index.getCollectionData().get(w);

                        // the word is already in the collection
                        if (valueMap != null) {
                            // the word has been already found in the current document
                            if (valueMap.containsKey(currentDocNum)) {
                                valueMap.put(currentDocNum, valueMap.get(currentDocNum) + 1);
                            } else {
                                //first occurrence of the word in this document
                                valueMap.put(currentDocNum, 1);
                            }
                        } // first occurrence of the word : add it to the collection
                        else {
                            index.getCollectionData().put(w, new HashMap<String, Integer>());
                            index.getCollectionData().get(w).put(currentDocNum, 1);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }

        this.extractionTime = System.currentTimeMillis() - startTime;
        System.out.println("End of indexing.\n");
        return index;

    }

    public Index parseDirectory(String[] elementTags, JProgressBar jpBarFile, JProgressBar jpBarGlobal) {
        System.out.println("Beginning of indexing.\n");

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

        Map<String, Integer> valueMap = null;
        String currentDocNum = "";
        String[] words = null;
        SAXParserFactory factory = null;
        SAXParser saxParser;

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
                this.index.setN(index.getN() + 1);
                for (String elementTag : elementTags) {

                    saxParser = factory.newSAXParser();
                    ArticleXMLParserElement articleParserElement = new ArticleXMLParserElement(elementTag);
                    saxParser.parse(f.getAbsolutePath(), articleParserElement);

                    currentDocNum = articleParserElement.getId().toString();

                    words = (articleParserElement.getText().toString()).split("\\W");

                    this.index.getDlMap().put(currentDocNum, words.length);

                    for (String w : words) {

                        // JProgressBar
                        percent = (100 * currentWordNumber) / words.length;
                        jpBarFile.setValue(percent);
                        jpBarGlobal.setValue(deltaPBGlobal + (percent / (nbFiles + 1)));
                        currentWordNumber++;
                        //--

                        w = w.toLowerCase();
                        if (!w.isEmpty() && (!Stopwords.isStopword(w))) {
                            valueMap = index.getCollectionData().get(w);
                            boolean isTermFrequencyFound = false;
                            // the word is already in the collection
                            if (valueMap != null) {
                                for (int i = 0; i < valueMap.size(); i++) {
                                    // the word has been already found in the current document
                                    if (valueMap.containsKey(currentDocNum) && !isTermFrequencyFound) {
                                        valueMap.put(currentDocNum, valueMap.get(currentDocNum) + 1);
                                        isTermFrequencyFound = true;
                                    }
                                }
                                if (!isTermFrequencyFound) {
                                    //first occurrence of the word in this document
                                    valueMap.put(currentDocNum, 1);
                                }
                            } // first occurrence of the word : add it to the collection
                            else {
                                index.getCollectionData().put(w, new HashMap<String, Integer>());
                                index.getCollectionData().get(w).put(currentDocNum, 1);
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            factory = null;
        }

        this.extractionTime = System.currentTimeMillis() - startTime;
        //System.out.println("End of indexing.\n");

        return index;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String[] elementTags = {"article"};

        JProgressBar jp1 = new JProgressBar();
        JProgressBar jp2 = new JProgressBar();

        Index index = new ArticlesDirectoryXMLParser("../../coll10").parseDirectory(elementTags, jp1, jp2);

        IndexSerialization.serialize(index, "fileSerialization/index.serial");

        index = IndexDeserialization.deserialize("fileSerialization/index.serial");

        System.out.println("N : " + index.getN() );
        System.out.println("avdl : " + index.getAvdl());
        System.out.println("dl : " + index.getDlMap().toString());
        System.out.println("\n"+index.toString());
    }
}
