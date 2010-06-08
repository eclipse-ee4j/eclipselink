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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;


/**
 * Tests the work copy clone copy policy method.
 */
public class WorkingCloneCopyPolicyTest extends AutoVerifyTestCase {
    protected CopyPolicy originalPolicy;
    protected ClassDescriptor descriptor;

    public WorkingCloneCopyPolicyTest() {
        super();
        setDescription("Verify that the working clone copy policy is used correctly.");
    }

    public void reset() {
        this.descriptor.setCopyPolicy(this.originalPolicy);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        this.descriptor = getSession().getDescriptor(Address.class);
        this.originalPolicy = this.descriptor.getCopyPolicy();
        this.descriptor.setCopyPolicy(new WorkingCloneCopyPolicy());
        this.descriptor.getCopyPolicy().initialize(getSession());
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Address address = (Address)uow.readObject(Address.class);
        if (!address.isWorkingCopy) {
            throw new TestErrorException("Failed to call the correct copy policy method");
        }
        if (((Address)((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)uow).getBackupClone(address)).isWorkingCopy) {
            throw new TestErrorException("Updated the backup clone with the working copy specific information");
        }
    }

    protected void verify() throws Exception {
    }

    protected class WorkingCloneCopyPolicy extends CloneCopyPolicy {
        public Object buildClone(Object domainObject, Session session) throws DescriptorException {
            return super.buildClone(domainObject, session);
        }

        /**
         * This method will be called when a UnitOfWork is attempting to create a Working Copy clone.
         * In certain cases (ie CMP) where the behavior of creating a working copy can be different then
         * when creating the other clones.
         */
        public Object buildWorkingCopyClone(Object domainObject, Session session) throws DescriptorException {
            //not implemented to perform special operations.
            Object workingClone = this.buildClone(domainObject, session);
            if (workingClone instanceof Address) {
                ((Address)workingClone).isWorkingCopy = true;
            }
            return workingClone;
        }
    }
}
