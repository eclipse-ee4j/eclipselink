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
package org.eclipse.persistence.testing.tests.schemaframework;

import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

/**
 * Test the auto table generator.
 */
public class AutoTableGeneratorBasicTestModel extends EmployeeBasicTestModel {
    public AutoTableGeneratorBasicTestModel() {
        setDescription("This model tests auto table generator utility with CRUD test on employee demo model");
    }

    /**
     * Swap a clean session to ensure only the desired descriptors are loaded.
     */
    public void addForcedRequiredSystems() {
        getExecutor().swapCleanDatabaseSession();
        // Must clear the employee system as the tables are replaced.
        getExecutor().removeConfigureSystem(new EmployeeSystem());
        addForcedRequiredSystem(new AutoTableGeneratorEmployeeSystem());
    }

    /**
     * Reset the swapped session.
     */
    public void reset() {
        getExecutor().resetSession();
    }
}
