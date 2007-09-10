/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.csm;

import org.eclipse.persistence.internal.sessions.factories.model.clustering.*;

/**
 * INTERNAL:
 */
public class CacheSynchronizationManagerConfig {
    private ClusteringServiceConfig m_clusteringService;
    private boolean m_isAsynchronous;
    private boolean m_removeConnectionOnError;

    public CacheSynchronizationManagerConfig() {
    }

    public void setIsAsynchronous(boolean isAsynchronous) {
        m_isAsynchronous = isAsynchronous;
    }

    public boolean getIsAsynchronous() {
        return m_isAsynchronous;
    }

    public void setRemoveConnectionOnError(boolean removeConnectionOnError) {
        m_removeConnectionOnError = removeConnectionOnError;
    }

    public boolean getRemoveConnectionOnError() {
        return m_removeConnectionOnError;
    }

    public void setClusteringServiceConfig(ClusteringServiceConfig clusteringService) {
        m_clusteringService = clusteringService;
    }

    public ClusteringServiceConfig getClusteringServiceConfig() {
        return m_clusteringService;
    }
}