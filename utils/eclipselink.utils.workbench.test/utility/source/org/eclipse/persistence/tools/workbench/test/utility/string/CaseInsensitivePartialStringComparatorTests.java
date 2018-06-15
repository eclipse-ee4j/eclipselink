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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.CaseInsensitivePartialStringComparator;

public class CaseInsensitivePartialStringComparatorTests extends TestCase {

    public static Test suite() {
        return new TestSuite(CaseInsensitivePartialStringComparatorTests.class);
    }

    public CaseInsensitivePartialStringComparatorTests(String name) {
        super(name);
    }

    public void testSameCase() {
        assertEquals(1.0, CaseInsensitivePartialStringComparator.instance().compare("FooBar", "FooBar"), 0.0);
    }

    public void testDifferentCase() {
        assertEquals(1.0, CaseInsensitivePartialStringComparator.instance().compare("FOOBAR", "FooBar"), 0.0);
    }

    public void testMismatch() {
        assertEquals(0.0, CaseInsensitivePartialStringComparator.instance().compare("FooBar1", "FooBar2"), 0.0);
    }

}
