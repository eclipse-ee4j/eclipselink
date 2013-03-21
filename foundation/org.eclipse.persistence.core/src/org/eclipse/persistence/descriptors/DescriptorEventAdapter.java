/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.descriptors;

import java.util.Vector;

/**
 * <p><b>Purpose</b>: Provides an empty implementation of DescriptorEventListener.
 * Users who do not require the full DescritorEventListener API can subclass this class
 * and implement only the methods required.
 *
 * @see DescriptorEventManager
 * @see DescriptorEvent
 */
public class DescriptorEventAdapter implements DescriptorEventListener {
    public void aboutToInsert(DescriptorEvent event) {}

    public void aboutToUpdate(DescriptorEvent event) {}

    public void aboutToDelete(DescriptorEvent event) {}

    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        return false;
    }
    
    public void postBuild(DescriptorEvent event) {}

    public void postClone(DescriptorEvent event) {}

    public void postDelete(DescriptorEvent event) {}

    public void postInsert(DescriptorEvent event) {}

    public void postMerge(DescriptorEvent event) {}

    public void postRefresh(DescriptorEvent event) {}

    public void postUpdate(DescriptorEvent event) {}

    public void postWrite(DescriptorEvent event) {}

    public void prePersist(DescriptorEvent event) {}

    public void preDelete(DescriptorEvent event) {}

    public void preRemove(DescriptorEvent event) {}
    
    public void preInsert(DescriptorEvent event) {}

    public void preUpdate(DescriptorEvent event) {}
    
    public void preUpdateWithChanges(DescriptorEvent event) {}

    public void preWrite(DescriptorEvent event) {}
}
