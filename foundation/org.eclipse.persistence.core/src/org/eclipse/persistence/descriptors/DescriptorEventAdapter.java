/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.descriptors;

import java.util.List;

/**
 * <p><b>Purpose</b>: Provides an empty implementation of DescriptorEventListener.
 * Users who do not require the full DescritorEventListener API can subclass this class
 * and implement only the methods required.
 *
 * @see DescriptorEventManager
 * @see DescriptorEvent
 */
public class DescriptorEventAdapter implements DescriptorEventListener {
    @Override
    public void aboutToInsert(DescriptorEvent event) {}

    @Override
    public void aboutToUpdate(DescriptorEvent event) {}

    @Override
    public void aboutToDelete(DescriptorEvent event) {}

    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        return false;
    }

    @Override
    public void postBuild(DescriptorEvent event) {}

    @Override
    public void postClone(DescriptorEvent event) {}

    @Override
    public void postDelete(DescriptorEvent event) {}

    @Override
    public void postInsert(DescriptorEvent event) {}

    @Override
    public void postMerge(DescriptorEvent event) {}

    @Override
    public void postRefresh(DescriptorEvent event) {}

    @Override
    public void postUpdate(DescriptorEvent event) {}

    @Override
    public void postWrite(DescriptorEvent event) {}

    @Override
    public void prePersist(DescriptorEvent event) {}

    @Override
    public void preDelete(DescriptorEvent event) {}

    @Override
    public void preRemove(DescriptorEvent event) {}

    @Override
    public void preInsert(DescriptorEvent event) {}

    @Override
    public void preUpdate(DescriptorEvent event) {}

    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {}

    @Override
    public void preWrite(DescriptorEvent event) {}
}
