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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A CacheInterceptor can be set on an Entity and allows all EclipseLink cache
 * access to be intercepted by the developer provided interceptor. In the case
 * of inheritance, a CacheInterceptor annotation should only be defined on the
 * root of the inheritance hierarchy.
 * 
 * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CacheInterceptor {
    /**
     * The Class that will be used to intercept EclipseLink's cache access.
     */ 
    Class value();
}
