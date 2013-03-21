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
 *     08/18/2011-2.3.1 Guy Pelletier 
 *       - 355093: Add new 'includeCriteria' flag to Multitenant metadata 
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Multitenant specifies that a given entity is shared amongst multiple tenants
 * of a given application. The multitenant type specifies how the data for these
 * entities are to be stored on the database for each tenant. 
 * 
 * Multitenant can be specified at the Entity or MappedSuperclass level.
 * 
 * @see org.eclipse.persistence.annotations.MultitenantType
 * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumn
 * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumns
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.3
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface Multitenant {
    /**
     * (Optional) Specify the multi-tenant strategy to use.
     */
    MultitenantType value() default MultitenantType.SINGLE_TABLE;
    
    /**
     * (Optional) Indicate if the database requires the tenant criteria to
     * be added to the SELECT, UPDATE, and DELETE queries. By default this is
     * done but when set to false the queries will not be modified and it will
     * be up to the application or database to ensure that the correct criteria 
     * is applied to all queries.
     */
    boolean includeCriteria() default true;
}
