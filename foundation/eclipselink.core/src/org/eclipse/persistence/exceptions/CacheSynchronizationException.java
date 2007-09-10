/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.util.Vector;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;

/**
 * <p><b>Purpose</b>: This exception is used when an error occurs during cache
 * synchronization distribution in synchronous mode.
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.exceptions.RemoteCommandManagerException}
 */
public class CacheSynchronizationException extends EclipseLinkException {
    public Vector errors;
    public transient UnitOfWorkChangeSet changeSet;// do not send the changeset remotely

    public CacheSynchronizationException(Vector errors, UnitOfWorkChangeSet changeSet) {
        this.errors = errors;
        this.changeSet = changeSet;
    }

    /**
     * This is the change Set that was being merged
     */
    public org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet getChangeSet() {
        return changeSet;
    }

    /**
     * The errors that occured
     */
    public java.util.Vector getErrors() {
        return errors;
    }
}