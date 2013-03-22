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
 *     Gordon Yorke - Initial Contribution
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The NonCacheable annotation is used to configure caching behavior for
 * relationships. If this annotation is set on a relationship that relationship
 * will not be cached even though the parent Entity may be cached. Each time the
 * Entity is retrieved the relationship will be reloaded from the data-source.
 * 
 * This may be useful for situations where caching of relationships is not
 * desired or when using different EclipseLink IdentityMap types and having
 * cached references extends the cache lifetime of related Entities using a
 * different caching scheme. 
 * For instance Entity A references Entity B, Entity A is FullIdentityMap and
 * Entity B is WeakIdentityMap. Without removing the caching of the relationship
 * the Entity B's cache effectively become a FullIdentityMap.
 * 
 * @author Gordon Yorke
 * @since EclipseLink 2.2
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Noncacheable {
}
