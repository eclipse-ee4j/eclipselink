/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings.InverseBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings.InverseWriteableBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalList2TestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalList3TestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalList4TestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalListObjectsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalListTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefWithWrapperTestCases;
import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlInverseRefBidirectionalTestSuite {
      public static Test suite() {
            TestSuite suite = new TestSuite("XmlInverseRefBidirectionalTestSuite");
            suite.addTestSuite(XmlInverseRefBidirectionalTestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectional2TestCases.class);
            suite.addTestSuite(XmlInverseRefEmployeeTestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectionalSubTestCases.class);
            suite.addTestSuite(XmlInverseRefObjectsTestCases.class);

            suite.addTestSuite(XmlInverseRefBidirectionalListTestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectionalList2TestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectionalList3TestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectionalList4TestCases.class);
            suite.addTestSuite(XmlInverseRefBidirectionalListObjectsTestCases.class);
            suite.addTestSuite(XmlInverseRefWithWrapperTestCases.class);

            //need external meta data test case
            //need true on one side false on the other side test
            suite.addTestSuite(InverseBindingsTestCases.class);
            suite.addTestSuite(InverseWriteableBindingsTestCases.class);
            return suite;
      }
}
