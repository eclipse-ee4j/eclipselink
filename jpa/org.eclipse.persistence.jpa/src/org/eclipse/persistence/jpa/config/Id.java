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
public interface Id {

    public Property addProperty();
    public Id setAccess(String access);
    public AccessMethods setAccessMethods();
    public Id setAttributeType(String attributeType);
    public CacheIndex setCacheIndex();
    public Column setColumn();
    public Id setConvert(String convert);
    public Converter setConverter();
    public Enumerated setEnumerated();
    public Id setFetch(String fetch);
    public Field setField();
    public GeneratedValue setGeneratedValue();
    public Index setIndex();
    public Lob setLob();
    public Id setMutable(Boolean mutable);
    public Id setName(String name);
    public ObjectTypeConverter setObjectTypeConverter();
    public Id setOptional(Boolean optional);
    public ReturnInsert setReturnInsert();
    public Id setReturnUpdate();
    public SequenceGenerator setSequenceGenerator();
    public StructConverter setStructConverter();
    public TableGenerator setTableGenerator();
    public Temporal setTemporal();
    public TypeConverter setTypeConverter();
    public UuidGenerator setUuidGenerator();

}
