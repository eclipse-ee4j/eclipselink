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
public interface TableGenerator {

    public Index addIndex();
    public UniqueConstraint addUniqueConstraint();
    public TableGenerator setAllocationSize(Integer allocationSize);
    public TableGenerator setCatalog(String catalog);
    public TableGenerator setCreationSuffix(String creationSuffix);
    public TableGenerator setInitialValue(Integer initialValue);
    public TableGenerator setName(String name);
    public TableGenerator setPKColumnName(String pkColumnName);
    public TableGenerator setPKColumnValue(String pkColumnValue);
    public TableGenerator setSchema(String schema);
    public TableGenerator setTable(String table);
    public TableGenerator setValueColumnName(String valueColumnName);

}
