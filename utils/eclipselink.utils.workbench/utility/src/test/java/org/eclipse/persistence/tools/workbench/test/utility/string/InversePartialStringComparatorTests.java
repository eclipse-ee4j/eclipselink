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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.CaseInsensitivePartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.InversePartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;

public class InversePartialStringComparatorTests extends TestCase {

    public static Test suite() {
        return new TestSuite(InversePartialStringComparatorTests.class);
    }

    public InversePartialStringComparatorTests(String name) {
        super(name);
    }

    public void testSameCase() {
        assertEquals(0.0, this.buildComparator().compare("FooBar", "FooBar"), 0.0);
    }

    public void testDifferentCase() {
        assertEquals(0.0, this.buildComparator().compare("FOOBAR", "FooBar"), 0.0);
    }

    public void testMismatch() {
        assertEquals(1.0, this.buildComparator().compare("FooBar1", "FooBar2"), 0.0);
    }

    private PartialStringComparator buildComparator() {
        return new InversePartialStringComparator(CaseInsensitivePartialStringComparator.instance());
    }
}
