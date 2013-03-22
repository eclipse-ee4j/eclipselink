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
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * Allow a cache index to be define.
 * A cache index allow singleResult queries to obtain a cache hit when querying on the indexed fields.
 * resultList queries cannot obtain cache hits, as it is unknown if all of the objects are in memory,
 * (unless the cache usage query hint is used).
 * The index should be unique, but if not unique, the first indexed object will be returned.
 * Cache indexes are only relevant when caching is enabled.
 * The @CacheIndex can be defined on a Entity class, or on an attribute.
 * The column is defaulted when defined on a attribute.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.4
 */ 
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface CacheIndex {    
    /**
     * Specify the set of columns to define the index on.
     * Not required when annotated on a field/method.
     */
    String[] columnNames() default {};
    
    /**
     * Specify if the indexed field is updateable.
     * If updateable the object will be re-indexed on each update/refresh.
     */
    boolean updateable() default true;
}
