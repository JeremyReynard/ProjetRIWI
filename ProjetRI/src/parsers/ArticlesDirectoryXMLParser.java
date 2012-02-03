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
import java.util.Iterator;
import java.util.Map;
import javax.swing.JProgressBar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import serialization.IndexSerialization;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class ArticlesDirectoryXMLParser extends ArticlesDirectoryParser {

    /**
     * the constructor
     * @param dirPath the path directory
     */
    public ArticlesDirectoryXMLParser(String dirPath) {

        super(dirPath);
    }

    /**
     * the parse directory function
     * @param jpBarFile used for JProgress bar
     * @param jpBarGlobal used for JProgress bar
     * @return the index
     */
    @Override
    public Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal) {

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

        Map<String, Map<String, Integer>> tempdlMap = new HashMap<>();

        long startTime = System.currentTimeMillis();
        ArrayList<String> valueMapDoc = null;
        Map<String, Integer> nMap = null;
        
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
                    nMap = index.getN();
                    if (nMap.containsKey(tag)) {
                        nMap.put(tag, nMap.get(tag) + articleParser.getN().get(tag));
                    } else {
                        nMap.put(tag, articleParser.getN().get(tag));
                    }
                }
                for (int i = 0; i < words.length; i++) {

                    w = words[i];
                    p = paths.get(i);

                    // JProgressBar
                    percent = (100 * currentWordNumber) / words.length;
                    jpBarFile.setValue(percent);
                    jpBarGlobal.setValue(deltaPBGlobal + (percent / (nbFiles + 1)));
                    currentWordNumber++;
                    //--

                    if (!w.isEmpty() && (!Stopwords.isStopword(w))) {
                        // lemmatization
                        w = w.toLowerCase();

                        valueMap = index.getCollectionData().get(w);

                        // the word is already in the collection
                        if (valueMap != null) {
                            // the word has been already found in the current document
                            if (valueMap.containsKey(currentDocNum)) {
                                valueMapDoc = valueMap.get(currentDocNum);
                                valueMapDoc.add(p);
                                valueMap.put(currentDocNum, valueMapDoc);
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
                }

                tempdlMap.put(articleParser.getId().toString(), articleParser.getDlMap());

                this.index.getPagerank().putAll(articleParser.getPagerank());

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }

        String docId, path;

        //fill in the dlMap
        for (Iterator i = tempdlMap.keySet().iterator(); i.hasNext();) {
            docId = i.next().toString();
            index.getDlMap().put(docId, new HashMap<String, Integer>());
            for (Iterator j = index.getN().keySet().iterator(); j.hasNext();) {
                path = j.next().toString();
                for (Iterator k = tempdlMap.get(docId).keySet().iterator(); k.hasNext();) {
                    p = k.next().toString();
                    if (p.startsWith(path)) {
                        if (index.getDlMap().get(docId).get(path) == null) {
                            index.getDlMap().get(docId).put(path, tempdlMap.get(docId).get(p));
                        } else {
                            index.getDlMap().get(docId).put(path, index.getDlMap().get(docId).get(path) + tempdlMap.get(docId).get(p));
                        }
                    }
                }
            }
        }

         //fill in the avdlMap
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            docId = i.next().toString();
            for (Iterator j = index.getDlMap().get(docId).keySet().iterator(); j.hasNext();) {
                path = j.next().toString();
                if (index.getAvdlMap().get(path) == null) {
                    index.getAvdlMap().put(path, index.getDlMap().get(docId).get(path).doubleValue());

                } else {
                    index.getAvdlMap().put(path, index.getAvdlMap().get(path) + index.getDlMap().get(docId).get(path).doubleValue());
                }
            }
        }
        for (Iterator i = index.getAvdlMap().keySet().iterator(); i.hasNext();) {
            path = i.next().toString();
            index.getAvdlMap().put(path, index.getAvdlMap().get(path).doubleValue() / index.getN(path));
        }

        this.extractionTime = System.currentTimeMillis() - startTime;
        System.out.println("End of indexation.\n");
        return index;

    }

}
