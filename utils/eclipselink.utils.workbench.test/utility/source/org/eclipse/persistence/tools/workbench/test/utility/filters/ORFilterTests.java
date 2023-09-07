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
package org.eclipse.persistence.tools.workbench.test.utility.filters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.filters.ORFilter;
import org.eclipse.persistence.tools.workbench.utility.filters.SimpleFilter;


public class ORFilterTests extends TestCase {
    private ORFilter orFilter;

    public static Test suite() {
        return new TestSuite(ORFilterTests.class);
    }

    public ORFilterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.orFilter = new ORFilter(buildMinFilter(1), buildMaxFilter(10));
    }

    private static Filter buildMinFilter(double min) {
        return new SimpleFilter(Double.valueOf(min)) {
            public boolean accept(Object next) {
                double minValue = ((Number) this.criterion).doubleValue();
                double value = ((Number) next).doubleValue();
                return value <= minValue;
            }
        };
    }

    private static Filter buildMaxFilter(double max) {
        return new SimpleFilter(Double.valueOf(max)) {
            public boolean accept(Object next) {
                double maxValue = ((Number) this.criterion).doubleValue();
                double value = ((Number) next).doubleValue();
                return value >= maxValue;
            }
        };
    }

    private static Filter buildEvenFilter() {
        return new Filter() {
            public boolean accept(Object next) {
                int value = ((Number) next).intValue();
                return (value & 1) == 0;
            }
        };
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testFiltering2() {
        assertFalse(this.orFilter.accept(Integer.valueOf(7)));
        assertFalse(this.orFilter.accept(Integer.valueOf(2)));
        assertFalse(this.orFilter.accept(Double.valueOf(6.666)));
        assertTrue(this.orFilter.accept(Double.valueOf(-99)));
        assertTrue(this.orFilter.accept(Double.valueOf(-1)));
        assertTrue(this.orFilter.accept(Double.valueOf(11)));
        assertTrue(this.orFilter.accept(Double.valueOf(111)));
    }

    public void testFiltering3() {
        ORFilter orFilter2 = new ORFilter(this.orFilter, buildEvenFilter());
        assertFalse(orFilter2.accept(Integer.valueOf(7)));
        assertFalse(orFilter2.accept(Integer.valueOf(3)));
        assertFalse(orFilter2.accept(Integer.valueOf(9)));
        assertTrue(orFilter2.accept(Integer.valueOf(2)));
        assertTrue(orFilter2.accept(Double.valueOf(6.1)));
        assertTrue(orFilter2.accept(Double.valueOf(-99)));
        assertTrue(orFilter2.accept(Double.valueOf(-1)));
        assertTrue(orFilter2.accept(Double.valueOf(11)));
        assertTrue(orFilter2.accept(Double.valueOf(111)));
        assertTrue(orFilter2.accept(Double.valueOf(-98)));
        assertTrue(orFilter2.accept(Double.valueOf(0)));
        assertTrue(orFilter2.accept(Double.valueOf(-2)));
        assertTrue(orFilter2.accept(Double.valueOf(12)));
        assertTrue(orFilter2.accept(Double.valueOf(222)));
    }

    public void testFilteringComposite() {
        Filter orFilter2 = ORFilter.or(new Filter[] {buildMinFilter(1), buildMaxFilter(10), buildEvenFilter()});
        assertFalse(orFilter2.accept(Integer.valueOf(7)));
        assertFalse(orFilter2.accept(Integer.valueOf(3)));
        assertFalse(orFilter2.accept(Integer.valueOf(9)));
        assertTrue(orFilter2.accept(Integer.valueOf(2)));
        assertTrue(orFilter2.accept(Double.valueOf(6.1)));
        assertTrue(orFilter2.accept(Double.valueOf(-99)));
        assertTrue(orFilter2.accept(Double.valueOf(-1)));
        assertTrue(orFilter2.accept(Double.valueOf(11)));
        assertTrue(orFilter2.accept(Double.valueOf(111)));
        assertTrue(orFilter2.accept(Double.valueOf(-98)));
        assertTrue(orFilter2.accept(Double.valueOf(0)));
        assertTrue(orFilter2.accept(Double.valueOf(-2)));
        assertTrue(orFilter2.accept(Double.valueOf(12)));
        assertTrue(orFilter2.accept(Double.valueOf(222)));
    }

    public void testClone() {
        ORFilter orFilter2 = (ORFilter) this.orFilter.clone();
        assertEquals(this.orFilter.getFilter1(), orFilter2.getFilter1());
        assertEquals(this.orFilter.getFilter2(), orFilter2.getFilter2());
        assertNotSame(this.orFilter, orFilter2);
    }

    public void testEquals() {
        ORFilter orFilter2 = new ORFilter(buildMinFilter(1), buildMaxFilter(10));
        assertEquals(this.orFilter, orFilter2);
        assertEquals(this.orFilter.hashCode(), orFilter2.hashCode());

        orFilter2 = new ORFilter(buildMaxFilter(10), buildMinFilter(1));
        assertEquals(this.orFilter, orFilter2);
        assertEquals(this.orFilter.hashCode(), orFilter2.hashCode());
    }

    public void testSerialization() throws Exception {
        ORFilter orFilter2 = (ORFilter) TestTools.serialize(this.orFilter);
        assertEquals(this.orFilter.getFilter1(), orFilter2.getFilter1());
        assertEquals(this.orFilter.getFilter2(), orFilter2.getFilter2());
        assertNotSame(this.orFilter, orFilter2);
    }

}
