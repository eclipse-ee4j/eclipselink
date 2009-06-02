/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import org.eclipse.persistence.internal.queries.OrderedListContainerPolicy.OrderValidationMode;
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
        BATCH_READ;
        
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
    boolean useSecondaryTable;
    ChangeTracking changeTracking;
    OrderValidationMode orderValidationMode;
    JoinFetchOrBatchRead joinFetchOrBatchRead;
    
    public EmployeeSystem(boolean useListOrderField, boolean useIndirection, boolean useSecondaryTable, ChangeTracking changeTracking, OrderValidationMode orderValidationMode, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useIndirection = useIndirection;
        this.useListOrderField = useListOrderField;
        this.useSecondaryTable = useSecondaryTable;
        this.changeTracking = changeTracking;
        this.orderValidationMode = orderValidationMode;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        this.project = new EmployeeProject(useListOrderField, useIndirection, useSecondaryTable, changeTracking, orderValidationMode, joinFetchOrBatchRead);
    }

    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }
}
