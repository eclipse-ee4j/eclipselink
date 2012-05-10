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
package org.eclipse.persistence.testing.tests.nondeferredwrites;

import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.testing.framework.*;

public class EmployeeDescriptorEventListener implements DescriptorEventListener {
    public boolean postInsertExecuted = false;
    public boolean postUpdateExecuted = false;
    public boolean postDeleteExecuted = false;

    public EmployeeDescriptorEventListener() {
    }

    public void postDelete(DescriptorEvent event) {
        if (event.getQuery() == null) {
            throw new TestErrorException("Query not set.");
        }
        if (event.getSession() == null) {
            throw new TestErrorException("Session not set.");
        }
        postDeleteExecuted = true;
    }

    public void postInsert(DescriptorEvent event) {
        if (event.getQuery() == null) {
            throw new TestErrorException("Query not set.");
        }
        if (event.getSession() == null) {
            throw new TestErrorException("Session not set.");
        }
        postInsertExecuted = true;
    }

    public void postUpdate(DescriptorEvent event) {
        if (event.getQuery() == null) {
            throw new TestErrorException("Query not set.");
        }
        if (event.getSession() == null) {
            throw new TestErrorException("Session not set.");
        }
        postUpdateExecuted = true;
    }

    public void aboutToInsert(DescriptorEvent event) {
    }

    public void aboutToUpdate(DescriptorEvent event) {
    }

    public void aboutToDelete(DescriptorEvent event) {
    }

    public void postBuild(DescriptorEvent event) {
    }

    public void postClone(DescriptorEvent event) {
    }

    public void postMerge(DescriptorEvent event) {
    }

    public void postRefresh(DescriptorEvent event) {
    }

    public void postWrite(DescriptorEvent event) {
    }

    public void preCreate(DescriptorEvent event) {
    }

    public void preDelete(DescriptorEvent event) {
    }

    public void preInsert(DescriptorEvent event) {
    }

    public void preRemove(DescriptorEvent event) {
    }

    public void preUpdate(DescriptorEvent event) {
    }

    public void preWrite(DescriptorEvent event) {
    }

    /**
     * Implementers should define this method if they need or want to restrict 
     * the calling of inherited events.
     */
    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        return false;
    }

    /**
     * This event is only raised by the EntityManager.  It is raised when the
     * create operation is initiated on an object.
     */
    public void prePersist(DescriptorEvent event) {
    }

    /**
     * This event is raised before an object is updated regardless if the object 
     * has any database changes. This event was created to support EJB 3.0 
     * events. The object in this case will not have a row accessible from the 
     * event. For objects that have database changes, an aboutToUpdate will also 
     * be triggered.
     */
    public void preUpdateWithChanges(DescriptorEvent event) {
    }

}
