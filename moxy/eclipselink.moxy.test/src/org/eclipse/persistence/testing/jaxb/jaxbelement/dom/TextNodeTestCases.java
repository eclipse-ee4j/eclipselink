package org.eclipse.persistence.testing.jaxb.jaxbelement.dom;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TextNodeTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/text.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/text.json";

    
    public TextNodeTestCases(String name) throws Exception {
        super(name);
        setContextPath("org.eclipse.persistence.testing.jaxb.jaxbelement.dom");
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        ObjectFactory factory = new ObjectFactory();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();       
            Element elm = doc.createElementNS(null, "abcdef");
            elm.setTextContent("thetext");
            Object value = elm;
            JAXBElement  obj = factory.createDoc(value);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception was thrown.");            
            return null;
        }
    }
    

}
