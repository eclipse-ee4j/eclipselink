/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDiscriminatorColumnImpl<T extends DiscriminatorColumnMetadata, R> extends AbstractColumnImpl<T, R> {

    public AbstractDiscriminatorColumnImpl(T t) {
        super(t);
    }

    public R setDiscriminatorType(String discriminatorType) {
        getMetadata().setDiscriminatorType(discriminatorType);
        return (R) this;
    }

    public R setLength(Integer length) {
        getMetadata().setLength(length);
        return (R) this;
    }

}
