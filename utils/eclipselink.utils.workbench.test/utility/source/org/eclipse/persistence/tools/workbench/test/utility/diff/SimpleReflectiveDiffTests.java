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
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Employee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.SimpleEmployee;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;



public class SimpleReflectiveDiffTests extends AbstractReflectiveDiffTests {

    public static Test suite() {
        return new TestSuite(SimpleReflectiveDiffTests.class);
    }

    public SimpleReflectiveDiffTests(String name) {
        super(name);
    }

    protected Differentiator buildDifferentiator() {
        ReflectiveDifferentiator result = new ReflectiveDifferentiator(SimpleEmployee.class);
        result.addKeyFieldNamed("id");
        return result;
    }

    protected Employee buildEmployee(int id, String name) {
        return new SimpleEmployee(id, name);
    }

    protected ReflectiveDifferentiator employeeDifferentiator() {
        return (ReflectiveDifferentiator) this.differentiator;
    }

}
