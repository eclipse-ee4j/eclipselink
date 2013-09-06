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
package org.eclipse.persistence.internal.jpa.config.mappings;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.mappings.BatchFetchMetadata;
import org.eclipse.persistence.jpa.config.BatchFetch;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class BatchFetchImpl extends MetadataImpl<BatchFetchMetadata> implements BatchFetch {
    
    public BatchFetchImpl() {
        super(new BatchFetchMetadata());
    }

    public BatchFetch setSize(Integer size) {
        getMetadata().setSize(size);
        return this;
    }
    
    public BatchFetch setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
