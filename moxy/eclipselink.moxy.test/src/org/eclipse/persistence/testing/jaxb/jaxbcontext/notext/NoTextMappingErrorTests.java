/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      rbarkhouse - 2013 June 24 - 2.5.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.notext;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

/**
 * Tests throwing a context creation exception in cases where an @XmlAttribute does
 * not reference a type that maps to text in XML.
 */
public class NoTextMappingErrorTests extends junit.framework.TestCase {

    private final static String ROOT_XMLATT = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-xmlatt.xml";
    private final static String ROOT_XMLATT_NOTEXT = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-xmlatt-notext.xml";
    private final static String ROOT_XMLATT_PLUS = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-xmlatt-plus.xml";
    private final static String ROOT_XMLPATH = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-xmlpath.xml";
    private final static String ROOT_XMLPATH_NOTEXT = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-xmlpath-notext.xml";
    private final static String ROOT_INTERMEDIATE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-intermediate.xml";
    private final static String ROOT_INTERMEDIATE_NOTEXT = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-intermediate-notext.xml";
    private final static String ROOT_INHERITANCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-inheritance.xml";
    private final static String ROOT_INHERITANCE_NOTEXT = "org/eclipse/persistence/testing/jaxb/jaxbcontext/notext/root-inheritance-notext.xml";

    public String getName() {
        return "No Text Mapping Error Tests: " + super.getName();
    }

    public void testXmlAttribute() throws Exception {
        // Root has @XmlAttribute of type TypeWithText
        positiveTest(ROOT_XMLATT);
    }

    public void testNoTextXmlAttribute() throws Exception {
        // Root has @XmlAttribute of type TypeWithNoText
        negativeTest(ROOT_XMLATT_NOTEXT);
    }

    public void testXmlAttributeXmlValueOnly() throws Exception {
        // Root has @XmlAttribute of type TypeWithTextPlus
        //      TypeWithTextPlus has an @XmlValue, but also an @XmlAttribute
        negativeTest(ROOT_XMLATT_PLUS);
    }

    public void testXmlPath() throws Exception {
        // Root has @XmlPath(value="@root-prop") of type TypeWithText
        positiveTest(ROOT_XMLPATH);
    }

    public void testNoTextXmlPath() throws Exception {
        // Root has @XmlPath(value="@root-prop") of type TypeWithNoText
        negativeTest(ROOT_XMLPATH_NOTEXT);
    }

    public void testIntermediate() throws Exception {
        // Root has @XmlAttribute of type IntermediateType
        //      IntermediateType has @XmlAttribute of type TypeWithText
        positiveTest(ROOT_INTERMEDIATE);
   }

    public void testNoTextIntermediate() throws Exception {
        // Root has @XmlAttribute of type IntermediateType
        //      IntermediateType has @XmlAttribute of type TypeWithNoText
        negativeTest(ROOT_INTERMEDIATE_NOTEXT);
    }

    public void testInheritance() throws Exception {
        // Root has @XmlAttribute of type TypeSubWithNoText (which extends TypeSuperWithNoText)
        //      TypeSubWithNoText has no text mapping, but its superclass does
        positiveTest(ROOT_INHERITANCE);
    }

    public void testInheritanceNoText() throws Exception {
        // Root has @XmlAttribute of type TypeSubWithNoText (which extends TypeSuperWithNoText)
        //      TypeSuperWithNoText has no text mapping
        negativeTest(ROOT_INHERITANCE_NOTEXT);
    }

    // ============================================================================================

    private void positiveTest(String bindings) throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Root.class }, getBindings(bindings));
    }

    private void negativeTest(String bindings) throws Exception {
        try {
            JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Root.class }, getBindings(bindings));
        } catch (JAXBException e) {
            if (e.getLinkedException() != null && e.getLinkedException() instanceof org.eclipse.persistence.exceptions.JAXBException) {
                org.eclipse.persistence.exceptions.JAXBException je = (org.eclipse.persistence.exceptions.JAXBException) e.getLinkedException();
                assertEquals(org.eclipse.persistence.exceptions.JAXBException.MUST_MAP_TO_TEXT, je.getErrorCode());
                return;
            } else {
                throw e;
            }
        }
        fail("Expected exception was not thrown.");
    }

    private HashMap<String, Object> getBindings(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(resource);

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);

        return properties;
    }

}
