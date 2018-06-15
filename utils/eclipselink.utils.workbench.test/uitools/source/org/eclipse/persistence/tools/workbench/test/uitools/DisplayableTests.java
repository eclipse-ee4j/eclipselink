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
package org.eclipse.persistence.tools.workbench.test.uitools;

import javax.swing.Icon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;

public class DisplayableTests extends TestCase {

    public static Test suite() {
        return new TestSuite(DisplayableTests.class);
    }

    public DisplayableTests(String name) {
        super(name);
    }

    public void testComparator() {
        Foo odin = new Foo("Odin");
        assertEquals(0, odin.compareTo(odin));
        Foo thor = new Foo("Thor");
        assertTrue(odin.compareTo(thor) < 0);
        assertTrue(thor.compareTo(odin) > 0);
        Foo lcOdin = new Foo("odin");
        assertTrue(odin.compareTo(lcOdin) != 0);

        Foo anotherOdin = new Foo("Odin");
        int result = odin.compareTo(anotherOdin);
        assertTrue(result != 0);
        int reverseResult = anotherOdin.compareTo(odin);
        assertTrue(reverseResult != 0);
        if (result < 0) {
            assertTrue(reverseResult > 0);
        } else {
            assertTrue(reverseResult < 0);
        }
    }

private static class Foo extends AbstractModel implements Displayable {
    private String name;
    public Foo(String name) {
        this.name = name;
    }
    public String displayString() {
        return this.name;
    }
    public Icon icon() {
        return null;
    }
    public int compareTo(Object o) {
        return DEFAULT_COMPARATOR.compare(this, o);
    }
}

}
