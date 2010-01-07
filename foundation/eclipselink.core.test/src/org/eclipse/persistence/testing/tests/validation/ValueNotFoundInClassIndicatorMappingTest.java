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
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;


//Created by Ian Reid
//Date: Feb 6, 2k3

public class ValueNotFoundInClassIndicatorMappingTest extends ExceptionTest {
    public ValueNotFoundInClassIndicatorMappingTest() {
        super();
        setDescription("This tests Value Not Found In Class Indicator Mapping (TL-ERROR 108) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.valueNotFoundInClassIndicatorMapping(null, null);
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class);
        if (orgDescriptor != null)
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class);
        descriptor.addTableName("PROJECT");
        descriptor.addPrimaryKeyFieldName("PROJECT.PROJ_ID");
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PROJ_TYPE");

        //if the following is missing then the correct error will occure.  
        // 	descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, "L");
        //	descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, "S");


        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("PROJECT.PROJ_ID");
        descriptor.addMapping(idMapping);

        // Inheritance properties.
        //	descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Interface properties.
        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.SmallProject.class);

        // Descriptor properties.

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event manager.

        return descriptor;

    }
}
