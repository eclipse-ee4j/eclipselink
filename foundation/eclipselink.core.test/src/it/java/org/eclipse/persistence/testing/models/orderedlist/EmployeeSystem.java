/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     05/05/2009 Andrei Ilitchev
//       - JPA 2.0 - OrderedList support.
package org.eclipse.persistence.testing.models.orderedlist;

import java.util.List;

import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;

public class EmployeeSystem extends TestSystem {

    public static enum ChangeTracking {
        DEFERRED,
        ATTRIBUTE
    }
    public static enum JoinFetchOrBatchRead {
        NONE,
        INNER_JOIN,
        OUTER_JOIN,
        BATCH_FETCH,
        BATCH_IN_FETCH,
        BATCH_EXISTS_FETCH;

        public String toString() {
            if(this == NONE) {
                return "";
            } else {
                return super.toString();
            }
        }
    }

    // if true listOrderField is used by all collection mappings.
    boolean useListOrderField;
    // if true all collection mappings use TransparentIndirection; otherwise collection mappings use no indirection.
    boolean useIndirection;
    boolean isPrivatelyOwned;
    boolean useSecondaryTable;
    boolean useVarcharOrder;
    ChangeTracking changeTracking;
    OrderCorrectionType orderCorrectionType;
    boolean shouldOverrideContainerPolicy;
    JoinFetchOrBatchRead joinFetchOrBatchRead;

    public EmployeeSystem(boolean useListOrderField, boolean useIndirection, boolean isPrivatelyOwned, boolean useSecondaryTable, boolean useVarcharOrder, ChangeTracking changeTracking, OrderCorrectionType orderCorrectionType, boolean shouldOverrideContainerPolicy, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useListOrderField = useListOrderField;
        this.useIndirection = useIndirection;
        this.isPrivatelyOwned = isPrivatelyOwned;
        this.useSecondaryTable = useSecondaryTable;
        this.useVarcharOrder = useVarcharOrder;
        this.changeTracking = changeTracking;
        this.orderCorrectionType = orderCorrectionType;
        this.shouldOverrideContainerPolicy = shouldOverrideContainerPolicy;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        this.project = new EmployeeProject(useListOrderField, useIndirection, isPrivatelyOwned, useSecondaryTable, useVarcharOrder, changeTracking, orderCorrectionType, shouldOverrideContainerPolicy, joinFetchOrBatchRead);
    }

    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    public void addDescriptors(DatabaseSession session) {
        if(shouldOverrideContainerPolicy) {
            List<CollectionMapping> listOrderMappings = ((EmployeeProject)this.project).getListOrderMappings();
            for(int i=0; i < listOrderMappings.size(); i++) {
                CollectionMapping mapping = listOrderMappings.get(i);
                mapping.setContainerPolicy(new NullsLastOrderedListContainerPolicy(mapping.getContainerPolicy().getContainerClass()));
            }
        }
        session.addDescriptors(project);
    }

    public static List<CollectionMapping> getListOrderMappings(DatabaseSession session) {
        return EmployeeProject.getListOrderMappings(session);
    }
}
