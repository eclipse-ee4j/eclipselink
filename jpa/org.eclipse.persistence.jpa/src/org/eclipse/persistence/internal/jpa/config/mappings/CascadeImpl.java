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
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.jpa.config.Cascade;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CascadeImpl extends MetadataImpl<CascadeMetadata> implements Cascade {
    public CascadeImpl() {
        super(new CascadeMetadata());
    }
    
    public Cascade setCascadeAll() {
        getMetadata().setCascadeAll(true);
        return this;
    }

    public Cascade setCascadeDetach() {
        getMetadata().setCascadeDetach(true);
        return this;
    }
    
    public Cascade setCascadeMerge() {
        getMetadata().setCascadeMerge(true);
        return this;
    }

    public Cascade setCascadePersist() {
        getMetadata().setCascadePersist(true);
        return this;
    }
    
    public Cascade setCascadeRefresh() {
        getMetadata().setCascadeRefresh(true);
        return this;
    }
    
    public Cascade setCascadeRemove() {
        getMetadata().setCascadeRemove(true);
        return this;
    }
    
}
