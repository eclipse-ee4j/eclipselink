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
public interface Version {

    public Property addProperty();
    public Version setAccess(String access);
    public AccessMethods setAccessMethods();
    public Version setAttributeType(String attributeType);
    public Column setColumn();
    public Version setConvert(String convert);
    public Converter setConverter();
    public Index setIndex();
    public Version setMutable(Boolean mutable);
    public Version setName(String name);
    public ObjectTypeConverter setObjectTypeConverter();
    public StructConverter setStructConverter();
    public Temporal setTemporal();
    public TypeConverter setTypeConverter();

}
