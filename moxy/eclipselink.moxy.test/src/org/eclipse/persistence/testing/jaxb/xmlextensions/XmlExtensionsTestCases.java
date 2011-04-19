/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmlextensions;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlExtensionsTestCases extends XMLTestCase {

    private JAXBContext ctx;
    private Map<String, Object> ctxProperties;

    public XmlExtensionsTestCases(String name) {
        super(name);
    }

    public String getName() {
        return "XML Extensions: " + super.getName();
    }

    @Override
    protected void setUp() throws Exception {
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(
                "org/eclipse/persistence/testing/jaxb/xmlextensions/eclipselink-oxm.xml");

        ctxProperties = new HashMap<String, Object>();
        ctxProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);
    }

    public void testBasicModel() throws Exception {
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
        /**
         * Extensions:
         *       - @directatt
         *       - directelem
         *       - nullpol
         *       - join
         *       - idref
         *       - xmllist
         *       - references
         */

        ctx = JAXBContextFactory.createContext(new Class[] {ExtObjectRoot.class, ExtObjectA.class,
                ExtObjectB.class, ExtObjectC.class}, ctxProperties);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(getControlObjectComplete(), marshalDoc);

        String[] extensions = new String[] {
                "directelem", "nullpol", "join", "idref", "xmllist", "references"
        };

        Node attNode = marshalDoc.getDocumentElement().getChildNodes().item(0).getAttributes().getNamedItem("directatt");
        assertNotNull("'directatt' extension not found.", attNode);

        for (int i = 0; i < extensions.length; i++) {
            // Test that Extensions appear in the marshalled document
            NodeList nodeList = marshalDoc.getDocumentElement().getElementsByTagName(extensions[i]);
            boolean found = nodeList != null && nodeList.getLength() > 0;
            assertTrue("'" + extensions[i] + "' extension not found.", found);
        }
    }

    /*
    public void testXmlExtensionsSchemaElements() throws Exception {
        ctx.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName)
                    throws IOException {
                StreamResult s = new StreamResult(System.out);
                return s;
            }
        });
    }

    public void testXmlExtensionsSchemaAny() throws Exception {
        ctx.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName)
                    throws IOException {
                StreamResult s = new StreamResult(System.out);
                return s;
            }
        });
    }
    */

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
        objB.set("directatt", "123");
        objB.set("directelem", "Bar");

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

        ArrayList<Object> mixed = new ArrayList<Object>();
        mixed.add("Hello ");
        mixed.add(new JAXBElement<String>(new QName("myNamespace", "title"), String.class, objA.getClass(), "MR"));
        mixed.add(new JAXBElement<String>(new QName("myNamespace", "name"), String.class, objA.getClass(), "Bob Dobbs"));
        mixed.add(", your point balance is ");
        mixed.add(new JAXBElement<BigInteger>(new QName("myNamespace", "rewardPoints"), BigInteger.class, objA.getClass(), BigInteger.valueOf(175)));
        mixed.add("Visit www.rewards.com!");
        objA.set("mixed", mixed);

        byte[] bytes = new byte[] {23,1,112,12,1,64,1,14,3,2};
        objA.set("base64", bytes);
        objA.set("hex", bytes);

        ArrayList<ExtObjectC> references = new ArrayList<ExtObjectC>();
        references.add(objC);
        references.add(objC2);
        objB.set("references", references);
        objC.set("inverse", objB);
        objC2.set("inverse", objB);

        ExtObjectRoot root = new ExtObjectRoot();
        root.flexObjectAs.add(objA);
        root.flexObjectBs.add(objB);

        return root;
    }

}