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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.identifiedbyname.withgroupingelement.*;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.identifiedbyname.withoutgroupingelement.*;

public class DirectCollectionTypeAttributeTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("DirectCollection  Type Attribute Test Cases");

        suite.addTestSuite(WithGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(WithGroupingElementIdentifiedByNameTestCases.class);

        suite.addTestSuite(WithoutGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(WithoutGroupingElementIdentifiedByNameTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.DirectCollectionTypeAttributeTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
