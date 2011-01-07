/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - 1.0M9 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.type;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

public class ElementWithBuiltInTypeNameTestCases extends SDOTestCase {
	private static String SCHEMA_PATH = "org/eclipse/persistence/testing/sdo/schemas/elementWithBuiltInTypeName/";
	
	public ElementWithBuiltInTypeNameTestCases(String name) {
		super(name);
	}
	
    public static void main(String[] args) throws Exception {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.type.ElementWithBuiltInTypeNameTestCases" };
        TestRunner.main(arguments);
    }

    public void testSingleSchema() {
        HelperContext hc = HelperProvider.getDefaultContext();
        Element[] schemas = new Element[1];
        if ((schemas[0] = getDOM(SCHEMA_PATH + "schema1.xsd")) == null) {
        	fail("Couldn't get document element from schema [" + SCHEMA_PATH + "schema1.xsd]");
        }
        try {
        	SDOElementResolver resolver = new SDOElementResolver(schemas);
        	((SDOXSDHelper) hc.getXSDHelper()).define(new DOMSource(schemas[0], SCHEMA_PATH + "schema1.xsd"), resolver);
        } catch (Exception x) {
        	fail();
        }
    }

    public void testSchemaWithImports() {
        HelperContext hc = HelperProvider.getDefaultContext();
        
        Element[] schemas = new Element[3];
        if ((schemas[0] = getDOM(SCHEMA_PATH + "schema0.xsd")) == null) {
        	fail("Couldn't get document element from schema [" + SCHEMA_PATH + "schema0.xsd]");
        }
        if ((schemas[1] = getDOM(SCHEMA_PATH + "schema1.xsd")) == null) {
        	fail("Couldn't get document element from schema [" + SCHEMA_PATH + "schema1.xsd]");
        }
        if ((schemas[2] = getDOM(SCHEMA_PATH + "schema2.xsd")) == null) {
        	fail("Couldn't get document element from schema [" + SCHEMA_PATH + "schema2.xsd]");
        }
        
	    SDOElementResolver resolver = new SDOElementResolver(schemas);
		
        try {
		    ((SDOXSDHelper) hc.getXSDHelper()).define(new DOMSource(schemas[0], SCHEMA_PATH + "schema0.xsd"), resolver);
        } catch (Exception x) {
        	fail("An exception occurred defining types for schema [" + SCHEMA_PATH + "schema0.xsd]");
        }
        try {
		    ((SDOXSDHelper) hc.getXSDHelper()).define(new DOMSource(schemas[1], SCHEMA_PATH + "schema1.xsd"), resolver);
	    } catch (Exception x) {
        	fail("An exception occurred defining types for schema [" + SCHEMA_PATH + "schema1.xsd]");
	    }
	    try {
		    ((SDOXSDHelper) hc.getXSDHelper()).define(new DOMSource(schemas[2], SCHEMA_PATH + "schema2.xsd"), resolver);
		} catch (Exception x) {
        	fail("An exception occurred defining types for schema [" + SCHEMA_PATH + "schema2.xsd]");
		}
		
	    String testMsg1 = " <tns:RetUri xmlns:tns=\"http://tempuri.org/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "   <tns:inUri>http://test/wsip</tns:inUri>\n" + "  </tns:RetUri>";
	    try {
	    	XMLDocument doc = hc.getXMLHelper().load(testMsg1);
	    	if (!doc.getRootElementName().equals("RetUri")) {
	    		fail("Loaded document root element name [" + doc.getRootElementName() + "] is incorrect.  Expected [RetUri]");
	    	}
	    	if (!doc.getRootElementURI().equals("http://tempuri.org/")) {
	    		fail("Loaded document root element uri [" + doc.getRootElementURI() + "] is incorrect.  Expected [http://tempuri.org/]");
	    	}
	    } catch (Exception x) {
        	fail("An exception occurred performing a load operation for [" + testMsg1 + "]");
	    }
    }

    private Element getDOM(String fileLoc) {
        try {
            FileInputStream is = new FileInputStream(fileLoc);
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            Document doc = fac.newDocumentBuilder().parse(is);
            return doc.getDocumentElement();
        } catch (Exception e) {
        }
        return null;
    }

    //================================================================================
    static class SDOElementResolver implements SchemaResolver {
        Element[] schemas = null;

        SDOElementResolver(Element[] schemas) {
            this.schemas = schemas;
        }

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            if (namespace.equals("http://tempuri.org/Imports")) {
                return new DOMSource(schemas[0], SCHEMA_PATH + "schema0.xsd");
            }
            if (namespace.equals("http://www.example.com/")) {
                return new DOMSource(schemas[2], SCHEMA_PATH + "schema1.xsd");
            }
            if (namespace.equals("http://tempuri.org/")) {
                return new DOMSource(schemas[1], SCHEMA_PATH + "schema2.xsd");
            }
            return null;
        }

        public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
            return null;
        }
    }
}
