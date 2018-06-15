/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.converter.jaxbenum.JAXBEnumTypeConverterTestCases;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.TypeSafeEnumConverterTestCases;

public class ConverterTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Object Type Converter");
        suite.addTestSuite(ObjectTypeConverterTestCases.class);
        suite.addTestSuite(TypeSafeEnumConverterTestCases.class);
        suite.addTestSuite(JAXBEnumTypeConverterTestCases.class);
        return suite;
    }
}
