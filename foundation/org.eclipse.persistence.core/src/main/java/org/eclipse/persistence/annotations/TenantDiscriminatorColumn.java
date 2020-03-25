/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.persistence.DiscriminatorType;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;

/**
 * Tenant discriminator column(s) are used with a SINGLE_TABLE multitenant
 * strategy. Tenant discriminator column(s) are completely user specified and
 * there is no limit on how many tenant discriminator columns an application
 * can define (using the TenantDiscriminatorColumns annotation)
 *
 * Tenant discriminator column(s) can be specified at the Entity or
 * MappedSuperclass level and must always be accompanied with a
 * Multitenant(SINGLE_TABLE) specification. It is not sufficient to specify
 * only tenant discriminator column(s).
 *
 * @see org.eclipse.persistence.annotations.TenantDiscriminatorColumns
 * @see org.eclipse.persistence.annotations.Multitenant
 * @see org.eclipse.persistence.annotations.MultitenantType
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(TenantDiscriminatorColumns.class)
public @interface TenantDiscriminatorColumn {
    /**
     * (Optional) The name of column to be used for the tenant discriminator.
     */
    String name() default "TENANT_ID";

    /**
     * (Optional) The name of the context property to apply to the
     * tenant discriminator column.
     */
    String contextProperty() default "eclipselink.tenant-id";

    /**
     * (Optional) The type of object/column to use as a class discriminator.
     * Defaults to {@link DiscriminatorType#STRING DiscriminatorType.STRING}.
     */
    DiscriminatorType discriminatorType() default DiscriminatorType.STRING;

    /**
     * (Optional) The SQL fragment that is used when generating the DDL
     * for the discriminator column.
     * <p> Defaults to the provider-generated SQL to create a column
     * of the specified discriminator type.
     */
    String columnDefinition() default "";

    /**
     * (Optional) The column length for String-based discriminator types.
     * Ignored for other discriminator types.
     */
    int length() default 31;

    /**
     * (Optional) The name of the table that contains the column.
     * If absent the column is assumed to be in the primary table.
     */
    String table() default "";

    /**
     * Specifies that the tenant discriminator column is part of the primary
     * key of the tables.
     */
    boolean primaryKey() default false;
}

