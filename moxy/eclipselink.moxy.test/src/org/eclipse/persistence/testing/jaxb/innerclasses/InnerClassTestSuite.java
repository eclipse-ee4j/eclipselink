/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.innerclasses;

import org.eclipse.persistence.testing.jaxb.innerclasses.notincontext.InnerClassNotInContextTestCases;
import org.eclipse.persistence.testing.jaxb.innerclasses.unreferenced.UnreferencedInnerClassTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class InnerClassTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Inner Class Test Suite");
        suite.addTestSuite(MappedInnerClassTestCases.class);
        suite.addTestSuite(MappedInnerClassNoneTestCases.class);
        suite.addTestSuite(UnmappedInnerClassTestCases.class);
        suite.addTestSuite(InnerClassNotInContextTestCases.class);
        suite.addTestSuite(UnreferencedInnerClassTestCases.class);
        return suite;
    }

}
