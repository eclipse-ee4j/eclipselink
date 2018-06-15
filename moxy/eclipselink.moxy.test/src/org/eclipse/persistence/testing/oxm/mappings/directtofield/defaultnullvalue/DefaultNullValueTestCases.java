/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute.*;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement.*;

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
