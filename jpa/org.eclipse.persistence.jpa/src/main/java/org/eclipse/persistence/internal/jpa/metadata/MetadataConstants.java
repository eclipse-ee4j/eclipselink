/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     10/01/2008-1.1 Guy Pelletier
//       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.internal.jpa.metadata;

/**
 * INTERNAL:
 * Common metadata processing constants. Of particular interest are JPA
 * annotations and enums. To ensure EclipseLink remains compliant from release
 * to release, we refer to all JPA annotations and enums with string names. We
 * also do this to be consistent (and alleviate any thought process as to which
 * annotations can and can not be referenced by class).
 *
 * NOTE: Internal EclipseLink annotations can always be referred to by class.
 *
 * 2.1 OR/Metadata annotations will not be added to the eclipselink jar as
 * the 2.0 annotations were. With this solution in place many of the 2.0
 * annotations can also be removed from the jar.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.1
 */
public class MetadataConstants {
    /** JPA Annotations */
    public static final String JPA_ACCESS = "jakarta.persistence.Access";
    public static final String JPA_ASSOCIATION_OVERRIDE = "jakarta.persistence.AssociationOverride";
    public static final String JPA_ASSOCIATION_OVERRIDES = "jakarta.persistence.AssociationOverrides";
    public static final String JPA_ATTRIBUTE_OVERRIDE = "jakarta.persistence.AttributeOverride";
    public static final String JPA_ATTRIBUTE_OVERRIDES = "jakarta.persistence.AttributeOverrides";
    public static final String JPA_BASIC = "jakarta.persistence.Basic";
    public static final String JPA_CACHE = "jakarta.persistence.Cache";
    public static final String JPA_CACHEABLE = "jakarta.persistence.Cacheable";
    public static final String JPA_CACHE_RETRIEVE_MODE = "jakarta.persistence.CacheRetrieveMode";
    public static final String JPA_CACHE_STORE_MODE = "jakarta.persistence.CacheStoreMode";
    public static final String JPA_COLLECTION_TABLE = "jakarta.persistence.CollectionTable";
    public static final String JPA_COLUMN = "jakarta.persistence.Column";
    public static final String JPA_COLUMN_RESULT = "jakarta.persistence.ColumnResult";
    public static final String JPA_CONVERT = "jakarta.persistence.Convert";
    public static final String JPA_CONVERTS = "jakarta.persistence.Converts";
    public static final String JPA_CONVERTER = "jakarta.persistence.Converter";
    public static final String JPA_DISCRIMINATOR_COLUMN = "jakarta.persistence.DiscriminatorColumn";
    public static final String JPA_DISCRIMINATOR_VALUE = "jakarta.persistence.DiscriminatorValue";
    public static final String JPA_ELEMENT_COLLECTION = "jakarta.persistence.ElementCollection";
    public static final String JPA_EMBEDDABLE = "jakarta.persistence.Embeddable";
    public static final String JPA_EMBEDDED = "jakarta.persistence.Embedded";
    public static final String JPA_EMBEDDED_ID = "jakarta.persistence.EmbeddedId";
    public static final String JPA_ENTITY = "jakarta.persistence.Entity";
    public static final String JPA_ENTITY_GRAPH = "jakarta.persistence.NamedEntityGraph";
    public static final String JPA_ENTITY_GRAPHS = "jakarta.persistence.NamedEntityGraphs";
    public static final String JPA_ENTITY_LISTENERS = "jakarta.persistence.EntityListeners";
    public static final String JPA_ENTITY_RESULT = "jakarta.persistence.EntityResult";
    public static final String JPA_ENUMERATED = "jakarta.persistence.Enumerated";
    public static final String JPA_EXCLUDE_DEFAULT_LISTENERS = "jakarta.persistence.ExcludeDefaultListeners";
    public static final String JPA_EXCLUDE_SUPERCLASS_LISTENERS = "jakarta.persistence.ExcludeSuperclassListeners";
    public static final String JPA_FIELD_RESULT = "jakarta.persistence.FieldResult";
    public static final String JPA_GENERATED_VALUE = "jakarta.persistence.GeneratedValue";
    public static final String JPA_ID = "jakarta.persistence.Id";
    public static final String JPA_ID_CLASS = "jakarta.persistence.IdClass";
    public static final String JPA_INHERITANCE = "jakarta.persistence.Inheritance";
    public static final String JPA_JOIN_COLUMN = "jakarta.persistence.JoinColumn";
    public static final String JPA_JOIN_COLUMNS = "jakarta.persistence.JoinColumns";
    public static final String JPA_JOIN_TABLE = "jakarta.persistence.JoinTable";
    public static final String JPA_LOB = "jakarta.persistence.Lob";
    public static final String JPA_MANY_TO_MANY = "jakarta.persistence.ManyToMany";
    public static final String JPA_MANY_TO_ONE = "jakarta.persistence.ManyToOne";
    public static final String JPA_MAP_KEY = "jakarta.persistence.MapKey";
    public static final String JPA_MAP_KEY_CLASS = "jakarta.persistence.MapKeyClass";
    public static final String JPA_MAP_KEY_COLUMN = "jakarta.persistence.MapKeyColumn";
    public static final String JPA_MAP_KEY_ENUMERATED = "jakarta.persistence.MapKeyEnumerated";
    public static final String JPA_MAP_KEY_JOIN_COLUMN = "jakarta.persistence.MapKeyJoinColumn";
    public static final String JPA_MAP_KEY_JOIN_COLUMNS = "jakarta.persistence.MapKeyJoinColumns";
    public static final String JPA_MAP_KEY_TEMPORAL = "jakarta.persistence.MapKeyTemporal";
    public static final String JPA_MAPPED_SUPERCLASS = "jakarta.persistence.MappedSuperclass";
    public static final String JPA_MAPS_ID = "jakarta.persistence.MapsId";
    public static final String JPA_NAMED_NATIVE_QUERY = "jakarta.persistence.NamedNativeQuery";
    public static final String JPA_NAMED_NATIVE_QUERIES = "jakarta.persistence.NamedNativeQueries";
    public static final String JPA_NAMED_QUERY = "jakarta.persistence.NamedQuery";
    public static final String JPA_NAMED_QUERIES = "jakarta.persistence.NamedQueries";
    public static final String JPA_NAMED_STORED_PROCEDURE_QUERY = "jakarta.persistence.NamedStoredProcedureQuery";
    public static final String JPA_NAMED_STORED_PROCEDURE_QUERIES = "jakarta.persistence.NamedStoredProcedureQueries";
    public static final String JPA_ONE_TO_MANY = "jakarta.persistence.OneToMany";
    public static final String JPA_ONE_TO_ONE = "jakarta.persistence.OneToOne";
    public static final String JPA_ORDER_BY = "jakarta.persistence.OrderBy";
    public static final String JPA_ORDER_COLUMN = "jakarta.persistence.OrderColumn";
    public static final String JPA_PARAMETER = "jakarta.persistence.Parameter";
    public static final String JPA_POST_LOAD = "jakarta.persistence.PostLoad";
    public static final String JPA_POST_PERSIST = "jakarta.persistence.PostPersist";
    public static final String JPA_POST_REMOVE = "jakarta.persistence.PostRemove";
    public static final String JPA_POST_UPDATE = "jakarta.persistence.PostUpdate";
    public static final String JPA_PRE_PERSIST = "jakarta.persistence.PrePersist";
    public static final String JPA_PRE_REMOVE = "jakarta.persistence.PreRemove";
    public static final String JPA_PRE_UPDATE = "jakarta.persistence.PreUpdate";
    public static final String JPA_PRIMARY_KEY_JOIN_COLUMN = "jakarta.persistence.PrimaryKeyJoinColumn";
    public static final String JPA_PRIMARY_KEY_JOIN_COLUMNS = "jakarta.persistence.PrimaryKeyJoinColumns";
    public static final String JPA_SECONDARY_TABLE = "jakarta.persistence.SecondaryTable";
    public static final String JPA_SECONDARY_TABLES = "jakarta.persistence.SecondaryTables";
    public static final String JPA_SEQUENCE_GENERATOR = "jakarta.persistence.SequenceGenerator";
    public static final String JPA_SEQUENCE_GENERATORS = "jakarta.persistence.SequenceGenerators";
    public static final String JPA_SQL_RESULT_SET_MAPPING = "jakarta.persistence.SqlResultSetMapping";
    public static final String JPA_SQL_RESULT_SET_MAPPINGS = "jakarta.persistence.SqlResultSetMappings";
    public static final String JPA_STORED_PROCEDURE_PARAMETER = "jakarta.persistence.StoredProcedureParameter";
    public static final String JPA_TABLE = "jakarta.persistence.Table";
    public static final String JPA_TABLE_GENERATOR = "jakarta.persistence.TableGenerator";
    public static final String JPA_TABLE_GENERATORS = "jakarta.persistence.TableGenerators";
    public static final String JPA_TEMPORAL = "jakarta.persistence.Temporal";
    public static final String JPA_TRANSIENT = "jakarta.persistence.Transient";
    public static final String JPA_UNIQUE_CONSTRAINT = "jakarta.persistence.UniqueConstraint";
    public static final String JPA_VERSION = "jakarta.persistence.Version";
    public static final String JPA_STATIC_METAMODEL = "jakarta.persistence.metamodel.StaticMetamodel";

