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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DirectCollectionMappingNillableTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("DirectCollection Mapping Nillable Test Suite");

        // NodeNullPolicy test cases see: TopLinkOXM-Nillable4_blaise.doc
        // see: TopLink_OX_Nillable_TestDesignSpec_v20061026.doc
        suite.addTestSuite(DirectCollectionOptionalNodeNullPolicyElementTestCases.class);
        suite.addTestSuite(DirectCollectionOptionalNodeNullPolicyAttributeTestCases.class);
        suite.addTestSuite(DirectCollectionNillableNodeNullPolicyTestCases.class);
        suite.addTestSuite(DirectCollectionNillableNodeNullPolicyTextTestCases.class);
        suite.addTestSuite(DirectCollectionIsSetNodeNullPolicyTrueTestCases.class);
        suite.addTestSuite(DirectCollectionNillableAndEmptyNodeTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable.DirectCollectionMappingNillableTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
