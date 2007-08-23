/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sequencing;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * <p>
 * <b>Purpose</b>: Define a database's native sequencing mechanism.
 * <p>
 * <b>Description</b>
 * Many databases have built in support for sequencing.
 * This can be a SEQUENCE object such as in Oracle,
 * or a auto-incrementing column such as the IDENTITY field in Sybase.
 * For an auto-incrementing column the preallocation size is always 1.
 * For a SEQUENCE object the preallocation size must match the SEQUENCE objects "increment by".
 */
public class NativeSequence extends QuerySequence {
    public NativeSequence() {
        super();
        setShouldSkipUpdate(true);
    }
    
    /**
     * Create a new sequence with the name.
     */
    public NativeSequence(String name) {
        super(name);
        setShouldSkipUpdate(true);
    }
    
    /**
     * Create a new sequence with the name and sequence pre-allocation size.
     */
    public NativeSequence(String name, int size) {
        super(name, size);
        setShouldSkipUpdate(true);
    }

    public NativeSequence(String name, int size, int initialValue) {
        super(name, size, initialValue);
        setShouldSkipUpdate(true);
    }    

    public boolean isNative() {
        return true;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof NativeSequence) {
            return equalNameAndSize(this, (NativeSequence)obj);
        } else {
            return false;
        }
    }

    /**
    * INTERNAL:
    */
    protected ValueReadQuery buildSelectQuery() {
        return ((DatabasePlatform)getDatasourcePlatform()).buildSelectQueryForNativeSequence();
    }

    /**
    * INTERNAL:
    */
    protected ValueReadQuery buildSelectQuery(String seqName, Integer size) {
        return ((DatabasePlatform)getDatasourcePlatform()).buildSelectQueryForNativeSequence(seqName, size);
    }

    /**
    * INTERNAL:
    */
    public void onConnect() {
        DatabasePlatform dbPlatform = null;
        try {
            dbPlatform = (DatabasePlatform)getDatasourcePlatform();
        } catch (ClassCastException ex) {
            if (getSelectQuery() == null) {
                throw ValidationException.platformDoesNotSupportSequence(getName(), Helper.getShortClassName(getDatasourcePlatform()), Helper.getShortClassName(this));
            }
        }
        if (!dbPlatform.supportsNativeSequenceNumbers() && (getSelectQuery() == null)) {
            throw ValidationException.platformDoesNotSupportSequence(getName(), Helper.getShortClassName(getDatasourcePlatform()), Helper.getShortClassName(this));
        }
        super.onConnect();
        if (dbPlatform != null) {
            setShouldAcquireValueAfterInsert(dbPlatform.shouldNativeSequenceAcquireValueAfterInsert());
            setShouldUseTransaction(dbPlatform.shouldNativeSequenceUseTransaction());
        }
    }

    /**
    * INTERNAL:
    */
    public void onDisconnect() {
        setShouldAcquireValueAfterInsert(false);
        setShouldUseTransaction(false);
        super.onDisconnect();
    }
}
