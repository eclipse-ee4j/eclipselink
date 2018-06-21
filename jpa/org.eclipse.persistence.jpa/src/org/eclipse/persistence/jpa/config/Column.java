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
package org.eclipse.persistence.jpa.config;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface Column {

    public Column setName(String name);
    public Column setUnique(Boolean unique);
    public Column setNullable(Boolean nullable);
    public Column setInsertable(Boolean insertable);
    public Column setUpdatable(Boolean updatable);
    public Column setColumnDefinition(String columnDefinition);
    public Column setTable(String table);
    public Column setLength(Integer length);
    public Column setPrecision(Integer precision);
    public Column setScale(Integer scale);

}
