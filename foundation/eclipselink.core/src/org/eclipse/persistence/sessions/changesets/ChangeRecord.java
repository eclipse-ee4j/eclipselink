/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.changesets;


/**
 * <p>
 * <b>Purpose</b>: Define the base Change Record API.
 * <p>
 * <b>Description</b>: This interface is meant to clarify the public protocol into TopLink.
 * It provides access into the information available from the TopLink Change Set
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the API for ChangeRecord.
 * </ul>
 */
public interface ChangeRecord {

    /**
     * ADVANCED:
     * Returns the name of the attribute this ChangeRecord Represents
     * @return java.lang.String
     */
    public String getAttribute();

    /**
     * ADVANCED:
     * This method returns the ObjectChangeSet that references this ChangeRecord
     * @return org.eclipse.persistence.sessions.changesets.ObjectChangeSet
     */
    public ObjectChangeSet getOwner();
}