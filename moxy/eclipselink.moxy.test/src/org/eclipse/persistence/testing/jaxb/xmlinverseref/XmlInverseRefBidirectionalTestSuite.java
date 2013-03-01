/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith, February 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings.InverseBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings.InverseWriteableBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalList2TestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalList3TestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalListObjectsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlinverseref.list.XmlInverseRefBidirectionalListTestCases;

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
	        suite.addTestSuite(XmlInverseRefBidirectionalListObjectsTestCases.class);
	        
	        //need external meta data test case
	        //need true on one side false on the other side test
	        suite.addTestSuite(InverseBindingsTestCases.class);
	        suite.addTestSuite(InverseWriteableBindingsTestCases.class);
	        return suite;
	  }
}
