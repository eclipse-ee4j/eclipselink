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
