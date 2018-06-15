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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public class StaticValueModelTests extends TestCase {
    private ValueModel objectHolder;

    public static Test suite() {
        return new TestSuite(StaticValueModelTests.class);
    }

    public StaticValueModelTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.objectHolder = this.buildObjectHolder();
    }

    private ValueModel buildObjectHolder() {
        return new AbstractReadOnlyPropertyValueModel() {
            public Object getValue() {
                return "foo";
            }
        };
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testGetValue() {
        assertEquals("foo", this.objectHolder.getValue());
    }

    public void testToString() {
        assertTrue(this.objectHolder.toString().indexOf("foo") >= 0);
    }

}
