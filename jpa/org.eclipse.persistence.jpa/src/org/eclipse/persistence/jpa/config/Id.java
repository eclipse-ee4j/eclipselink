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