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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * A TenantDiscriminatorColumns annotation allows the definition of multiple
 * TenantDiscriminatorColumn.
 * 
 * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
 * @author Guy Pelletier
 * @since EclipseLink 2.3
 */ 
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface TenantDiscriminatorColumns {
   /**
    * (Required) One or more <code>TenantDiscriminatorColumn</code> annotations.
    */
   TenantDiscriminatorColumn[] value();
}

