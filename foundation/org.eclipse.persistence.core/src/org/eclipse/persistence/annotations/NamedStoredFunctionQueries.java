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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * A NamedStoredFunctionQueries annotation allows the definition of multiple
 * NamedStoredFunctionQuery.
 * 
 * @author James
 * @since EclipseLink 2.3
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface NamedStoredFunctionQueries {
    /**
     * (Required) An array of named stored procedure query.
     */
    NamedStoredFunctionQuery[] value();
}
