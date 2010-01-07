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
package org.eclipse.persistence.internal.sessions;

/**
 * Abstract change record for collection type records that allow deferrable change detection.
 * Used for change tracking when user sets entire collection.
 */
public abstract class DeferrableChangeRecord extends ChangeRecord {

    /**
     * Used for change tracking when user sets entire collection.
     */
    protected transient Object originalCollection;

    /**
     * Used for change tracking when user sets entire collection.
     */
    protected transient Object latestCollection;

    /**
     * Defines if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    protected boolean isDeferred = false;
    
    public DeferrableChangeRecord() {
        super();
    }

    public DeferrableChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * Returns if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    public boolean isDeferred() {
        return isDeferred;
    }

    /**
     * Sets if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    public void setIsDeferred(boolean isDeferred) {
        this.isDeferred = isDeferred;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the last collection that was set on the object.
     */
    public Object getLatestCollection() {
        return latestCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the original collection that was set on the object when it was cloned.
     */
    public Object getOriginalCollection() {
        return originalCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the last collection that was set on the object.
     */
    public void setLatestCollection(Object latestCollection) {
        this.latestCollection = latestCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the original collection that was set on the object when it was cloned.
     */
    public void setOriginalCollection(Object originalCollection) {
        this.originalCollection = originalCollection;
    }
}
