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
 *     rbarkhou - new Policy Config classes for XMLLogin XML Support
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.login;

public abstract class DocumentPreservationPolicyConfig {

    private NodeOrderingPolicyConfig m_nodeOrderingPolicy;

    public NodeOrderingPolicyConfig getNodeOrderingPolicy() {
        return m_nodeOrderingPolicy;
    }

    public void setNodeOrderingPolicy(NodeOrderingPolicyConfig value) {
        m_nodeOrderingPolicy = value;
    }

}
