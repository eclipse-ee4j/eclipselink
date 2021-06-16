/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
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