    /** JPA AccessType enum values */
    public static final String JPA_ACCESS_FIELD = "FIELD";
    public static final String JPA_ACCESS_PROPERTY = "PROPERTY";
    public static final String EL_ACCESS_VIRTUAL = "VIRTUAL";

    /** JPA ConstraintMode enum values */
    public static final String JPA_CONSTRAINT_MODE_CONSTRAINT = "CONSTRAINT";
    public static final String JPA_CONSTRAINT_MODE_NO_CONSTRAINT = "NO_CONSTRAINT";
    public static final String JPA_CONSTRAINT_MODE_PROVIDER_DEFAULT = "PROVIDER_DEFAULT";

    /** JPA FetchType enum values */
    public static final String JPA_FETCH_EAGER = "EAGER";
    public static final String JPA_FETCH_LAZY = "LAZY";

    /** JPA DiscriminatorType enum values */
    public static final String JPA_DISCRIMINATOR_STRING = "STRING";
    public static final String JPA_DISCRIMINATOR_CHAR = "CHAR";
    public static final String JPA_DISCRIMINATOR_INTEGER = "INTEGER";

    /** JPA EnumType enum values */
    public static final String JPA_ENUM_ORDINAL = "ORDINAL";
    public static final String JPA_ENUM_STRING = "STRING";

