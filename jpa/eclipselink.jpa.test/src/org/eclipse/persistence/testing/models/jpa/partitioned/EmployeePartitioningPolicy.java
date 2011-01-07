/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.partitioned;


import org.eclipse.persistence.descriptors.partitioning.ReplicationPartitioningPolicy;

/**
 * Used to test a custom policy.
 */
public class EmployeePartitioningPolicy extends ReplicationPartitioningPolicy {
    public EmployeePartitioningPolicy() {
        addConnectionPool("default");
        addConnectionPool("node2");
        addConnectionPool("node3");
    }
}
