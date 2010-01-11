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

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class MappingCanNotBeReadOnlyTest extends ExceptionTest {

    ClassDescriptor descriptor;
    DirectToFieldMapping mapping;
    boolean orgReadOnly;
    IntegrityChecker orgIntegrityChecker;
    VersionLockingPolicy policy;

    public MappingCanNotBeReadOnlyTest() {
        setDescription("This tests Mapping Can Not Be Read Only (TL-ERROR 118)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName("firstName");
        // Need to fix object builder as already initialized.
        descriptor.getObjectBuilder().getMappingsByField().remove(mapping.getField());
        Vector mappings = new Vector();
        mappings.add(mapping);
        descriptor.getObjectBuilder().getReadOnlyMappingsByField().put(mapping.getField(), mappings);
        orgReadOnly = mapping.isReadOnly();
        mapping.readOnly();
        expectedException = DescriptorException.mappingCanNotBeReadOnly(mapping);

        policy = new VersionLockingPolicy();
        policy.storeInObject();
        policy.setWriteLockFieldName("EMPLOYEE.F_NAME");
        descriptor.setOptimisticLockingPolicy(policy);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        descriptor.getObjectBuilder().getMappingsByField().put(mapping.getField(), mapping);
        descriptor.getObjectBuilder().getReadOnlyMappingsByField().remove(mapping.getField());
        mapping.setIsReadOnly(orgReadOnly);
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }


    public void test() {
        try {
            policy.initialize((AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
