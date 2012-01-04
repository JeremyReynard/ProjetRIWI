package parsers;

import index.Index;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JProgressBar;

public class ArticlesDirectoryTextParser extends ArticlesDirectoryParser{

    private String[] filesList;

    public ArticlesDirectoryTextParser(String dirPath) {

        super(dirPath);
        // add all the files in the selected directory to list
        Path directory = Paths.get(dirPath);
        this.filesList = directory.toFile().list();
    }

    @SuppressWarnings("CallToThreadDumpStack")
    @Override
    public Index parseDirectory(JProgressBar jpBarFile, JProgressBar jpBarGlobal) {

        int nbWordsInDoc = 0;
        Map<String, Integer> mapDL = new HashMap<>();
        Map<String, Integer> valueMap = null;

        // JProgress Bar
        int nbFiles = this.filesList.length;
        int currentLine = 0;
        int nbLines = 0;
        int deltaPBGlobal = 0;
        int percent = 0;
        // ---

        Charset UTF8 = Charset.forName("UTF-8");
        String currentDocNum = "";
        String[] tabString;
        String word = null;

        Path currentPath = null;
        String line = null;

        long startTime = System.currentTimeMillis();
        for (int f = 0; f < nbFiles; ++f) {

            currentPath = Paths.get(this.directoryPath + "/" + this.filesList[f]);

            // JProgress Bar
            jpBarFile.setString(this.filesList[f]);
            jpBarGlobal.setString("Global : " + (f + 1) + " / " + (nbFiles + 1));
            currentLine = 0;
            deltaPBGlobal = 100 * f / nbFiles;
            // ---

            try (BufferedReader reader = Files.newBufferedReader(currentPath, UTF8)) {
                line = null;
                nbLines = countNBLines(currentPath);

                while ((line = reader.readLine()) != null) {

                    // JProgress Bar
                    percent = (100 * currentLine) / nbLines;
                    jpBarFile.setValue(percent);
                    jpBarGlobal.setValue(deltaPBGlobal + (percent / (nbFiles + 1)) );                    
                    currentLine++;
                    // ---

                    // if the line is just a \n do nothing
                    if (line.length() > 0) {
                        // docno line
                        if (line.contains("<doc><docno>")) {
                            currentDocNum = line.replace("<doc><docno>", "").replace("</docno>", "");
                            index.setN(index.getN() + 1);
                        } // others lines
                        else if (!(line.contains("</doc>"))) {
                            // Punctuation & digit                           
                            tabString = null;
                            
                            tabString = line.split("[\\W]+");

                            for (int i = 0; i < tabString.length; ++i) {
                                // lowercase
                                
                                word = tabString[i].toLowerCase();

                                if (!word.isEmpty() && (!Stopwords.isStopword(word))){

                                    nbWordsInDoc ++;
                                    
                                    valueMap = this.index.getCollectionData().get(word);

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
                                        this.index.getCollectionData().put(word, new HashMap<String, Integer>());
                                        this.index.getCollectionData().get(word).put(currentDocNum, 1);
                                    }
                                }
                            }
                        }
                        else {
                            mapDL.put(currentDocNum, nbWordsInDoc);
                            nbWordsInDoc = 0;
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("Parsing error !");
                //e.printStackTrace();
            }
        }
        index.setDlMap(mapDL);
        this.extractionTime = System.currentTimeMillis() - startTime;

        return (this.index);
    }

    private int countNBLines(Path path) {

        int count = 0;

        Charset UTF8 = Charset.forName("UTF-8");

        try {
            BufferedReader reader = Files.newBufferedReader(path, UTF8);

            while (reader.readLine() != null) {
                ++count;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return count;
    }
}