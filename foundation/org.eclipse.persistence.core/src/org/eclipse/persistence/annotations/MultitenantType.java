/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

/** 
 * An enum that is used within the Multitenant annotation.
 * 
 * @see org.eclipse.persistence.annotations.Multitenant.
 * @author Guy Pelletier
 * @since EclipseLink 2.3  
 */
public enum MultitenantType {
    /**
     * Specifies that table(s) the entity maps to includes rows for multiple 
     * tenants. The tenant discriminator column(s) are used with application 
     * context values to limit what a persistence context can access.
     */
    SINGLE_TABLE, 

    /**
     * NOTE: The TABLE_PER_TENANT type is currently not supported and 
     * specifying it on your model classes will result in an exception being 
     * thrown. This type will be supported in a later release (post 2.3).
     *  
     * When supported, this type specifies that different tables are used for 
     * each tenant. The table scan  be uniquely identified by name, schema/tablespace.
     */
    TABLE_PER_TENANT, 
    
    /**
     * Specifies that the DB will handle the tenant filtering on all SELECT,
     * UPDATE and DELETE queries. Using this type assumes that the platform
     * used with your persistence unit does indeed support VPD.
     */
    VPD
}
