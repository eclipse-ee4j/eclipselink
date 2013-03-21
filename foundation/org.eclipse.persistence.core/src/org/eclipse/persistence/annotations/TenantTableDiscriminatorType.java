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

/** 
 * An enum that is used within the TenantTableDiscriminator annotation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */ 
public enum TenantTableDiscriminatorType {
    /**
     * Apply the tenant table discriminator as a schema to all multitenant tables.
     * NOTE: this strategy requires appropriate database provisioning.
     */
    SCHEMA, 
    
    /**
     * Apply the tenant table discriminator as a suffix to all multitenant tables. This
     * is the default strategy.
     */
    SUFFIX, 
    
    /**
     * Apply the tenant table discriminator as a prefix to all multitenant tables.
     */
    PREFIX
}
