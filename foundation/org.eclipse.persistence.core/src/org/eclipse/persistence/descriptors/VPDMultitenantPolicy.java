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
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     11/15/2011-2.3.2 Guy Pelletier
 *       - 363820: Issue with clone method from VPDMultitenantPolicy
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A vpd multitenant policy.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.3.1
 */
public class VPDMultitenantPolicy extends SingleTableMultitenantPolicy {
    protected String vpdIdentifier;
    protected String vpdIdentifierFieldName;
    
    public VPDMultitenantPolicy(ClassDescriptor descriptor) {
        super(descriptor);
    }
    
    /**
     * INTERNAL:
     * Return all the tenant id fields.
     */
    public void addTenantDiscriminatorField(String property, DatabaseField field) {
        super.addTenantDiscriminatorField(property, field);
        
        // TODO: we could check for multiple different vpdIdentifiers for the
        // same policy. Right now, last one in partly wins (i.e. unless another
        // class sets a vpd identifier different then the last one in here).
        
        vpdIdentifier = property;
        vpdIdentifierFieldName = field.getName();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void addToTableDefinition(TableDefinition tableDefinition) {
        tableDefinition.setCreateVPDCalls(true, vpdIdentifierFieldName);
    }
    
    /**
     * INTERNAL:
     */
    public MultitenantPolicy clone(ClassDescriptor descriptor) {
        VPDMultitenantPolicy clonedPolicy = new VPDMultitenantPolicy(descriptor);
        clonedPolicy.includeTenantCriteria = includeTenantCriteria;
        clonedPolicy.tenantDiscriminatorFields = tenantDiscriminatorFields;
        clonedPolicy.vpdIdentifier = vpdIdentifier;
        clonedPolicy.vpdIdentifierFieldName = vpdIdentifierFieldName;
        return clonedPolicy;
    }
    
    /**
     * INTERNAL:
     * Return the single identifier.
     */
    public String getVPDIdentifier() {
        return vpdIdentifier;
    }
    
    /**
     * INTERNAL:
     * Allow the descriptor to initialize any dependencies on this session.
     */
    @Override
    public void preInitialize(AbstractSession session) throws DescriptorException {
        super.preInitialize(session);
        
        // Set the VPD identifier and connection queries on the session.
        String vpdIdentifier = session.getProject().getVPDIdentifier();
        
        if (vpdIdentifier != null) {
            if (! vpdIdentifier.equals(getVPDIdentifier())) {
                throw ValidationException.multipleVPDIdentifiersSpecified(vpdIdentifier, session.getProject().getVPDLastIdentifierClassName(), getVPDIdentifier(), getDescriptor().getJavaClassName());
            }
        } else {
            // Initialize the VPD queries and set them on the project.
            session.getProject().setVPDIdentifier(getVPDIdentifier());
            session.getProject().setVPDLastIdentifierClassName(getDescriptor().getJavaClassName());
        }
    }
}