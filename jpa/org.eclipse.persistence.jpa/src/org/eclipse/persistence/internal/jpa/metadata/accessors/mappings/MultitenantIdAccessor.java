/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.mappings.MultitenantPrimaryKeyMapping;

/**
 * A multitenant id accessor. This is a  special type of mapping accessor in 
 * that it is not mapped in the object and this mapping accessor is not
 * mapped through annotations and/or XML. They are created on the fly when
 * tenant discrimator columns are found that specify primaryKey=true. 
 * 
 * Key notes:
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class MultitenantIdAccessor extends IdAccessor {
    protected DatabaseField m_tenantDiscriminatorField;
    protected String m_contextProperty;
    
    /**
     * INTERNAL:
     */
    public MultitenantIdAccessor(String contextProperty, DatabaseField tenantDiscriminatorField, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(null, accessibleObject, classAccessor);
        
        m_contextProperty = contextProperty;
        m_tenantDiscriminatorField = tenantDiscriminatorField;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isMultitenantId() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a multitenant id accessor.
     */
    @Override
    public void process() {
        MultitenantPrimaryKeyMapping mapping = new MultitenantPrimaryKeyMapping();
        setMapping(mapping);
        
        // Set the tenant discriminator field.
        mapping.setField(m_tenantDiscriminatorField);

        // Set the context property.
        mapping.setContextProperty(m_contextProperty);
        
        // Add it to the list of primary key fields.
        getDescriptor().addPrimaryKeyField(m_tenantDiscriminatorField, this);
    }
}
