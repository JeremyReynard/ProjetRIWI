package extractor;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Extractor {

    private String[] filesList;
    private String dirPath;
    private Map<String, List<Couple>> collection;
    private long extractionTime;
    private int docCount;

    public Extractor(String dirPath, String[] filesList) {

        this.filesList = filesList;
        this.dirPath = dirPath;
        this.collection = new HashMap<>();
    }

    public void extract() {

        this.docCount = 0;

        System.out.println("==================== Début de l'extraction ====================");
        System.out.println("Fichiers (" + filesList.length + ") :");

        Charset UTF8 = Charset.forName("UTF-8");
        int currentDocNum = 0;
        String[] tabString;
        String word = null;

        Path currentPath = null;
        String line = null;

        List<Couple> valueMap = null;

        long startTime = System.currentTimeMillis();
        for (int f = 0; f < this.filesList.length; ++f) {
            currentPath = Paths.get(this.dirPath + "/" + this.filesList[f]);
            System.out.println(currentPath);
            try (BufferedReader reader = Files.newBufferedReader(currentPath, UTF8)) {
                line = null;
                while ((line = reader.readLine()) != null) {
                    // if the line is just a \n do nothing
                    if (line.length() > 0) {
                        // docno line
                        if (line.contains("<doc><docno>")) {
                            line = line.replace("<doc><docno>", "");
                            line = line.replace("</docno>", "");
                            currentDocNum = Integer.parseInt(line);
                            this.docCount++;
                        } // others lines
                        else if (!(line.contains("</doc>"))) {
                            line = line.trim();
                            // Punctuation & digit
                            line = line.replaceAll("[\\d\\W]", " ");
                            tabString = null;

                            tabString = line.split("[ ]+");
                            for (int i = 0; i < tabString.length; ++i) {
                                word = tabString[i];

                                // lowercase
                                word = word.toLowerCase();
                                valueMap = this.collection.get(word);

                                // the word is already in the collection
                                if (valueMap != null) {
                                    // the word has been already found in the current document
                                    if (valueMap.get(0).docNum == currentDocNum) {
                                        valueMap.get(0).nbOcc++;
                                    } else {
                                        //first occurrence of the word in this document									
                                        valueMap.add(0, new Couple(currentDocNum, 1));
                                    }
                                } // first occurrence of the word : add it to the collection
                                else {
                                    this.collection.put(word, new ArrayList<Couple>());
                                    this.collection.get(word).add(0, new Couple(currentDocNum, 1));
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("Erreur pendant le parsing du fichier !");
                e.printStackTrace();
            }
        }
        this.extractionTime = System.currentTimeMillis() - startTime;

        System.out.println();
        System.out.println("==================== Extraction terminée ====================");

        this.export();
    }

    public String showResults() {

        long sec = (this.extractionTime + 500) / 1000;

        String result = "";
        result += "Temps d'indexation : " + sec + "sec\n";
        result += "Nombre de docs traités : " + this.docCount + "\n";
        result += "Nombre de mots indéxés : " + this.collection.size() + "\n";

        return result;
    }

    public String showResults(String word) {

        String result = "";
        int number = this.getNumberOccurences(word);
        result += "Nb occurences total : " + number + "\n";

        return result;
    }

    public int getNumberOccurences(String word) {

        if (this.collection.containsKey(word)) {
            int number = 0;
            Iterator<Couple> it = this.collection.get(word).iterator();

            while (it.hasNext()) {
                number += (it.next().nbOcc);
            }

            return number;
        }
        return 0;
    }

    public int getNumberDoc() {

        return this.docCount;
    }

    public void export() {

        String export = this.docCount + " " + this.extractionTime + " (" + this.collection.size() + ")\n";

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