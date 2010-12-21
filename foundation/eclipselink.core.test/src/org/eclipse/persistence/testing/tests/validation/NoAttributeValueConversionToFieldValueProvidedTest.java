/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;


//Created by Ian Reid
//Date: Feb 17, 2k3
//uses class org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems
public class NoAttributeValueConversionToFieldValueProvidedTest extends ExceptionTest {
    public NoAttributeValueConversionToFieldValueProvidedTest() {
        super();
        setDescription("This tests No Attribute Value Conversion To Field Value Provided (TL-ERROR 115) ");
    }

    protected void setup() {
        expectedException = DescriptorException.noAttributeValueConversionToFieldValueProvided(null, null);
        orgDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();   
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
        ((DatabaseSession)getSession()).addDescriptor(descriptor());
    }

    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        getSession().getDescriptors().remove(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        if (orgDescriptor != null) {
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        }
        if (orgIntegrityChecker != null) {
            getSession().setIntegrityChecker(orgIntegrityChecker);
        }
        //    getAbstractSession().rollbackTransaction();
        //    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();    
    }

    public void test() {
        Object employee = getSession().readObject(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems employeeClone = (org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems)uow.registerObject(employee);
        //the following causes the correct error to occure. 
        employeeClone.setGender("Other");
        try {
            uow.commit();
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

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping genderMapping = new DirectToFieldMapping();
        genderMapping.setAttributeName("gender");
        genderMapping.setFieldName("EMPLOYEE.GENDER");
        ObjectTypeConverter genderConverter = new ObjectTypeConverter();
        genderConverter.addConversionValue("M", "Male");
        genderConverter.addConversionValue("F", "Female");
        genderMapping.setConverter(genderConverter);
        descriptor.addMapping(genderMapping);

        return descriptor;
    }

}
