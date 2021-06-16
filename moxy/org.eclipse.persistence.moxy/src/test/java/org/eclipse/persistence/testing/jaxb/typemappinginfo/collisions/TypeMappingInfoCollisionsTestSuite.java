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
//     Denise Smith -  January, 2010 - 2.0.1
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TypeMappingInfoCollisionsTestSuite extends TestCase {
    public TypeMappingInfoCollisionsTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions.TypeMappingInfoCollisionsTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TypeMappingInfoCollisionsTestSuite Test Suite");
        suite.addTestSuite(ConflictingClassesTestCases.class);
        suite.addTestSuite(ConflictingTypesTestCases.class);
        suite.addTestSuite(ConflictingClassAndTypeTestCases.class);
        suite.addTestSuite(ConflictingListClassesTestCases.class);
        suite.addTestSuite(ConflictingListTypeTestCases.class);
        suite.addTestSuite(ConflictingListObjectsTypeTestCases.class);
        suite.addTestSuite(ConflictingListClassAndTypeTestCases.class);
        suite.addTestSuite(ConflictingStringClassTestCases.class);
        suite.addTestSuite(ConflictingStringTypeTestCases.class);
        suite.addTestSuite(ConflictingStringClassAndTypeTestCases.class);
        suite.addTestSuite(NonConflictingListClassAndTypeTestCases.class);
        suite.addTestSuite(ConflictingMapsTestCases.class);
        suite.addTestSuite(ConflictingByteArrayTestCases.class);
        suite.addTestSuite(ConflictingStringArrayClassesTestCases.class);
        suite.addTestSuite(ConflictingClassAndAdapterClassTestCases.class);
        suite.addTestSuite(ConflictingCollectionTestCases.class);
        suite.addTestSuite(StringAndListOfStringConflictTestCases.class);
        return suite;
    }
}
