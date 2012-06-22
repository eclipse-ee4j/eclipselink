/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLRootComplexInheritanceTestSuite extends TestCase
{
  public XMLRootComplexInheritanceTestSuite(String name)
  {
    super(name);
  }
  
  
    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance.XMLRootComplexInheritanceTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLRoot Complex Inheritance Test Suite");

        //suite.addTestSuite(NullDefaultRootTestCases.class);
        suite.addTestSuite(NullDefaultRootXMLRootTestCases.class);
        
        //suite.addTestSuite(NonNullDefaultRootEmpTestCases.class);
        //suite.addTestSuite(NonNullDefaultRootEmpXMLRootTestCases.class);
        //suite.addTestSuite(NonNullDefaultRootMatchingEmpXMLRootTestCases.class);
        //suite.addTestSuite(NonNullDefaultRootMatchingPersonEmpXMLRootTestCases.class);

        suite.addTestSuite(NonNullDefaultRootPersonTestCases.class);
        suite.addTestSuite(NonNullDefaultRootPersonXMLRootTestCases.class);
        suite.addTestSuite(NonNullDefaultRootMatchingPersonXMLRootTestCases.class);
        //suite.addTestSuite(NonNullDefaultRootMatchingEmpPersonXMLRootTestCases.class);


        return suite;
    }
}
