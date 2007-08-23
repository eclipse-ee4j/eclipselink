/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.TypeSafeEnumConverterTestCases;

public class ConverterTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Object Type Converter");
        suite.addTestSuite(ObjectTypeConverterTestCases.class);
        suite.addTestSuite(TypeSafeEnumConverterTestCases.class);
        return suite;
    }
}