/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.partitioned;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.partitioning.RoundRobinPartitioningPolicy;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;

public class PartitionedCustomizer implements SessionCustomizer {
    public PartitionedCustomizer() {
    }

    public void customize(Session session) {
        
        RoundRobinPartitioningPolicy partitioningPolicy = new RoundRobinPartitioningPolicy();
        partitioningPolicy.addConnectionPool("default");
        partitioningPolicy.addConnectionPool("node2");
        partitioningPolicy.addConnectionPool("node3");
        partitioningPolicy.setReplicateWrites(true);
        ((ServerSession)session).setPartitioningPolicy(partitioningPolicy);        
        
    }
}
