/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     11/15/2011-2.3.2 Guy Pelletier
//       - 363820: Issue with clone method from VPDMultitenantPolicy
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
    @Override
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
    @Override
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
