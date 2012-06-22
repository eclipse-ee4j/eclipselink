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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder

public class TargetInvocationWhileInvokingFieldToMethodTest extends ExceptionTestSaveDescriptor {

    public TargetInvocationWhileInvokingFieldToMethodTest() {
        setDescription("This tests Target Invocation While Invoking Field To Method(TL-ERROR 102)");
    }

    protected void setup() {
        expectedException = DescriptorException.targetInvocationWhileInvokingFieldToMethod("buildNormalHours", new TransformationMapping(), null);
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        super.setup();
    }

    public void reset() {
        super.reset();
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        PersonWithValueHolder person = new PersonWithValueHolder();
        person.setName("Person");
        ((DatabaseSession)getSession()).addDescriptor(descriptor());
        UnitOfWork uow = ((DatabaseSession)getSession()).acquireUnitOfWork();
        try {
            uow.registerObject(person); //error is thrown at this line, the rest is not needed - Ian
            //      uow.commit();
            //      DatabaseRecord row = new DatabaseRecord ();
            //      TransformationMapping hoursMapping = (TransformationMapping)descriptor().getMappingForAttributeName("normalHours");
            //      hoursMapping.invokeAttributeMethod(row, person, getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }


    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        //idMapping.setGetMethodName("getId");
        //idMapping.setSetMethodName("setId");
        ((InstanceVariableAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(idMapping);

        TransformationMapping normalHoursMapping = new TransformationMapping();
        normalHoursMapping.setAttributeName("normalHours");
        normalHoursMapping.setAttributeTransformation("buildNormalHours");
        normalHoursMapping.addFieldTransformation("EMPLOYEE.START_TIME", "getStartTime");
        normalHoursMapping.addFieldTransformation("EMPLOYEE.END_TIME", "getEndTime");
        ((InstanceVariableAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(normalHoursMapping);

        return descriptor;
    }


}
