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
    private int currentXMLDepth;
    private int isInIdTag;
    private Map<Integer, Couple> XMLTree;
    private int XMLTreeId;
    private Map<String, Integer> N;
    private int maximumDepth = 5;
    private Map<String, Integer> dlMap;    
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
        this.currentXMLDepth = 1;
        
        this.XMLTree = new HashMap<>();
        this.pagerank = new HashMap<>();
        this.XMLTreeId = 0;
        this.idIsSet  = 0;
        this.isInIdTag = 0;
        
        this.N = new HashMap<>();
        
        this.dlMap = new HashMap<>();
        
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //<header><id>
        if (qName.equalsIgnoreCase("id") && this.isInIdTag == 0) {
            this.isInIdTag = 1;
        }else if (qName.equalsIgnoreCase("link") && this.idIsSet == 1) {
            //<link xlink:type="simple" xlink:href="../262/9262.xml">
            String currentDocId = this.id.toString();
            String nameAttribute ="";
            String value = "";
            String[] valueSplitted ={};
            String linkedDocIdWithXML ="";
            String linkedDocIdWithoutXML="";
            
            // Get the number of attribute
            int length = attributes.getLength();
            
            // Process each attribute
            for (int i=0; i<length; i++) {
                // Get names and values for each attribute
                nameAttribute = attributes.getQName(i);
                
                if(nameAttribute.equals("xlink:href")){
                    
                    value = attributes.getValue(i);
                    valueSplitted = value.split("/");
                    linkedDocIdWithXML = valueSplitted[valueSplitted.length-1];
                    linkedDocIdWithoutXML = linkedDocIdWithXML.replace(".xml", "");
                    
                    if(this.pagerank.containsKey(linkedDocIdWithoutXML)){
                        this.pagerank.get(linkedDocIdWithoutXML).add(currentDocId);
                    }else{
                        ArrayList<String> targettedDocumentId = new ArrayList<>();
                        targettedDocumentId.add(currentDocId);
                        this.pagerank.put(linkedDocIdWithoutXML, targettedDocumentId);
                    }
                    
                }
            }
            
        }
        
        int n = 1;
        for (int i = 0; i < this.XMLTree.size(); ++i) {
            if (this.currentXMLDepth == this.XMLTree.get(i).n && qName.equals(this.XMLTree.get(i).s)) {
                n++;
            }
        }
        
        this.XMLTree.put(this.XMLTreeId, new Couple(qName, this.currentXMLDepth));
        this.XMLTreeId++;
        this.currentXMLDepth++;
        
        this.currentPath.add(new Couple(qName, n));
        
        String path = "";
        Couple c;
        for (int i = 0; i < this.currentPath.size() && i < maximumDepth; ++i) {
            c = this.currentPath.get(i);
            path = path + "/" + c.s + "[" + c.n + "]";
        }
        
        path = path.substring(0, path.length() - 2 - Integer.toString(n).length());
        
        if (this.N.containsKey(path)) {
            this.N.put(path, this.N.get(path).intValue() + 1);
        } else {
            this.N.put(path, 1);
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //<header></id>
        if (qName.equalsIgnoreCase("id")) {
            this.isInIdTag = 2;
            this.idIsSet = 1;
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
        String path = "";
        Couple c;
        for (int i = 0; i < this.currentPath.size() && i < maximumDepth; i++) {
            c = this.currentPath.get(i);
            if (i == this.currentPath.size() - 1 || i == maximumDepth - 1) {
                path = path + "/" + c.s;
            } else {
                path = path + "/" + c.s + "[" + c.n + "]";
            }
        }
        
        Integer integer = null;
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                integer = this.dlMap.get(path);
                if (integer != null) {
                    this.dlMap.put(path, integer + 1);
                } else {
                    this.dlMap.put(path, 1);
                }
                this.paths.add(path);
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
    }
}
