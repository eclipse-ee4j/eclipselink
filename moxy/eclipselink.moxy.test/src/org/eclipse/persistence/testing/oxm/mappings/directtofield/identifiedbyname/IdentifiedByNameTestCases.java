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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
