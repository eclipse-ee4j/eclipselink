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
package org.eclipse.persistence.tools.workbench.test.utility.filters;

import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.filters.NOTFilter;


public class NOTFilterTests extends TestCase {
    private NOTFilter notFilter;

    public static Test suite() {
        return new TestSuite(NOTFilterTests.class);
    }

    public NOTFilterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.notFilter = new NOTFilter(buildPositiveFilter());
    }

    private static Filter buildPositiveFilter() {
        class LocalFilter implements Filter, Serializable {
            public boolean accept(Object next) {
                double value = ((Number) next).doubleValue();
                return value > 0;
            }
            public boolean equals(Object obj) {
                return this.getClass() == obj.getClass();
            }
            public int hashCode() {
                return 789;
            }
        }
        return new LocalFilter();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testFiltering() {
        assertTrue(this.notFilter.accept(new Integer(0)));
        assertTrue(this.notFilter.accept(new Integer(-1)));
        assertTrue(this.notFilter.accept(new Double(-0.001)));
        assertFalse(this.notFilter.accept(new Double(1)));
        assertFalse(this.notFilter.accept(new Double(11)));
        assertFalse(this.notFilter.accept(new Double(111)));
    }

    public void testClone() {
        NOTFilter notFilter2 = (NOTFilter) this.notFilter.clone();
        assertEquals(this.notFilter.getFilter(), notFilter2.getFilter());
        assertNotSame(this.notFilter, notFilter2);
    }

    public void testEquals() {
        NOTFilter notFilter2 = new NOTFilter(buildPositiveFilter());
        assertEquals(this.notFilter, notFilter2);
        assertEquals(this.notFilter.hashCode(), notFilter2.hashCode());
    }

    public void testSerialization() throws Exception {
        NOTFilter notFilter2 = (NOTFilter) TestTools.serialize(this.notFilter);
        assertEquals(this.notFilter.getFilter(), notFilter2.getFilter());
        assertNotSame(this.notFilter, notFilter2);
    }

}
