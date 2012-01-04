package parsers;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArticleXMLParserElement extends DefaultHandler {

    private StringBuffer text;
    private boolean isInElementTag;
    private int isInIdTag;
    private StringBuffer id;
    private String elementTag;

    public ArticleXMLParserElement(String elementTag) {

        this.elementTag = elementTag;

        text = new StringBuffer();
        id = new StringBuffer();

        isInElementTag = false;
        isInIdTag = 0;

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        //<header><id>
        if (qName.equalsIgnoreCase("id") && isInIdTag == 0) {
            isInIdTag = 1;
        }
        else if (qName.equalsIgnoreCase(elementTag)) {
            isInElementTag = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        //<header></id>
        if (qName.equalsIgnoreCase("id")) {
            isInIdTag = 2;
        }
        else if (qName.equalsIgnoreCase(elementTag)) {
            isInElementTag = false;
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (isInIdTag == 1) {
            id.append(ch, start, length);
        }
        if (isInElementTag) {
            text.append(ch, start, length);
        }
    }

    public StringBuffer getText() {
        return text;
    }

    public StringBuffer getId() {
        return id;
    }

    public void setId(StringBuffer id) {
        this.id = id;
    }
}
