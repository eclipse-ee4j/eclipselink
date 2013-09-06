/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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
