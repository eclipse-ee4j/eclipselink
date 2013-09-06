/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier, Doug Clarke - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.config.multitenant;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.columns.TenantDiscriminatorColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.MultitenantMetadata;
import org.eclipse.persistence.jpa.config.Multitenant;
import org.eclipse.persistence.jpa.config.TenantDiscriminatorColumn;
import org.eclipse.persistence.jpa.config.TenantTableDiscriminator;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class MultitenantImpl extends MetadataImpl<MultitenantMetadata> implements Multitenant {

    public MultitenantImpl() {
        super(new MultitenantMetadata());
        
        getMetadata().setTenantDiscriminatorColumns(new ArrayList<TenantDiscriminatorColumnMetadata>());
    }
    
    public TenantDiscriminatorColumn addTenantDiscriminatorColumn() {
        TenantDiscriminatorColumnImpl tenantDiscriminatorColumn = new TenantDiscriminatorColumnImpl();
        getMetadata().getTenantDiscriminatorColumns().add(tenantDiscriminatorColumn.getMetadata());
        return tenantDiscriminatorColumn;
    }

    public Multitenant setIncludeCriteria(Boolean includeCriteria) {
        getMetadata().setIncludeCriteria(includeCriteria);
        return this;
    }
    
    public TenantTableDiscriminator setTenantTableDiscriminator() {
        TenantTableDiscriminatorImpl tenantTableDiscriminator = new TenantTableDiscriminatorImpl();
        getMetadata().setTenantTableDiscriminator(tenantTableDiscriminator.getMetadata());
        return tenantTableDiscriminator;
    }

    public Multitenant setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
