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
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.config.partitioning;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitionMetadata;
import org.eclipse.persistence.jpa.config.RangePartition;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class RangePartitionImpl extends MetadataImpl<RangePartitionMetadata> implements RangePartition {

    public RangePartitionImpl() {
        super(new RangePartitionMetadata());
    }
    
    public RangePartition setConnectionPool(String connectionPool) {
        getMetadata().setConnectionPool(connectionPool);
        return this;
    }
    
    public RangePartition setEndValue(String endValue) {
        getMetadata().setEndValue(endValue);
        return this;
    }
    
    public RangePartition setStartValue(String startValue) {
        getMetadata().setStartValue(startValue);
        return this;
    }
    
}
