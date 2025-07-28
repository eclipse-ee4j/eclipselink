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
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;

import java.util.List;

public class CustomerDescriptorEventListener implements DescriptorEventListener {
    @Override
    public void aboutToInsert(DescriptorEvent event) {
    }

    @Override
    public void aboutToDelete(DescriptorEvent event) {
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event) {
    }

    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        return false;
    }

    @Override
    public void postBuild(DescriptorEvent event) {
    }

    @Override
    public void postClone(DescriptorEvent event) {
    }

    @Override
    public void postDelete(DescriptorEvent event) {
    }

    @Override
    public void postInsert(DescriptorEvent event) {
    }

    @Override
    public void postMerge(DescriptorEvent event) {
    }

    @Override
    public void postRefresh(DescriptorEvent event) {
    }

    @Override
    public void postUpdate(DescriptorEvent event) {
        ObjectLevelModifyQuery query = (ObjectLevelModifyQuery)event.getQuery();
        Customer customer = (Customer)query.getObject();
        if (customer.postWrite) {
            event.updateAttributeWithObject("name", "PostWrite");
            event.updateAttributeAddObjectToCollection("associations", null, "PostWrite");
            event.updateAttributeRemoveObjectFromCollection("associations", null, "Mickey Mouse Club");
            event.updateAttributeAddObjectToCollection("orders", null, Order.example4());
            customer.creditCard.number = "0";
            event.updateAttributeWithObject("creditCard", customer.creditCard);
        }

        // In this event we will modify the object to ensure that the changes
        // get merged.
    }

    @Override
    public void postWrite(DescriptorEvent event) {
    }

    public void preCreate(DescriptorEvent event) {
    }

    @Override
    public void preDelete(DescriptorEvent event) {
    }

    @Override
    public void preInsert(DescriptorEvent event) {
    }

    @Override
    public void preRemove(DescriptorEvent event) {
    }

    @Override
    public void preUpdate(DescriptorEvent event) {
        ObjectLevelModifyQuery query = (ObjectLevelModifyQuery)event.getQuery();
        Customer customer = (Customer)query.getObject();
        if (customer.preUpdate) {
            customer.associations.add("PreUpdate");
        }

        // in this preUpdate event we will modify the object so that a changeSet will
        // be created.
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        ObjectLevelModifyQuery query = (ObjectLevelModifyQuery)event.getQuery();
        Customer customer = (Customer)query.getObject();
        if (customer.preWrite) {
            customer.name = "PreWrite";
        }

        // In this event we will modify the object to ensure that the changes
        // get merged.
    }

    @Override
    public void prePersist(DescriptorEvent event) {
    }

    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {
    }
}
