package org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

public class XmlWriteOnlyOverrideTestCases extends JAXBTestCases  {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlwriteonly/employee.xml";
    public XmlWriteOnlyOverrideTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
    }
    

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setTypes(new Class[] {Employee.class});
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.writeOnlyField = "Write Only Data";
        return emp;
    }
    
    protected Map getProperties() throws Exception{
        
        String bindings = 
            "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"> " +
                "<java-types>" + 
                    "<java-type name=\"org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly.Employee\">" + 
                        "<java-attributes>" + 
                            "<xml-element java-attribute=\"writeOnlyField\" write-only=\"false\"/>" + 
                         "</java-attributes>" + 
                   "</java-type>" + 
                "</java-types>" + 
             "</xml-bindings>";

        DOMSource src = null;
        try {             
            Document doc = parser.parse(new ByteArrayInputStream(bindings.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }
            
        HashMap<String, Source> overrides = new HashMap<String, Source>();
        overrides.put("org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly", src);
        HashMap properties = new HashMap();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    } 
}
