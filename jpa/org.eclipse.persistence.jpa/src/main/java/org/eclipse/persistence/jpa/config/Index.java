/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
public interface Index {

    Index addColumnName(String columnName); // EclipseLink index
    Index setCatalog(String catalog); // EclipseLink index
    Index setColumnList(String columnList); // JPA 2.1 index
    Index setName(String name); // EclipseLink and JPA 2.1 index
    Index setSchema(String schema); // EclipseLink index
    Index setTable(String table); // EclipseLink index
    Index setUnique(Boolean unique); // EclipseLink and JPA 2.1 index

}
