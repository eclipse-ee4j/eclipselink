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
package org.eclipse.persistence.testing.tests.nondeferredwrites;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

public class NonDeferredWritesTestModel extends TestModel {

    public NonDeferredWritesTestModel() {
        setDescription("This model tests the NonDeferred Writes Support.");
    }

    public NonDeferredWritesTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(new ProjectXMLTest());
    }
    
    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
    }

}
