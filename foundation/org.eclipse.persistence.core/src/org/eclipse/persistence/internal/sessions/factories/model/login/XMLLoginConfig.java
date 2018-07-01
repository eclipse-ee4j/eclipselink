/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
