package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl.PropertyWrapper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class DynamicJAXBRefreshTestCases extends TestCase {

    private static final String OXM_METADATA = "org/eclipse/persistence/testing/jaxb/dynamic/metadata.xml";
    private static final String XSD_METADATA = "org/eclipse/persistence/testing/jaxb/dynamic/refresh.xsd";
    private static final String XML_RESOURCE_BEFORE = "org/eclipse/persistence/testing/jaxb/dynamic/before.xml";
    private static final String XML_RESOURCE_AFTER = "org/eclipse/persistence/testing/jaxb/dynamic/after.xml";

    public String getName() {
        return "Dynamic JAXB: Metadata Refresh: " + super.getName();
    }

    public void testRefreshOXM() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream metadataStream = classLoader.getResourceAsStream(OXM_METADATA);
        Document metadataDocument = db.parse(metadataStream);
        metadataStream.close();

        Map<String, Object> props = new HashMap<String, Object>(1);
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataDocument);

        DynamicJAXBContext jc = DynamicJAXBContextFactory.createContextFromOXM(classLoader, props);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        DynamicEntity controlRoot = jc.newDynamicEntity("org.eclipse.persistence.testing.jaxb.dynamic.Root");
        controlRoot.set("name", "R");

        InputStream xmlBeforeStream = classLoader.getResourceAsStream(XML_RESOURCE_BEFORE);
        DynamicEntity rootBefore = (DynamicEntity) unmarshaller.unmarshal(xmlBeforeStream);
        assertEquals(controlRoot.get("name"), rootBefore.get("name"));

        Element xmlElementElement = (Element) metadataDocument.getElementsByTagNameNS("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element").item(0);
        xmlElementElement.setAttribute("name", "after-name");
        JAXBHelper.getJAXBContext(jc).refreshMetadata();

        InputStream xmlAfterStream = classLoader.getResourceAsStream(XML_RESOURCE_AFTER);
        DynamicEntity rootAfter = (DynamicEntity) unmarshaller.unmarshal(xmlAfterStream);
        assertEquals(controlRoot.get("name"), rootAfter.get("name"));
    }

    public void testRefreshSchemaNode() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Get XSD as Element
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XSD_METADATA);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Element xsdElement = xsdDocument.getDocumentElement();

        DynamicJAXBContext jc = DynamicJAXBContextFactory.createContextFromXSD(xsdElement, null, classLoader, null);

        DynamicEntity controlRoot = jc.newDynamicEntity("Root");
        Exception caughtException = null;
        try {
            controlRoot.set("age", "10");
        } catch (Exception e) {
            caughtException = e;
        }

        // Should have caught an exception because we haven't added 'age' to
        // the metadata yet
        assertNotNull(caughtException);

        // Modify METADATA and REFRESH
        Element newElem = xsdDocument.createElementNS("http://www.w3.org/2001/XMLSchema", "element");
        newElem.setAttribute("name", "age");
        newElem.setAttribute("type", "xs:string");
        Element sequence = (Element) xsdElement.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "sequence").item(0);
        sequence.appendChild(newElem);
        JAXBHelper.getJAXBContext(jc).refreshMetadata();

        caughtException = null;
        try {
            controlRoot.set("age", "10");
        } catch (Exception e) {
            caughtException = e;
        }

        // Should NOT have caught an exception because 'age'
        // is now available
        assertNull(caughtException);
    }

    public void testRefreshSchemaInputStream() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream metadataStream = classLoader.getResourceAsStream(XSD_METADATA);

        DynamicJAXBContext jc = DynamicJAXBContextFactory.createContextFromXSD(metadataStream, null, classLoader, null);

        Exception caughtException = null;
        try {
            JAXBHelper.getJAXBContext(jc).refreshMetadata();
        } catch (Exception e) {
            caughtException = e;
        }

        assertNotNull("Refresh Exception was not thrown as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", JAXBException.class, caughtException.getClass());
        assertEquals("Incorrect exception thrown.", JAXBException.CANNOT_REFRESH_METADATA, ((JAXBException) caughtException).getErrorCode());
    }

}
