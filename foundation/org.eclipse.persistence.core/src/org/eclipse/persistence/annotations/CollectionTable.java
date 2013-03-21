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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.UniqueConstraint;

/** 
 *  A CollectionTable annotation is used in conjunction with a BasicCollection 
 *  or a BasicMap annotation. However, if a CollectionTable is not defined, one 
 *  will be defaulted.
 * 
 * @see org.eclipse.persistence.annotations.BasicMap
 * @see org.eclipse.persistence.annotations.BasicCollection
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 * 
 * @deprecated
 * @see javax.persistence.CollectionTable
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface CollectionTable {
    /**
     * (Optional) The name of the collection table. If it is not specified, it 
     * is defaulted to the concatenation of the following: the name of the 
     * source entity; "_" ; the name of the relationship property or field of 
     * the source entity.
     */
    String name() default ""; 

    /**
     * (Optional) The catalog of the table. It defaults to the persistence unit 
     * default catalog.
     */
    String catalog() default ""; 

    /**
     * (Optional) The schema of the table. It defaults to the persistence unit 
     * default schema.
     */
    String schema() default ""; 

    /**
     * (Optional) Used to specify a primary key column that is used as a foreign 
     * key to join to another table. If the source entity uses a composite 
     * primary key, a primary key join column must be specified for each field 
     * of the composite primary key. In a single primary key case, a primary key 
     * join column may optionally be specified. Defaulting will apply otherwise 
     * as follows:
     * name, the same name as the primary key column of the primary table of the 
     * source entity.
     * referencedColumnName, the same name of primary key column of the primary 
     * table of the source entity.
     */
    PrimaryKeyJoinColumn[] primaryKeyJoinColumns() default {}; 
 
    /**
     * (Optional) Unique constraints that are to be placed on the table. These 
     * are only used if table generation is in effect.
     */
    UniqueConstraint[] uniqueConstraints() default {}; 
}
