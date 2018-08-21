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
package org.eclipse.persistence.testing.models.events;

import java.util.*;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class EventHookSystem extends TestSystem {
    public static int POST_CALCULATE_UOW_CHANGE_SET = 0;

    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();
        descriptors.add(Order.descriptor());
        descriptors.add(CreditCard.descriptor());
        descriptors.add(getCustomerDescriptor());
        descriptors.add(getEmailAccountDescriptor());
        session.getEventManager().addListener(new SessionEventAdapter() {
                public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
                    POST_CALCULATE_UOW_CHANGE_SET++;
                }
            });
        descriptors.add(Phone.descriptor());
        descriptors.add(getAddressDescriptor());
        session.addDescriptors(descriptors);
        session.addDescriptors(new AboutToInsertProject());
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(Customer.tableDefinition());
        schemaManager.replaceObject(Customer.directCollectionTableDefinition());
        schemaManager.replaceObject(Order.tableDefinition());
        schemaManager.replaceObject(EmailAccount.tableDefinition());
        schemaManager.replaceObject(Phone.tableDefinition());
        schemaManager.replaceObject(Address.tableDefinition());
        (new AboutToInsertProjectTableCreator()).replaceTables(session);
        schemaManager.createSequences();
    }

    public ClassDescriptor getAddressDescriptor() {
        ClassDescriptor addressDescriptor = Address.descriptor();
        DescriptorEventManager eventManager = addressDescriptor.getDescriptorEventManager();

        eventManager.addListener(new AddressDescriptorEventListener());
        return addressDescriptor;
    }

    public ClassDescriptor getCustomerDescriptor() {
        ClassDescriptor customerDescriptor = Customer.descriptor();
        customerDescriptor.getEventManager().addListener(new CustomerDescriptorEventListener());
        return customerDescriptor;
    }

    public ClassDescriptor getEmailAccountDescriptor() {
        ClassDescriptor emailDescriptor = EmailAccount.descriptor();

        DescriptorEventManager eventManager = emailDescriptor.getDescriptorEventManager();

        // Set the event hooks for pre and post Insert
        eventManager.setPreInsertSelector("preInsertMethod");
        eventManager.setPostInsertSelector("postInsertMethod");

        // Set the event hooks for pre and post Delete
        eventManager.setPreDeleteSelector("preDeleteMethod");
        eventManager.setPostDeleteSelector("postDeleteMethod");

        // Set the event hooks for pre and post Update
        eventManager.setPreUpdateSelector("preUpdateMethod");
        eventManager.setPostUpdateSelector("postUpdateMethod");

        // Set the event hooks for pre and post Write
        eventManager.setPreWriteSelector("preWriteMethod");
        eventManager.setPostWriteSelector("postWriteMethod");

        // Set the event hooks for post Build
        eventManager.setPostBuildSelector("postBuildMethod");

        // Set the event hooks for about to insert, update and delete
        eventManager.setAboutToInsertSelector("aboutToInsertMethod");
        eventManager.setAboutToUpdateSelector("aboutToUpdateMethod");
        eventManager.setAboutToDeleteSelector("aboutToDeleteMethod");

        // Set the event hook for post clone
        eventManager.setPostCloneSelector("postCloneMethod");

        // Set the event hook for post merge
        eventManager.setPostMergeSelector("postMergeMethod");

        // Set the event hook for post refresh
        eventManager.setPostRefreshSelector("postRefreshMethod");

        return emailDescriptor;
    }

    public void populate(DatabaseSession session) {
    }
}
