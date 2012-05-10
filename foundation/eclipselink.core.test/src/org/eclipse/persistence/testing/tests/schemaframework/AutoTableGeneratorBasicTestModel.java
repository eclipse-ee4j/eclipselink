/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
