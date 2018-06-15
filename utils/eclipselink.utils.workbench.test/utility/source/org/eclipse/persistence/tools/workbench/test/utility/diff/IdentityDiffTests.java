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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.IdentityDifferentiator;



public class IdentityDiffTests extends TestCase {
    private Differentiator differentiator;

    public static Test suite() {
        return new TestSuite(IdentityDiffTests.class);
    }

    public IdentityDiffTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.differentiator = IdentityDifferentiator.instance();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testSameObject() {
        Object object1 = new Object();
        Object object2 = object1;
        Diff diff = this.differentiator.diff(object1, object2);
        this.verifyDiff(diff, object1, object2);
        diff = this.differentiator.keyDiff(object1, object2);
        this.verifyDiff(diff, object1, object2);
    }

    public void testBothNull() {
        Object object1 = null;
        Object object2 = null;
        Diff diff = this.differentiator.diff(object1, object2);
        this.verifyDiff(diff, object1, object2);
        diff = this.differentiator.keyDiff(object1, object2);
        this.verifyDiff(diff, object1, object2);
    }

    private void verifyDiff(Diff diff, Object object1, Object object2) {
        assertEquals(object1, diff.getObject1());
        assertEquals(object2, diff.getObject2());
        assertTrue(diff.identical());
        assertFalse(diff.different());
        assertEquals(0, diff.getDescription().length());
    }

    public void testDifferentObjects() {
        Object object1 = new Object();
        Object object2 = new Object();
        Diff diff = this.differentiator.diff(object1, object2);
        this.verifyDiffNot(diff, object1, object2);
        diff = this.differentiator.keyDiff(object1, object2);
        this.verifyDiffNot(diff, object1, object2);
    }

    public void testOneNull() {
        Object object1 = new Object();
        Object object2 = null;
        Diff diff = this.differentiator.diff(object1, object2);
        this.verifyDiffNot(diff, object1, object2);
        diff = this.differentiator.keyDiff(object1, object2);
        this.verifyDiffNot(diff, object1, object2);
    }

    private void verifyDiffNot(Diff diff, Object object1, Object object2) {
        assertEquals(object1, diff.getObject1());
        assertEquals(object2, diff.getObject2());
        assertFalse(diff.identical());
        assertTrue(diff.different());
        assertTrue(diff.getDescription().length() > 0);
    }

}
