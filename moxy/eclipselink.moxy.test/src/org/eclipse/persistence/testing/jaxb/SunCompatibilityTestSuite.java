/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory.ClassLevelAccessorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory.PackageLevelAccessorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory.XmlAccessorFactorySupportTestCases;
import org.eclipse.persistence.testing.jaxb.cycle.CycleRecoverableTestCases;
import org.eclipse.persistence.testing.jaxb.cycle.inverse.InverseTestCases;
import org.eclipse.persistence.testing.jaxb.sun.charescape.NonELCharacterEscapeHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.sun.idresolver.NonELIDResolverTestCases;
import org.eclipse.persistence.testing.jaxb.sun.prefixmapper.NonELPrefixMapperTestCases;
import org.eclipse.persistence.testing.jaxb.sun.xmllocation.XmlLocationTestSuite;

public class SunCompatibilityTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Sun Compatability Test Suite");

        suite.addTestSuite(CycleRecoverableTestCases.class);
        suite.addTestSuite(InverseTestCases.class);

        suite.addTestSuite(ClassLevelAccessorTestCases.class);
        suite.addTestSuite(PackageLevelAccessorTestCases.class);
        suite.addTestSuite(NonELIDResolverTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.sun.idresolver.collection.NonELIDResolverTestCases.class);
        suite.addTestSuite(NonELCharacterEscapeHandlerTestCases.class); // depends on jaxb-impl.jar
        suite.addTestSuite(NonELPrefixMapperTestCases.class); // depends on jaxb-impl.jar
        suite.addTest(XmlLocationTestSuite.suite()); // depends on jaxb-impl.jar
        suite.addTestSuite(XmlAccessorFactorySupportTestCases.class); // depends on jaxb-impl.jar

        return suite;
    }
}
