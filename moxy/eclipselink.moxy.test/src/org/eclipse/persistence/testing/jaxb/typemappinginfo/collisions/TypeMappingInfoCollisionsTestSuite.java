/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/  
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
        return suite;
    }
}