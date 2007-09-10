/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.UnitOfWork;


public class NullPointerWhileGettingValueThruInstanceVariableAccessorTest extends ExceptionTest {
    public NullPointerWhileGettingValueThruInstanceVariableAccessorTest() {
        super();
        setDescription("This tests Null Pointer While Getting Value Thru Instance Variable Accessor (TL-ERROR 69)");
    }

    protected void setup() {
        expectedException = DescriptorException.nullPointerWhileGettingValueThruInstanceVariableAccessor("p_name", "Person", null);
        getAbstractSession().beginTransaction();
        orgDescriptor = getAbstractSession().getDescriptor(org.eclipse.persistence.testing.tests.validation.PersonInstanceAccess.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        getAbstractSession().getDescriptors().remove(org.eclipse.persistence.testing.tests.validation.PersonInstanceAccess.class);
        if (orgDescriptor != null)
            getDatabaseSession().addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        PersonInstanceAccess person = new PersonInstanceAccess();
        person.setName("Person");
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            getDatabaseSession().addDescriptor(descriptor());
            //     ((DatabaseSession) getSession()).login();
            UnitOfWork uow = getAbstractSession().acquireUnitOfWork();
            uow.registerObject(person);
            uow.commit();
            DatabaseMapping dMapping = descriptor().getMappingForAttributeName("p_name");
            String attributeName = dMapping.getAttributeName();
            dMapping.getAttributeValueFromObject(attributeName);

            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("p_name").equal("Person");
            PersonInstanceAccess personRead = (PersonInstanceAccess)getAbstractSession().readObject(PersonInstanceAccess.class, expression);
            // System.out.println("\n\t Person's name is: " + personRead.getName());
            //     ((DatabaseSession) getSession()).logout();

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonInstanceAccess.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");
        descriptor.setAlias("EMPLOYEE");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        //Named Queries

        // Event manager.

        // Mappings.
        DirectToFieldMapping p_idMapping = new DirectToFieldMapping();
        p_idMapping.setAttributeName("p_id");
        p_idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(p_idMapping);

        DirectToFieldMapping p_nameMapping = new DirectToFieldMapping();
        p_nameMapping.setAttributeName("p_name");
        p_nameMapping.setFieldName("EMPLOYEE.F_NAME");
        descriptor.addMapping(p_nameMapping);

        return descriptor;
    }
}

