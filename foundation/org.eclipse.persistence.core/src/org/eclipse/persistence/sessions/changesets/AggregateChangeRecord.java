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
package org.eclipse.persistence.sessions.changesets;

/**
 * <p>
 * <b>Purpose</b>: Define the Public interface for the Aggregate Change Record.
 * <p>
 * <b>Description</b>: This interface is meant to clarify the public protocol into TopLink.
 */
public interface AggregateChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * This method is used to return the ObjectChangeSet representing the changed Aggregate.
     */
    public ObjectChangeSet getChangedObject();
    
    /**
     * ADVANCED:
     * If the owning UnitOfWork has shouldChangeRecordKeepOldValue set to true,
     * then return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue();
}
