/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import javax.xml.validation.Validator;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import org.xml.sax.*;

public class XmlVirtualAccessMethodsTestCases extends XMLTestCase {

    private static boolean DEBUG = false;
    private static boolean THROW_VALIDATION_ERRORS = false;

    private JAXBContext ctx;

    private byte[] bytes = new byte[] {23,1,112,12,1,64,1,14,3,2};
    private Byte[] bigBytes = new Byte[] {23,1,112,12,1,64,1,14,3,2};

    public XmlVirtualAccessMethodsTestCases(String name) {
        super(name);
    }

    public String getName() {
        return "XML Virtual Access Methods: " + super.getName();
    }

    public void testBasicModel() throws Exception {
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(
            "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/basic-eclipselink-oxm.xml");

        Map<String, Object> ctxProperties = new HashMap<String, Object>();
        ctxProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);

        /**
         * Employee extensions:
         *      - salary (BigDecimal)
         *      - age (Integer)
         *      - batphone (PhoneNumber)
         *
         * PhoneNumber extensions:
         *      - ext (String)
         *      - countryCode (String)
         *      - forwards (LinkedList of PhoneNumber)
         */

        ctx = JAXBContextFactory.createContext(new Class[] {Employee.class, PhoneNumber.class}, ctxProperties);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(getControlObjectBasic(), marshalDoc);

        if (DEBUG) {
            System.out.println("*** TEST BASIC MODEL ***");
            m.marshal(getControlObjectBasic(), System.out);
        }

        String[] extensions = new String[] {
                "salary", "age", "batphone", "ext", "country-code", "forwards"
        };

        for (int i = 0; i < extensions.length; i++) {
            // Test that Extensions appear in the marshalled document
            NodeList nodeList = marshalDoc.getDocumentElement().getElementsByTagName(extensions[i]);
            boolean found = nodeList != null && nodeList.getLength() > 0;
            assertTrue("'" + extensions[i] + "' extension not found.", found);
        }

        // Test that all 'forwards' PhoneNumbers were marshalled
        NodeList nodeList = marshalDoc.getDocumentElement().getElementsByTagName("forwards");
        Node forwadsNode = nodeList.item(0);

