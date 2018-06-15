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
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationBindingsTestCase;
import org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationRuntimeTestCase;
import org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationSpecialtiesTestCase;
import org.eclipse.persistence.testing.jaxb.beanvalidation.ValidationXMLTestCase;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class JAXBBeanValidationTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Bean Validation Test Suite");

        suite.addTestSuite(BeanValidationRuntimeTestCase.class);
        suite.addTestSuite(BeanValidationBindingsTestCase.class);
        suite.addTestSuite(BeanValidationSpecialtiesTestCase.class);
        suite.addTestSuite(ValidationXMLTestCase.class);

        return suite;
    }


    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBBeanValidationTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
