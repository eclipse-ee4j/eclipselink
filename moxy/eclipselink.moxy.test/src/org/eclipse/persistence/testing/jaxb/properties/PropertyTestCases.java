/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
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
        m.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER));

        m.setProperty(SUN_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(SUN_NAMESPACE_PREFIX_MAPPER));
        m.setProperty(SUN_JSE_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, m.getProperty(SUN_JSE_NAMESPACE_PREFIX_MAPPER));
    }

    public void testMarshallerIndentString() throws Exception {
        String SUN_INDENT_STRING = "com.sun.xml.bind.indentString";
        String SUN_JSE_INDENT_STRING = "com.sun.xml.internal.bind.indentString";

        String myTab = "\t";
        m.setProperty(MarshallerProperties.INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(MarshallerProperties.INDENT_STRING));
        m.setProperty(SUN_INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(SUN_INDENT_STRING));
        m.setProperty(SUN_JSE_INDENT_STRING, myTab);
        assertEquals(myTab, m.getProperty(SUN_JSE_INDENT_STRING));
    }

    public void testMarshallerCharacterEscapeHandler() throws Exception {
        String SUN_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
        String SUN_JSE_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

        CharacterEscapeHandler handler = new CustomCharacterEscapeHandler();
        m.setProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER, handler);
        assertEquals(handler, m.getProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER));
        m.setProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER, null);
        assertNull(m.getProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER));

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

    public void testUnmarshallerIdResolver() throws Exception {
        String SUN_ID_RESOLVER = "com.sun.xml.bind.IDResolver";
        String SUN_JSE_ID_RESOLVER = "com.sun.xml.internal.bind.IDResolver";

        IDResolver resolver = new MyIDResolver();
        u.setProperty(UnmarshallerProperties.ID_RESOLVER, resolver);
        assertEquals(resolver, u.getProperty(UnmarshallerProperties.ID_RESOLVER));

        Object nonELResolver = new NonELIDResolver();
        u.setProperty(SUN_ID_RESOLVER, nonELResolver);
        assertEquals(nonELResolver, u.getProperty(SUN_ID_RESOLVER));
        u.setProperty(SUN_JSE_ID_RESOLVER, nonELResolver);
        assertEquals(nonELResolver, u.getProperty(SUN_JSE_ID_RESOLVER));
    }

}
