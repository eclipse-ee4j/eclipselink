package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1.Container;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2.Unmappable;
import org.w3c.dom.Document;

public class ClassLevelTestCases extends JAXBTestCases {
    protected Map getProperties() throws Exception{

        InputStream inStream1 = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/unmappable/package1/no-adapter.xml");
        InputStream inStream2 = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/unmappable/package2/class-adapter.xml");
        Map<String, Source> metadata = new LinkedHashMap<String, Source>();
        DOMSource src1 = null;
        DOMSource src2 = null;
        try {             
            Document doc1 = parser.parse(inStream1);
            Document doc2 = parser.parse(inStream2);
            src1 = new DOMSource(doc1.getDocumentElement());
            src2 = new DOMSource(doc2.getDocumentElement());
        } catch(Exception e){
            e.printStackTrace();
            fail("An error occurred during setup");            
        }
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1", src1);
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2", src2);
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);
        
        return properties;
    }
    
    public ClassLevelTestCases(String name) throws Exception {
        super(name);
        setUp();
        setTypes(new Class[]{Container.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/unmappable/container.xml");
    }
    
    public Object getControlObject() {
        Container container = new Container();
        container.setContainerProperty(Unmappable.getInstance("aaa"));
        return container;
    }
}