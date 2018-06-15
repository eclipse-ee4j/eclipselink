/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.casesensitivity;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.casesensitivity.correctCase.CustomerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the functionality correctness of the case insensitive unmarshalling feature.
 *
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=331241">EclipseLink Forum, Bug 331241.</a>
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class JAXBCaseInsensitivityTestCase extends junit.framework.TestCase {

    private static final URL CUSTOMER_FILE_URL = Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/casesensitivity/customer.xml");
    private static final Class[] CAMEL_CASE_CUSTOMER = new Class[]{CustomerImpl.class};
    private static final Class[] UPPER_CASE_CUSTOMER = new Class[]{org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl.class};
    private static final boolean DEBUG = false;

    private CustomerImpl baseCustomer;
    private Unmarshaller unmarshallerCorrectCaseInsensitive;
    private Unmarshaller unmarshallerOtherCaseInsensitive;
    private Unmarshaller unmarshallerCorrectCaseSensitive;
    private Unmarshaller unmarshallerOtherCaseSensitive;

    @Test
    public void testMain() throws Exception {

        if (DEBUG) System.out.println("Case-insensitive unmarshalling test.");
            assertTrue(unmarshalOtherCaseInsensitive().equals(baseCustomer));
            CustomerImpl ciCorrect = unmarshalCorrectCaseInsensitive();
            assertTrue(ciCorrect.equals(baseCustomer));
            assertTrue(null == ciCorrect.getPersonalName());
                if (DEBUG) System.out.println("Collision handled correctly.");


        if (DEBUG) System.out.println("Case-sensitive unmarshalling test.");
            assertFalse(unmarshalOtherCaseSensitive().equals(baseCustomer));
            assertTrue(unmarshalCorrectCaseSensitive().equals(baseCustomer));
    }

    /* Case-insensitive part */
    private CustomerImpl unmarshalCorrectCaseInsensitive() throws JAXBException {

        CustomerImpl correctCaseCustomer = (CustomerImpl) unmarshallerCorrectCaseInsensitive.unmarshal(CUSTOMER_FILE_URL);
        if (DEBUG) System.out.println(correctCaseCustomer);

        return correctCaseCustomer;
    }

    private org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl unmarshalOtherCaseInsensitive() throws JAXBException {

        org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl otherCaseCustomer
                = (org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl) unmarshallerOtherCaseInsensitive.unmarshal(CUSTOMER_FILE_URL);
        if (DEBUG) System.out.println(otherCaseCustomer);

        return otherCaseCustomer;
    }

    /* Case-sensitive part */
    private CustomerImpl unmarshalCorrectCaseSensitive() throws JAXBException {

        CustomerImpl correctCaseCustomer = (CustomerImpl) unmarshallerCorrectCaseSensitive.unmarshal(CUSTOMER_FILE_URL);
        if (DEBUG) System.out.println(correctCaseCustomer);

        return correctCaseCustomer;
    }

    private org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl unmarshalOtherCaseSensitive() throws JAXBException {

        org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl otherCaseCustomer
                = (org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl) unmarshallerOtherCaseSensitive.unmarshal(CUSTOMER_FILE_URL);
        if (DEBUG) System.out.println(otherCaseCustomer);

        return otherCaseCustomer;
    }

    @Before
    public void setUp() throws Exception {
        baseCustomer = new CustomerImpl();
        baseCustomer.setId(1234007);
        baseCustomer.setAge(24);
        baseCustomer.setPersonalname("collisionBabe");
        baseCustomer.setPersonalName("cafeBabe");

        /* Create and assign case-insensitive unmarshallers */
        Map<String, Boolean> properties = new HashMap<String, Boolean>();
        properties.put(JAXBContextProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.TRUE);
        JAXBContext ctxCorrectCaseInsensitive = JAXBContextFactory.createContext(CAMEL_CASE_CUSTOMER, properties);
        JAXBContext ctxOtherCaseInsensitive = JAXBContextFactory.createContext(UPPER_CASE_CUSTOMER, null); /* we set CI by setProperty() */

        unmarshallerCorrectCaseInsensitive = ctxCorrectCaseInsensitive.createUnmarshaller();
        unmarshallerOtherCaseInsensitive = ctxOtherCaseInsensitive.createUnmarshaller();
        unmarshallerOtherCaseInsensitive.setProperty(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.TRUE);

        /* Create and assign case-sensitive unmarshallers */
        properties.put(JAXBContextProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.FALSE);
        JAXBContext ctxCorrectCaseSensitive = JAXBContextFactory.createContext(CAMEL_CASE_CUSTOMER, properties);

        /* Test that the property can be turned off by using case insensitive context and altering its property */
        unmarshallerOtherCaseSensitive = ctxOtherCaseInsensitive.createUnmarshaller();
        unmarshallerOtherCaseSensitive.setProperty(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.FALSE);
        unmarshallerCorrectCaseSensitive = ctxCorrectCaseSensitive.createUnmarshaller();
    }

    @After
    public void tearDown() throws Exception {
        baseCustomer = null;
        unmarshallerCorrectCaseInsensitive = unmarshallerOtherCaseInsensitive = unmarshallerCorrectCaseSensitive = unmarshallerOtherCaseSensitive = null;
    }
}
