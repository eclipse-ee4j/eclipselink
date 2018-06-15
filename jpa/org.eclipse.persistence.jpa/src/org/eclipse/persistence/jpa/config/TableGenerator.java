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
