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
package org.eclipse.persistence.testing.models.events;

import java.util.Vector;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.testing.framework.*;

public class AddressDescriptorEventListener implements DescriptorEventListener {
    public boolean preInsertExecuted;
    public boolean postInsertExecuted;
    public boolean preUpdateExecuted;
    public boolean postUpdateExecuted;
    public boolean preDeleteExecuted;
    public boolean postDeleteExecuted;
    public boolean preWriteExecuted;
    public boolean postWriteExecuted;
    public boolean postBuildExecuted;
    public boolean postRefreshExecuted;
    public boolean postMergeExecuted;
    public boolean postCloneExecuted;
    public boolean aboutToInsertExecuted;
    public boolean aboutToUpdateExecuted;
    public boolean aboutToDeleteExecuted;

    public AddressDescriptorEventListener() {
        resetFlags();
    }

    public void checkEvent(DescriptorEvent event) {
        if (event.getSession() == null) {
            throw new TestErrorException("Session not set.");
        }
        if (event.getDescriptor() == null) {
            throw new TestErrorException("Descriptor not set on event.");
        }
    }

    public void checkQueryEvent(DescriptorEvent event) {
        checkEvent(event);
        if (event.getQuery() == null) {
            throw new TestErrorException("Query not set.");
        }
    }

    public void checkRowEvent(DescriptorEvent event) {
        checkQueryEvent(event);
        if (event.getRecord() == null) {
            throw new TestErrorException("Row not set.");
        }
    }

    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        return false;
    }

    public void aboutToInsert(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToInsertExecuted = true;
    }

    public void aboutToUpdate(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToUpdateExecuted = true;
    }

    public void aboutToDelete(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToDeleteExecuted = true;
    }

    public void postBuild(DescriptorEvent event) {
        checkRowEvent(event);
        postBuildExecuted = true;
    }

    public void postClone(DescriptorEvent event) {
        checkEvent(event);
        if (event.getOriginalObject() == null) {
            throw new TestErrorException("Original not set.");
        }
        postCloneExecuted = true;
    }

    public void postDelete(DescriptorEvent event) {
        checkQueryEvent(event);
        postDeleteExecuted = true;
    }

    public void postInsert(DescriptorEvent event) {
        checkQueryEvent(event);
        postInsertExecuted = true;
    }

    public void postMerge(DescriptorEvent event) {
        checkEvent(event);
        if (event.getOriginalObject() == null) {
            throw new TestErrorException("Original not set.");
        }
        postMergeExecuted = true;
    }

    public void postRefresh(DescriptorEvent event) {
        checkRowEvent(event);
        postRefreshExecuted = true;
    }

    public void postUpdate(DescriptorEvent event) {
        checkQueryEvent(event);
        postUpdateExecuted = true;
    }

    public void postWrite(DescriptorEvent event) {
        checkQueryEvent(event);
        postWriteExecuted = true;
    }

    public void preCreate(DescriptorEvent event) {
    }

    public void preDelete(DescriptorEvent event) {
        checkQueryEvent(event);
        preDeleteExecuted = true;
    }

    public void preInsert(DescriptorEvent event) {
        checkQueryEvent(event);
        preInsertExecuted = true;
    }

    public void preRemove(DescriptorEvent event) {
    }

    public void preUpdate(DescriptorEvent event) {
        checkQueryEvent(event);
        Address.preUpdateCount++;
        preUpdateExecuted = true;
    }

    public void preWrite(DescriptorEvent event) {
        checkQueryEvent(event);
        preWriteExecuted = true;
    }

    public void prePersist(DescriptorEvent event) {
    }

    public void preUpdateWithChanges(DescriptorEvent event) {
    }

    public void resetFlags() {
        preInsertExecuted = false;
        postInsertExecuted = false;
        preUpdateExecuted = false;
        postUpdateExecuted = false;
        preDeleteExecuted = false;
        postDeleteExecuted = false;
        preWriteExecuted = false;
        postWriteExecuted = false;
        postBuildExecuted = false;
        aboutToInsertExecuted = false;
        aboutToUpdateExecuted = false;
        aboutToDeleteExecuted = false;
        postCloneExecuted = false;
        postMergeExecuted = false;
        postRefreshExecuted = false;
    }
}
