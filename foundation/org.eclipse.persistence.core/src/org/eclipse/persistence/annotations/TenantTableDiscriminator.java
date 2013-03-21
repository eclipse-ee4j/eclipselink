/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * A tenant table discriminator is used with a TABLE_PER_TENANT multitenant 
 * strategy. The tenant table discriminator describes the type of table 
 * discriminator to use. The user may choose their own tenant identifier
 * property or use the default property:
 * 
 * org.eclipse.persistence.config.PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT = "eclipselink.tenant-id"
 * 
 * <p>
 * Tenant table discriminator can be specified at the Entity or MappedSuperclass 
 * level and must always be accompanied with a Multitenant(TABLE_PER_TENANT) 
 * specification. It is not sufficient to specify only a tenant table discriminator.
 * 
 * @see org.eclipse.persistence.annotations.TenantTableDiscriminator
 * @see org.eclipse.persistence.annotations.Multitenant
 * @see org.eclipse.persistence.annotations.MultitenantType
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface TenantTableDiscriminator {
    /**
     * (Optional) The name of the context property to apply to as 
     * tenant table discriminator. Default is "eclipselink-tenant.id"
     */
    String contextProperty() default PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT;
    
    /**
     * (Optional) The type of tenant table discriminator to use with the tables
     * of the persistence unit.
     * Defaults to {@link TenantTableDiscriminatorType#SUFFIX TenantTableDiscriminatorType.SUFFIX}.
     */
    TenantTableDiscriminatorType type() default TenantTableDiscriminatorType.SUFFIX;
}
