/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 ******************************************************************************/  
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
    public static final String JPA_ACCESS = "javax.persistence.Access";
    public static final String JPA_ASSOCIATION_OVERRIDE = "javax.persistence.AssociationOverride";
    public static final String JPA_ASSOCIATION_OVERRIDES = "javax.persistence.AssociationOverrides";
    public static final String JPA_ATTRIBUTE_OVERRIDE = "javax.persistence.AttributeOverride";
    public static final String JPA_ATTRIBUTE_OVERRIDES = "javax.persistence.AttributeOverrides";
    public static final String JPA_BASIC = "javax.persistence.Basic";
    public static final String JPA_CACHE = "javax.persistence.Cache";
    public static final String JPA_CACHEABLE = "javax.persistence.Cacheable";
    public static final String JPA_CACHE_RETRIEVE_MODE = "javax.persistence.CacheRetrieveMode";
    public static final String JPA_CACHE_STORE_MODE = "javax.persistence.CacheStoreMode";
    public static final String JPA_COLLECTION_TABLE = "javax.persistence.CollectionTable";
    public static final String JPA_COLUMN = "javax.persistence.Column";
    public static final String JPA_COLUMN_RESULT = "javax.persistence.ColumnResult";
    public static final String JPA_DISCRIMINATOR_COLUMN = "javax.persistence.DiscriminatorColumn";
    public static final String JPA_DISCRIMINATOR_VALUE = "javax.persistence.DiscriminatorValue";
    public static final String JPA_ELEMENT_COLLECTION = "javax.persistence.ElementCollection";
    public static final String JPA_EMBEDDABLE = "javax.persistence.Embeddable";
    public static final String JPA_EMBEDDED = "javax.persistence.Embedded";
    public static final String JPA_EMBEDDED_ID = "javax.persistence.EmbeddedId";
    public static final String JPA_ENTITY = "javax.persistence.Entity";
    public static final String JPA_ENTITY_LISTENERS = "javax.persistence.EntityListeners";
    public static final String JPA_ENTITY_RESULT = "javax.persistence.EntityResult";
    public static final String JPA_ENUMERATED = "javax.persistence.Enumerated";
    public static final String JPA_EXCLUDE_DEFAULT_LISTENERS = "javax.persistence.ExcludeDefaultListeners";
    public static final String JPA_EXCLUDE_SUPERCLASS_LISTENERS = "javax.persistence.ExcludeSuperclassListeners";
    public static final String JPA_FIELD_RESULT = "javax.persistence.FieldResult";
    public static final String JPA_GENERATED_VALUE = "javax.persistence.GeneratedValue";
    public static final String JPA_ID = "javax.persistence.Id";
    public static final String JPA_ID_CLASS = "javax.persistence.IdClass";
    public static final String JPA_INHERITANCE = "javax.persistence.Inheritance";
    public static final String JPA_JOIN_COLUMN = "javax.persistence.JoinColumn";
    public static final String JPA_JOIN_COLUMNS = "javax.persistence.JoinColumns";
    public static final String JPA_JOIN_TABLE = "javax.persistence.JoinTable";
    public static final String JPA_LOB = "javax.persistence.Lob";
    public static final String JPA_MANY_TO_MANY = "javax.persistence.ManyToMany";
    public static final String JPA_MANY_TO_ONE = "javax.persistence.ManyToOne";
    public static final String JPA_MAP_KEY = "javax.persistence.MapKey";
    public static final String JPA_MAP_KEY_CLASS = "javax.persistence.MapKeyClass";
    public static final String JPA_MAP_KEY_COLUMN = "javax.persistence.MapKeyColumn";
    public static final String JPA_MAP_KEY_ENUMERATED = "javax.persistence.MapKeyEnumerated";
    public static final String JPA_MAP_KEY_JOIN_COLUMN = "javax.persistence.MapKeyJoinColumn";
    public static final String JPA_MAP_KEY_JOIN_COLUMNS = "javax.persistence.MapKeyJoinColumns";
    public static final String JPA_MAP_KEY_TEMPORAL = "javax.persistence.MapKeyTemporal";
    public static final String JPA_MAPPED_SUPERCLASS = "javax.persistence.MappedSuperclass";
    public static final String JPA_MAPS_ID = "javax.persistence.MapsId";
    public static final String JPA_NAMED_NATIVE_QUERY = "javax.persistence.NamedNativeQuery";
    public static final String JPA_NAMED_NATIVE_QUERIES = "javax.persistence.NamedNativeQueries";
    public static final String JPA_NAMED_QUERY = "javax.persistence.NamedQuery";
    public static final String JPA_NAMED_QUERIES = "javax.persistence.NamedQueries";
    public static final String JPA_NAMED_STORED_PROCEDURE_QUERY = "javax.persistence.NamedStoredProcedureQuery";
    public static final String JPA_NAMED_STORED_PROCEDURE_QUERIES = "javax.persistence.NamedStoredProcedureQueries";
    public static final String JPA_ONE_TO_MANY = "javax.persistence.OneToMany";
    public static final String JPA_ONE_TO_ONE = "javax.persistence.OneToOne";
    public static final String JPA_ORDER_BY = "javax.persistence.OrderBy";
    public static final String JPA_ORDER_COLUMN = "javax.persistence.OrderColumn";
    public static final String JPA_PARAMETER = "javax.persistence.Parameter";
    public static final String JPA_POST_LOAD = "javax.persistence.PostLoad";
    public static final String JPA_POST_PERSIST = "javax.persistence.PostPersist";
    public static final String JPA_POST_REMOVE = "javax.persistence.PostRemove";
    public static final String JPA_POST_UPDATE = "javax.persistence.PostUpdate";
    public static final String JPA_PRE_PERSIST = "javax.persistence.PrePersist";
    public static final String JPA_PRE_REMOVE = "javax.persistence.PreRemove";
    public static final String JPA_PRE_UPDATE = "javax.persistence.PreUpdate";
    public static final String JPA_PRIMARY_KEY_JOIN_COLUMN = "javax.persistence.PrimaryKeyJoinColumn";
    public static final String JPA_PRIMARY_KEY_JOIN_COLUMNS = "javax.persistence.PrimaryKeyJoinColumns";
    public static final String JPA_SECONDARY_TABLE = "javax.persistence.SecondaryTable";
    public static final String JPA_SECONDARY_TABLES = "javax.persistence.SecondaryTables";
    public static final String JPA_SEQUENCE_GENERATOR = "javax.persistence.SequenceGenerator";
    public static final String JPA_SQL_RESULT_SET_MAPPING = "javax.persistence.SqlResultSetMapping";
    public static final String JPA_SQL_RESULT_SET_MAPPINGS = "javax.persistence.SqlResultSetMappings";
    public static final String JPA_STORED_PROCEDURE_PARAMETER = "javax.persistence.StoredProcedureParameter";
    public static final String JPA_TABLE = "javax.persistence.Table";
    public static final String JPA_TABLE_GENERATOR = "javax.persistence.TableGenerator";
    public static final String JPA_TEMPORAL = "javax.persistence.Temporal";
    public static final String JPA_TRANSIENT = "javax.persistence.Transient";
    public static final String JPA_UNIQUE_CONSTRAINT = "javax.persistence.UniqueConstraint";
    public static final String JPA_VERSION = "javax.persistence.Version";
    public static final String JPA_STATIC_METAMODEL = "javax.persistence.metamodel.StaticMetamodel.class";
    
    /** JPA AccessType enum values */
    public static final String JPA_ACCESS_FIELD = "FIELD";
    public static final String JPA_ACCESS_PROPERTY = "PROPERTY";
    public static final String EL_ACCESS_VIRTUAL = "VIRTUAL";

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
     * @See MetadataProject.addMetamodelMappedSuperclass() 
     **/
    public static final String MAPPED_SUPERCLASS_RESERVED_PK_NAME = "__PK_METAMODEL_RESERVED_IN_MEM_ONLY_FIELD_NAME";
    public static final String MAPPED_SUPERCLASS_RESERVED_TABLE_NAME = "__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME";
    
    /** Used by canonical model generator */ 
    public static final String JPA_PERSISTENCE_PACKAGE_PREFIX = "javax.persistence";
    public static final String ECLIPSELINK_PERSISTENCE_PACKAGE_PREFIX = "org.eclipse.persistence.annotations";
}
