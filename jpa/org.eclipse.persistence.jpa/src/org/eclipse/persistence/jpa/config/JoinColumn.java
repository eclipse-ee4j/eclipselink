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
public interface JoinColumn {

    public JoinColumn setName(String name);
    public JoinColumn setReferencedColumnName(String referencedColumnName);
    public JoinColumn setUnique(Boolean unique);
    public JoinColumn setNullable(Boolean nullable);
    public JoinColumn setInsertable(Boolean insertable);
    public JoinColumn setUpdatable(Boolean updatable);
    public JoinColumn setColumnDefinition(String columnDefinition);
    public JoinColumn setTable(String table);

}
