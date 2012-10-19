package org.eclipse.persistence.testing.jaxb.jaxbelement.dom;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ElementTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/element.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/element.json";

    
    public ElementTestCases(String name) throws Exception {
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
            Element elm = doc.createElementNS("AGroupDef/annotation", "rootchild");
            elm.setTextContent("");
            JAXBElement  obj = factory.createRoot(elm);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception was thrown.");            
            return null;
        }
    }
    

}
