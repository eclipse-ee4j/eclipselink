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
public interface Index {

    public Index addColumnName(String columnName); // EclipseLink index
    public Index setCatalog(String catalog); // EclipseLink index
    public Index setColumnList(String columnList); // JPA 2.1 index
    public Index setName(String name); // EclipseLink and JPA 2.1 index
    public Index setSchema(String schema); // EclipseLink index
    public Index setTable(String table); // EclipseLink index
    public Index setUnique(Boolean unique); // EclipseLink and JPA 2.1 index

}
