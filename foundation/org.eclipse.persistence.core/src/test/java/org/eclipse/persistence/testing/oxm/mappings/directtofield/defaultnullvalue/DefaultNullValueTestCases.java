/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute.EmptyAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute.MissingAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute.NotMissingAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement.EmptyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement.MissingElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement.NotMissingElementTestCases;

public class DefaultNullValueTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Default Null Value Test Suite");
        suite.addTestSuite(MissingAttributeTestCases.class);
        suite.addTestSuite(EmptyAttributeTestCases.class);
        suite.addTestSuite(NotMissingAttributeTestCases.class);
        suite.addTestSuite(MissingElementTestCases.class);
        suite.addTestSuite(EmptyElementTestCases.class);
        suite.addTestSuite(NotMissingElementTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.DefaultNullValueTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
