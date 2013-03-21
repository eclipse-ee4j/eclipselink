/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - Bug 244124 - Added to handle copying using AttributeGroup
 *                              Functionality copied from ObjectCopyPolicy 
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

import java.util.*;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * <b>Purpose</b>: Define how an object is to be copied.<p>
 * <b>Description</b>: This is for usage with the object copying feature, not the unit of work.
 *                     This is useful for copying an entire object graph as part of the
 *                     host application's logic.<p>
 * <b>Responsibilities</b>:<ul>
 * <li> Indicate through CASCADE levels the depth relationships will copied.
 * <li> Indicate if PK attributes should be copied with existing value or should be reset.
 * <li> Copies only the attributes found in the group.
 * </ul>
 * @see Session#copy(Object, CopyGroup)
 */
public class CopyGroup extends AttributeGroup {
    protected boolean shouldResetPrimaryKey;
    protected boolean shouldResetVersion;
    protected transient org.eclipse.persistence.internal.sessions.AbstractSession session;
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

    /** Depth level indicating that only the attributes found in the attribute group should be copied */
    public static final int CASCADE_TREE = 4;
    
    /**
     * PUBLIC:
     * By default only copy the attributes found in the attribute group and don't null primary keys.
     */
    public CopyGroup() {
        super();
        // 2612538 - the default size of Map (32) is appropriate
        this.copies = new IdentityHashMap();
        this.depth = CASCADE_PRIVATE_PARTS;
    }

    /**
     * PUBLIC:
     * By default only copy the attributes found in the attribute group and don't null primary keys.
     */
    public CopyGroup(String name) {
        super(name);
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
     * Set if the copy should cascade only those attributes that are found in the group.
     */
    public void cascadeTree() {
        setDepth(CASCADE_TREE);
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
     * Set if the version should be reset to null.
     */
    public void setShouldResetVersion(boolean newShouldResetVersion) {
        shouldResetVersion = newShouldResetVersion;
    }

    /**
     * PUBLIC:
     * Return true if has been configured to CASCADE_ALL_PARTS or CASCADE_PRIVATE_PARTS.
     */
    public boolean shouldCascade() {
        return getDepth() != NO_CASCADE;
    }

    /**
     * PUBLIC:
     * Return true if should CASCADE_ALL_PARTS
     */
    public boolean shouldCascadeAllParts() {
        return getDepth() == CASCADE_ALL_PARTS;
    }

    /**
     * PUBLIC:
     * Return true if should CASCADE_PRIVATE_PARTS
     */
    public boolean shouldCascadePrivateParts() {
        return getDepth() == CASCADE_PRIVATE_PARTS;
    }

    /**
     * PUBLIC:
     * Return true if should CASCADE_TREE
     */
    public boolean shouldCascadeTree() {
        return getDepth() == CASCADE_TREE;
    }

    /**
     * PUBLIC:
     * Return if the primary key should be reset to null.
     */
    public boolean shouldResetPrimaryKey() {
        return shouldResetPrimaryKey;
    }

    /**
     * PUBLIC:
     * Return if the version should be reset to null.
     */
    public boolean shouldResetVersion() {
        return shouldResetVersion;
    }

    /**
     * INTERNAL:
     */
    protected String toStringAdditionalInfo() {
        String depthString = "";
        if (shouldCascadeAllParts()) {
            depthString = "CASCADE_ALL_PARTS";
        } else if (shouldCascadePrivateParts()) {
            depthString = "CASCADE_PRIVATE_PARTS";
        } else if (shouldCascadeTree()) {
            depthString = "CASCADE_TREE";
        } else {
            depthString = "NO_CASCADE";
        }
        Object[] args = { depthString, Boolean.valueOf(shouldResetPrimaryKey()), Boolean.valueOf(shouldResetVersion())};
        return ToStringLocalization.buildMessage("depth_reset_key", args);
    }

    @Override
    public void addAttribute(String attributeNameOrPath, CoreAttributeGroup group) {
        cascadeTree();
        if(group == null) {
            super.addAttribute(attributeNameOrPath, (AttributeGroup)null);
        } else {
            addAttribute(attributeNameOrPath, ((AttributeGroup)group).toCopyGroup());
        }
    }

    public void addAttribute(String attributeNameOrPath, CopyGroup group) {
        cascadeTree();
        if(group != null) {
            group.setCopies(getCopies());
            group.cascadeTree();
        }
        super.addAttribute(attributeNameOrPath, group);
    }

    @Override
    public boolean isCopyGroup() {
        return true;
    }
    
    @Override
    public CopyGroup clone() {
        CopyGroup clone = (CopyGroup)super.clone();
        clone.copies = new IdentityHashMap();
        return clone;
    }

    /**
     * Returns CopyGroup corresponding to the passed (possibly nested) attribute.
     */
    @Override
    public CopyGroup getGroup(String attributeNameOrPath) {
        return (CopyGroup)super.getGroup(attributeNameOrPath);
    }

    @Override
    protected CopyGroup newGroup(String name, CoreAttributeGroup parent) {
        CopyGroup copyGroup = new CopyGroup(name);
        copyGroup.cascadeTree();
        if(parent != null) {
            copyGroup.setShouldResetPrimaryKey(((CopyGroup)parent).shouldResetPrimaryKey());
            copyGroup.setShouldResetVersion(((CopyGroup)parent).shouldResetVersion());
            copyGroup.setCopies(((CopyGroup)parent).getCopies());
        }
        return copyGroup;
    }
}
