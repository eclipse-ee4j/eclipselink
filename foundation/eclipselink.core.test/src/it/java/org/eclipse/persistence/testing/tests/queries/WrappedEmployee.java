/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Represents a wrapped Employee.  Allows dummy wrapper policy could be
 * implemented for testing.
 * Added for bugs 2612366 and 2766379.
 * @author Stephen McRitchie
 */
public class WrappedEmployee extends Employee {
    public WrappedEmployee() {
    }

    public String getFirstName() {
        throw new RuntimeException("Trying to access a wrapped object.");
    }

    public String getLastName() {
        throw new RuntimeException("Trying to access a wrapped object.");
    }

    /**
     * Override super's toString() as it depends on getFirstName() and
     * getLastName() return actual values (as opposed to exceptions).
     */
    public String toString() {
        return "WrappedEmployee";
    }
}
