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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     04/30/2009-2.0 Michael O'Brien 
 *       - 266912: JPA 2.0 Metamodel API (part of Criteria API)
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     06/17/2009-2.0 Michael O'Brien 
 *       - 266912: change mappedSuperclassDescriptors Set to a Map
 *          keyed on MetadataClass - avoiding the use of a hashCode/equals
 *          override on RelationalDescriptor, but requiring a contains check prior to a put
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel
 *     08/11/2009-2.0 Michael O'Brien 
 *       - 284147: do not add a pseudo PK Field for MappedSuperclasses when
 *         1 or more PK fields already exist on the descriptor. 
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/13/2009-2.0 Guy Pelletier 
 *       - 293629: An attribute referenced from orm.xml is not recognized correctly
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/08/2010-2.1 Michael O'Brien 
 *       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
 *                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
 *                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
 *                      temporary PK field used to process MappedSuperclasses for the Metamodel API
 *                      during MetadataProject.addMetamodelMappedSuperclass()
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     06/09/2010-2.0.3 Guy Pelletier 
 *       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     07/23/2010-2.2 Guy Pelletier 
 *       - 237902: DDL GEN doesn't qualify SEQUENCE table with persistence unit schema
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 251554: ExcludeDefaultMapping annotation needed
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 324471: Do not default to VariableOneToOneMapping for interfaces unless a managed class implementing it is found
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.SharedCacheMode;
import javax.persistence.Embeddable;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.InterfaceAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DirectCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.partitioning.AbstractPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;

import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;

import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;

