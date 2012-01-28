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
    
    public ArticleXMLParser() {
        this.text = new StringBuffer();
        this.paths = new ArrayList<>();
        this.id = new StringBuffer();
        
        this.currentPath = new ArrayList<>();
        this.currentXMLDepth = 1;
        
        this.XMLTree = new HashMap<>();
        this.XMLTreeId = 0;
        
        this.isInIdTag = 0;
        
        this.N = new HashMap<>();
        
        this.dlMap = new HashMap<>();
        
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //<header><id>
        if (qName.equalsIgnoreCase("id") && this.isInIdTag == 0) {
            this.isInIdTag = 1;
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
        
        this.currentPath.add(new Couple(qName, n));
        
        String path = "";
        Couple c;
        for (int i = 0; i < this.currentPath.size() && i < maximumDepth; i++) {
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
        }
        
        this.currentPath.remove(this.currentPath.size() - 1);
        this.currentXMLDepth--;

        // System.out.println("Fin " + qName + " | Depth : " + this.currentXMLDepth + " | TreeId " + this.XMLTreeId);

        
        for (int i = this.XMLTree.size() - 1; i >= 0; i--) {
            if (this.currentXMLDepth < this.XMLTree.get(i).n) {
                //System.out.println("Removing ===> Depth : " + this.currentXMLDepth + " -- s : " + this.XMLTree.get(i).s);
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

        //System.out.println(words.length + " " + line);
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
        
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                if (this.dlMap.get(path) != null) {
                    this.dlMap.put(path, this.dlMap.get(path) + 1);
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
