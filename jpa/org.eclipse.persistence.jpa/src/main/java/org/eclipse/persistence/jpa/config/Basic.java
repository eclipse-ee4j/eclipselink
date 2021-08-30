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
public interface Basic {

    Convert addConvert();
    Property addProperty();
    Basic setAccess(String access);
    AccessMethods setAccessMethods();
    Basic setAttributeType(String attributeType);
    CacheIndex setCacheIndex();
    Column setColumn();
    Basic setConvert(String convert);
    Converter setConverter();
    Enumerated setEnumerated();
    Basic setFetch(String fetch);
    Field setField();
    GeneratedValue setGeneratedValue();
    Index setIndex();
    Lob setLob();
    Basic setMutable(Boolean mutable);
    Basic setName(String name);
    ObjectTypeConverter setObjectTypeConverter();
    Basic setOptional(Boolean optional);
    ReturnInsert setReturnInsert();
    Basic setReturnUpdate();
    SequenceGenerator setSequenceGenerator();
    StructConverter setStructConverter();
    TableGenerator setTableGenerator();
    Temporal setTemporal();
    TypeConverter setTypeConverter();
    UuidGenerator setUuidGenerator();

}
