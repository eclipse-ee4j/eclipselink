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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllClassFileTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllClassFileTests.class));

        suite.addTest(ClassFileTests.suite());
        suite.addTest(ClassDependencyTests.suite());

        return suite;
    }

    /**
     * suppress instantiation
     */
    private AllClassFileTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
