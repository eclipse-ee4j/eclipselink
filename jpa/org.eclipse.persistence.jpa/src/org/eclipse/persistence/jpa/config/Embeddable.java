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
public interface Embeddable {

    public Array addArray();
    public AssociationOverride addAssociationOverride();
    public AttributeOverride addAttributeOverride();
    public Basic addBasic();
    public Converter addConverter();
    public ElementCollection addElementCollection();
    public Embedded addEmbedded();
    public Id addId();
    public ManyToMany addManyToMany();
    public ManyToOne addManyToOne();
    public ObjectTypeConverter addObjectTypeConverter();
    public OneToMany addOneToMany();
    public OneToOne addOneToOne();
    public OracleArray addOracleArray();
    public OracleObject addOracleObject();
    public PlsqlRecord addPlsqlRecord();
    public PlsqlTable addPlsqlTable();
    public Property addProperty();
    public StructConverter addStructConverter();
    public Structure addStructure();
    public Transformation addTransformation();
    public Transient addTransient();
    public TypeConverter addTypeConverter();
    public VariableOneToOne addVariableOneToOne();
    public Version addVersion();
    public Embeddable setAccess(String access);
    public AccessMethods setAccessMethods();
    public ChangeTracking setChangeTracking();
    public Embeddable setClass(String cls);
    public CloneCopyPolicy setCloneCopyPolicy();
    public CopyPolicy setCopyPolicy();
    public Embeddable setCustomizer(String customizer);
    public EmbeddedId setEmbeddedId();
    public Embeddable setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    public InstantiationCopyPolicy setInstantiationCopyPolicy();
    public Embeddable setMetadataComplete(Boolean metadataComplete);
    public NoSql setNoSql();
    public Embeddable setParentClass(String parentClass);
    public Struct setStruct();

}
