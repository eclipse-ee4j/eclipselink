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

import org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement.RootFromAnnotationTestCases;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement.RootFromJAXBElementTestCases;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement.RootFromNothingTestCases;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement.RootFromTypeMappingInfoTestCases;

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
        suite.addTestSuite(MultipleMapWithBindingsTestCases.class);
        suite.addTestSuite(JavaTypeAdapterListToStringTestCases.class);
        suite.addTestSuite(JavaTypeAdapterStringToListTestCases.class);
        suite.addTestSuite(JavaTypeAdapterMapToEmpTestCases.class);
        suite.addTestSuite(JavaTypeAdapterMapTypeTestCases.class);
        suite.addTestSuite(IntegerArrayTestCases.class);
        suite.addTestSuite(ListOfDataHandlerTestCases.class);
        suite.addTestSuite(ListOfByteArrayTestCases.class);
        suite.addTestSuite(ListOfByteObjectArrayTestCases.class);
        suite.addTestSuite(EmployeeTestCases.class);

        suite.addTestSuite(RootFromAnnotationTestCases.class);
        suite.addTestSuite(RootFromNothingTestCases.class);
        suite.addTestSuite(RootFromJAXBElementTestCases.class);
        suite.addTestSuite(RootFromTypeMappingInfoTestCases.class);

        suite.addTestSuite(RootLevelByteArrayTestCases.class);
        return suite;
    }
}
