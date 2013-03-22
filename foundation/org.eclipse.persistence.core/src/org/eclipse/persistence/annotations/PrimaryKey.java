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
 *     James Sutherland - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.Column;

/** 
 *  The PrimaryKey annotation allows advanced configuration of the Id.
 *  A validation policy can be given that allows specifying if zero is a valid id value.
 *  The set of primary key columns can also be specified precisely.
 * 
 * @author James Sutherland
 * @since EclipseLink 1.1
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface PrimaryKey {
    /**
     * (Optional) Configures what id validation is done.
     * By default 0 is not a valid id value, this can be used to allow 0 id values.
     */
    IdValidation validation() default IdValidation.ZERO;

    /**
     * (Optional) Configures what cache key type is used to store the object in the cache.
     * By default the type is determined by what type is optimal for the class.
     */
    CacheKeyType cacheKeyType() default CacheKeyType.AUTO;

    /**
     * (Optional) Used to specify the primary key columns directly.
     * This can be used instead of @Id if the primary key includes a non basic field,
     * such as a foreign key, or a inheritance discriminator, embedded, or transformation mapped field.
     */
    Column[] columns() default {}; 

}
