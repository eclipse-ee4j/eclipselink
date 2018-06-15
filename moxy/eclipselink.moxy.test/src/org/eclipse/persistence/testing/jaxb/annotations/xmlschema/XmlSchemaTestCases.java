/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Ondrej Cerny
package org.eclipse.persistence.testing.jaxb.annotations.xmlschema;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.testing.jaxb.annotations.xmlschema.model.base.BaseObject;
import org.eclipse.persistence.testing.jaxb.annotations.xmlschema.model.extended.ExtendedObject;
import org.eclipse.persistence.testing.jaxb.annotations.xmlschema.model.extended.ExtensionWrapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlSchemaTestCases {
    private static final String JAXB_FACTORY_KEY = "javax.xml.bind.context.factory";
    private static final String MOXY_JAXB_FACTORY = "org.eclipse.persistence.jaxb.JAXBContextFactory";
    private static String jaxbFactoryBackup;

    @BeforeClass
    public static void setJAXBProvider() {
        jaxbFactoryBackup = System.getProperty(JAXB_FACTORY_KEY);
        System.setProperty(JAXB_FACTORY_KEY, MOXY_JAXB_FACTORY);
    }

    @AfterClass
    public static void unsetJAXBProvider() {
        if (jaxbFactoryBackup == null) {
            System.clearProperty(JAXB_FACTORY_KEY);
        } else {
            System.setProperty(JAXB_FACTORY_KEY, jaxbFactoryBackup);
        }
    }

    @Test
    public void testValidateOutput() throws Exception {
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(createObject(), writer);
        createValidator().validate(
                new StreamSource(new StringReader(buff.toString())));
    }

    @Test
    public void testRoundTrip() throws Exception {
        final ExtensionWrapper data = createObject();
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(data, writer);
        final Unmarshaller um = ctx.createUnmarshaller();
        Object result = um.unmarshal(new StringReader(buff.toString()));
        assertEquals(data, result);
    }

    private JAXBContext createContext() throws JAXBException {
        return JAXBContext.newInstance(BaseObject.class.getPackage().getName() + ":" +
                ExtendedObject.class.getPackage().getName());
    }

    private ExtensionWrapper createObject() {
        ExtensionWrapper w = new ExtensionWrapper();
        w.wrapperStringField = "outerText";
        w.wrapperObjectField = new ExtendedObject();
        w.wrapperObjectField.stringField = "innerText";
        return w;
    }

    public Validator createValidator() throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema articleSchema = factory.newSchema(new File(Thread.currentThread().getContextClassLoader().
                getResource("org/eclipse/persistence/testing/jaxb/annotations/xmlschema/extended.xsd").toURI()));
        return articleSchema.newValidator();
    }
}
