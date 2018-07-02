/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//  - rbarkhouse - 08 March 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.properties;

import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.employee.Employee;
import org.eclipse.persistence.testing.jaxb.idresolver.MyIDResolver;
import org.eclipse.persistence.testing.jaxb.sun.idresolver.NonELIDResolver;
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
        try{
           m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, null);
        }catch(PropertyException pException){
            return;
        }
        fail("A PropertyException should have been thrown");
    }

    public void testMarshallerEncoding() throws Exception {
        m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        assertEquals("ISO-8859-1", m.getProperty(Marshaller.JAXB_ENCODING));
        m.setProperty(Marshaller.JAXB_ENCODING, null);
        assertNull( m.getProperty(Marshaller.JAXB_ENCODING));
    }

    public void testMarshallerSchemaLoc() throws Exception {
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "/scratch/jdoe/schemas");
        assertEquals("/scratch/jdoe/schemas", m.getProperty(Marshaller.JAXB_SCHEMA_LOCATION));
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, null);
        assertNull( m.getProperty(Marshaller.JAXB_SCHEMA_LOCATION));
    }

    public void testMarshallerNoNamespaceSchemaLoc() throws Exception {
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "/scratch/jdoe/schemas");
        assertEquals("/scratch/jdoe/schemas", m.getProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION));
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, null);
        assertNull( m.getProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION));
    }

    public void testMarshallerJaxbFragment() throws Exception {
        m.setProperty(XMLConstants.JAXB_FRAGMENT, true);
        assertTrue((Boolean) m.getProperty(XMLConstants.JAXB_FRAGMENT));
        m.setProperty(XMLConstants.JAXB_FRAGMENT, false);
        assertFalse((Boolean) m.getProperty(XMLConstants.JAXB_FRAGMENT));
        try{
            m.setProperty(Marshaller.JAXB_FRAGMENT, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
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

        m.setProperty(SUN_NAMESPACE_PREFIX_MAPPER, null);
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

        m.setProperty(SUN_JSE_INDENT_STRING, null);
        assertNull( m.getProperty(SUN_JSE_INDENT_STRING));

        m.setProperty(SUN_INDENT_STRING, null);
        assertNull( m.getProperty(SUN_INDENT_STRING));
    }

    public void testMarshallerCharacterEscapeHandler() throws Exception {
        String SUN_CHARACTER_ESCAPE_HANDLER_MARSHALLER = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
        String SUN_JSE_CHARACTER_ESCAPE_HANDLER_MARSHALLER = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

        CharacterEscapeHandler handler = new CustomCharacterEscapeHandler();
        m.setProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER, handler);
        assertEquals(handler, m.getProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER));
        m.setProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER, null);
        assertNull(m.getProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER));

        m.setProperty(SUN_CHARACTER_ESCAPE_HANDLER_MARSHALLER, handler);
        assertEquals(handler, m.getProperty(SUN_CHARACTER_ESCAPE_HANDLER_MARSHALLER));
        m.setProperty(SUN_CHARACTER_ESCAPE_HANDLER_MARSHALLER, null);
        assertNull(m.getProperty(SUN_CHARACTER_ESCAPE_HANDLER_MARSHALLER));

        m.setProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER_MARSHALLER, handler);
        assertEquals(handler, m.getProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER_MARSHALLER));
        m.setProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER_MARSHALLER, null);
        assertNull(m.getProperty(SUN_JSE_CHARACTER_ESCAPE_HANDLER_MARSHALLER));
    }

    public void testMarshallerCharacterEscapeHandler2() throws Exception {
        String SUN_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.bind.characterEscapeHandler";
        String SUN_JSE_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.internal.bind.characterEscapeHandler";

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

        try{
            m.setProperty(XML_DECLARATION, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testMarshallerXmlHeaders() throws Exception {
        String XML_HEADERS = "com.sun.xml.bind.xmlHeaders";
        String HEADER_STRING = "ABC";

        m.setProperty(XML_HEADERS, HEADER_STRING);
        assertEquals(HEADER_STRING, m.getProperty(XML_HEADERS));
        m.setProperty(XML_HEADERS, null);
        assertNull(m.getProperty(XML_HEADERS));

    }

    public void testMarshallerObjectIdentity() throws Exception {
        String prop = "com.sun.xml.bind.objectIdentitityCycleDetection";

        m.setProperty(prop, false);
        assertFalse((Boolean) m.getProperty(prop));
        m.setProperty(prop, true);
        assertTrue((Boolean) m.getProperty(prop));

        try{
            m.setProperty(prop, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");

    }

    public void testMarshallerMediaTypeEnum() throws Exception {
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));

        try{
            m.setProperty(MarshallerProperties.MEDIA_TYPE, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testMarshallerMediaTypeString() throws Exception {
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));
    }

    public void testMarshallerJsonAttributePrefix() throws Exception {
        m.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        assertEquals("@", m.getProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX));
        m.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, null);
        assertNull( m.getProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX));
    }

    public void testMarshallerJsonIncludeRoot() throws Exception {
        // Must set media type to JSON, otherwise get will always return true
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        assertTrue((Boolean) m.getProperty(MarshallerProperties.JSON_INCLUDE_ROOT));
        m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        assertFalse((Boolean) m.getProperty(MarshallerProperties.JSON_INCLUDE_ROOT));

        try{
            m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testMarshallerJsonNamespaceSeparator() throws Exception {
        assertEquals (XMLConstants.DOT, m.getProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR));
        m.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, '&');
        assertEquals ('&', m.getProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR));
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals ('&', m.getProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR));
         try{
             m.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, null);
          }catch(PropertyException pException){
              return;
          }
          fail("A PropertyException should have been thrown");
    }


    public void testUnmarshallerJsonNamespaceSeparator() throws Exception {
        assertEquals (XMLConstants.DOT, m.getProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR));
        u.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, '&');
        assertEquals ('&', u.getProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR));
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals ('&', u.getProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR));
        try{
            u.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testInvalidValue() throws Exception {
        try{
            m.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, "mySep");
        }catch(PropertyException pException){
            assertTrue(pException.getCause() instanceof ClassCastException);
            return;
        }
        fail("A PropertyException should have occurred");
    }


    public void testMarshallerJsonValueWrapper() throws Exception {
        m.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "wrapper");
        assertEquals("wrapper", m.getProperty(MarshallerProperties.JSON_VALUE_WRAPPER));
    }

    public void testMarshallerJsonValueWrapperNull() throws Exception {
        try{
            m.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, null);
        }catch(PropertyException e){
            return;
        }catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    public void testMarshallerJsonValueWrapperEmptyString() throws Exception {
        try{
            m.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "");
        }catch(PropertyException e){
                return;
            }catch(Exception e){
                e.printStackTrace();
                fail();
            }
    }

    public void testUnmarshallerMediaTypeString() throws Exception {
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));
        try{
            u.setProperty(MarshallerProperties.MEDIA_TYPE, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testUnmarshallerMediaTypeEnum() throws Exception {
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));
    }

    public void testUnmarshallerJsonAttributePrefix() throws Exception {
        u.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        assertEquals("@", u.getProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX));
        u.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, null);
        assertNull( u.getProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX));
    }

    public void testUnmarshallerJsonIncludeRoot() throws Exception {
        // Must set media type to JSON, otherwise get will always return true
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");

        u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
        assertTrue((Boolean) u.getProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT));
        u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        assertFalse((Boolean) u.getProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT));

        try{
            u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, null);
         }catch(PropertyException pException){
             return;
         }
         fail("A PropertyException should have been thrown");
    }

    public void testUnmarshallerJsonNamespacePrefixMapper() throws Exception {
        NamespacePrefixMapper mapper = new MyPrefixMapper();
        u.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, mapper);
        assertEquals(mapper, u.getProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("mynamespace", "ns1");
        u.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, map);
        assertEquals(map, u.getProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER));

        u.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, null);
        assertNull( u.getProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER));
    }

    public void testUnmarshallerJsonValueWrapper() throws Exception {
        u.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "wrapper");
        assertEquals("wrapper", u.getProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER));

        u.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, null);
        assertNull( u.getProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER));
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

        u.setProperty(SUN_JSE_ID_RESOLVER, null);
        assertNull( u.getProperty(SUN_JSE_ID_RESOLVER));
    }

}
