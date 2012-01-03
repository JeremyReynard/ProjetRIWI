package parsers;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArticleXMLParser extends DefaultHandler {

    private StringBuffer text;
    private boolean isInTitleTag;
    private int isInIdTag;
    private boolean isInCategoryTag;
    private boolean isInBodyTag;
    private StringBuffer id;

    public ArticleXMLParser() {
        text = new StringBuffer();
        id = new StringBuffer();

        isInTitleTag = false;
        isInIdTag = 0;
        isInCategoryTag = false;
        isInBodyTag = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //<header><title>
        if (qName.equalsIgnoreCase("title")) {
            isInTitleTag = true;
        }
        //<header><id>
        else if (qName.equalsIgnoreCase("id") && isInIdTag == 0) {
            isInIdTag = 1;
        }
        //<header><categories><category>
        else if (qName.equalsIgnoreCase("category")) {
            isInCategoryTag = true;
        }
        else if (qName.equalsIgnoreCase("bdy")) {
            isInBodyTag = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //<header></title>
        if (qName.equalsIgnoreCase("title")) {
            text.append(" ");
            isInTitleTag = false;
        }
        //<header></id>
        else if (qName.equalsIgnoreCase("id")) {
            isInIdTag = 2;
        }
        //<header><categories></category>
        else if (qName.equalsIgnoreCase("category")) {
            isInCategoryTag = false;
        }
        else if (qName.equalsIgnoreCase("bdy")) {
            isInBodyTag = false;
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (isInTitleTag) {
            text.append(ch, start, length);
        }
        if (isInIdTag == 1) {
            id.append(ch, start, length);
        }
        if (isInCategoryTag) {
            text.append(ch, start, length);
        }
        if (isInBodyTag) {
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
