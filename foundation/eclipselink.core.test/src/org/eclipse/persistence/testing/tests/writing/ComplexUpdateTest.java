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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.testing.framework.WriteObjectTest;

/**
 * Test changing private parts of an object.
 */
public class ComplexUpdateTest extends WriteObjectTest {

    /** The object which is actually changed */
    public Object workingCopy;
    public boolean usesUnitOfWork = false;
    public boolean usesNestedUnitOfWork = false;
    public boolean shouldCommitParent = false;
    /** TODO: Set this to true, and fix issues from tests that fail. */
    public boolean shouldCompareClone = true;

    public ComplexUpdateTest() {
        super();
    }

    public ComplexUpdateTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        // By default do nothing
    }

    public void commitParentUnitOfWork() {
        useNestedUnitOfWork();
        this.shouldCommitParent = true;
    }

    public String getName() {
        return super.getName() + new Boolean(usesUnitOfWork) + new Boolean(usesNestedUnitOfWork);
    }

    public void reset() {
        if (getExecutor().getSession().isUnitOfWork()) {
            getExecutor().setSession(((UnitOfWork)getSession()).getParent());
            // Do the same for nested units of work.
            if (getExecutor().getSession().isUnitOfWork()) {
                getExecutor().setSession(((UnitOfWork)getSession()).getParent());
            }
        }
        super.reset();
    }

    protected void setup() {
        super.setup();

        if (this.usesUnitOfWork) {
            getExecutor().setSession(getSession().acquireUnitOfWork());
            if (this.usesNestedUnitOfWork) {
                getExecutor().setSession(getSession().acquireUnitOfWork());
            }
            this.workingCopy = ((UnitOfWork)getSession()).registerObject(this.objectToBeWritten);
        } else {
            this.workingCopy = this.objectToBeWritten;
        }
    }

    protected void test() {
        changeObject();
        if (this.usesUnitOfWork) {
            // Ensure that the original has not been changed.
            if (!((UnitOfWork)getSession()).getParent().compareObjects(this.originalObject, this.objectToBeWritten)) {
                throw new TestErrorException("The original object was changed through changing the clone.");
            }
            ((UnitOfWork)getSession()).commit();
            getExecutor().setSession(((UnitOfWork)getSession()).getParent());
            if (this.usesNestedUnitOfWork) {
                if (this.shouldCommitParent) {
                    ((UnitOfWork)getSession()).commit();
                }
                getExecutor().setSession(((UnitOfWork)getSession()).getParent());
            }
            // Ensure that the clone matches the cache.
            if (this.shouldCompareClone) {
                ClassDescriptor descriptor = getSession().getClassDescriptor(this.objectToBeWritten);
                if(descriptor.shouldIsolateObjectsInUnitOfWork()) {
                    getSession().logMessage("ComplexUpdateTest: descriptor.shouldIsolateObjectsInUnitOfWork() == null. In this case object's changes are not merged back into parent's cache");
                } else if (descriptor.shouldIsolateProtectedObjectsInUnitOfWork() && getSession().isClientSession()){
                    if (!getAbstractSession().compareObjects(this.workingCopy, ((ClientSession)getSession()).getParent().getIdentityMapAccessor().getFromIdentityMap(this.workingCopy))) {
                        throw new TestErrorException("The clone does not match the cached object.");
                    }
                }
                else {
                    if (!getAbstractSession().compareObjects(this.workingCopy, this.objectToBeWritten)) {
                        throw new TestErrorException("The clone does not match the cached object.");
                    }
                }
            }
        } else {
            super.test();
        }
    }

    public void useNestedUnitOfWork() {
        this.usesNestedUnitOfWork = true;
        this.usesUnitOfWork = true;
    }
}
