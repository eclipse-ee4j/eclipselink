/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

public class NameTransformerExceptionTestCases extends OXTestCase{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/nametransformerupper.xml";
    protected DocumentBuilder parser;
    public NameTransformerExceptionTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
         try {
             DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
             builderFactory.setNamespaceAware(true);
             builderFactory.setIgnoringElementContentWhitespace(true);
             parser = builderFactory.newDocumentBuilder();
         } catch (Exception e) {
             e.printStackTrace();
             fail("An exception occurred during setup");
         }

    }

    public void testExceptionDuringTransform(){
         Type[] types = new Type[1];
         types[0] = Employee.class;
         try{
             JAXBContext jaxbContext = JAXBContextFactory.createContext(types, getPropertiesWithException(), Thread.currentThread().getContextClassLoader());
         } catch (javax.xml.bind.JAXBException e) {
//             e.printStackTrace();

        Exception linkedException = (Exception) e.getLinkedException();
        Exception nestedExcpetion = (Exception) e.getCause();
             assertTrue(nestedExcpetion instanceof JAXBException);

             assertEquals(JAXBException.EXCEPTION_DURING_NAME_TRANSFORMATION,((JAXBException)nestedExcpetion).getErrorCode());
             return;
         } catch (Exception e) {
             fail("A JAXBException should have occurred but didn't");
         }
         fail("A JAXBException should have occurred but didn't");
    }

    public void testInvalidNameTransformer(){
        Type[] types = new Type[1];
        types[0] = Employee.class;
        try{
            JAXBContext jaxbContext = JAXBContextFactory.createContext(types, getProperties(), Thread.currentThread().getContextClassLoader());
        } catch (javax.xml.bind.JAXBException e) {
            Exception linkedException = (Exception) e.getLinkedException();
            Exception nestedExcpetion = (Exception) e.getCause();
            assertTrue(nestedExcpetion instanceof JAXBException);

            assertEquals(JAXBException.EXCEPTION_WITH_NAME_TRANSFORMER_CLASS,((JAXBException)nestedExcpetion).getErrorCode());
            return;
        } catch (Exception e) {
            fail("A JAXBException should have occurred but didn't");
        }
        fail("A JAXBException should have occurred but didn't");
    }

    protected Map getProperties() {

        Map overrides = new HashMap();
        String overridesString =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm' xml-name-transformer='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.MyNonExistentTransformer'>" +
        "<xml-schema namespace='myuri' />" +
        "<java-types/>" +
        "</xml-bindings>";

        DOMSource src = null;
        try {
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }

        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer", src);

        Map props = new HashMap();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }

protected Map getPropertiesWithException() throws Exception{

        Map overrides = new HashMap();
        String overridesString =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm' xml-name-transformer='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.TransformerWithException'>" +
        "<xml-schema namespace='myuri' />" +
        "<java-types/>" +
        "</xml-bindings>";

        DOMSource src = null;
        try {
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }

        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer", src);

        Map props = new HashMap();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }

}