/**
 * INTERNAL:
 * A MetadataProject stores metadata and also helps to facilitate the metadata
 * processing.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataProject {
    // Sequencing constants.
    public static final String DEFAULT_AUTO_GENERATOR = "SEQ_GEN";
    public static final String DEFAULT_TABLE_GENERATOR = "SEQ_GEN_TABLE";
    public static final String DEFAULT_SEQUENCE_GENERATOR = "SEQ_GEN_SEQUENCE";
    public static final String DEFAULT_IDENTITY_GENERATOR = "SEQ_GEN_IDENTITY";
    
    // Boolean to specify if we should weave eager relationships.
    private boolean m_weaveEager;
    
    // Boolean to specify if we should weave for value holders.
    private boolean m_weavingEnabled;
    
    // Persistence unit info that is represented by this project.
    private PersistenceUnitInfo m_persistenceUnitInfo;

    // The session we are currently processing for.
    private AbstractSession m_session;

    // The logger for the project.
    private MetadataLogger m_logger;

    // Persistence unit metadata for this project.
    private XMLPersistenceUnitMetadata m_persistenceUnitMetadata;

    // A linked map of all the entity mappings (XML file representation)
    private Map<String, XMLEntityMappings> m_entityMappings;
    
    // Map of mapped-superclasses found in XML for this project/persistence unit.
    private Map<String, MappedSuperclassAccessor> m_mappedSuperclasseAccessors;

    // All the class accessors for this project (Entities and Embeddables).
    private HashMap<String, ClassAccessor> m_allAccessors;
    
    // The entity accessors for this project
    private Map<String, EntityAccessor> m_entityAccessors;
    
    // The embeddable accessors for this project
    private Map<String, EmbeddableAccessor> m_embeddableAccessors;
    
    // The interface accessors for this project
    private Map<String, InterfaceAccessor> m_interfaceAccessors;
    
    // Query metadata.
    private Map<String, NamedQueryMetadata> m_queries;
    
    // SQL result set mapping
    private Map<String, SQLResultSetMappingMetadata> m_sqlResultSetMappings;
    
    // Sequencing metadata.
    private Map<MetadataClass, GeneratedValueMetadata> m_generatedValues;
    private Map<String, TableGeneratorMetadata> m_tableGenerators;
    private Map<String, SequenceGeneratorMetadata> m_sequenceGenerators;
    
    // Metadata converters, that is, EclipseLink converters.
    private Map<String, AbstractConverterMetadata> m_converters;
    
    // Store partitioning policies by name, to allow reuse.
    private Map<String, AbstractPartitioningMetadata> m_partitioningPolicies;
    
    // All id classes (IdClass and EmbeddedId classes) used through-out the 
    // persistence unit. We need this list to determine derived id accessors.
    private Set<String> m_idClasses;
    
    // Default listeners that need to be applied to each entity in the
    // persistence unit (unless they exclude them).
    private Set< EntityListenerMetadata> m_defaultListeners;
    
    // Class accessors that have a customizer.
    private Set<ClassAccessor> m_accessorsWithCustomizer;
    
    // Class accessors that have their id derived from a relationship.
    private Set<ClassAccessor> m_accessorsWithDerivedId;
    
    // All direct collection accessors.
    private HashSet<DirectCollectionAccessor> m_directCollectionAccessors;
    
    // Accessors that map to an Embeddable class
    private Set<MappingAccessor> m_embeddableMappingAccessors;
    
    // All owning relationship accessors.
    private Set<RelationshipAccessor> m_owningRelationshipAccessors;
    // All non-owning (mappedBy) relationship accessors.
    private Set<RelationshipAccessor> m_nonOwningRelationshipAccessors;
    
    // Root level embeddable accessors. When we pre-process embeddable
    // accessors we need to process them from the root down so as to set
    // the correct owning descriptor.
    private Set<EmbeddableAccessor> m_rootEmbeddableAccessors;
    
    // Cache the shared cache mode
    private SharedCacheMode m_sharedCacheMode;
    private boolean m_isSharedCacheModeInitialized;
    
    // All mappedSuperclass accessors, identity is handled by keying on className.
    private Map<String, MappedSuperclassAccessor> m_metamodelMappedSuperclasses;
    
    // Boolean to specify if we should uppercase all field names.
    // @see PersistenceUnitProperties.UPPERCASE_COLUMN_NAMES
    private boolean m_forceFieldNamesToUpperCase = false;
    
    // Contains those embeddables and entities that are VIRTUAL (do not exist)
    private Set<ClassAccessor> m_virtualClasses;
    
    // Contains a list of all interfaces that are implemented by entities in
    // this project/pu.
    private Set<String> m_interfacesImplementedByEntities;
    
    /**
     * INTERNAL:
     * Create and return a new MetadataProject with puInfo as its PersistenceUnitInfo, 
     * session as its Session and weavingEnabled as its global dynamic weaving state.
     * @param puInfo - the PersistenceUnitInfo
     * @param session - the Session
     * @param weavingEnabled - flag for global dynamic weaving state
     */
    public MetadataProject(PersistenceUnitInfo puInfo, AbstractSession session, boolean weavingEnabled, boolean weaveEager) {
        m_isSharedCacheModeInitialized = false;
        
        m_persistenceUnitInfo = puInfo;
        m_session = session;
        m_logger = new MetadataLogger(session);
        m_weavingEnabled = weavingEnabled;
        m_weaveEager = weaveEager;
        
        // Using linked collections since their ordering needs to be preserved.
        m_entityMappings = new LinkedHashMap<String, XMLEntityMappings>();
        m_defaultListeners = new LinkedHashSet<EntityListenerMetadata>();

        m_queries = new HashMap<String, NamedQueryMetadata>();
        m_sqlResultSetMappings = new HashMap<String, SQLResultSetMappingMetadata>();

        m_allAccessors = new HashMap<String, ClassAccessor>();
        m_entityAccessors = new HashMap<String, EntityAccessor>();
        m_embeddableAccessors = new HashMap<String, EmbeddableAccessor>();
        m_interfaceAccessors = new HashMap<String, InterfaceAccessor>();
        m_mappedSuperclasseAccessors = new HashMap<String, MappedSuperclassAccessor>();
        
        m_idClasses = new HashSet<String>();
        m_virtualClasses = new HashSet<ClassAccessor>();
        m_accessorsWithCustomizer = new HashSet<ClassAccessor>();
        m_owningRelationshipAccessors = new HashSet<RelationshipAccessor>();
        m_nonOwningRelationshipAccessors = new HashSet<RelationshipAccessor>();
        m_rootEmbeddableAccessors = new HashSet<EmbeddableAccessor>();
        m_embeddableMappingAccessors = new HashSet<MappingAccessor>();
        m_directCollectionAccessors = new HashSet<DirectCollectionAccessor>();

        m_generatedValues = new HashMap<MetadataClass, GeneratedValueMetadata>();
        m_tableGenerators = new HashMap<String, TableGeneratorMetadata>();
        m_sequenceGenerators = new HashMap<String, SequenceGeneratorMetadata>();
        
        m_converters = new HashMap<String, AbstractConverterMetadata>();
        m_partitioningPolicies = new HashMap<String, AbstractPartitioningMetadata>();
        m_accessorsWithDerivedId = new HashSet<ClassAccessor>();
        m_interfacesImplementedByEntities = new HashSet<String>();
        
        m_metamodelMappedSuperclasses = new HashMap<String, MappedSuperclassAccessor>();
    }
    
    /**
     * INTERNAL:
     * This method will add the descriptor to the actual EclipseLink project,
     * if it has not already been added. This method if called for entities
     * and embeddable classes (which are both weavable classes).
     */
    protected void addAccessor(ClassAccessor accessor) {
        MetadataDescriptor descriptor = accessor.getDescriptor();
        
        // Process the persistence unit meta data (if there is any).
        processPersistenceUnitMetadata(descriptor);

        // Process and set the parent class (if one is available).
        accessor.processParentClass();
        
        // Add the descriptor to the actual EclipseLink Project.
        m_session.getProject().addDescriptor(descriptor.getClassDescriptor());

        // Keep a map of all the accessors that have been added.
        m_allAccessors.put(accessor.getJavaClassName(), accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addAccessorWithCustomizer(ClassAccessor accessor) {
        m_accessorsWithCustomizer.add(accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addAccessorWithDerivedId(ClassAccessor accessor) {
        m_accessorsWithDerivedId.add(accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addAlias(String alias, MetadataDescriptor descriptor) {
        ClassDescriptor existingDescriptor = m_session.getProject().getDescriptorForAlias(alias);
        
        if (existingDescriptor == null) {
            descriptor.setAlias(alias);
            m_session.getProject().addAlias(alias, descriptor.getClassDescriptor());
        } else {
            throw ValidationException.nonUniqueEntityName(existingDescriptor.getJavaClassName(), descriptor.getJavaClassName(), alias);
        }
    }
    
    /**
     * INTERNAL:
     * Add a abstract converter metadata to the project. The actual processing 
     * isn't done until an accessor referencing the converter is processed. 
     */
    public void addConverter(AbstractConverterMetadata converter) {
        // Check for another converter with the same name.
        if (converter.shouldOverride(m_converters.get(converter.getName()))) {
            m_converters.put(converter.getName(), converter);
        }
    }
    
    /**
     * Add the partitioning policy by name.
     */
    public void addPartitioningPolicy(AbstractPartitioningMetadata policy) {
        // Check for another converter with the same name.
        if (policy.shouldOverride(m_partitioningPolicies.get(policy.getName()))) {
            m_partitioningPolicies.put(policy.getName(), policy);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addDefaultListener(EntityListenerMetadata defaultListener) {
        m_defaultListeners.add(defaultListener);
    }

    /**
     * INTERNAL:
     * Store basic collection accessors for later processing and quick look up.
     */
    public void addDirectCollectionAccessor(MappingAccessor accessor) {
        m_directCollectionAccessors.add((DirectCollectionAccessor) accessor);
    }
    
    /**
     * INTERNAL:
     * Add an embeddable accessor to this project. Assumes the embeddable
     * needs to be added. That is, does not check if it already exists and
     * cause a merge. The caller is responsible for that.
     */
    public void addEmbeddableAccessor(EmbeddableAccessor accessor) {
        // Add accessor will apply persistence unit defaults.
        addAccessor(accessor);
        accessor.getDescriptor().setIsEmbeddable();
        m_embeddableAccessors.put(accessor.getJavaClassName(), accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addEmbeddableMappingAccessor(MappingAccessor accessor) {
        m_embeddableMappingAccessors.add(accessor);
    }
    
    /**
     * INTERNAL:
     * Add an entity accessor to this project. Assumes the entity needs to be 
     * added. That is, does not check if it already exists and cause a merge. 
     * The caller is responsible for that.
     */
    public void addEntityAccessor(EntityAccessor accessor) {
        // Add accessor will apply persistence unit defaults.
        addAccessor(accessor);
        // Grab the implemented interfaces (used when defaulting v1-1 mappings)
        m_interfacesImplementedByEntities.addAll(accessor.getJavaClass().getInterfaces());
        m_entityAccessors.put(accessor.getJavaClassName(), accessor);       
    }
    
    /**
     * INTERNAL:
     * The avoid processing the same mapping file twice (e.g. user may 
     * explicitly specify the orm.xml file) we store the list of entity 
     * mappings in a map keyed on their URL.
     */
    public void addEntityMappings(XMLEntityMappings entityMappings) {
        // Add the new entity mappings file to the list.
        m_entityMappings.put(entityMappings.getMappingFileOrURL(), entityMappings);
    }
    
    /**
     * INTERNAL:
     */
    public void addGeneratedValue(GeneratedValueMetadata generatedvalue, MetadataClass entityClass) {
        m_generatedValues.put(entityClass, generatedvalue);
    }
    
    /**
     * INTERNAL:
     * Add EmbeddedId and IdClass ids to the project
     */
    public void addIdClass(String idClassName) {
        m_idClasses.add(idClassName);
    }
    
    /**
     * INTERNAL:
     * Add a InterfaceAccessor to this project.
     */
    public void addInterfaceAccessor(InterfaceAccessor accessor) {
        m_interfaceAccessors.put(accessor.getJavaClassName(), accessor);
        
        // Add it directly and avoid the persistence unit defaults and stuff for now.
        m_session.getProject().addDescriptor(accessor.getDescriptor().getClassDescriptor());
    }
    
    /**
     * INTERNAL:
     * Add a mapped superclass accessor to this project. Assumes the mapped
     * superclass needs to be added. That is, does not check if it already 
     * exists and cause a merge. The caller is responsible for that. At runtime,
     * this map will contain mapped superclasses from XML only. The canonical
     * model processor will populate all mapped superclasses in this map.
     */
    public void addMappedSuperclass(MappedSuperclassAccessor mappedSuperclass) {
        // Process and set the parent class (if one is available).
        mappedSuperclass.processParentClass();
        
        m_mappedSuperclasseAccessors.put(mappedSuperclass.getJavaClassName(), mappedSuperclass);
    }

    /**
     * INTERNAL:
     * The metamodel API requires that descriptors exist for mappedSuperclasses 
     * in order to obtain their mappings.<p>
     * In order to accomplish this, this method that is called from EntityAccessor 
     * will ensure that the descriptors on all mappedSuperclass accessors 
     * are setup so that they can be specially processed later in 
     * MetadataProject.processStage2() - where the m_mappedSuperclassAccessors 
     * Map is required.
     * <p>
     * We do not use the non-persisting MAPPED_SUPERCLASS_RESERVED_PK_NAME PK field.
     * Normally when the MappedSuperclass is part of an inheritance hierarchy of the form MS->MS->E,
     * where there is an PK Id on the root Entity E, we need to add the 
     * MAPPED_SUPERCLASS_RESERVED_PK_NAME PK field solely for metadata processing to complete.
     * Why? because even though we treat MappedSuperclass objects as a RelationalDescriptor - we only persist
     * RelationalDescriptor objects that relate to concrete Entities.
     * <p>
     *  This method is referenced by EntityAccessor.addPotentialMappedSuperclass()
     *  during an initial predeploy() and later during a deploy()
     *  </p>
     * @param accessor - The mappedSuperclass accessor for the field on the mappedSuperclass<p>
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */    
    public void addMetamodelMappedSuperclass(MappedSuperclassAccessor accessor, MetadataDescriptor childDescriptor) {
        // Check for an existing entry before proceeding. Metamodel mapped 
        // superclasses need only (and should only) be added once. This code 
        // will be called from every entity that inherits from it. There is no
        // need to check for className == null here as the mapped superclass
        // accessor is always created with a class.
        if (! m_metamodelMappedSuperclasses.containsKey(accessor.getJavaClassName())) {
            MetadataDescriptor metadataDescriptor = accessor.getDescriptor();
                       
            // Set a child entity descriptor on the mapped superclass descriptor.
            // This descriptor (and its mapping accessors) will help to resolve
            // any generic mapping accessors from the mapped superclass.
            metadataDescriptor.setMetamodelMappedSuperclassChildDescriptor(childDescriptor);
            
            // Note: set the back pointer from the MetadataDescriptor back to its' accessor manually before we add accessors
            metadataDescriptor.setClassAccessor(accessor);
            
            // Make sure you apply the persistence unit metadata and defaults.
            processPersistenceUnitMetadata(metadataDescriptor);
            
            // After the pu metadata and defaults have been applied, it is safe to process the access type.
            accessor.processAccessType();
            
            // Set the referenceClass for Id mappings
            // Generics Handler: Check if the referenceType is not set for Collection accessors            
            accessor.addAccessors();
            
            // Add the accessor to our custom Map keyed on className for separate processing in stage2
            m_metamodelMappedSuperclasses.put(accessor.getJavaClassName(), accessor);
            
            // Fake out a database table and primary key for MappedSuperclasses
            // We require string names for table processing that does not actually goto the database.
            // There will be no conflict with customer values
            // The descriptor is assumed never to be null
            metadataDescriptor.setPrimaryTable(new DatabaseTable(MetadataConstants.MAPPED_SUPERCLASS_RESERVED_TABLE_NAME));
            
            /*
             * We need to add a PK field to the temporary mappedsuperclass table above - in order to continue processing.
             * Note: we add this field only if no IdClass or EmbeddedId attributes are set on or above the MappedSuperclass.
             * Both the table name and PK name are not used to actual database writes.
             * Check accessor collection on the metadataDescriptor (note: getIdAttributeName() and getIdAttributeNames() are not populated yet - so are unavailable
             * 300051: The check for at least one IdAccessor or an EmbeddedIdAccessor requires that the map and field respectively
             * are set previously in MetadataDescriptor.addAccessor().
             * The checks below will also avoid a performance hit on searching the accessor map directly on the descriptor. 
             */
            if (!metadataDescriptor.hasIdAccessor() && !metadataDescriptor.hasEmbeddedId()) {
                metadataDescriptor.addPrimaryKeyField(new DatabaseField(MetadataConstants.MAPPED_SUPERCLASS_RESERVED_PK_NAME));
            }
            
            /*
             * We store our descriptor on the core project for later retrieval by MetamodelImpl.
             * Why not on MetadataProject? because the Metadata processing is transient. 
             * We could set the javaClass on the descriptor for the current classLoader
             * but we do not need it until metamodel processing time avoiding a _persistence_new call.
             * See MetamodelImpl.initialize()
             */
            m_session.getProject().addMappedSuperclass(accessor.getJavaClassName(), metadataDescriptor.getClassDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Add a query to the project overriding where necessary.
     */
    public void addQuery(NamedQueryMetadata query) {
        if (query.shouldOverride(m_queries.get(query.getName()))) {
            m_queries.put(query.getName(), query);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addRelationshipAccessor(RelationshipAccessor accessor) {
        if (accessor.hasMappedBy()) {
            m_nonOwningRelationshipAccessors.add(accessor);
        } else {
            m_owningRelationshipAccessors.add(accessor);
        }
    }
    
    /**
     * INTERNAL:
     * Add a root level embeddable accessor.
     */
    public void addRootEmbeddableAccessor(EmbeddableAccessor accessor) {
        m_rootEmbeddableAccessors.add(accessor);
    }
    
    /**
     * INTERNAL:
     * Add a sequence generator metadata to the project. The actual processing 
     * isn't done till processSequencing is called.
     */
    public void addSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator, String defaultCatalog, String defaultSchema) {
        String name = sequenceGenerator.getName();
        
        // Check if the sequence generator name uses a reserved name.
        if (name.equals(DEFAULT_TABLE_GENERATOR)) {
             throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_TABLE_GENERATOR, sequenceGenerator.getLocation());
        } else if (name.equals(DEFAULT_IDENTITY_GENERATOR)) {
            throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_IDENTITY_GENERATOR, sequenceGenerator.getLocation());
        }
        
        // Catalog could be "" or null, need to check for an XML default.
        sequenceGenerator.setCatalog(MetadataHelper.getName(sequenceGenerator.getCatalog(), defaultCatalog, sequenceGenerator.getCatalogContext(), m_logger, sequenceGenerator.getLocation()));
        // Schema could be "" or null, need to check for an XML default.
        sequenceGenerator.setSchema(MetadataHelper.getName(sequenceGenerator.getSchema(), defaultSchema, sequenceGenerator.getSchemaContext(), m_logger, sequenceGenerator.getLocation()));
        
        // Check if the name is used with a table generator.
        TableGeneratorMetadata tableGenerator = m_tableGenerators.get(name);
        if (tableGenerator != null) {
            if (sequenceGenerator.shouldOverride(tableGenerator)) {
                m_tableGenerators.remove(name);
            } else {
                throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(name, sequenceGenerator.getLocation(), tableGenerator.getLocation());
            }
        }
            
        for (TableGeneratorMetadata otherTableGenerator : m_tableGenerators.values()) {
            if ((tableGenerator != otherTableGenerator) && otherTableGenerator.getPkColumnValue().equals(sequenceGenerator.getSequenceName())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if (otherTableGenerator.getPkColumnValue().length() > 0) {
                    throw ValidationException.conflictingSequenceNameAndTablePkColumnValueSpecified(sequenceGenerator.getSequenceName(), sequenceGenerator.getLocation(), otherTableGenerator.getLocation());
                }
            }
        }

        // Add the sequence generator if there isn't an existing one or if
        // we should override an existing one.
        if (sequenceGenerator.shouldOverride(m_sequenceGenerators.get(name))) {
            m_sequenceGenerators.put(sequenceGenerator.getName(), sequenceGenerator);
        }
    }

    /**
     * INTERNAL:
     * Add an sql results set mapping to the project overriding where necessary.
     */
    public void addSQLResultSetMapping(SQLResultSetMappingMetadata sqlResultSetMapping) {
        if (sqlResultSetMapping.shouldOverride(m_sqlResultSetMappings.get(sqlResultSetMapping.getName()))) {
            m_sqlResultSetMappings.put(sqlResultSetMapping.getName(), sqlResultSetMapping);
        }
    }
    
    /**
     * INTERNAL:
     * Add a discovered metamodel class to the session.
     */
    public void addStaticMetamodelClass(MetadataAnnotation annotation, MetadataClass metamodelClass) {
        MetadataClass modelClass = metamodelClass.getMetadataClass((String) annotation.getAttributeString("value"));
        
        m_session.addStaticMetamodelClass(modelClass.getName(), metamodelClass.getName());
    }
    
    /**
     * INTERNAL:
     * Add a table generator metadata to the project. The actual processing 
     * isn't done till processSequencing is called.
     */     
    public void addTableGenerator(TableGeneratorMetadata tableGenerator, String defaultCatalog, String defaultSchema) {
        // Process the default values.
        processTable(tableGenerator, "", defaultCatalog, defaultSchema);
        
        String generatorName = tableGenerator.getGeneratorName();
        
        // Check if the table generator name uses a reserved name.
        if (generatorName.equals(DEFAULT_SEQUENCE_GENERATOR)) {
            throw ValidationException.tableGeneratorUsingAReservedName(DEFAULT_SEQUENCE_GENERATOR, tableGenerator.getLocation());
        } else if (generatorName.equals(DEFAULT_IDENTITY_GENERATOR)) {
            throw ValidationException.tableGeneratorUsingAReservedName(DEFAULT_IDENTITY_GENERATOR, tableGenerator.getLocation());
        }

        // Check if the generator name is used with a sequence generator.
        SequenceGeneratorMetadata otherSequenceGenerator = m_sequenceGenerators.get(generatorName);
        if (otherSequenceGenerator != null) {
            if (tableGenerator.shouldOverride(otherSequenceGenerator)) {
                m_sequenceGenerators.remove(generatorName);
            } else {
                throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(generatorName, otherSequenceGenerator.getLocation(), tableGenerator.getLocation());
            }
        }
            
        for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators.values()) {
            if ((otherSequenceGenerator != sequenceGenerator) && sequenceGenerator.getSequenceName().equals(tableGenerator.getPkColumnValue())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if (sequenceGenerator.getSequenceName().length() > 0) {
                    throw ValidationException.conflictingSequenceNameAndTablePkColumnValueSpecified(sequenceGenerator.getSequenceName(), sequenceGenerator.getLocation(), tableGenerator.getLocation());
                }
            }
        }
        
        // Add the table generator if there isn't an existing one or if we 
        // should override an existing one.
        if (tableGenerator.shouldOverride(m_tableGenerators.get(generatorName))) {
            m_tableGenerators.put(generatorName, tableGenerator);
        }
    }  
    
    /**
     * INTERNAL:
     * Add virtual class accessor to the project. A virtual class is one that
     * has VIRTUAL access and the class does not exist on the classpath. 
     */
    public void addVirtualClass(ClassAccessor accessor) {
        m_virtualClasses.add(accessor);
    }
    
    /**
     * INTERNAL:
     * Create the dynamic class using JPA metadata processed descriptors. Called 
     * at deploy time after all metadata processing has completed.
     */
    protected void createDynamicClass(MetadataDescriptor descriptor, Map<String, MetadataDescriptor> virtualEntities, DynamicClassLoader dcl) {
        // Build the virtual class only if we have not already done so.
        if (! virtualEntities.containsKey(descriptor.getJavaClassName())) {
            
            if (descriptor.isInheritanceSubclass()) {
                // Get the parent descriptor.
                MetadataDescriptor parentDescriptor = descriptor.getInheritanceParentDescriptor();
                
                // Recursively call up the parents.
                createDynamicClass(parentDescriptor, virtualEntities, dcl);
                
                // Create and set the virtual class using the parent class.
                descriptor.getClassDescriptor().setJavaClass(dcl.createDynamicClass(descriptor.getJavaClassName(), parentDescriptor.getClassDescriptor().getJavaClass()));
            } else {
                // Create and set the virtual class on the descriptor
                descriptor.getClassDescriptor().setJavaClass(dcl.createDynamicClass(descriptor.getJavaClassName()));
            }
            
            // Store the descriptor by java class name.
            virtualEntities.put(descriptor.getJavaClassName(), descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Create the dynamic class using JPA metadata processed descriptors. Called 
     * at deploy time after all metadata processing has completed.
     */
    public void createDynamicClasses(ClassLoader loader) {
        if (! m_virtualClasses.isEmpty()) {
            if (DynamicClassLoader.class.isAssignableFrom(loader.getClass())) {
                DynamicClassLoader dcl = (DynamicClassLoader) loader;
            
                // Create the dynamic classes.
                Map<String, MetadataDescriptor> dynamicClasses = new HashMap<String, MetadataDescriptor>();
                for (ClassAccessor accessor : m_virtualClasses) {
                    createDynamicClass(accessor.getDescriptor(), dynamicClasses, dcl);
                }
            
                // Create the dynamic types.
                Map<String, DynamicType> dynamicTypes = new HashMap<String, DynamicType>();
                for (MetadataDescriptor descriptor : dynamicClasses.values()) {
                    createDynamicType(descriptor, dynamicTypes, dcl);
                }
            } else {
                // If we have virtual classes that need creation and we do not
                // have a dynamic class loader throw an exception.
                throw ValidationException.invalidClassLoaderForDynamicPersistence();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Create the dynamic types using JPA metadata processed descriptors. Called 
     * at deploy time after all metadata processing has completed.
     */
    protected void createDynamicType(MetadataDescriptor descriptor, Map<String, DynamicType> dynamicTypes, DynamicClassLoader dcl) {
        // Build the dynamic class only if we have not already done so.
        if (! dynamicTypes.containsKey(descriptor.getJavaClassName())) {
            JPADynamicTypeBuilder typeBuilder = null;
            
            if (descriptor.isInheritanceSubclass()) {
                // Get the parent descriptor
                MetadataDescriptor parentDescriptor = descriptor.getInheritanceParentDescriptor();
                
                // Recursively call up the parents.
                createDynamicType(parentDescriptor, dynamicTypes, dcl);
                
                // Create the dynamic type using the parent type.
                typeBuilder = new JPADynamicTypeBuilder(dcl, descriptor.getClassDescriptor(), dynamicTypes.get(parentDescriptor.getJavaClassName()));
            } else {
                // Create the dynamic type
                typeBuilder = new JPADynamicTypeBuilder(dcl, descriptor.getClassDescriptor(), null);
            }
            
            // Store the type builder by java class name.
            dynamicTypes.put(descriptor.getJavaClassName(), typeBuilder.getType());
        }
    }
    
    /**
     * INTERNAL:
     * Return true if an exclude-default-mappings setting have been set for this 
     * persistence unit.
     */
    public boolean excludeDefaultMappings() {
        if (m_persistenceUnitMetadata != null) {
            return m_persistenceUnitMetadata.excludeDefaultMappings();
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return the accessor for the given class. Could be an entity or an
     * embeddable. Note: It may return null.
     */
    public ClassAccessor getAccessor(String className) {
        return m_allAccessors.get(className);
    }
    
    /**
     * INTERNAL:
     */
    public Set<ClassAccessor> getAccessorsWithCustomizer() {
        return m_accessorsWithCustomizer;
    }
    
    /**
     * INTERNAL:
     */
    public Collection<ClassAccessor> getAllAccessors() {
        return m_allAccessors.values();
    }
    
    /**
     * INTERNAL:
     */
    public AbstractConverterMetadata getConverter(String name) {
        return m_converters.get(name);
    }

    /**
     * Return the named partitioning policy.
     */
    public AbstractPartitioningMetadata getPartitioningPolicy(String name) {
        return m_partitioningPolicies.get(name);
    }
    
    /**
     * INTERNAL:
     */
    public Set<EntityListenerMetadata> getDefaultListeners() {
        return m_defaultListeners;
    }
    
    /**
     * INTERNAL:
     * This method will attempt to look up the embeddable accessor for the
     * reference class provided. If no accessor is found, null is returned.
     */
    public EmbeddableAccessor getEmbeddableAccessor(MetadataClass cls) {
        return getEmbeddableAccessor(cls, false);
    }
    
    /**
     * INTERNAL:
     * This method will attempt to look up the embeddable accessor for the
     * reference class provided. If no accessor is found, null is returned.
     */
    public EmbeddableAccessor getEmbeddableAccessor(MetadataClass cls, boolean checkIsIdClass) {
        EmbeddableAccessor accessor = m_embeddableAccessors.get(cls.getName());

        if (accessor == null) {
            // Before we return null we must make a couple final checks:
            // 
            // 1 - Check for an Embeddable annotation on the class itself. At 
            // this point we know the class was not tagged as an embeddable in 
            // a mapping file and was not included in the list of classes for 
            // this persistence unit. Its inclusion therefore in the persistence 
            // unit is through the use of an Embedded annotation or an embedded 
            // element within a known entity. 
            // 2 - If checkIsIdClass is true, JPA 2.0 introduced support for
            // 
            // derived id's where a parent entity's id class may be used within 
            // a dependants embedded id class. We will treat the id class as
            // and embeddable accessor at this point.
            //
            // Callers to this method will have to handle the null case if they
            // so desire.
            if (cls.isAnnotationPresent(Embeddable.class) || (checkIsIdClass && isIdClass(cls))) {
                accessor = new EmbeddableAccessor(cls.getAnnotation(Embeddable.class), cls, this);
                addEmbeddableAccessor(accessor);
            }
       } 
        
       return accessor;
    }
    
    /**
     * INTERNAL:
     * Return the embeddable accessor with the given classname.
     */
    public EmbeddableAccessor getEmbeddableAccessor(String className) {
        return m_embeddableAccessors.get(className);
    }
    
    /**
     * INTERNAL:
     * Return the embeddable accessor with the given classname.
     */
    public Collection<EmbeddableAccessor> getEmbeddableAccessors() {
        return m_embeddableAccessors.values();
    }
    
    /**
     * INTERNAL:
     * Return the entity accessor for the given class name.
     */
    public EntityAccessor getEntityAccessor(MetadataClass cls) {
        return getEntityAccessor(cls.getName());
    }
    
    /**
     * INTERNAL:
     * Return the entity accessor for the given class name.
     */
    public EntityAccessor getEntityAccessor(String className) {
        return m_entityAccessors.get(className);
    }
    
    /**
     * INTERNAL:
     */
    public Collection<EntityAccessor> getEntityAccessors() {
        return m_entityAccessors.values();
    }
    
    /**
     * INTERNAL:
     */
    public Collection<XMLEntityMappings> getEntityMappings() {
        return m_entityMappings.values();
    }
    
    /**
     * INTERNAL:
     * Return the entity accessor for the given class.
     */
    public InterfaceAccessor getInterfaceAccessor(String className) {
        return m_interfaceAccessors.get(className);
    }
    
    /** 
     * INTERNAL:
     * Return the logger used by the processor.
     */
    public MetadataLogger getLogger() {
        return m_logger;
    }
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor getMappedSuperclassAccessor(MetadataClass cls) {
        return getMappedSuperclassAccessor(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor getMappedSuperclassAccessor(String className) {
        return m_mappedSuperclasseAccessors.get(className);
    }
    
    /**
     * INTERNAL:
     */
    public Collection<MappedSuperclassAccessor> getMappedSuperclasses() {
        return m_mappedSuperclasseAccessors.values();
    }
    
    /**
     * INTERNAL:
     * Returns the collection of metamodel MappedSuperclassAccessors. This
     * collection is NOT and should NOT be used for any deployment descriptor 
     * metadata processing. It is used solely with the metamodel.
     * @see getMappedSuperclass(MetadataClass)
     * @see getMappedSuperclass(String)
     * @see getMappedSuperclasses()
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */
    public Collection<MappedSuperclassAccessor> getMetamodelMappedSuperclasses() {
        return m_metamodelMappedSuperclasses.values();
    }
    
    /**
     * INTERNAL:
     * Return the persistence unit default catalog.
     */
    protected String getPersistenceUnitDefaultCatalog() {
        if (m_persistenceUnitMetadata != null) {
            return m_persistenceUnitMetadata.getCatalog();
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the persistence unit default schema.
     */
    protected String getPersistenceUnitDefaultSchema() {
        if (m_persistenceUnitMetadata != null) {
            return m_persistenceUnitMetadata.getSchema();
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     */
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return m_persistenceUnitInfo;
    }
    
    /** 
     * INTERNAL:
     */
    public XMLPersistenceUnitMetadata getPersistenceUnitMetadata() {
        return m_persistenceUnitMetadata;
    }
    
    /**
     * INTERNAL:
     * Return the core API Project associated with this MetadataProject.
     * @return
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     */
    public Project getProject() {
        return m_session.getProject();
    }
    
    /**
     * INTERNAL:
     * Add a root level embeddable accessor. Nested embeddables will be
     * pre-processed from their roots down.
     * @see processStage1()
     */
    public Set<EmbeddableAccessor> getRootEmbeddableAccessors() {
        return m_rootEmbeddableAccessors;
    }
    
    /**
     * INTERNAL:
     */
    public AbstractSession getSession() {
        return m_session;
    }

    /**
     * INTERNAL:
     * This method will return the name of the SharedCacheMode if specified in 
     * the persistence.xml file. Note, this is a JPA 2.0 feature, therefore, 
     * this method needs to catch any exception as a result of trying to access 
     * this information from a JPA 1.0 container.   
     */
    protected String getSharedCacheModeName() {
        if (! m_isSharedCacheModeInitialized) {
            try {
                Method method = null;
            
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    method = (Method) AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(PersistenceUnitInfo.class, "getSharedCacheMode", null));
                    m_sharedCacheMode = (SharedCacheMode) AccessController.doPrivileged(new PrivilegedMethodInvoker(method, m_persistenceUnitInfo));
                } else {
                    method = PrivilegedAccessHelper.getDeclaredMethod(PersistenceUnitInfo.class, "getSharedCacheMode", null);
                    m_sharedCacheMode = (SharedCacheMode) PrivilegedAccessHelper.invokeMethod(method, m_persistenceUnitInfo, null);
                }
            } catch (Throwable exception) {
                // Swallow any exceptions, shared cache mode will be null.
                m_sharedCacheMode = null;
            }
            
            // Set the shared cache mode as initialized to avoid the reflective
            // calls over and over again.
            m_isSharedCacheModeInitialized = true;
        }
        
        return (m_sharedCacheMode == null) ? null : m_sharedCacheMode.name();
    }

    /**
     * INTERNAL:
     * Used to uppercase default and user defined column field names
     */
    public boolean getShouldForceFieldNamesToUpperCase(){
        return m_forceFieldNamesToUpperCase;
    }

    /**
     * INTERNAL:
     */
    public List<StructConverterMetadata> getStructConverters(){
        List<StructConverterMetadata> structConverters = new ArrayList<StructConverterMetadata>();
        
        for (AbstractConverterMetadata converter : m_converters.values()) {
            if (converter.isStructConverter()) {
                structConverters.add((StructConverterMetadata) converter);
            }
        }
        
        return structConverters;
    }
    
    /**
     * INTERNAL:
     * Returns all those classes in this project that are available for 
     * weaving. This list currently includes entity and embeddables classes.
     */
    public Collection<String> getWeavableClassNames() {
        return Collections.unmodifiableCollection(m_allAccessors.keySet());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasConverter(String name) {
        return m_converters.containsKey(name);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEmbeddable(MetadataClass cls) {
        return hasEmbeddable(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEmbeddable(String className) {
        return m_embeddableAccessors.containsKey(className);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEntity(MetadataClass cls) {
        return hasEntity(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEntity(String className) {
        return m_entityAccessors.containsKey(className);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEntityThatImplementsInterface(String interfaceName) {
       return m_interfacesImplementedByEntities.contains(interfaceName);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasInterface(MetadataClass cls) {
        return m_interfaceAccessors.containsKey(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappedSuperclass(MetadataClass cls) {
        return hasMappedSuperclass(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappedSuperclass(String className) {
        return m_mappedSuperclasseAccessors.containsKey(className);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasSharedCacheMode() {
        return getSharedCacheModeName() != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isIdClass(MetadataClass idClass) {
        return m_idClasses.contains(idClass.getName());
    }

    /**
     * INTERNAL:
     * Return true if the caching has been specified as ALL in the
     * persistence.xml.
     */
    public boolean isSharedCacheModeAll() {
        return hasSharedCacheMode() && getSharedCacheModeName().equals(SharedCacheMode.ALL.name());
    }
    
    /**
     * INTERNAL:
     * Return true if the caching has been specified as DISABLE_SELECTIVE in the
     * persistence.xml. DISABLE_SELECTIVE is the default therefore this will 
     * also return true if no caching setting was set.
     */
    public boolean isSharedCacheModeDisableSelective() {
        return (! hasSharedCacheMode()) || getSharedCacheModeName().equals(SharedCacheMode.DISABLE_SELECTIVE.name());
    }
    
    /**
     * INTERNAL:
     * Return true if the caching has been specified as ENABLE_SELECTIVE in the
     * persistence.xml. 
     */
    public boolean isSharedCacheModeEnableSelective() {
        return hasSharedCacheMode() && getSharedCacheModeName().equals(SharedCacheMode.ENABLE_SELECTIVE.name());
    }
    
    /**
     * INTERNAL:
     * Return true if the caching has been specified as NONE in the 
     * persistence.xml.  
     */
    public boolean isSharedCacheModeNone() {
        return hasSharedCacheMode() && getSharedCacheModeName().equals(SharedCacheMode.NONE.name());
    }
    
    /**
     * INTERNAL:
     * Return true if the caching has been specified as UNSPECIFIED in the 
     * persistence.xml.  
     */
    public boolean isSharedCacheModeUnspecified() {
        return hasSharedCacheMode() && getSharedCacheModeName().equals(SharedCacheMode.UNSPECIFIED.name());
    }
    
    /**
     * INTERNAL:
     * This flag represents dynamic weaving state for 1-1, many-1, fetch groups 
     * and change tracking.
     */
    public boolean isWeavingEnabled() {
        return m_weavingEnabled;
    }  
    
    /**
     * INTERNAL:
     * Return true if an xml-mapping-metadata-complete setting has been set
     * for this persistence unit.
     */
    public boolean isXMLMappingMetadataComplete() {
        if (m_persistenceUnitMetadata != null) {
            return m_persistenceUnitMetadata.isXMLMappingMetadataComplete();
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Process the embeddable mapping accessors.
     */
    protected void processEmbeddableMappingAccessors() {
        for (MappingAccessor mappingAccessor : m_embeddableMappingAccessors) {
            if (! mappingAccessor.isProcessed()) {
                mappingAccessor.process();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process descriptors with IDs derived from relationships. This will also 
     * complete unfinished validation as well as secondary table processing
     * on entity accessors. This method will fast track some relationship
     * mappings which is ok since simple primary keys will already have been
     * discovered and processed whereas any derived id's and their fast tracking
     * to be processed will be handled now.
     */
    protected void processAccessorsWithDerivedIDs() {
        HashSet<ClassAccessor> processed = new HashSet();
        HashSet<ClassAccessor> processing = new HashSet();
        
        for (ClassAccessor classAccessor : m_accessorsWithDerivedId) {
            classAccessor.processDerivedId(processing, processed);
        }
    }
    
    /**
     * INTERNAL:
     * Process any BasicCollection annotation and/or BasicMap annotation that 
     * were found. They are not processed till after an id has been processed 
     * since they rely on one to map the collection table.
     */
    public void processDirectCollectionAccessors() {
        for (DirectCollectionAccessor accessor : m_directCollectionAccessors) {
            accessor.process();
        }
    }
    
    /** 
     * INTERNAL:
     * This method will iterate through all the entities in the PU and check
     * if we should add them to a variable one to one mapping that was either
     * defined (incompletely) or defaulted.
     */
    protected void processInterfaceAccessors() {
        for (EntityAccessor accessor : getEntityAccessors()) {
            for (String interfaceClass : accessor.getJavaClass().getInterfaces()) {
                if (m_interfaceAccessors.containsKey(interfaceClass)) {
                    m_interfaceAccessors.get(interfaceClass).addEntityAccessor(accessor);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the non-owning relationship accessors. All owning relationshuip
     * accessors should be processed. Some non-owning relationships may have
     * already been fast tracked to from an element collection containing
     * an embeddable (with a non-owning relationship).
     */
    protected void processNonOwningRelationshipAccessors() {
        for (RelationshipAccessor accessor : m_nonOwningRelationshipAccessors) {
            if (! accessor.isProcessed()) {
                accessor.process();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the owning relationship accessors. Some may have already been
     * processed through the processing of derived id's therefore don't process
     * them again.
     */
    protected void processOwningRelationshipAccessors() {
        for (RelationshipAccessor accessor : m_owningRelationshipAccessors) {
            if (! accessor.isProcessed()) {
                accessor.process();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process any and all persistence unit metadata and defaults to the given 
     * descriptor. This method for will called for every descriptor belonging
     * to this project/persistence unit.
     * 
     */
    protected void processPersistenceUnitMetadata(MetadataDescriptor descriptor) {
        // Set the persistence unit meta data (if there is any) on the descriptor.
        if (m_persistenceUnitMetadata != null) {
            // Persistence unit metadata level annotations are not defaults
            // and therefore should not be set on the descriptor.
            
            // Set the persistence unit defaults (if there are any) on the descriptor.
            XMLPersistenceUnitDefaults persistenceUnitDefaults = m_persistenceUnitMetadata.getPersistenceUnitDefaults();
            
            if (persistenceUnitDefaults != null) {
                descriptor.setDefaultAccess(persistenceUnitDefaults.getAccess());
                descriptor.setDefaultSchema(persistenceUnitDefaults.getSchema());
                descriptor.setDefaultCatalog(persistenceUnitDefaults.getCatalog());
                descriptor.setDefaultTenantDiscriminatorColumns(persistenceUnitDefaults.getTenantDiscriminatorColumns());
                descriptor.setIsCascadePersist(persistenceUnitDefaults.isCascadePersist());
                
                // Set any default access methods if specified.
                if (persistenceUnitDefaults.hasAccessMethods()) {
                    descriptor.setDefaultAccessMethods(persistenceUnitDefaults.getAccessMethods());
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the named native queries we found and add them to the given
     * session.
     */
    public void processQueries(ClassLoader loader) {
        // Step 1 - process the sql result set mappings first.
        for (SQLResultSetMappingMetadata sqlResultSetMapping : m_sqlResultSetMappings.values()) {
            sqlResultSetMapping.process(m_session, loader, this);
        }
        
        // Step 2 - process the named queries second, some may need to validate
        // a sql result set mapping specification.
        for (NamedQueryMetadata query : m_queries.values()) {
            query.process(m_session, loader, this);
        }
    }
    
    /**
     * INTERNAL:
     * Process the sequencing information. At this point, through validation, it 
     * is not possible to have:
     *   1 - a table generator with the generator name equal to 
     *       DEFAULT_SEQUENCE_GENERATOR or DEFAULT_IDENTITY_GENERATOR
     *   2 - a sequence generator with the name eqaul to DEFAULT_TABLE_GENERATOR 
     *       or DEFAULT_IDENTITY_GENERATOR
     *   3 - you can't have both a sequence generator and a table generator with 
     *       the same DEFAULT_AUTO_GENERATOR name.
     *       
     * @see addTableGenerator and addSequenceGenerator.
     */
    protected void processSequencingAccessors() {
        if (! m_generatedValues.isEmpty()) {
            // 1 - Build our map of sequences keyed on generator names.
            Hashtable<String, Sequence> sequences = new Hashtable<String, Sequence>();
            
            for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators.values()) {
                sequences.put(sequenceGenerator.getName(), sequenceGenerator.process(m_logger));
            }

            for (TableGeneratorMetadata tableGenerator : m_tableGenerators.values()) {
                sequences.put(tableGenerator.getGeneratorName(), tableGenerator.process(m_logger));
            }

            // 2 - Check if the user defined default generators, otherwise 
            // create them using the Table and Sequence generator metadata.
            if (! sequences.containsKey(DEFAULT_TABLE_GENERATOR)) {
                TableGeneratorMetadata tableGenerator = new TableGeneratorMetadata(DEFAULT_TABLE_GENERATOR);
                
                // This will go through the same table defaulting as all other
                // tables (table, secondary table, join table etc.) in the PU.
                // Here the default table name should come from the platform 
                // in case the current one is not legal for this platform (e.g.
                // SEQUENCE for Symfoware). We should always try to avoid making
                // metadata changes after processing and ensure we always 
                // process with the correct and necessary metadata.
                Sequence seq = m_session.getDatasourcePlatform().getDefaultSequence();
                String defaultTableGeneratorName = (seq instanceof TableSequence) ? ((TableSequence) seq).getTableName() : DEFAULT_TABLE_GENERATOR;
                // Process the default values.
                processTable(tableGenerator, defaultTableGeneratorName, getPersistenceUnitDefaultCatalog(), getPersistenceUnitDefaultSchema());
                
                sequences.put(DEFAULT_TABLE_GENERATOR, tableGenerator.process(m_logger));
            }
            
            if (! sequences.containsKey(DEFAULT_SEQUENCE_GENERATOR)) {
                sequences.put(DEFAULT_SEQUENCE_GENERATOR, new SequenceGeneratorMetadata(DEFAULT_SEQUENCE_GENERATOR, getPersistenceUnitDefaultCatalog(), getPersistenceUnitDefaultSchema()).process(m_logger));
            }
            
            if (! sequences.containsKey(DEFAULT_IDENTITY_GENERATOR)) {
                sequences.put(DEFAULT_IDENTITY_GENERATOR, new SequenceGeneratorMetadata(DEFAULT_IDENTITY_GENERATOR, 1, getPersistenceUnitDefaultCatalog(), getPersistenceUnitDefaultSchema(), true).process(m_logger));
            }

            // Use a temporary sequence generator to build a qualifier to set on 
            // the default generator. Don't use this generator as the default 
            // auto generator though.
            SequenceGeneratorMetadata tempGenerator = new SequenceGeneratorMetadata(DEFAULT_AUTO_GENERATOR, getPersistenceUnitDefaultCatalog(), getPersistenceUnitDefaultSchema());
            DatasourceLogin login = m_session.getProject().getLogin();
            login.getDefaultSequence().setQualifier(tempGenerator.processQualifier());
                
            // 3 - Loop through generated values and set sequences for each. 
            for (MetadataClass entityClass : m_generatedValues.keySet()) {
                // Skip setting sequences if our accessor is null, must be a mapped superclass
                ClassAccessor accessor = m_allAccessors.get(entityClass.getName());
                if (accessor != null) {
                    m_generatedValues.get(entityClass).process(accessor.getDescriptor(), sequences, login);
                }
            }
        }
    }
    
    /**
     * Process the partitioning metedata and add the PartitioningPolicys to the project.
     */
    protected void processPartitioning() {
        for (AbstractPartitioningMetadata metadata : m_partitioningPolicies.values()) {
            m_session.getProject().addPartitioningPolicy(metadata.buildPolicy());
        }
    }
    
    /**
     * INTERNAL:
     * Stage 1 processing is a pre-processing stage that will perform the 
     * following tasks:
     *  - gather a list of mapping accessors for all entities and embeddables.
     *  - discover all global converter specifications.
     *  - discover mapped superclasses and inheritance parents.
     * 
     * NOTE: This method should only perform any preparatory work like, class
     * discovery, flag settings etc. Hard processing will begin in stage 2.
     * 
     * @see processStage2
     */
    public void processStage1() {
        // 1 - Pre-process the entities first. This will also pre-process
        // the mapped superclasses and build/add/complete our list of 
        // embeddables that will be pre-processed in step 2 below. This is 
        // necessary so that we may gather our list of id classes which may be 
        // referenced in embeddable classes as part of a mapped by id accessor. 
        // This will avoid more complicated processing and ease in building the 
        // correct accessor at buildAccessor time.
        for (EntityAccessor entity : getEntityAccessors()) {
            if (! entity.isPreProcessed()) {
                entity.preProcess();
            }
        }
        
        // 2 - Pre-process the embeddables. This will also pre-process any and
        // all nested embeddables as well. Embeddables must be processed from
        // the root down.
        for (EmbeddableAccessor embeddable : getRootEmbeddableAccessors()) {
            if (! embeddable.isPreProcessed()) {
                embeddable.preProcess();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Stage 2 processing will perform the following tasks:
     * - process all direct mapping accessors from entities, embeddables and
     *   mapped superclasses.
     * - gather a list of relationship accessors and any other special interest
     *   accessors to be processed in stage 3.  
     * 
     * @see processStage3
     */
    public void processStage2() {
        // 266912: process mappedSuperclasses separately from entity descriptors
        for (MappedSuperclassAccessor msAccessor : m_metamodelMappedSuperclasses.values()) {
            if (! msAccessor.isProcessed()) {               
                msAccessor.processMetamodelDescriptor();    
            }
        }
        
        for (EntityAccessor entity : getEntityAccessors()) {
            // If the accessor hasn't been processed yet, then process it. An
            // EntityAccessor may get fast tracked if it is an inheritance
            // parent.
            if (! entity.isProcessed()) {
                entity.process();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Stage 3 processing does all the extra processing that couldn't be 
     * completed in the first two stages of processing. The biggest thing 
     * being that all entities will have processed an id by now and we can 
     * process those accessors that rely on them. NOTE: The order of invocation 
     * here is very important here, see the comments.
     */
    public void processStage3() {
        // 1 - Process accessors with IDs derived from relationships. This will 
        // finish up any stage2 processing that relied on the PK processing 
        // being complete as well. Note: some relationships mappings may be 
        // processed in this stage. This is ok since it is to determine and
        // validate the primary key.
        processAccessorsWithDerivedIDs();

        // 2 - Process all the direct collection accessors we found. This list
        // does not include direct collections to an embeddable class.
        processDirectCollectionAccessors();
        
        // 3 - Process the sequencing metadata now that every entity has a 
        // validated primary key.
        processSequencingAccessors();
        
        // 4 - Process the owning relationship accessors now that every entity 
        // has a validated primary key and we can process join columns.
        processOwningRelationshipAccessors();
        
        // 5 - Process the embeddable mapping accessors. These are the
        // embedded, embedded id and element collection accessors that map
        // to an embeddable class. We must hold off on their processing till
        // now to ensure their owning relationship accessors have been processed 
        // and we can therefore process any association overrides correctly.
        processEmbeddableMappingAccessors();
        
        // 6 - Process the non owning relationship accessors now that every 
        // owning relationship should be fully processed.
        processNonOwningRelationshipAccessors(); 
        
        // 7 - Process the interface accessors which will iterate through all 
        // the entities in the PU and check if we should add them to a variable 
        // one to one mapping that was either defined (incompletely) or 
        // defaulted.
        processInterfaceAccessors();
        
        processPartitioning();
    }
    
    /**
     * INTERNAL:
     * Common table processing for table, secondary table, join table, 
     * collection table and table generators
     */
    public void processTable(TableMetadata table, String defaultName, String defaultCatalog, String defaultSchema) {
        // Name could be "" or null, need to check against the default name.
        String name = MetadataHelper.getName(table.getName(), defaultName, table.getNameContext(), m_logger, table.getLocation());
        
        // Catalog could be "" or null, need to check for an XML default.
        String catalog = MetadataHelper.getName(table.getCatalog(), defaultCatalog, table.getCatalogContext(), m_logger, table.getLocation());
        
        // Schema could be "" or null, need to check for an XML default.
        String schema = MetadataHelper.getName(table.getSchema(), defaultSchema, table.getSchemaContext(), m_logger, table.getLocation());
        
        // Build a fully qualified name and set it on the table.
        // schema, attach it if specified
        String tableName = new String(name);
        if (! schema.equals("")) {
            tableName = schema + "." + tableName;
        }
    
        // catalog, attach it if specified
        if (! catalog.equals("")) {
            tableName = catalog + "." + tableName;
        }
        
        table.setFullyQualifiedTableName(tableName);

        if (useDelimitedIdentifier()){
            table.setUseDelimiters(useDelimitedIdentifier());
        }

        table.getDatabaseTable().setCreationSuffix(table.getCreationSuffix());

        // Process the unique constraints.
        table.processUniqueConstraints();
    }
    
    /**
     * INTERNAL:
     * Used from the canonical model generator. Specifically when the user
     * removes the embeddable designation or changes the embeddable to either 
     * a mapped superclass or entity. 
     */
    public void removeEmbeddableAccessor(MetadataClass metadataClass) {
        m_allAccessors.remove(metadataClass.getName());
        m_embeddableAccessors.remove(metadataClass.getName());
    }
    
    /**
     * INTERNAL:
     * Used from the canonical model generator. Specifically when the user
     * removes the entity designation or changes the entity to either 
     * a mapped superclass or embeddable. 
     */
    public void removeEntityAccessor(MetadataClass metadataClass) {
        m_allAccessors.remove(metadataClass.getName());
        m_entityAccessors.remove(metadataClass.getName());
    }
    
    /**
     * INTERNAL:
     * Used from the canonical model generator. Specifically when the user
     * removes the mapped superclass designation or changes the mapped 
     * superclass to either an entity or embeddable. 
     */
    public void removeMappedSuperclassAccessor(MetadataClass metadataClass) {
        m_mappedSuperclasseAccessors.remove(metadataClass.getName());
    }
    
    /** 
     * INTERNAL:
     */
    public void setPersistenceUnitMetadata(XMLPersistenceUnitMetadata persistenceUnitMetadata) {
        // Set the persistence unit metadata if null otherwise try to merge.
        if (m_persistenceUnitMetadata == null) {
            m_persistenceUnitMetadata = persistenceUnitMetadata;
        } else {
            m_persistenceUnitMetadata.merge(persistenceUnitMetadata);
        }
    }

    /**
     * INTERNAL:
     * Used to uppercase default and user defined column field names
     */
    public void setShouldForceFieldNamesToUpperCase(boolean shouldForceFieldNamesToUpperCase){
        m_forceFieldNamesToUpperCase = shouldForceFieldNamesToUpperCase;
    }

    /**
     * INTERNAL:
     * This flag represents dynamic weaving state for 1-1, many-1, fetch groups 
     * and change tracking.  
     * @param weavingEnabled (false = weaving disabled)
     */
    public void setWeavingEnabled(boolean weavingEnabled) {
        m_weavingEnabled = weavingEnabled;
    }
  
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return "Project[" + getPersistenceUnitInfo().getPersistenceUnitName() + "]";
    }
    
    /**
     * INTERNAL:
     */
    public boolean useDelimitedIdentifier() {
        return m_persistenceUnitMetadata != null && m_persistenceUnitMetadata.isDelimitedIdentifiers();
    }
    
    /**
     * Return if the project should use indirection for eager relationships.
     */
    public boolean weaveEager() {
        return m_weaveEager;
    }
 }


