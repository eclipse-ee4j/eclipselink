/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationBindingsTestCase;
import org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationRuntimeTestCase;
import org.eclipse.persistence.testing.jaxb.casesensitivity.JAXBCaseInsensitivityTestCase;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class JAXBTestSuite5 {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite5");

        suite.addTestSuite(JAXBCaseInsensitivityTestCase.class);
        suite.addTestSuite(BeanValidationRuntimeTestCase.class);
        suite.addTestSuite(BeanValidationBindingsTestCase.class);

        return suite;
    }


    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite5" };
        junit.textui.TestRunner.main(arguments);
    }

}
