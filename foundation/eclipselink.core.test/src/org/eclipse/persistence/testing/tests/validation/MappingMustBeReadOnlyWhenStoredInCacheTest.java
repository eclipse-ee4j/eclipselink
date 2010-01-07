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
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class MappingMustBeReadOnlyWhenStoredInCacheTest extends ExceptionTest {

    ClassDescriptor descriptor;
    DirectToFieldMapping mapping;
    boolean orgReadOnly;
    IntegrityChecker orgIntegrityChecker;
    VersionLockingPolicy policy;

    public MappingMustBeReadOnlyWhenStoredInCacheTest() {
        setDescription("This tests Mapping must Be Read Only when stored in cache (TL-ERROR 119)");
    }

    protected void setup() {

        descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName("firstName");
        orgReadOnly = mapping.isReadOnly();
        mapping.readWrite();
        expectedException = DescriptorException.mustBeReadOnlyMappingWhenStoredInCache(mapping);

        policy = new VersionLockingPolicy();
        policy.setWriteLockFieldName("EMPLOYEE.F_NAME");
        policy.storeInCache();
        descriptor.setOptimisticLockingPolicy(policy);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
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