        assertEquals("Incorrect number of 'forwards'.", 3, forwadsNode.getChildNodes().getLength());
    }

    public void testCompleteModel() throws Exception {
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(
            "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/eclipselink-oxm.xml");

        Map<String, Object> ctxProperties = new HashMap<String, Object>();
        ctxProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);

        /**
         * Extensions:
         *       - @directatt
         *       - directelem
         *       - nullpol
         *       - join
         *       - idref
         *       - xmllist
         *       - references
         *       - base64
         *       - hex
         *       - bigByteArray
         *       - myStringArray
         */

        ctx = JAXBContextFactory.createContext(new Class[] {ExtObjectRoot.class, ExtObjectA.class,
                ExtObjectB.class, ExtObjectC.class}, ctxProperties);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(getControlObjectComplete(), marshalDoc);

        if (DEBUG) {
            System.out.println("*** TEST COMPLETE MODEL ***");
            m.marshal(getControlObjectComplete(), System.out);
        }

        String[] extensions = new String[] {
                "directelem", "nullpol", "join", "idref", "xmllist", "references", "base64", "hex", /*"bigByteArray",*/ "myStringArray"
        };

        Node attNode = marshalDoc.getDocumentElement().getChildNodes().item(0).getChildNodes().item(0).getAttributes().getNamedItem("directatt");
        assertNotNull("'directatt' extension not found.", attNode);

        for (int i = 0; i < extensions.length; i++) {
            // Test that Extensions appear in the marshalled document
            NodeList nodeList = marshalDoc.getDocumentElement().getElementsByTagName(extensions[i]);
            boolean found = nodeList != null && nodeList.getLength() > 0;
            assertTrue("'" + extensions[i] + "' extension not found.", found);
        }

        Unmarshaller u = ctx.createUnmarshaller();
        ExtObjectRoot root = (ExtObjectRoot) u.unmarshal(marshalDoc);

        // Check that the Inverse Reference was set
        try {
            ExtObjectA a = root.flexObjectAs.get(0);
            ExtObjectB b = (ExtObjectB) a.get("join");
            List refs = (List) b.get("references");
            ExtObjectC c = (ExtObjectC) refs.get(0);
            assertTrue("Inverse Reference not set correctly.", c.get("inverse") == b);
        } catch (Exception e) {
            fail("Error testing inverse reference.");
        }

        // Check Binary formats
        String base64Str =
            XMLConversionManager.getDefaultXMLManager().convertObject(bytes, String.class, XMLConstants.BASE_64_BINARY_QNAME).toString();
        String hexStr =
            XMLConversionManager.getDefaultXMLManager().convertObject(bytes, String.class, XMLConstants.HEX_BINARY_QNAME).toString();

        Element docElement = marshalDoc.getDocumentElement();
        Element flexObjectAsElem = (Element) docElement.getChildNodes().item(0);
        Element base64Elem = (Element) flexObjectAsElem.getElementsByTagName("base64").item(0);
        Element hexElem = (Element) flexObjectAsElem.getElementsByTagName("hex").item(0);

        assertEquals(base64Str, base64Elem.getFirstChild().getNodeValue());
        assertEquals(hexStr, hexElem.getFirstChild().getNodeValue());
    }

    public void testSchemaValidationBasic() throws Exception {
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(
            "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/basic-eclipselink-oxm.xml");

        Map<String, Object> ctxProperties = new HashMap<String, Object>();
        ctxProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);

        ctx = JAXBContextFactory.createContext(new Class[] {Employee.class, PhoneNumber.class}, ctxProperties);

        validateAgainstSchema(ctx, getControlObjectBasic());
    }

    public void testSchemaValidationComplete() throws Exception {
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(
            "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/eclipselink-oxm.xml");

        Map<String, Object> ctxProperties = new HashMap<String, Object>();
        ctxProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);

        ctx = JAXBContextFactory.createContext(new Class[] {ExtObjectRoot.class, ExtObjectA.class,
                ExtObjectB.class, ExtObjectC.class}, ctxProperties);

        validateAgainstSchema(ctx, getControlObjectComplete());
    }

    private void validateAgainstSchema(JAXBContext ctx, Object obj) throws Exception {
        JAXBSource source = new JAXBSource(ctx, obj);

        // Generate Schemas
        StringOutputResolver sor = new StringOutputResolver();
        ctx.generateSchema(sor);

        if (DEBUG) {
            System.out.println("*** VALIDATE SCHEMA " + obj.getClass());
            System.out.println(sor.getSchemas());
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(obj, System.out);
        }

        // Build javax.xml.validation.Schema
        Schema schema;
        Source[] schemaSources = new StreamSource[sor.getSchemas().size()];
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        for (int i = 0; i < sor.getSchemas().size() ; i++) {
            StringReader r = new StringReader(sor.getSchemas().get(i));
            schemaSources[i] = new StreamSource(r);
        }
        schema = schemaFactory.newSchema(schemaSources);


        LogErrorHandler handler = new LogErrorHandler();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(handler);
        validator.validate(source);

        assertEquals("Unexpected number of validation errors thrown.", 0, handler.getErrorCount());
    }

    // ========================================================================

    private Object getControlObjectBasic() {
        PhoneNumber p1 = new PhoneNumber();
        p1.setId(1);
        p1.setAreaCode(613);
        p1.setNumber(2832684);
        p1.setType("H");

        PhoneNumber p2 = new PhoneNumber();
        p2.setId(2);
        p2.setAreaCode(613);
        p2.setNumber(2884613);
        p2.setType("W");

        PhoneNumber p3 = new PhoneNumber();
        p3.setId(3);
        p3.setAreaCode(613);
        p3.setNumber(8500210);
        p3.setType("C");

        Employee e = new Employee();
        e.id = 1;
        e.firstName = "Rick";
        e.lastName = "Barkhouse";
        e.phoneNumbers.add(p1);
        e.phoneNumbers.add(p2);
        e.phoneNumbers.add(p3);

        e.extensions.put("salary", new BigDecimal(151755.75));
        e.setInt("age", 35);
        e.setInt("dept", 14113);

        PhoneNumber p4 = new PhoneNumber();
        p4.setId(4);
        p4.setAreaCode(555);
        p4.setNumber(8884441);
        p4.setType("Secret");
        p4.putExt("ext", "666");
        p4.putExt("countryCode", "GOTHAM");

        e.extensions.put("batphone", p4);

        LinkedList<PhoneNumber> forwards = new LinkedList<PhoneNumber>();
        forwards.add(p1);
        forwards.add(p2);
        forwards.add(p3);
        p4.putExt("forwards", forwards);

        return e;
    }

    private Object getControlObjectComplete() {
        ExtObjectC objC = new ExtObjectC();
        objC.set("directatt", "456");
        objC.set("directelem", "Baz1");

        ExtObjectC objC2 = new ExtObjectC();
        objC2.set("directatt", "457");
        objC2.set("directelem", "Baz2");

        ExtObjectB objB = new ExtObjectB();
        objB.set("directatt", "z123");
        objB.set("directelem", "Bar");

        ArrayList<ExtObjectC> references = new ArrayList<ExtObjectC>();
        references.add(objC);
        references.add(objC2);
        objB.set("references", references);
        objC.set("inverse", objB);
        objC2.set("inverse", objB);

        ExtObjectA objA = new ExtObjectA();
        objA.set("directatt", "888");
        objA.set("directelem", "Foo");
        objA.set("nullpol", null);
        objA.set("join", objB);
        objA.set("idref", objB);
        objA.set("anEnum", "N");

        ArrayList<String> xmllist = new ArrayList<String>();
        xmllist.add("ONE"); xmllist.add("TWO"); xmllist.add("THREE");
        objA.set("xmllist", xmllist);

        ArrayList elements = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream xml = ClassLoader.getSystemClassLoader().getResourceAsStream(
                "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/basic-eclipselink-oxm.xml");
            Document doc = builder.parse(xml);
            Element rootElem = doc.getDocumentElement();
            NodeList children = rootElem.getChildNodes();
            for(int i = 0; i < children.getLength(); i++) {
                if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    elements.add(children.item(i));
                }
            }
        } catch(Exception ex) {}
        objA.set("mixed", elements);

        objA.set("base64", bytes);
        objA.set("hex", bytes);
        objA.set("bigByteArray", bigBytes);

        String[] s = new String[] {"one", "two", "three", "for"};
        objA.set("myStringArray", s);

        ExtObjectRoot root = new ExtObjectRoot();
        root.flexObjectAs.add(objA);
        root.flexObjectBs.add(objB);

        return root;
    }

    private class LogErrorHandler implements ErrorHandler {
        private int errorCount = 0;

        public void warning(SAXParseException exception) throws SAXException {
            errorCount++;
            if (DEBUG) {
                System.out.println("\nWARNING");
                System.out.println(exception.getMessage());
            }
            if (THROW_VALIDATION_ERRORS) throw exception;
        }

        public void error(SAXParseException exception) throws SAXException {
            errorCount++;
            if (DEBUG) {
                System.out.println("\nERROR");
                System.out.println(exception.getMessage());
            }
            if (THROW_VALIDATION_ERRORS) throw exception;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            errorCount++;
            if (DEBUG) {
                System.out.println("\nFATAL ERROR");
                exception.printStackTrace();
            }
            if (THROW_VALIDATION_ERRORS) throw exception;
        }

        public int getErrorCount() {
            return errorCount;
        }
    }

    private class StringOutputResolver extends SchemaOutputResolver {
        private HashMap<String, StringWriter> stringWriters;

        public StringOutputResolver() {
            stringWriters = new HashMap<String, StringWriter>();
        }

        @Override
        public Result createOutput(String arg0, String arg1) throws IOException {
            StringWriter sw = stringWriters.get(arg1);
            if (sw == null) {
                sw = new StringWriter();
                stringWriters.put(arg1, sw);
            }
            StreamResult sr = new StreamResult(sw);
            sr.setSystemId(arg1);
            return sr;
        }

        private List<String> getSchemas() {
            List<String> schemas = new ArrayList<String>();
            Iterator<StringWriter> it = stringWriters.values().iterator();
            while (it.hasNext()) {
                StringWriter sw = it.next();
                schemas.add(sw.toString());
            }
            return schemas;
        }
    }

}