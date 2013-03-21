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
 *     Gordon Yorke - ER 214661 - VM Managed Entity Detachment
 ******************************************************************************/  
package org.eclipse.persistence.config;

/**
 * <b>Purpose:</b> This class is a configuration property used to specify
 * What type of Referenes EclipseLink will use when referencing Entities
 * within the Persistence Context / UnitOfWork.  Depending on the configured
 * ReferenceMode some Entities may be garbage collected.
 * 
 * @author Gordon
 *
 */
public enum ReferenceMode {
    /**
     * References to Objects will be through hard references.  These objects will not be available for
     * garbage collection until the referencing artifact (usually a Persistence Context or UnitOfWork)
     * released or closed.
     */
    HARD,
    
    /**
     * References to Objects that support active attribute change tracking
     * (enabled through weaving or by the developer)will be held by weak
     * references. This means any of afore mentioned objects no longer referenced directly or
     * indirectly will be available for garbage collection. If the object is
     * gc'd before the EM/UnitOfWork flushes to the database then this object
     * and any others like it will not be checked for changes. When a change is
     * made to a change tracked object that object is moved to a hard reference
     * and will not be available for GC until flushed. New and removed objects
     * are also held by hard references. Non change tracked objects will always
     * be held by "hard" references and are not available for GC. This is the
     * default mode for EclipsLink. See:
     * {@link java.lang.ref.WeakReference}
     * {@link org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy}
     */
    WEAK,
    
    /**
     * Same as weak reference except Objects that can not be changed
     * tracked (Deferred Change Detection) will not be prevented from being
     * garbage collected.  This may result in a loss of changes if a changed object
     * is removed before being flushed to the database.
     * When a change is made to a change tracked object that object
     * is moved to a hard reference and will not be available for GC until
     * flushed. New and removed objects are also held by hard references until
     * flush.. 
     */
    FORCE_WEAK
    


}
