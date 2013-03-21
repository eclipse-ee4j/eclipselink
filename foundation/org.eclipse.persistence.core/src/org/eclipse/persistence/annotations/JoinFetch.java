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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A JoinFetch annotation can be used on any relationship mapping,
 * (OneToOne, ManyToOne, OneToMany, ManyToMany, BasicCollection, BasicMap).
 * It allows the related objects to be joined and read in the same query as the 
 * source object. Join fetching can also be set at the query level, and it is 
 * normally recommended to do so as all queries may not require joining.
 * Batch reading should be considered as an alternative to join fetching, 
 * especially for collection relationships as it is typically more efficient.
 * 
 * @author James Sutherland
 * @since Oracle TopLink 11.1.1.0.0 
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface JoinFetch {
    /**
     * (Optional) The type of join-fetch to use.
     * Either an inner or outer-join,
     * an outer-join allows for null/empty values, where as inner does not.
     */ 
    JoinFetchType value() default JoinFetchType.INNER;
}
