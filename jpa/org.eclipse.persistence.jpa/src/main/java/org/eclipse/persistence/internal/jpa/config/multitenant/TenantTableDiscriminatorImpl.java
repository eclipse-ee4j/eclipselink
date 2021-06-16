/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier, Doug Clarke - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.multitenant;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.TenantTableDiscriminatorMetadata;
import org.eclipse.persistence.jpa.config.TenantTableDiscriminator;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class TenantTableDiscriminatorImpl extends MetadataImpl<TenantTableDiscriminatorMetadata> implements TenantTableDiscriminator {

    public TenantTableDiscriminatorImpl() {
        super(new TenantTableDiscriminatorMetadata());
    }

    @Override
    public TenantTableDiscriminator setContextProperty(String contextProperty) {
        getMetadata().setContextProperty(contextProperty);
        return this;
    }

    @Override
    public TenantTableDiscriminator setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
