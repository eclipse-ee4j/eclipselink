package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nillable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

public class ListPropertyNillableElementTestCases extends LoadAndSaveTestCases {
    static String SCHEMA_NAME = "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/nillable/ListPropertyTest.xsd";

    public ListPropertyNillableElementTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nillable.ListPropertyNillableElementTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getRootInterfaceName() {
        return "ListPropertyTestType";
    }

    @Override
    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/nillable/ListPropertyNillableElementTest.xml";
    }

    @Override
    protected String getControlRootName() {
        return "ListPropertyTest";
    }
    
    @Override
    protected String getControlRootURI() {
        return "http://www.example.org";
    }
    
    @Override
    protected String getSchemaName() {
        return SCHEMA_NAME;
    }

    @Override
    protected String getNoSchemaControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/nillable/ListPropertyNillableElementTestNoSchema.xml";
    }
        
    @Override
    protected void registerTypes() {}
    @Override
    public void testClassGenerationLoadAndSave() throws Exception {}
    
    // ====== CUSTOM TESTS DERIVED FROM THE SDO 2.1.1 TCK ====== //
    /**
     * 'child' property is nillable, so null is allowed
     */
    public void testValidSetNullInList() throws Exception {
        try {
            Element[] schemas = new Element[1];
            schemas[0] = getDOM(getSchemaName());

            SDOElementResolver resolver = new SDOElementResolver(schemas);
            List<SDOType> types = ((SDOXSDHelper) aHelperContext.getXSDHelper()).define(new DOMSource(schemas[0], getSchemaName()), resolver);
            assertEquals(3, types.size());
            
            XMLDocument doc = aHelperContext.getXMLHelper().load(new FileInputStream(new File(getControlFileName())));

            DataObject rootObj = doc.getRootObject();
            DataObject childDo = rootObj.getDataObject("childContain");
            List listTest = childDo.getList("child");

            assertNotNull(listTest);
            assertEquals(2, listTest.size());
            listTest.add(null);
            assertEquals(3, listTest.size());
            
            // DEBUGGING
            //hc.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName(), System.out);
            
            String xml = aHelperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName());
            assertTrue(xml.indexOf("xsi:nil=\"true\"") > 0);
        } catch (Exception xxx) {
            xxx.printStackTrace();
            throw xxx;
        }
    }

    /**
     * 'kid' property is not nillable, so null is not allowed
     */
    public void testInvalidSetNullInList() throws Exception {
        try {
            Element[] schemas = new Element[1];
            schemas[0] = getDOM(getSchemaName());

            SDOElementResolver resolver = new SDOElementResolver(schemas);
            List<SDOType> types = ((SDOXSDHelper) aHelperContext.getXSDHelper()).define(new DOMSource(schemas[0], getSchemaName()), resolver);
            assertEquals(3, types.size());
            
            XMLDocument doc = aHelperContext.getXMLHelper().load(new FileInputStream(new File(getControlFileName())));

            DataObject rootObj = doc.getRootObject();
            DataObject childDo = rootObj.getDataObject("childContain");
            List listTest = childDo.getList("kid");

            assertNotNull(listTest);
            assertEquals(2, listTest.size());
            boolean exceptionOccurred = false;
            try {
                listTest.add(null);
            } catch (UnsupportedOperationException uoe) {
                exceptionOccurred = true;
            }
            
            assertTrue("An UnsupportedOperationException did not occur as expected (adding null to non-nillable list is not allowed)", exceptionOccurred);
            assertEquals(2, listTest.size());
            aHelperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName());
        } catch (Exception xxx) {
            xxx.printStackTrace();
            throw xxx;
        }
    }

    /**
     * 'child' property is nillable, so a null entry in the collection to be
     * added is allowed.
     */
    public void testAddAllSetNullInList() throws Exception {
        try {
            Element[] schemas = new Element[1];
            schemas[0] = getDOM(getSchemaName());

            SDOElementResolver resolver = new SDOElementResolver(schemas);
            List<SDOType> types = ((SDOXSDHelper) aHelperContext.getXSDHelper()).define(new DOMSource(schemas[0], getSchemaName()), resolver);
            assertEquals(3, types.size());
            
            XMLDocument doc = aHelperContext.getXMLHelper().load(new FileInputStream(new File(getControlFileName())));

            DataObject rootObj = doc.getRootObject();
            DataObject childDo = rootObj.getDataObject("childContain");
            List listTest = childDo.getList("child");
            
            Collection kids = new ArrayList();
            kids.add(listTest.get(0));
            kids.add(null);
            kids.add(listTest.get(1));

            boolean exceptionOccurred = false;
            try {
                listTest.addAll(kids);
            } catch (UnsupportedOperationException uoe) {
                exceptionOccurred = true;
            }
            
            assertFalse("An unexpected UnsupportedOperationException occurred", exceptionOccurred);
            aHelperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName());
        } catch (Exception xxx) {
            xxx.printStackTrace();
            throw xxx;
        }
    }

    /**
     * 'kid' property is not nillable, so a null entry in the collection to be
     * added is not allowed.
     */
    public void testInvalidAddAllSetNullInList() throws Exception {
        try {
            Element[] schemas = new Element[1];
            schemas[0] = getDOM(getSchemaName());

            SDOElementResolver resolver = new SDOElementResolver(schemas);
            List<SDOType> types = ((SDOXSDHelper) aHelperContext.getXSDHelper()).define(new DOMSource(schemas[0], getSchemaName()), resolver);
            assertEquals(3, types.size());
            
            XMLDocument doc = aHelperContext.getXMLHelper().load(new FileInputStream(new File(getControlFileName())));

            DataObject rootObj = doc.getRootObject();
            DataObject childDo = rootObj.getDataObject("childContain");
            List listTest = childDo.getList("kid");
            
            Collection kids = new ArrayList();
            kids.add(listTest.get(0));
            kids.add(null);
            kids.add(listTest.get(1));

            boolean exceptionOccurred = false;
            try {
                listTest.addAll(kids);
            } catch (UnsupportedOperationException uoe) {
                exceptionOccurred = true;
            }
            
            assertTrue("An UnsupportedOperationException did not occur as expected (adding a collection with a null entry to non-nillable list property is not allowed)", exceptionOccurred);
            aHelperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName());
        } catch (Exception xxx) {
            xxx.printStackTrace();
            throw xxx;
        }
    }

    // ====== CONVENIENCE METHODS & CLASSES ====== //
    static Element getDOM(String fileLoc) throws Exception {
        FileInputStream is = new FileInputStream(fileLoc);
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            Document doc = fac.newDocumentBuilder().parse(is);
            return doc.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class SDOElementResolver implements SchemaResolver {
        Element[] schemas = null;

        SDOElementResolver(Element[] schemas) {
            this.schemas = schemas;
        }

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            if (namespace.equals("http://www.example.org")) {
                return new DOMSource(schemas[0], SCHEMA_NAME);
            }
            return null;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    }
}