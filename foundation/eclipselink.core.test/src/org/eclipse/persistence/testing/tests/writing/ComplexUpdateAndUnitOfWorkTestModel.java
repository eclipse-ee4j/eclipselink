/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.ownership.*;
import org.eclipse.persistence.testing.tests.unitofwork.*;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectListSystem;

public class ComplexUpdateAndUnitOfWorkTestModel extends TestModel {
    public ComplexUpdateAndUnitOfWorkTestModel() {
        setDescription("This model tests complex update features using the employee demo and ownership model.");
    }

    public ComplexUpdateAndUnitOfWorkTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        addRequiredSystem(new OwnershipSystem());
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new InsuranceSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.mapping.MappingSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.unitofwork.UOWSystem());
        addRequiredSystem(new InheritanceSystem());
    }

    public void addTests() {
        addTest(getComplexUpdateTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(new UnitOfWorkEventTestSuite());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(new org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkTestSuite(true));
    }

    public static TestSuite getComplexUpdateTestSuite() {
        return new ComplexUpdateTestSuite();
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        return new org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkTestSuite();
    }
}
