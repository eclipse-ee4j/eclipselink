/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile.AllModelSPIMetaClassFileTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader.AllModelSPIMetaClassLoaderTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllModelSPIMetaTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelSPIMetaTests.class));

        suite.addTest(AllModelSPIMetaClassFileTests.suite());
        suite.addTest(AllModelSPIMetaClassLoaderTests.suite());

        return suite;
    }

    /**
     * suppress instantiation
     */
    private AllModelSPIMetaTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
