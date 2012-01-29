package parsers;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArticleXMLParser extends DefaultHandler {

    private StringBuffer text;
    private ArrayList<String> paths;
    private StringBuffer id;
    private ArrayList<Couple> currentPath;
    private ArrayList<Couple> currentCompressedPath;
    private int currentXMLDepth;
    private int isInIdTag;
    private Map<Integer, Couple> XMLTree;
    private int XMLTreeId;
    private Map<String, Integer> N;
    private int maximumDepth = 5;
    private Map<String, Integer> dlMap;
    private boolean isInArticle = false;
    private boolean isInHeader = false;
    private boolean isInBody = false;
    private boolean isInSec = false;
    private boolean isInP = false;
    /*
     * Map of links
     */
    private Map<String, ArrayList<String>> pagerank;
    //1 if id has been already filled else 0
    private int idIsSet;

    public ArticleXMLParser() {
        this.text = new StringBuffer();
        this.paths = new ArrayList<>();
        this.id = new StringBuffer();

        this.currentPath = new ArrayList<>();
        this.currentCompressedPath = new ArrayList<>();
        this.currentXMLDepth = 1;

        this.XMLTree = new HashMap<>();
        this.pagerank = new HashMap<>();
        this.XMLTreeId = 0;
        this.idIsSet = 0;
        this.isInIdTag = 0;

        this.N = new HashMap<>();

        this.dlMap = new HashMap<>();

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        String pathWithoutN = "";
        String compressedPathWithoutN = "";

        //<header><id>
        if (qName.equalsIgnoreCase("id") && this.isInIdTag == 0) {
            this.isInIdTag = 1;
        } else if (qName.equalsIgnoreCase("link") && this.idIsSet == 1) {
            //<link xlink:type="simple" xlink:href="../262/9262.xml">
            String currentDocId = this.id.toString();
            String nameAttribute = "";
            String value = "";
            String[] valueSplitted = {};
            String linkedDocIdWithXML = "";
            String linkedDocIdWithoutXML = "";

            // Get the number of attribute
            int length = attributes.getLength();

            // Process each attribute
            for (int i = 0; i < length; i++) {
                // Get names and values for each attribute
                nameAttribute = attributes.getQName(i);

                if (nameAttribute.equals("xlink:href")) {

                    value = attributes.getValue(i);
                    valueSplitted = value.split("/");
                    linkedDocIdWithXML = valueSplitted[valueSplitted.length - 1];
                    linkedDocIdWithoutXML = linkedDocIdWithXML.replace(".xml", "");

                    if (this.pagerank.containsKey(linkedDocIdWithoutXML)) {
                        this.pagerank.get(linkedDocIdWithoutXML).add(currentDocId);
                    } else {
                        ArrayList<String> targettedDocumentId = new ArrayList<>();
                        targettedDocumentId.add(currentDocId);
                        this.pagerank.put(linkedDocIdWithoutXML, targettedDocumentId);
                    }

                }
            }

        }

        int n = 1;
        for (int i = 0; i < this.XMLTree.size(); i++) {
            if (this.currentXMLDepth == this.XMLTree.get(i).n && qName.equals(this.XMLTree.get(i).s)) {
                n++;
            }
        }

        this.XMLTree.put(this.XMLTreeId, new Couple(qName, this.currentXMLDepth));
        this.XMLTreeId++;
        this.currentXMLDepth++;

        switch (qName) {
            case "article":
                isInArticle = true;
                this.currentCompressedPath.add(new Couple("article", n));
                break;
            case "header":
                if (!isInBody) {
                    isInHeader = true;
                    this.currentCompressedPath.add(new Couple("header", n));
                }
                break;
            case "bdy":
                isInBody = true;
                this.currentCompressedPath.add(new Couple("bdy", n));
                break;
        }

        this.currentPath.add(new Couple(qName, n));

        if (!(isInArticle && !(isInHeader || isInBody)) || qName.equals("article")) {
            String path = "";
            String compressedPath = "";
            Couple c;
            int compressedN = 0;
            boolean found = false;
            if (!isInP) {
                for (int i = 0; i < this.currentPath.size(); i++) {
                    c = this.currentPath.get(i);
                    path = path + "/" + c.s + "[" + c.n + "]";
                    if (c.s.equals(this.currentCompressedPath.get(this.currentCompressedPath.size() - 1).s) && !found) {
                        compressedN = c.n;
                        found = true;
                    }
                }
                for (int i = 0; i < this.currentCompressedPath.size(); i++) {
                    c = this.currentCompressedPath.get(i);
                    compressedPath = compressedPath + "/" + c.s + "[" + c.n + "]";
                }
                pathWithoutN = path.substring(0, path.length() - 2 - Integer.toString(n).length());
                compressedPathWithoutN = compressedPath.substring(0, compressedPath.length() - 2 - Integer.toString(compressedN).length());;

                if (this.N.containsKey(compressedPathWithoutN) && qName.equals("bdy") && qName.equals("article") && qName.equals("header")) {
                    this.N.put(compressedPathWithoutN, this.N.get(compressedPathWithoutN).intValue() + 1);
                } else {
                    this.N.put(compressedPathWithoutN, 1);
                }

            }
            if (qName.equals("p")) {
                isInP = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //<header></id>
        if (qName.equalsIgnoreCase("id")) {
            this.isInIdTag = 2;
            this.idIsSet = 1;
        } else if (qName.equals("p")) {
            isInP = false;
        }

        switch (qName) {
            case "article":
                isInArticle = false;
                this.currentCompressedPath.remove(this.currentCompressedPath.size() - 1);
                break;
            case "header":
                if (!isInBody) {
                    isInHeader = false;
                    this.currentCompressedPath.remove(this.currentCompressedPath.size() - 1);
                }
                break;
            case "bdy":
                isInBody = false;
                this.currentCompressedPath.remove(this.currentCompressedPath.size() - 1);
                break;
        }

        this.currentPath.remove(this.currentPath.size() - 1);
        this.currentXMLDepth--;

        for (int i = this.XMLTree.size() - 1; i >= 0; i--) {
            if (this.currentXMLDepth < this.XMLTree.get(i).n) {
                this.XMLTree.remove(i);
                this.XMLTreeId--;
            }
        }


    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (isInIdTag == 1) {
            this.id.append(ch, start, length);
            this.text.append(ch, start, length);
            this.text.append(" ");
        } else {
            this.text.append(ch, start, length);
            this.text.append(" ");
        }

        StringBuilder line = new StringBuilder();
        line.append(ch, start, length);

        String words[] = line.toString().split("[\\W]+");

        String compressedPath = "";
        String uncompressedPath = "";
        Couple c;

        for (int i = 0; i < this.currentPath.size(); i++) {
            c = this.currentPath.get(i);
            uncompressedPath = uncompressedPath + "/" + c.s + "[" + c.n + "]";
        }

        for (int i = 0; i < this.currentCompressedPath.size() - 1; i++) {
            c = this.currentCompressedPath.get(i);
            compressedPath = compressedPath + "/" + c.s + "[" + c.n + "]";
        }
        c = this.currentCompressedPath.get(this.currentCompressedPath.size() - 1);
        compressedPath = compressedPath + "/" + c.s;

        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                if (this.dlMap.get(compressedPath) != null) {
                    this.dlMap.put(compressedPath, this.dlMap.get(compressedPath) + 1);
                } else {
                    this.dlMap.put(compressedPath, 1);
                }
                this.paths.add(uncompressedPath);
            }
        }
    }

    public StringBuffer getText() {
        return this.text;
    }

    public StringBuffer getId() {
        return this.id;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public Map<String, Integer> getN() {
        return N;
    }

    public Map<String, Integer> getDlMap() {
        return dlMap;
    }

    public Map<String, ArrayList<String>> getPagerank() {
        return pagerank;
    }

    private static class Couple {

        String s;
        int n;

        public Couple(String s, int n) {
            this.s = s;
            this.n = n;
        }

        public void inc() {
            this.n++;
        }

        public String toString() {
            return this.s + "[" + n + "]";
        }
    }
}
