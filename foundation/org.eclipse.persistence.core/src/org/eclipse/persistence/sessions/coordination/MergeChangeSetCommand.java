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
package org.eclipse.persistence.sessions.coordination;

import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.internal.sessions.*;

/**
 * <p>
 * <b>Purpose</b>: Provide a remote command implementation for remote cache
 * merges of changes.
 * <p>
 * <b>Description</b>: This command provides the implementation for cache
 * synchronization using RCM.
 * <P>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 *
 */
public class MergeChangeSetCommand extends Command {

    /** The changes to be applied remotely */
    protected transient UnitOfWorkChangeSet changeSet;
    protected byte[] changeSetBytes;

    /**
     * INTERNAL:
     * Return the changes to be applied
     */
    public UnitOfWorkChangeSet getChangeSet(AbstractSession session) {
        if ((changeSet == null) && (changeSetBytes != null)) {
            try {
                changeSet = new UnitOfWorkChangeSet(changeSetBytes, session);
            } catch (java.io.IOException exception) {
                throw CommunicationException.unableToPropagateChanges(getServiceId().toString(), exception);
            } catch (ClassNotFoundException exception) {
                throw CommunicationException.unableToPropagateChanges(getServiceId().toString(), exception);
            }
        }
        return changeSet;
    }

    /**
     * INTERNAL:
     * Set the changes to be applied
     */
    public void setChangeSet(UnitOfWorkChangeSet newChangeSet) {
        changeSet = newChangeSet;
    }

    /**
     * INTERNAL:
     * Custom serialize this change set by converting it to a byte array.
     * @return false if converted byte array is null.  Otherwise, return true.
     */
    public boolean convertChangeSetToByteArray(AbstractSession session) throws java.io.IOException {
        changeSetBytes = changeSet.getByteArrayRepresentation(session);
        return changeSetBytes != null;
    }

    /**
     * INTERNAL:
     * This method will be invoked by the RCM only when the CommandProcessor is a
     * TopLink session. The session will be passed in for the command to use.
     */
    public void executeWithSession(AbstractSession session) {
        MergeManager manager = new MergeManager(session);
        manager.mergeIntoDistributedCache();
        manager.setCascadePolicy(MergeManager.CASCADE_ALL_PARTS);

        // Do the main merge
        manager.mergeChangesFromChangeSet(getChangeSet(session));
    }
}
