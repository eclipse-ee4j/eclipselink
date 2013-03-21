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
 *     James Sutherland - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A BatchFetch annotation can be used on any relationship mapping,
 * (OneToOne, ManyToOne, OneToMany, ManyToMany, ElementCollection, BasicCollection, BasicMap).
 * It allows the related objects to be batch read in a single query.
 * Batch fetching can also be set at the query level, and it is 
 * normally recommended to do so as all queries may not require batching.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.1
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BatchFetch {
    /**
     * (Optional) The type of batch-fetch to use.
     * Either JOIN, EXISTS or IN.
     * JOIN is the default.
     */ 
    BatchFetchType value() default BatchFetchType.JOIN;
    
    /**
     * Define the default batch fetch size.
     * This is only used for IN type batch reading and defines
     * the number of keys used in each IN clause.
     * The default size is 256, or the query's pageSize for cursor queries.
     */
    int size() default -1;
}
