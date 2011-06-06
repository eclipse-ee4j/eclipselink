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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class SetMethodParameterTypeNotValidTest extends ExceptionTest {
    public SetMethodParameterTypeNotValidTest() {
        super();
        setDescription("This tests Set Method Parameter Type Not Valid (TL-ERROR 133) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.setMethodParameterTypeNotValid(new OneToManyMapping());

        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();

            //the following causes the correct error to occure. 
            RelationalDescriptor descriptor = descriptor();
            OneToManyMapping dMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("managedEmployeesWithProblems");
            org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
            NoIndirectionPolicy indirectionPolicy = (NoIndirectionPolicy)dMapping.getIndirectionPolicy();

            indirectionPolicy.validateSetMethodParameterTypeForCollection(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class, getSession().getIntegrityChecker());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");


        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        //the following mapping causes the correct error to occure. 
        OneToManyMapping managedEmployeesMapping = new OneToManyMapping();
        managedEmployeesMapping.setAttributeName("managedEmployeesWithProblems");
        managedEmployeesMapping.setReferenceClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        managedEmployeesMapping.dontUseIndirection();
        //the following causes the correct error to occure.
        managedEmployeesMapping.setSetMethodName("setManagedEmployeesWithProblems");
        managedEmployeesMapping.addTargetForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
        descriptor.addMapping(managedEmployeesMapping);

        return descriptor;
    }
}
