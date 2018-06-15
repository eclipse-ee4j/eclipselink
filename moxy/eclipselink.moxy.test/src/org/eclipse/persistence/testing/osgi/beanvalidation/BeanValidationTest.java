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

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.osgi.OSGITestHelper;
import org.hibernate.validator.HibernateValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Marshal/unmarshal + validation tests in OSGi container with installed bean validation implementation bundles.
 * The purpose of these tests is to make sure that bean validation works in OSGi environment.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BeanValidationTest extends BaseBeanValidationTest {
    private static JAXBContext jaxbContext;

    @Configuration
    public static Option[] config() {
        return OSGITestHelper.getOptionsWithBeanValidation();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null);
    }

    @Test(expected = BeanValidationException.class)
    public void testMarshalInvalid() throws JAXBException {
        StringWriter xml = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(MarshallerProperties.BEAN_VALIDATION_FACTORY, getValidatorFactory());
        marshaller.marshal(createInvalidCustomer(), xml);
    }

    @Test
    public void testMarshalValid() throws JAXBException {
        StringWriter xml = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(MarshallerProperties.BEAN_VALIDATION_FACTORY, getValidatorFactory());
        marshaller.marshal(createValidCustomer(), xml);
        assertTrue(xml.toString().contains(CUSTOMER_VALID_XML));
    }

    @Test(expected = JAXBException.class)
    public void testUnmarshalInvalid() throws JAXBException {
        StringReader xml = new StringReader(CUSTOMER_INVALID_XML);
        Unmarshaller unmarshaler = jaxbContext.createUnmarshaller();
        unmarshaler.setProperty(MarshallerProperties.BEAN_VALIDATION_FACTORY, getValidatorFactory());
        unmarshaler.unmarshal(xml);
    }

    @Test
    public void testUnmarshalValid() throws JAXBException {
        StringReader xml = new StringReader(CUSTOMER_VALID_XML);
        Unmarshaller unmarshaler = jaxbContext.createUnmarshaller();
        unmarshaler.setProperty(MarshallerProperties.BEAN_VALIDATION_FACTORY, getValidatorFactory());
        Customer testCustomer = (Customer)unmarshaler.unmarshal(xml);
        assertTrue(createValidCustomer().equals(testCustomer));
    }

    private ValidatorFactory getValidatorFactory() {
        return Validation.byProvider(HibernateValidator.class)
                .providerResolver(new BeanValidationTest.MyValidationProviderResolver())
                .configure()
                .buildValidatorFactory();
    }

    private static class MyValidationProviderResolver implements ValidationProviderResolver {
        @Override
        public List<ValidationProvider<?>> getValidationProviders() {
            return Collections.<ValidationProvider<?>>singletonList(new HibernateValidator());
        }
    }
}