    /** JPA TemporalType enum values */
    public static final String JPA_TEMPORAL_DATE = "DATE";
    public static final String JPA_TEMPORAL_TIME = "TIME";
    public static final String JPA_TEMPORAL_TIMESTAMP = "TIMESTAMP";

    /** JPA InheritanceType enum values */
    public static final String JPA_INHERITANCE_SINGLE_TABLE = "SINGLE_TABLE";
    public static final String JPA_INHERITANCE_TABLE_PER_CLASS = "TABLE_PER_CLASS";
    public static final String JPA_INHERITANCE_JOINED = "JOINED";

    /** JPA CascadeType enum values */
    public static final String JPA_CASCADE_ALL = "ALL";
    public static final String JPA_CASCADE_PERSIST = "PERSIST";
    public static final String JPA_CASCADE_MERGE = "MERGE";
    public static final String JPA_CASCADE_REMOVE = "REMOVE";
    public static final String JPA_CASCADE_REFRESH = "REFRESH";
    public static final String JPA_CASCADE_DETACH = "DETACH";

    /** JPA ParameterMode enum values */
    public static final String JPA_PARAMETER_IN = "IN";
    public static final String JPA_PARAMETER_INOUT = "INOUT";
    public static final String JPA_PARAMETER_OUT = "OUT";
    public static final String JPA_PARAMETER_REF_CURSOR = "REF_CURSOR";

    /** JPA GenerationType enum values */
    public static final String JPA_GENERATION_TABLE = "TABLE";
    public static final String JPA_GENERATION_SEQUENCE = "SEQUENCE";
    public static final String JPA_GENERATION_IDENTITY = "IDENTITY";
    public static final String JPA_GENERATION_AUTO = "AUTO";

    /**
     * Metamodel processing for MappedSuperclasses non-functional names.<p>
     * @see MetadataProject#addMetamodelMappedSuperclass(org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor, MetadataDescriptor)
     **/
    public static final String MAPPED_SUPERCLASS_RESERVED_PK_NAME = "__PK_METAMODEL_RESERVED_IN_MEM_ONLY_FIELD_NAME";
    public static final String MAPPED_SUPERCLASS_RESERVED_TABLE_NAME = "__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME";

    /** Used by canonical model generator */
    public static final String JPA_PERSISTENCE_PACKAGE_PREFIX = "jakarta.persistence";
    public static final String ECLIPSELINK_PERSISTENCE_PACKAGE_PREFIX = "org.eclipse.persistence";
    public static final String ECLIPSELINK_OXM_PACKAGE_PREFIX = "org.eclipse.persistence.oxm";

}
