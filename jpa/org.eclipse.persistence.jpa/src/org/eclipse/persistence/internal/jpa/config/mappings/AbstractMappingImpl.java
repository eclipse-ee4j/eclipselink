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

import org.eclipse.persistence.internal.jpa.config.AbstractAccessorImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractMappingImpl<T extends MappingAccessor, R> extends AbstractAccessorImpl<T, R> {

    public AbstractMappingImpl(T t) {
        super(t);
    }

    public R setAttributeType(String attributeType) {
        getMetadata().setAttributeType(attributeType);
        return (R) this;
    }

}
