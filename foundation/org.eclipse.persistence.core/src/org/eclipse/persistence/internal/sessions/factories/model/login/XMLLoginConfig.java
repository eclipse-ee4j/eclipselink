/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.login;

/**
 * INTERNAL:
 */
public class XMLLoginConfig extends LoginConfig {

    private boolean m_equalNamespaceResolvers;
    private DocumentPreservationPolicyConfig m_documentPreservationPolicy;

    public XMLLoginConfig() {
        super();
        m_equalNamespaceResolvers = true;
    }

    public boolean getEqualNamespaceResolvers() {
        return m_equalNamespaceResolvers;
    }

    public void setEqualNamespaceResolvers(boolean value) {
        m_equalNamespaceResolvers = value;
    }

    public DocumentPreservationPolicyConfig getDocumentPreservationPolicy() {
        return m_documentPreservationPolicy;
    }

    public void setDocumentPreservationPolicy(DocumentPreservationPolicyConfig value) {
        m_documentPreservationPolicy = value;
    }

}
