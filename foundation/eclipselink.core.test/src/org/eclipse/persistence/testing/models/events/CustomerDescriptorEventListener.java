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
package org.eclipse.persistence.testing.models.events;

import java.util.Vector;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.queries.*;

public class CustomerDescriptorEventListener implements DescriptorEventListener {
    public void aboutToInsert(DescriptorEvent event) {
    }

    public void aboutToDelete(DescriptorEvent event) {
    }

    public void aboutToUpdate(DescriptorEvent event) {
    }

    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        return false;
    }

    public void postBuild(DescriptorEvent event) {
    }

    public void postClone(DescriptorEvent event) {
    }

    public void postDelete(DescriptorEvent event) {
    }

    public void postInsert(DescriptorEvent event) {
    }

    public void postMerge(DescriptorEvent event) {
    }

    public void postRefresh(DescriptorEvent event) {
    }

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
        ObjectLevelModifyQuery query = (ObjectLevelModifyQuery)event.getQuery();
        Customer customer = (Customer)query.getObject();
        if (customer.preUpdate) {
            customer.associations.add("PreUpdate");
        }

        // in this preUpdate event we will modify the object so that a changeSet will
        // be created.
    }

    public void preWrite(DescriptorEvent event) {
        ObjectLevelModifyQuery query = (ObjectLevelModifyQuery)event.getQuery();
        Customer customer = (Customer)query.getObject();
        if (customer.preWrite) {
            customer.name = "PreWrite";
        }

        // In this event we will modify the object to ensure that the changes
        // get merged.
    }

    public void prePersist(DescriptorEvent event) {
    }

    public void preUpdateWithChanges(DescriptorEvent event) {
    }
}
