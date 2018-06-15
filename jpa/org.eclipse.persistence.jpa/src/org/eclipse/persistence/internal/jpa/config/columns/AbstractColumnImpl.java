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
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractColumnImpl<T extends MetadataColumn, R> extends MetadataImpl<T> {

    public AbstractColumnImpl(T t) {
        super(t);
    }

    public R setColumnDefinition(String columnDefinition) {
        getMetadata().setColumnDefinition(columnDefinition);
        return (R) this;
    }

    public R setName(String name) {
        getMetadata().setName(name);
        return (R) this;
    }
}
