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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlattribute.DirectToXMLAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlattribute.DirectToXMLAttributeNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement.DirectToXMLElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement.EmptyElementEmptyStringTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelementwithwhitespace.DirectToXMLElementWithWhitespaceTestCases;

public class SingleAttributeTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Single Attribute Test Cases");
    suite.addTestSuite(DirectToXMLAttributeTestCases.class);
        suite.addTestSuite(DirectToXMLAttributeNullTestCases.class);
    suite.addTestSuite(DirectToXMLElementTestCases.class);
    suite.addTestSuite(DirectToXMLElementWithWhitespaceTestCases.class);
    suite.addTestSuite(EmptyElementEmptyStringTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.SingleAttributeTestCases"};
    junit.textui.TestRunner.main(arguments);
  }
}
