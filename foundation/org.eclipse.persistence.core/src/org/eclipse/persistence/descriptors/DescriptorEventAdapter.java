/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
    public void aboutToInsert(DescriptorEvent event) {}

    public void aboutToUpdate(DescriptorEvent event) {}

    public void aboutToDelete(DescriptorEvent event) {}

    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
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
