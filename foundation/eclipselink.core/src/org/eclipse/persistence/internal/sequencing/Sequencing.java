/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sequencing;


/**
 * <p>
 * <b>Purpose</b>: Define interface to use sequencing.
 * <p>
 * <b>Description</b>: This interface accessed through Session.getSequencing() method.
 * Used by EclipseLink internals to obtain sequencing values.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Provides sequencing objects and supporting APIs.
 * </ul>
 */
public interface Sequencing {
    // Possible return values for whenShouldAcquireValueForAll() method:
    // all classes should acquire sequencing value before insert;
    public static final int BEFORE_INSERT = -1;

    // some classes should acquire sequencing value before insert, some after;
    public static final int UNDEFINED = 0;

    // all classes should acquire sequencing value after insert;
    public static final int AFTER_INSERT = 1;

    /**
    * INTERNAL:
    * Indicates when sequencing value should be acquired for all classes.
    * There are just three possible return values:
    * BEFORE_INSERT, UNDEFINED, AFTER_INSERT.
    * Used as a shortcut to avoid individual checks for each class:
    * shouldAcquireValueAfterInsert(Class cls).
    * Currently UNDEFINED only happens in a case of a SessionBroker:
    * session1 - BEFORE_INSERT, session2 - AFTER_INSERT
    */
    public int whenShouldAcquireValueForAll();

    /**
    * INTERNAL:
    * Indicates whether sequencing value should be acquired
    * before or after INSERT
    */
    public boolean shouldAcquireValueAfterInsert(Class cls);

    /**
    * INTERNAL:
    * Indicates whether existing attribute value should be overridden.
    * This method is called in case an attribute mapped to PK of sequencing-using
    * descriptor contains non-null value.
    * @param seqName String is sequencing number field name
    * @param existingValue Object is a non-null value of PK-mapped attribute.
    */
    public boolean shouldOverrideExistingValue(Class cls, Object existingValue);

    /**
    * INTERNAL:
    * Return the newly-generated sequencing value.
    * @param cls Class for which the sequencing value is generated.
    */
    public Object getNextValue(Class cls);
}