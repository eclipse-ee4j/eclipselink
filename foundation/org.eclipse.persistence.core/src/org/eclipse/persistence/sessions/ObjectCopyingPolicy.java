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
package org.eclipse.persistence.sessions;

import java.util.*;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.*;

/**
 * <b>Purpose</b>: Define how an object is to be copied.<p>
 * <b>Description</b>: This is for usage with the object copying feature, not the unit of work.
 *                     This is useful for copying an entire object graph as part of the
 *                     host application's logic.<p>
 * <b>Responsibilities</b>:<ul>
 * <li> Indicate through CASCADE levels the depth relationships will copied.
 * <li> Indicate if PK attributes should be copied with existing value or should be reset.
 * </ul>
 * @since TOPLink/Java 3.0
 * @see Session#copyObject(Object, ObjectCopyingPolicy)
 */
public class ObjectCopyingPolicy {
    protected boolean shouldResetPrimaryKey;
    protected org.eclipse.persistence.internal.sessions.AbstractSession session;
    protected Map copies;

    /** Policy depth that determines how the copy will cascade to the object's
        related parts */
    protected int depth;

    /** Depth level indicating that NO relationships should be included in the copy.
        Relationships that are not copied will include the default value of the object's
        instantiation policy */
    public static final int NO_CASCADE = 1;

    /** Depth level indicating that only relationships with mapping indicated privately-
        owned should be copied */
    public static final int CASCADE_PRIVATE_PARTS = 2;

    /** Depth level indicating that all relationships with mappings should be used when
        building the copied object graph */
    public static final int CASCADE_ALL_PARTS = 3;

    /**
     * PUBLIC:
     * Return a new copying policy.
     * By default the policy cascades privately owned parts and nulls primary keys.
     */
    public ObjectCopyingPolicy() {
        this.shouldResetPrimaryKey = true;
        // 2612538 - the default size of Map (32) is appropriate
        this.copies = new IdentityHashMap();
        this.depth = CASCADE_PRIVATE_PARTS;
    }

    /**
     * PUBLIC:
     * Set if the copy should cascade all relationships when copying the object(s).
     */
    public void cascadeAllParts() {
        setDepth(CASCADE_ALL_PARTS);
    }

    /**
     * PUBLIC:
     * Set if the copy should cascade only those relationships that are configured
     * as privately-owned.
     */
    public void cascadePrivateParts() {
        setDepth(CASCADE_PRIVATE_PARTS);
    }

    /**
     * PUBLIC:
     * Set if the copy should not cascade relationships when copying the object(s)
     */
    public void dontCascade() {
        setDepth(NO_CASCADE);
    }

    /**
     * INTERNAL: Get the session.
     */
    public Map getCopies() {
        return copies;
    }

    /**
     * INTERNAL: Return the cascade depth.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * INTERNAL: Return the session.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL: Set the copies.
     */
    public void setCopies(Map newCopies) {
        copies = newCopies;
    }

    /**
     * INTERNAL: Set the cascade depth.
     */
    public void setDepth(int newDepth) {
        depth = newDepth;
    }

    /**
     * INTERNAL: Set the session.
     */
    public void setSession(org.eclipse.persistence.internal.sessions.AbstractSession newSession) {
        session = newSession;
    }

    /**
     * PUBLIC:
     * Set if the primary key should be reset to null.
     */
    public void setShouldResetPrimaryKey(boolean newShouldResetPrimaryKey) {
        shouldResetPrimaryKey = newShouldResetPrimaryKey;
    }

    /**
     * PUBLIC:
     * Return true if the policy has been configured to CASCADE_ALL_PARTS or CASCADE_PRIVATE_PARTS.
     */
    public boolean shouldCascade() {
        return getDepth() != NO_CASCADE;
    }

    /**
     * PUBLIC:
     * Return true if the policy should CASCADE_ALL_PARTS
     */
    public boolean shouldCascadeAllParts() {
        return getDepth() == CASCADE_ALL_PARTS;
    }

    /**
     * PUBLIC:
     * Return true if the policy should CASCADE_PRIVATE_PARTS
     */
    public boolean shouldCascadePrivateParts() {
        return getDepth() == CASCADE_PRIVATE_PARTS;
    }

    /**
     * PUBLIC:
     * Return if the primary key should be reset to null.
     */
    public boolean shouldResetPrimaryKey() {
        return shouldResetPrimaryKey;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        String depthString = "";
        if (shouldCascadeAllParts()) {
            depthString = "CASCADE_ALL_PARTS";
        } else if (shouldCascadePrivateParts()) {
            depthString = "CASCADE_PRIVATE_PARTS";
        } else {
            depthString = "NO_CASCADING";
        }
        Object[] args = { depthString, Boolean.valueOf(shouldResetPrimaryKey()) };
        return Helper.getShortClassName(this) + ToStringLocalization.buildMessage("depth_reset_key", args);
    }
}
