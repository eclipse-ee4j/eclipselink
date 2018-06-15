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
package org.eclipse.persistence.jpa.config;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface OrderColumn {

    public OrderColumn setColumnDefinition(String columnDefinition);
    public OrderColumn setCorrectionType(String correctionType);
    public OrderColumn setName(String name);
    public OrderColumn setNullable(Boolean nullable);
    public OrderColumn setInsertable(Boolean insertable);
    public OrderColumn setUpdatable(Boolean updatable);

}
