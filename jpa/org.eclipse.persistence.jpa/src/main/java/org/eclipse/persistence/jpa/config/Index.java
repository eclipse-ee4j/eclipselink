/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    public Index addColumnName(String columnName); // EclipseLink index
    public Index setCatalog(String catalog); // EclipseLink index
    public Index setColumnList(String columnList); // JPA 2.1 index
    public Index setName(String name); // EclipseLink and JPA 2.1 index
    public Index setSchema(String schema); // EclipseLink index
    public Index setTable(String table); // EclipseLink index
    public Index setUnique(Boolean unique); // EclipseLink and JPA 2.1 index

}
