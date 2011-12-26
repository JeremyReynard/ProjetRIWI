package parsers;

import index.Couple;
import index.Index;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JProgressBar;

public class ArticlesDirectoryTextParser {

    private String[] filesList;
    private String dirPath;
    private Index index;
    private long extractionTime;
    private int docCount;

    public ArticlesDirectoryTextParser(String dirPath, String[] filesList) {

        this.filesList = filesList;
        this.dirPath = dirPath;
        this.index = new Index();
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public void extract(JProgressBar jpBarFile, JProgressBar jpBarGlobal) {   
        
        this.docCount = 0;
        int nbFiles = this.filesList.length;

        Charset UTF8 = Charset.forName("UTF-8");
        int currentDocNum = 0;
        String[] tabString;
        String word = null;

        Path currentPath = null;
        String line = null;
        int currentLine = 0;
        int nbLines = 0;   
        
        int deltaPBGlobal = 0;
        int percent = 0;

        List<Couple> valueMap = null;        
        
        long startTime = System.currentTimeMillis();
        for (int f = 0; f < nbFiles; ++f) {   
            
            currentPath = Paths.get(this.dirPath + "/" + this.filesList[f]);
            jpBarFile.setString(this.filesList[f]);
            jpBarGlobal.setString("Global : " + (f + 1) + " / " + nbFiles);
            
            try (BufferedReader reader = Files.newBufferedReader(currentPath, UTF8)) {
                line = null;
                nbLines = countNBLines(currentPath);
                
                currentLine = 0;                
                deltaPBGlobal = 100 * f / nbFiles;
                
                while ((line = reader.readLine()) != null) {
                    percent = (100 * currentLine) / nbLines;
                    jpBarFile.setValue(percent);
                    jpBarGlobal.setValue(deltaPBGlobal + (percent / nbFiles) );                    
                    currentLine++;                   
                    
                    // if the line is just a \n do nothing
                    if (line.length() > 0) {
                        // docno line
                        if (line.contains("<doc><docno>")) {
                            line = line.replace("<doc><docno>", "").replace("</docno>", "");
                            currentDocNum = Integer.parseInt(line);
                            this.docCount++;
                        } // others lines
                        else if (!(line.contains("</doc>"))) {
                             // Punctuation & digit
                            line = line.trim().replaceAll("[\\d\\W]", " ");
                           
                            tabString = null;

                            tabString = line.split("[ ]+");
                            for (int i = 0; i < tabString.length; ++i) {
                                // lowercase
                                word = tabString[i].toLowerCase();

                                valueMap = this.index.getCollectionData().get(word);

                                // the word is already in the collection
                                if (valueMap != null) {
                                    // the word has been already found in the current document
                                    if (valueMap.get(0).getDocumentNumber() == currentDocNum) {
                                        valueMap.get(0).setTermFrequency(valueMap.get(0).getTermFrequency()+1);
                                    } else {
                                        //first occurrence of the word in this document									
                                        valueMap.add(0, new Couple(currentDocNum, 1));
                                    }
                                } // first occurrence of the word : add it to the collection
                                else {
                                    this.index.getCollectionData().put(word, new ArrayList<Couple>());
                                    this.index.getCollectionData().get(word).add(0, new Couple(currentDocNum, 1));
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("Parsing error !");
                //e.printStackTrace();
            }
        }
        this.extractionTime = System.currentTimeMillis() - startTime;

        //this.export();
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

    public String showResults() {

        long sec = (this.extractionTime + 500) / 1000;

        String result = "";
        result += "Indexation time " + sec + "sec\n";
        result += "Nb docs : " + this.docCount + "\n";
        result += "Nb words " + this.index.getCollectionData().size() + "\n";

        return result;
    }

    public String showResults(String word) {

        String result = "";
        int number = this.getNumberOccurences(word);
        result += "Nb occurs : " + number + "\n";

        return result;
    }

    public int getNumberOccurences(String word) {

        if (this.index.getCollectionData().containsKey(word)) {
            int number = 0;
            Iterator<Couple> it = this.index.getCollectionData().get(word).iterator();

            while (it.hasNext()) {
                number += (it.next().getTermFrequency());
            }

            return number;
        }
        return 0;
    }

    public int getNumberDoc() {

        return this.docCount;
    }

    public void export() {

        String export = this.docCount + " " + this.extractionTime + " (" + this.index.getCollectionData().size() + ")\n";

        this.export(export);
    }

    private void export(String str) {
        Charset UTF8 = Charset.forName("UTF-8");
        Path sourcePath = Paths.get("export.txt");

        try (BufferedWriter writer =
                        Files.newBufferedWriter(
                        sourcePath,
                        UTF8,
                        new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND})) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}