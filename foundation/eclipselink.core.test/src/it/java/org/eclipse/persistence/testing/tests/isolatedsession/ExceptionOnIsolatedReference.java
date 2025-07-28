/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Vector;

public class ExceptionOnIsolatedReference extends TestCase {
    protected ServerSession server;

    public ExceptionOnIsolatedReference() {
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();

        for (ClassDescriptor classDescriptor : session.getDescriptors().values()) {
            descriptors.add(classDescriptor);
        }
        this.server.addDescriptors(descriptors);
    }

    @Override
    public void test() {
        this.server = new ServerSession(getSession().getLogin().clone(), 2, 5);
        copyDescriptors(getSession());
        ClassDescriptor descriptor = buildNonIsolatedDescriptor();
        this.server.addDescriptor(descriptor);
        // the exception we are expecting gets logged as a severe exception - make sure it does not get logged so the srg will not get diffs.
        this.server.dontLogMessages();
        try {
            this.server.login();
            if (!this.server.getDescriptor(descriptor.getJavaClass()).isProtectedIsolation()){
                throw new TestErrorException("Shared Cache Descriptor was not switched to Protected when referencing Isolated data");
            }
        } catch (IntegrityException ex) {
            Vector exceptions = ex.getIntegrityChecker().getCaughtExceptions();
            for (Object exception : exceptions) {
                if (((EclipseLinkException) exception).getErrorCode() == DescriptorException.ISOLATED_DESCRIPTOR_REFERENCED_BY_SHARED_DESCRIPTOR) {
                    throw new TestErrorException("Validation Exception error  thrown.  Non-isolated data was not allowed to reference isolated Data");
                }
            }
        } finally {
            if(this.server.isConnected()) {
                this.server.logout();
            }
            this.server = null;
        }
    }

    public RelationalDescriptor buildNonIsolatedDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class);
        descriptor.addTableName("PROJECT");
        descriptor.addPrimaryKeyFieldName("PROJECT.PROJ_ID");

        // Descriptor properties.
        descriptor.setSequenceNumberFieldName("PROJ_ID");
        descriptor.setSequenceNumberName("PROJ_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event manager.
        // Mappings.
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("PROJECT.DESCRIP");
        descriptionMapping.setNullValue("");
        descriptor.addMapping(descriptionMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("PROJECT.PROJ_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("PROJECT.PROJ_NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        OneToOneMapping teamLeaderMapping = new OneToOneMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setReferenceClass(IsolatedEmployee.class);
        teamLeaderMapping.useBasicIndirection();
        teamLeaderMapping.addForeignKeyFieldName("PROJECT.LEADER_ID", "ISOLATED_EMPLOYEE.EMP_ID");
        descriptor.addMapping(teamLeaderMapping);

        return descriptor;
    }
}
