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
package org.eclipse.persistence.sequencing;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;

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
    /**
     * true indicates that identity should be used - if the platform supports identity.
     * false indicates that sequence objects should be used - if the platform supports sequence objects.
     */
    protected boolean shouldUseIdentityIfPlatformSupports = true;
    
    /**
     * Allow sequencing to be delegated to another sequence if native sequencing is not supported.
     */
    protected QuerySequence delegateSequence;
    
    public NativeSequence() {
        super();
        setShouldSkipUpdate(true);
    }
    
    public NativeSequence(boolean shouldUseIdentityIfPlatformSupports) {
        super();
        setShouldSkipUpdate(true);
        setShouldUseIdentityIfPlatformSupports(shouldUseIdentityIfPlatformSupports);
    }
    
    /**
     * Create a new sequence with the name.
     */
    public NativeSequence(String name) {
        super(name);
        setShouldSkipUpdate(true);
    }
    
    public NativeSequence(String name, boolean shouldUseIdentityIfPlatformSupports) {
        super(name);
        setShouldSkipUpdate(true);
        setShouldUseIdentityIfPlatformSupports(shouldUseIdentityIfPlatformSupports);
    }
    
    /**
     * Create a new sequence with the name and sequence pre-allocation size.
     */
    public NativeSequence(String name, int size) {
        super(name, size);
        setShouldSkipUpdate(true);
    }

    public NativeSequence(String name, int size, boolean shouldUseIdentityIfPlatformSupports) {
        super(name, size);
        setShouldSkipUpdate(true);
        setShouldUseIdentityIfPlatformSupports(shouldUseIdentityIfPlatformSupports);
    }

    public NativeSequence(String name, int size, int initialValue) {
        super(name, size, initialValue);
        setShouldSkipUpdate(true);
    }    

    public NativeSequence(String name, int size, int initialValue, boolean shouldUseIdentityIfPlatformSupports) {
        super(name, size, initialValue);
        setShouldSkipUpdate(true);
        setShouldUseIdentityIfPlatformSupports(shouldUseIdentityIfPlatformSupports);
    }    

    public boolean isNative() {
        if (this.delegateSequence != null) {
            return this.delegateSequence.isNative();
        }
        return true;
    }
    
    public void setShouldUseIdentityIfPlatformSupports(boolean shouldUseIdentityIfPlatformSupports) {
        this.shouldUseIdentityIfPlatformSupports = shouldUseIdentityIfPlatformSupports;
    }
    
    public boolean shouldUseIdentityIfPlatformSupports() {
        return shouldUseIdentityIfPlatformSupports;
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
    @Override
    protected ValueReadQuery buildSelectQuery() {
        if (this.delegateSequence != null) {
            return this.delegateSequence.buildSelectQuery();
        } else if (shouldAcquireValueAfterInsert()) {
            return ((DatasourcePlatform)getDatasourcePlatform()).buildSelectQueryForIdentity();
        } else {
            return ((DatasourcePlatform)getDatasourcePlatform()).buildSelectQueryForSequenceObject();
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    protected ValueReadQuery buildSelectQuery(String seqName, Integer size) {
        if (this.delegateSequence != null) {
            return this.delegateSequence.buildSelectQuery(seqName, size);
        } else if (shouldAcquireValueAfterInsert()) {
            return ((DatabasePlatform)getDatasourcePlatform()).buildSelectQueryForIdentity(getQualified(seqName), size);
        } else {
            return ((DatasourcePlatform)getDatasourcePlatform()).buildSelectQueryForSequenceObject(getQualified(seqName), size);
        }
    }

    /**
     * Return if the sequence should be replaced by another sequence implementation.
     * This is used when the platform does not support the native sequence type.
     */
    public boolean hasDelegateSequence() {
        return delegateSequence != null;
    }

    /**
     * Return the sequence delegate.
     * This is used when the platform does not support the native sequence type.
     */
    public QuerySequence getDelegateSequence() {
        return delegateSequence;
    }

    /**
     * Set the sequence delegate.
     * This is used when the platform does not support the native sequence type.
     */
    public void setDelegateSequence(QuerySequence delegateSequence) {
        this.delegateSequence = delegateSequence;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void onConnect() {
        DatasourcePlatform platform = (DatasourcePlatform)getDatasourcePlatform();
        // Set shouldAcquireValueAfterInsert flag: identity -> true; sequence objects -> false.
        if (platform.supportsIdentity() && shouldUseIdentityIfPlatformSupports()) {
            // identity is both supported by platform and desired by the NativeSequence
            setShouldAcquireValueAfterInsert(true);
        } else if (platform.supportsSequenceObjects() && !shouldUseIdentityIfPlatformSupports()) {
            // sequence objects is both supported by platform and desired by the NativeSequence
            setShouldAcquireValueAfterInsert(false);
        } else {
            if (platform.getDefaultNativeSequenceToTable() || !platform.supportsNativeSequenceNumbers()) {
                // If native sequencing is not supported, or IDENTITY not desire, use TABLE.
                this.delegateSequence = new TableSequence();
                this.delegateSequence.setName(getName());
                this.delegateSequence.onConnect(platform);
                setShouldUseTransaction(this.delegateSequence.shouldUseTransaction());
                setShouldAcquireValueAfterInsert(this.delegateSequence.shouldAcquireValueAfterInsert());
                setShouldSkipUpdate(this.delegateSequence.shouldSkipUpdate());
                setShouldSelectBeforeUpdate(this.delegateSequence.shouldSelectBeforeUpdate());
                setUpdateQuery(this.delegateSequence.getUpdateQuery());
                super.onConnect();
                return;
            } else {
                // platform support contradicts to NativeSequence setting - go with platform supported choice.
                // platform must support either identity or sequence objects (otherwise ValidationException would've been thrown earlier),
                // therefore here dbPlatform.supportsIdentity() == !dbPlatform.supportsSequenceObjects().
                setShouldAcquireValueAfterInsert(platform.supportsIdentity());
            }
        }
        setShouldUseTransaction(platform.shouldNativeSequenceUseTransaction());
        super.onConnect();
    }

    /**
     * INTERNAL:
     */
    @Override
    public void onDisconnect() {
        this.delegateSequence = null;
        setShouldAcquireValueAfterInsert(false);
        setShouldUseTransaction(false);
        super.onDisconnect();
    }
}
