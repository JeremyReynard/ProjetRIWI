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

    public ArticleParser() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("article")) {
            System.out.println("New article");
        }
        if(qName.equalsIgnoreCase("header")){
            System.out.println("New head");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("article")) {
            System.out.println("End of the article");
        }
        if(qName.equalsIgnoreCase("header")){
            System.out.println("End of the head");
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
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
