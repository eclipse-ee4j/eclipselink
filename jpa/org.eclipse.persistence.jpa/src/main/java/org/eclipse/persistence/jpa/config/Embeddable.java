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
public interface Embeddable {

    Array addArray();
    AssociationOverride addAssociationOverride();
    AttributeOverride addAttributeOverride();
    Basic addBasic();
    Converter addConverter();
    ElementCollection addElementCollection();
    Embedded addEmbedded();
    Id addId();
    ManyToMany addManyToMany();
    ManyToOne addManyToOne();
    ObjectTypeConverter addObjectTypeConverter();
    OneToMany addOneToMany();
    OneToOne addOneToOne();
    OracleArray addOracleArray();
    OracleObject addOracleObject();
    PlsqlRecord addPlsqlRecord();
    PlsqlTable addPlsqlTable();
    Property addProperty();
    StructConverter addStructConverter();
    Structure addStructure();
    Transformation addTransformation();
    Transient addTransient();
    TypeConverter addTypeConverter();
    VariableOneToOne addVariableOneToOne();
    Version addVersion();
    Embeddable setAccess(String access);
    AccessMethods setAccessMethods();
    ChangeTracking setChangeTracking();
    Embeddable setClass(String cls);
    CloneCopyPolicy setCloneCopyPolicy();
    CopyPolicy setCopyPolicy();
    Embeddable setCustomizer(String customizer);
    EmbeddedId setEmbeddedId();
    Embeddable setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    InstantiationCopyPolicy setInstantiationCopyPolicy();
    Embeddable setMetadataComplete(Boolean metadataComplete);
    NoSql setNoSql();
    Embeddable setParentClass(String parentClass);
    Struct setStruct();

}
