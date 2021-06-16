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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute.DirectToXMLAttributeIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute.DirectToXMLAttributeIdentifiedByNameNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute.DirectToXMLAttributeIdentifiedByNameEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute.DirectToXMLAttributeIdentifiedByNameSpecialCharactersTestCases;

import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameMissingTextXPathTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameWithCommentTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameSpecialCharactersTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement.DirectToXMLElementIdentifiedByNameNegativeTestCases;

import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.calendartest.CalendarTestIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.calendartest.CalendarDateTestIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.calendartest.CalendarTimeTestIdentifiedByNameTestCases;

public class IdentifiedByNameTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Identified By Name Test Cases");
    suite.addTestSuite(DirectToXMLAttributeIdentifiedByNameTestCases.class);
    suite.addTestSuite(DirectToXMLAttributeIdentifiedByNameNullTestCases.class);
    suite.addTestSuite(DirectToXMLAttributeIdentifiedByNameEmptyTestCases.class);
    suite.addTestSuite(DirectToXMLAttributeIdentifiedByNameSpecialCharactersTestCases.class);

    suite.addTestSuite(DirectToXMLElementIdentifiedByNameTestCases.class);
    suite.addTestSuite(DirectToXMLElementIdentifiedByNameNullTestCases.class);
    suite.addTestSuite(DirectToXMLElementIdentifiedByNameEmptyTestCases.class);
    suite.addTestSuite(DirectToXMLElementIdentifiedByNameMissingTextXPathTestCases.class);
        suite.addTestSuite(DirectToXMLElementIdentifiedByNameWithCommentTestCases.class);
    suite.addTestSuite(DirectToXMLElementIdentifiedByNameSpecialCharactersTestCases.class);
    suite.addTestSuite(DirectToXMLElementIdentifiedByNameNegativeTestCases.class);

    suite.addTestSuite(CalendarDateTestIdentifiedByNameTestCases.class);
        suite.addTestSuite(CalendarTimeTestIdentifiedByNameTestCases.class);
        suite.addTestSuite(CalendarTestIdentifiedByNameTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.IdentifiedByNameTestCases"};
    junit.textui.TestRunner.main(arguments);
  }

}
