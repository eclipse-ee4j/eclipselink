/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
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

    @Override
    public Cascade setCascadeAll() {
        getMetadata().setCascadeAll(true);
        return this;
    }

    @Override
    public Cascade setCascadeDetach() {
        getMetadata().setCascadeDetach(true);
        return this;
    }

    @Override
    public Cascade setCascadeMerge() {
        getMetadata().setCascadeMerge(true);
        return this;
    }

    @Override
    public Cascade setCascadePersist() {
        getMetadata().setCascadePersist(true);
        return this;
    }

    @Override
    public Cascade setCascadeRefresh() {
        getMetadata().setCascadeRefresh(true);
        return this;
    }

    @Override
    public Cascade setCascadeRemove() {
        getMetadata().setCascadeRemove(true);
        return this;
    }

}
