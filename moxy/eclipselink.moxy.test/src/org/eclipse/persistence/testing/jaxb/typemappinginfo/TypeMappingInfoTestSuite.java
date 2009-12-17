/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  November, 2009 
 ******************************************************************************/  

package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TypeMappingInfoTestSuite extends TestCase {
    public TypeMappingInfoTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TypeMappingInfo Test Suite");
        suite.addTestSuite(DatahandlerWithAnnotationsTestCases.class);
        suite.addTestSuite(DatahandlerWithXMLTestCases.class);
        suite.addTestSuite(DuplicateListOfStringsTestCases.class);
        suite.addTestSuite(DuplicateListOfStringsTestCasesWithXML.class);
        suite.addTestSuite(MapStringIntegerTestCases.class);

        suite.addTestSuite(ImageTestCases.class);
        suite.addTestSuite(ConflictingQNamesTestCases.class);
        suite.addTestSuite(MultipleMapTestCases.class);
        return suite;
    }
}
