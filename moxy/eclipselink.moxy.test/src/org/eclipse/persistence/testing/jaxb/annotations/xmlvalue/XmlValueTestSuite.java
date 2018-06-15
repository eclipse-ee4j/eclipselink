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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue;

import org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.adapter.AdapterTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.attribute.NestedAttributeNullTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.attribute.NestedAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.integer.IntegerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.text.NestedTextTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlValueTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlValue Test Suite");
        suite.addTestSuite(ObjectValueTestCases.class);
        suite.addTestSuite(AdapterTestCases.class);
        suite.addTestSuite(NestedAttributeTestCases.class);
        suite.addTestSuite(NestedAttributeNullTestCases.class);
        suite.addTestSuite(IntegerTestCases.class);
        suite.addTestSuite(NestedTextTestCases.class);
        suite.addTestSuite(XsiTypeTestCases.class);
        return suite;
    }

}
