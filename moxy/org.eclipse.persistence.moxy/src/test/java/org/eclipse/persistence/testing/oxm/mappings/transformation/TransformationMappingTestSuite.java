/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.transformation.element.ElementNSTestCases;
import org.eclipse.persistence.testing.oxm.mappings.transformation.element.ElementTestCases;

/**
 *  @version $Header: TransformationMappingTestSuite.java 17-apr-2007.11:15:15 mmacivor Exp $
 *  @author  mmacivor
 */
public class TransformationMappingTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Transformation Mapping Suite");
        suite.addTestSuite(TransformationMappingTestCases.class);
        suite.addTestSuite(TransformationMappingPrefixTestCases.class);
        suite.addTestSuite(TransformationMappingErrorTestCases.class);
        suite.addTestSuite(TransformationMappingAnyCollectionTestCases.class);
        suite.addTestSuite(TransformationMappingAnyObjectTestCases.class);
        suite.addTestSuite(TransformationMappingCompositeCollectionTestCases.class);
        suite.addTestSuite(TransformationMappingCompositeObjectTestCases.class);
        suite.addTestSuite(TransformationMappingNullTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(ElementNSTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.transformation.TransformationMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
