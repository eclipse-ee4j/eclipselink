package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementRefNillStringTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/stringNill.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/stringNill.json";
    
    public XmlElementRefNillStringTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class [] {Root.class});
    }

    @Override
    protected Object getControlObject() {
        Root r = new Root();
        r.foo = null;
        r.bar = new JAXBElement<String>(new QName("bar"), String.class, null);
        r.bar.setNil(true);
        
        return r;
    }

}
