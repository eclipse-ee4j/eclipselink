/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.testing.osgi.beanvalidation;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.osgi.OSGITestHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * Marshal/unmarshal tests in OSGi container without installed bean validation implementation bundles.
 * The purpose of these tests is to make sure that javax.validation import is optional.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NoBeanValidationTest extends BaseBeanValidationTest {
    private static JAXBContext jaxbContext;

    @Configuration
    public static Option[] config() {
        return OSGITestHelper.getDefaultOptions();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null);
    }

    @Test
    public void testMarshalValid() throws JAXBException {
        StringWriter xml = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(createValidCustomer(), xml);
        assertTrue(xml.toString().contains(CUSTOMER_VALID_XML));
    }

    @Test
    public void testMarshalInvalid() throws JAXBException {
        StringWriter xml = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(createInvalidCustomer(), xml);
        assertTrue(xml.toString().contains(CUSTOMER_INVALID_XML));
    }

    @Test
    public void testUnmarshalValid() throws JAXBException {
        StringReader xml = new StringReader(CUSTOMER_VALID_XML);
        Customer testCustomer = (Customer) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertTrue(createValidCustomer().equals(testCustomer));
    }

    @Test
    public void testUnmarshalInvalid() throws JAXBException {
        StringReader xml = new StringReader(CUSTOMER_INVALID_XML);
        Customer testCustomer = (Customer) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertTrue(createInvalidCustomer().equals(testCustomer));
    }
}
