/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 08 March 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.properties;

import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.employee.Employee;
import org.eclipse.persistence.testing.jaxb.idresolver.MyIDResolver;
import org.eclipse.persistence.testing.jaxb.idresolver.NonELIDResolver;
import org.eclipse.persistence.testing.jaxb.prefixmapper.MyPrefixMapper;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.CustomCharacterEscapeHandler;


public class PropertyTestCases extends TestCase {

    private Marshaller m;
    private Unmarshaller u;

    public PropertyTestCases(String name) throws Exception {
        super(name);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Employee.class }, null);
        m = ctx.createMarshaller();
        u = ctx.createUnmarshaller(); 
    }

    public String getName() {
        return "JAXB set/getProperty Tests: " + super.getName();
    }

    public void testMarshallerFormattedOutput() throws Exception {
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        assertTrue((Boolean) m.getProperty(Marshaller.JAXB_FORMATTED_OUTPUT));
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        assertFalse((Boolean) m.getProperty(Marshaller.JAXB_FORMATTED_OUTPUT));
    }

    public void testMarshallerEncoding() throws Exception {
        m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        assertEquals("ISO-8859-1", m.getProperty(Marshaller.JAXB_ENCODING));
    }

    public void testMarshallerSchemaLoc() throws Exception {
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "/scratch/jdoe/schemas");
        assertEquals("/scratch/jdoe/schemas", m.getProperty(Marshaller.JAXB_SCHEMA_LOCATION));
    }

    public void testMarshallerNoNamespaceSchemaLoc() throws Exception {
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "/scratch/jdoe/schemas");
        assertEquals("/scratch/jdoe/schemas", m.getProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION));
    }

    public void testMarshallerJaxbFragment() throws Exception {
        m.setProperty(XMLConstants.JAXB_FRAGMENT, true);
        assertTrue((Boolean) m.getProperty(XMLConstants.JAXB_FRAGMENT));
        m.setProperty(XMLConstants.JAXB_FRAGMENT, false);
        assertFalse((Boolean) m.getProperty(XMLConstants.JAXB_FRAGMENT));
    }

    public void testMarshallerNamespacePrefixMapper() throws Exception {
        String SUN_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
        String SUN_JSE_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";

        NamespacePrefixMapper mapper = new MyPrefixMapper();
        m.setProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.NAMESPACE_PREFIX_MAPPER));

        m.setProperty(SUN_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(SUN_NAMESPACE_PREFIX_MAPPER));
        m.setProperty(SUN_JSE_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(SUN_JSE_NAMESPACE_PREFIX_MAPPER));
    }

    public void testMarshallerIndentString() throws Exception {
        String SUN_INDENT_STRING = "com.sun.xml.bind.indentString";
        String SUN_JSE_INDENT_STRING = "com.sun.xml.internal.bind.indentString";

        String myTab = "\t";
        m.setProperty(JAXBMarshaller.INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(JAXBMarshaller.INDENT_STRING));
        m.setProperty(SUN_INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(SUN_INDENT_STRING));
        m.setProperty(SUN_JSE_INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(SUN_JSE_INDENT_STRING));
    }

    public void testMarshallerCharacterEscapeHandler() throws Exception {
        String SUN_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
        String SUN_JSE_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

        CharacterEscapeHandler handler = new CustomCharacterEscapeHandler();
        m.setProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.CHARACTER_ESCAPE_HANDLER, handler);
        assertEquals(handler, m.getProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.CHARACTER_ESCAPE_HANDLER));
        m.setProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.CHARACTER_ESCAPE_HANDLER, null);
        assertNull(m.getProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.CHARACTER_ESCAPE_HANDLER));

        m.setProperty(SUN_CHARACTER_ESCAPE_HANDLER, handler);
        assertEquals(handler, m.getProperty(SUN_CHARACTER_ESCAPE_HANDLER));
        m.setProperty(SUN_CHARACTER_ESCAPE_HANDLER, null);
        assertNull(m.getProperty(SUN_CHARACTER_ESCAPE_HANDLER));

        m.setProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER, handler);
        assertEquals(handler, m.getProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER));
        m.setProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER, null);
        assertNull(m.getProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER));
    }

    public void testMarshallerXmlDeclaration() throws Exception {
        String XML_DECLARATION = "com.sun.xml.bind.xmlDeclaration";

        m.setProperty(XML_DECLARATION, true);
        assertTrue((Boolean) m.getProperty(XML_DECLARATION));
        m.setProperty(XML_DECLARATION, false);
        assertFalse((Boolean) m.getProperty(XML_DECLARATION));
    }

    public void testMarshallerMediaType() throws Exception {
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");
        assertEquals("application/json", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
    }

    public void testMarshallerJsonAttributePrefix() throws Exception {
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_ATTRIBUTE_PREFIX, "@");
        assertEquals("@", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_ATTRIBUTE_PREFIX));
    }

    public void testMarshallerJsonIncludeRoot() throws Exception {
        // Must set media type to JSON, otherwise get will always return true
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");

        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, true);
        assertTrue((Boolean) m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT));
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, false);
        assertFalse((Boolean) m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT));
    }

    public void testMarshallerJsonValueWrapper() throws Exception {
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_VALUE_WRAPPER, "wrapper");
        assertEquals("wrapper", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_VALUE_WRAPPER));
    }

    public void testUnmarshallerMediaType() throws Exception {
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");
        assertEquals("application/json", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
    }

    public void testUnmarshallerJsonAttributePrefix() throws Exception {
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_ATTRIBUTE_PREFIX, "@");
        assertEquals("@", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_ATTRIBUTE_PREFIX));
    }

    public void testUnmarshallerJsonIncludeRoot() throws Exception {
        // Must set media type to JSON, otherwise get will always return true
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");

        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, true);
        assertTrue((Boolean) u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT));
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, false);
        assertFalse((Boolean) u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT));
    }

    public void testUnmarshallerJsonNamespacePrefixMapper() throws Exception {
        NamespacePrefixMapper mapper = new MyPrefixMapper();
        u.setProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, u.getProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("mynamespace", "ns1");
        u.setProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, map);
        assertEquals(map, u.getProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER));
    }

    public void testUnmarshallerJsonValueWrapper() throws Exception {
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_VALUE_WRAPPER, "wrapper");
        assertEquals("wrapper", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_VALUE_WRAPPER));
    }

    public void testUnmarshallerIdResolver() throws Exception {
        String SUN_ID_RESOLVER = "com.sun.xml.bind.IDResolver";
        String SUN_JSE_ID_RESOLVER = "com.sun.xml.internal.bind.IDResolver";

        IDResolver resolver = new MyIDResolver();
        u.setProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.ID_RESOLVER, resolver);
        assertEquals(resolver, u.getProperty(org.eclipse.persistence.jaxb.JAXBUnmarshaller.ID_RESOLVER));

        Object nonELResolver = new NonELIDResolver();
        u.setProperty(SUN_ID_RESOLVER, nonELResolver);
        assertEquals(nonELResolver, u.getProperty(SUN_ID_RESOLVER));
        u.setProperty(SUN_JSE_ID_RESOLVER, nonELResolver);
        assertEquals(nonELResolver, u.getProperty(SUN_JSE_ID_RESOLVER));
    }

}
