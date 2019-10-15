/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonMethodAccess

public class IllegalArgumentWhileGettingValueThruMethodAccessorTest extends ExceptionTest {
    public IllegalArgumentWhileGettingValueThruMethodAccessorTest() {
        super();
        setDescription("This tests Illegal Argument While Getting Value Thru Method Accessor (TL-ERROR 27)");
    }

    protected void setup() {
        expectedException = DescriptorException.illegalArgumentWhileGettingValueThruMethodAccessor("getName", "Person", null);
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.tests.validation.PersonMethodAccess.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.tests.validation.PersonMethodAccess.class);
        if (orgDescriptor != null)
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        PersonMethodAccess person = new PersonMethodAccess();
        person.setName("Person");
        Address address = new org.eclipse.persistence.testing.tests.validation.Address();
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            ((DatabaseSession)getSession()).addDescriptor(descriptor());

            //      ((DatabaseSession) getSession()).login();

            UnitOfWork uow = ((DatabaseSession)getSession()).acquireUnitOfWork();
            uow.registerObject(person);
            uow.commit();

            DatabaseMapping dMapping = descriptor().getMappingForAttributeName("p_name");
            //      DatabaseMapping idMapping = descriptor().getMappingForAttributeName("p_id");
            //      ((MethodAttributeAccessor)dMapping.getAttributeAccessor()).initializeAttributes(PersonMethodAccess.class);
            //      ((MethodAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonMethodAccess.class);
            dMapping.getAttributeValueFromObject(address);

            //      ((DatabaseSession) getSession()).logout();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }


    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonMethodAccess.class);
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
        p_idMapping.setGetMethodName("getId");
        p_idMapping.setSetMethodName("setId");

        ((MethodAttributeAccessor)p_idMapping.getAttributeAccessor()).initializeAttributes(PersonMethodAccess.class);

        descriptor.addMapping(p_idMapping);


        DirectToFieldMapping p_nameMapping = new DirectToFieldMapping();
        p_nameMapping.setAttributeName("p_name");
        p_nameMapping.setFieldName("EMPLOYEE.F_NAME");
        p_nameMapping.setGetMethodName("getName");
        p_nameMapping.setSetMethodName("setName");

        ((MethodAttributeAccessor)p_nameMapping.getAttributeAccessor()).initializeAttributes(PersonMethodAccess.class);

        descriptor.addMapping(p_nameMapping);

        return descriptor;
    }


}
