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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.Column;

import static org.eclipse.persistence.annotations.OptimisticLockingType.VERSION_COLUMN;

/** 
 * The OptimisticLocking annotation is used to specify the type of optimistic 
 * locking TopLink should use when updating or deleting entities.
 * 
 * An optimistic-locking specification is supported on an Entity or 
 * MappedSuperclass annotation.
 * 
 * @see org.eclipse.persistence.annotations.OptimisticLockingType.
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface OptimisticLocking {
    /**
     * (Optional) The type of optimistic locking policy to use.
     */
    OptimisticLockingType type() default VERSION_COLUMN;

    /**
     * (Optional) For an optimistic locking policy of type SELECTED_COLUMNS, 
     * this annotation member becomes a (Required) field.
     */
    Column[] selectedColumns() default {};

    /**
     * (Optional) Specify where the optimistic locking policy should cascade 
     * lock. Currently only supported with VERSION_COLUMN locking.
     */
    boolean cascade() default false;
}
