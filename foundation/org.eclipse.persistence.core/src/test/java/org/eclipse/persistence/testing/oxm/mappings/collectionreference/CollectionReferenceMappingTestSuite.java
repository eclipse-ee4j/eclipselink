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
//     rbarkhouse - 2009-10-06 14:57:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse.CollectionReferenceReuseTestCases;

public class CollectionReferenceMappingTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Collection Reference Mapping Test Suite");

        suite.addTestSuite(CollectionReferenceReuseTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.collectionreference.CollectionReferenceMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
