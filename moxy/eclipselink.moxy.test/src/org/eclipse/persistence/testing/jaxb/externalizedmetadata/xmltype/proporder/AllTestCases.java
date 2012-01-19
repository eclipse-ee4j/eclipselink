package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AllTestCases extends JAXBTestCases {

    private static final String OXM_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all-oxm.xml";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all.xml";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all.xsd";

    public AllTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Map getProperties() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, OXM_RESOURCE);
        return properties;
    }

    @Override
    protected Root getControlObject() {
        Root root = new Root();
        root.a = "A";
        root.b = "B";
        root.c = "C";
        return root;
    }

    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList(1);
        InputStream is = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }


}
