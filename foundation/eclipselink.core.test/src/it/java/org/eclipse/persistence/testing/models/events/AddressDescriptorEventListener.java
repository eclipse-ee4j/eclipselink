/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.testing.models.events;

import java.util.List;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.testing.framework.TestErrorException;

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

    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        return false;
    }

    @Override
    public void aboutToInsert(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToInsertExecuted = true;
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToUpdateExecuted = true;
    }

    @Override
    public void aboutToDelete(DescriptorEvent event) {
        checkRowEvent(event);
        aboutToDeleteExecuted = true;
    }

    @Override
    public void postBuild(DescriptorEvent event) {
        checkRowEvent(event);
        postBuildExecuted = true;
    }

    @Override
    public void postClone(DescriptorEvent event) {
        checkEvent(event);
        if (event.getOriginalObject() == null) {
            throw new TestErrorException("Original not set.");
        }
        postCloneExecuted = true;
    }

    @Override
    public void postDelete(DescriptorEvent event) {
        checkQueryEvent(event);
        postDeleteExecuted = true;
    }

    @Override
    public void postInsert(DescriptorEvent event) {
        checkQueryEvent(event);
        postInsertExecuted = true;
    }

    @Override
    public void postMerge(DescriptorEvent event) {
        checkEvent(event);
        if (event.getOriginalObject() == null) {
            throw new TestErrorException("Original not set.");
        }
        postMergeExecuted = true;
    }

    @Override
    public void postRefresh(DescriptorEvent event) {
        checkRowEvent(event);
        postRefreshExecuted = true;
    }

    @Override
    public void postUpdate(DescriptorEvent event) {
        checkQueryEvent(event);
        postUpdateExecuted = true;
    }

    @Override
    public void postWrite(DescriptorEvent event) {
        checkQueryEvent(event);
        postWriteExecuted = true;
    }

    public void preCreate(DescriptorEvent event) {
    }

    @Override
    public void preDelete(DescriptorEvent event) {
        checkQueryEvent(event);
        preDeleteExecuted = true;
    }

    @Override
    public void preInsert(DescriptorEvent event) {
        checkQueryEvent(event);
        preInsertExecuted = true;
    }

    @Override
    public void preRemove(DescriptorEvent event) {
    }

    @Override
    public void preUpdate(DescriptorEvent event) {
        checkQueryEvent(event);
        Address.preUpdateCount++;
        preUpdateExecuted = true;
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        checkQueryEvent(event);
        preWriteExecuted = true;
    }

    @Override
    public void prePersist(DescriptorEvent event) {
    }

    @Override
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
