package xmlParser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArticleParser extends DefaultHandler {

    private StringBuffer accumulator;
    
    
    public ArticleParser() {
        accumulator = new StringBuffer();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("article")) {
            System.out.println("New article");
        }
        if(qName.equalsIgnoreCase("header")){
            System.out.println("New head");
        }
        if(qName.equalsIgnoreCase("title")){
            accumulator.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("article")) {
            System.out.println("End of article");
        }
        if(qName.equalsIgnoreCase("header")){
            System.out.println("End of header");
        }
        if(qName.equalsIgnoreCase("title")){
            System.out.println("Title = "+ accumulator.toString());
            //TODO Add Title in Index
            
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        accumulator.append(ch, start, length);
    }

    public static void main(String[] args) {
        System.out.println("Parsing du fichier");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            ArticleParser ArticleParser = new ArticleParser();
            saxParser.parse("2166.xml", ArticleParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
