/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.GenerationType;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.InterfaceAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DirectCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;

import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.NativeSequence;

import org.eclipse.persistence.sessions.DatasourceLogin;

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
    private static final String DEFAULT_AUTO_GENERATOR = "SEQ_GEN";
    private static final String DEFAULT_TABLE_GENERATOR = "SEQ_GEN_TABLE";
    private static final String DEFAULT_SEQUENCE_GENERATOR = "SEQ_GEN_SEQUENCE";
    private static final String DEFAULT_IDENTITY_GENERATOR = "SEQ_GEN_IDENTITY";
    
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
    private HashMap<URL, XMLEntityMappings> m_entityMappings;
    
    // List of mapped-superclasses found in XML for this project/persistence unit.
    private HashMap<String, MappedSuperclassAccessor> m_mappedSuperclasses;

    // All the class accessors for this project (Entities and Embeddables).
    private HashMap<String, ClassAccessor> m_allAccessors;
    
    // The entity accessors for this project
    private HashMap<String, EntityAccessor> m_entityAccessors;
    
    // The embeddable accessors for this project
    private HashMap<String, EmbeddableAccessor> m_embeddableAccessors;
    
    // The interface accessors for this project
    private HashMap<String, InterfaceAccessor> m_interfaceAccessors;
    
    // Query metadata.
    private HashMap<String, NamedQueryMetadata> m_queries;
    
    // SQL result set mapping
    private HashMap<String, SQLResultSetMappingMetadata> m_sqlResultSetMappings;
    
    // Sequencing metadata.
    private HashMap<Class, GeneratedValueMetadata> m_generatedValues;
    private HashMap<String, TableGeneratorMetadata> m_tableGenerators;
    private HashMap<String, SequenceGeneratorMetadata> m_sequenceGenerators;
    
    // Metadata converters, that is, EclipseLink converters.
    private HashMap<String, AbstractConverterMetadata> m_converters;
    
    // Default listeners that need to be applied to each entity in the
    // persistence unit (unless they exclude them).
    private HashSet< EntityListenerMetadata> m_defaultListeners;
    
    // Class accessors that have a customizer.
    private HashSet<ClassAccessor> m_accessorsWithCustomizer;
    
    // Class accessors that have Ids derived from relationships.
    private HashSet<ClassAccessor> m_accessorsWithDerivedIDs;
    
    // All direct collection accessors.
    private HashSet<DirectCollectionAccessor> m_directCollectionAccessors;
    
    // Accessors that map to an Embeddable class
    private HashSet<MappingAccessor> m_embeddableMappingAccessors;
    
    // All relationship accessors.
    private HashSet<RelationshipAccessor> m_relationshipAccessors;
    
    /**
     * INTERNAL:
     * Create and return a new MetadataProject with puInfo as its PersistenceUnitInfo, 
     * session as its Session and weavingEnabled as its global dynamic weaving state.
     * @param puInfo - the PersistenceUnitInfo
     * @param session - the Session
     * @param weavingEnabled - flag for global dynamic weaving state
     */
    public MetadataProject(PersistenceUnitInfo puInfo, AbstractSession session, boolean weavingEnabled, boolean weaveEager) {
        m_persistenceUnitInfo = puInfo;
        m_session = session;
        m_logger = new MetadataLogger(session);
        m_weavingEnabled = weavingEnabled;
        m_weaveEager = weaveEager;
        
        // Using linked collections since their ordering needs to be preserved.
        m_entityMappings = new LinkedHashMap<URL, XMLEntityMappings>();
        m_defaultListeners = new LinkedHashSet<EntityListenerMetadata>();

        m_queries = new HashMap<String, NamedQueryMetadata>();
        m_sqlResultSetMappings = new HashMap<String, SQLResultSetMappingMetadata>();

        m_mappedSuperclasses = new HashMap<String, MappedSuperclassAccessor>();
        m_allAccessors = new HashMap<String, ClassAccessor>();
        m_entityAccessors = new HashMap<String, EntityAccessor>();
        m_embeddableAccessors = new HashMap<String, EmbeddableAccessor>();
        m_interfaceAccessors = new HashMap<String, InterfaceAccessor>();
        
        m_accessorsWithCustomizer = new HashSet<ClassAccessor>();
        m_relationshipAccessors = new HashSet<RelationshipAccessor>();
        m_embeddableMappingAccessors = new HashSet<MappingAccessor>();
        m_directCollectionAccessors = new HashSet<DirectCollectionAccessor>();

        m_generatedValues = new HashMap<Class, GeneratedValueMetadata>();
        m_tableGenerators = new HashMap<String, TableGeneratorMetadata>();
        m_sequenceGenerators = new HashMap<String, SequenceGeneratorMetadata>();
        
        m_converters = new HashMap<String, AbstractConverterMetadata>();
        
        m_accessorsWithDerivedIDs = new HashSet<ClassAccessor>();
    }
    
    /**
     * INTERNAL:
     * This method will add the descriptor to the actual EclipseLink project,
     * if it has not already been added. This method if called for entities
     * and embeddable classes (which are both weavable classes).
     */
    protected void addAccessor(ClassAccessor accessor) {
        MetadataDescriptor descriptor = accessor.getDescriptor();
        
        // Set the persistence unit meta data (if there is any) on the descriptor.
        if (m_persistenceUnitMetadata != null) {
            descriptor.setIgnoreAnnotations(m_persistenceUnitMetadata.isXMLMappingMetadataComplete());
            descriptor.setIgnoreDefaultMappings(m_persistenceUnitMetadata.excludeDefaultMappings());
            
            // Set the persistence unit defaults (if there are any) on the descriptor.
            XMLPersistenceUnitDefaults persistenceUnitDefaults = m_persistenceUnitMetadata.getPersistenceUnitDefaults();
            
            if (persistenceUnitDefaults != null) {
                descriptor.setDefaultAccess(persistenceUnitDefaults.getAccess());
                descriptor.setDefaultSchema(persistenceUnitDefaults.getSchema());
                descriptor.setDefaultCatalog(persistenceUnitDefaults.getCatalog());
                descriptor.setIsCascadePersist(persistenceUnitDefaults.isCascadePersist());
            }
        }

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
    public void addAccessorWithDerivedIDs(ClassAccessor accessor) {
        m_accessorsWithDerivedIDs.add(accessor);
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
        m_entityMappings.put(entityMappings.getMappingFile(), entityMappings);
    }
    
    /**
     * INTERNAL:
     */
    public void addGeneratedValue(GeneratedValueMetadata generatedvalue, Class entityClass) {
        m_generatedValues.put(entityClass, generatedvalue);
    }
    
    /**
     * INTERNAL:
     * Add a InterfaceAccessor to this project.
     */
    public void addInterfaceAccessor(InterfaceAccessor accessor) {
        m_interfaceAccessors.put(accessor.getJavaClassName(), accessor);
        
        // TODO: Add it directly and avoid the persistence unit defaults and
        // stuff for now.
        m_session.getProject().addDescriptor(accessor.getDescriptor().getClassDescriptor());
    }
    
    /**
     * INTERNAL:
     * Add a mapped superclass accessor to this project. Every consecutive 
     * mapped superclass accessor to the same class is merged with the first 
     * one that was added.
     * 
     * Note: The mapped-superclasses that are added here are those that are 
     * defined in XML only!
     */
    public void addMappedSuperclass(String className, MappedSuperclassAccessor mappedSuperclass) {
        if (m_mappedSuperclasses.containsKey(className)) {
            m_mappedSuperclasses.get(className).merge(mappedSuperclass);
        } else {
            m_mappedSuperclasses.put(className, mappedSuperclass);
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
    public void addRelationshipAccessor(MappingAccessor accessor) {
        m_relationshipAccessors.add((RelationshipAccessor) accessor);
    }
    
    /**
     * INTERNAL:
     * Add a sequence generator metadata to the project. The actual processing 
     * isn't done till processSequencing is called.
     */
    public void addSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
        String name = sequenceGenerator.getName();
        
        // Check if the sequence generator name uses a reserved name.
        if (name.equals(DEFAULT_TABLE_GENERATOR)) {
             throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_TABLE_GENERATOR, sequenceGenerator.getLocation());
        } else if (name.equals(DEFAULT_IDENTITY_GENERATOR)) {
            throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_IDENTITY_GENERATOR, sequenceGenerator.getLocation());
        }
        
        // Check if the name is used with a table generator.
        TableGeneratorMetadata tableGenerator = m_tableGenerators.get(name);
        if (tableGenerator != null) {
            throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(name, sequenceGenerator.getLocation(), tableGenerator.getLocation());          
        }
            
        for (TableGeneratorMetadata otherTableGenerator : m_tableGenerators.values()) {
            if (otherTableGenerator.getPkColumnValue().equals(sequenceGenerator.getSequenceName())) {
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
     * Add a table generator metadata to the project. The actual processing 
     * isn't done till processSequencing is called.
     */     
    public void addTableGenerator(TableGeneratorMetadata tableGenerator, String defaultCatalog, String defaultSchema) {
        // Process the default values.
        processTable(tableGenerator, TableSequence.defaultTableName, defaultCatalog, defaultSchema);
        
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
            throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(generatorName, otherSequenceGenerator.getLocation(), tableGenerator.getLocation());            
        }
            
        for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators.values()) {
            if (sequenceGenerator.getSequenceName().equals(tableGenerator.getPkColumnValue())) {
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
    public Set<ClassAccessor> getAccessorsWithDerivedIDs() {
        return m_accessorsWithDerivedIDs;
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
    public EmbeddableAccessor getEmbeddableAccessor(Class cls) {
        EmbeddableAccessor accessor = m_embeddableAccessors.get(cls.getName());

        if (accessor == null) {
          // Before return null we must make one final check for an Embeddable 
          // annotation on the class itself. At this point we know the class was 
          // not tagged as an embeddable in a mapping file and was not included 
          // in the list of classes for this persistence unit. Its inclusion 
          // therefore in the persistence unit is through the use of an Embedded 
          // annotation or an embedded element within a known entity. The
          // callers to this method will have to handle the null case if they
          // so desire.
          MetadataClass metadataClass = new MetadataClass(cls);
          if (metadataClass.isAnnotationPresent(Embeddable.class)) {
              accessor = new EmbeddableAccessor(metadataClass.getAnnotation(Embeddable.class), cls, this);
              addEmbeddableAccessor(accessor);
          }
       } 
        
       return accessor;
    }
    
    /**
     * INTERNAL:
     * Return the entity accessor for the given class.
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
    public MappedSuperclassAccessor getMappedSuperclass(Class cls) {
        return m_mappedSuperclasses.get(cls.getName());
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
    public boolean hasEmbeddable(Class cls) {
        return m_embeddableAccessors.containsKey(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEntity(Class cls) {
        return m_entityAccessors.containsKey(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasInterface(Class cls) {
        return m_interfaceAccessors.containsKey(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappedSuperclass(Class cls) {
        return m_mappedSuperclasses.containsKey(cls.getName());
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
     * Process descriptors with IDs derived from relationships.  This will also 
     * complete unfinished validation as well as secondary table and basic 
     * collection processing
     */
    protected void processAccessorsWithDerivedIDs() {
        HashSet<ClassAccessor> processed = new HashSet();
        HashSet<ClassAccessor> processing = new HashSet();
        
        for (ClassAccessor classAccessor : getAccessorsWithDerivedIDs()) {
            classAccessor.processDerivedIDs(processing, processed);
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
            for (Class interfaceClass : accessor.getJavaClass().getInterfaces()) {
                if (m_interfaceAccessors.containsKey(interfaceClass.getName())) {
                    m_interfaceAccessors.get(interfaceClass.getName()).addEntityAccessor(accessor);
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
        for (SQLResultSetMappingMetadata sqlResultSetMapping : m_sqlResultSetMappings.values()) {
            sqlResultSetMapping.process(m_session, loader);
        }
        
        for (NamedQueryMetadata query : m_queries.values()) {
            query.process(m_session, loader);
        }
    }
    
    /**
     * INTERNAL:
     * Process the related descriptors.
     */
    protected void processRelationshipAccessors() {
        for (RelationshipAccessor accessor : m_relationshipAccessors) {
            accessor.processRelationship();
        }
    }
    
    /**
     * INTERNAL:
     * Process the sequencing information.
     */
    protected void processSequencingAccessors() {
        if (! m_generatedValues.isEmpty()) {
            DatasourceLogin login = m_session.getProject().getLogin();
    
            Sequence defaultAutoSequence = null;
            TableSequence defaultTableSequence = new TableSequence(DEFAULT_TABLE_GENERATOR);
            NativeSequence defaultObjectNativeSequence = new NativeSequence(DEFAULT_SEQUENCE_GENERATOR, false);
            NativeSequence defaultIdentityNativeSequence = new NativeSequence(DEFAULT_IDENTITY_GENERATOR, 1, true);
            
            // Sequences keyed on generator names.
            Hashtable<String, Sequence> sequences = new Hashtable<String, Sequence>();
            
            for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators.values()) {
                String sequenceGeneratorName = sequenceGenerator.getName();
                
                String seqName;
                if (sequenceGenerator.getSequenceName() != null && (! sequenceGenerator.getSequenceName().equals(""))) {
                    seqName = sequenceGenerator.getSequenceName();
                } else {
                    // TODO: Log a message.
                    seqName = sequenceGeneratorName;
                }
                
                Integer allocationSize = sequenceGenerator.getAllocationSize();
                if (allocationSize == null) {
                    // Default value, same as annotation default.
                    // TODO: Log a message.
                    allocationSize = new Integer(50);
                }
                
                NativeSequence sequence = new NativeSequence(seqName, allocationSize, false);
                sequences.put(sequenceGeneratorName, sequence);
                
                if (sequenceGeneratorName.equals(DEFAULT_AUTO_GENERATOR)) {
                    // SequenceGenerator defined with DEFAULT_AUTO_GENERATOR.
                    // The sequence it defines will be used as a defaultSequence.
                    defaultAutoSequence = sequence;
                } else if (sequenceGeneratorName.equals(DEFAULT_SEQUENCE_GENERATOR)) {
                    // SequenceGenerator defined with DEFAULT_SEQUENCE_GENERATOR.
                    // All sequences of GeneratorType SEQUENCE referencing 
                    // non-defined generators will use a clone of the sequence 
                    // defined by this generator.
                    defaultObjectNativeSequence = sequence;
                }
            }

            for (TableGeneratorMetadata tableGenerator : m_tableGenerators.values()) {
                String tableGeneratorName = tableGenerator.getGeneratorName();
                
                String seqName;
                if (tableGenerator.getPkColumnValue() != null && (! tableGenerator.getPkColumnValue().equals(""))) {
                    seqName = tableGenerator.getPkColumnValue();
                } else {
                    // TODO: Log a message.
                    seqName = tableGeneratorName;
                }
                
                Integer allocationSize = tableGenerator.getAllocationSize();
                if (allocationSize == null) {
                    // Default value, same as annotation default.
                    // TODO: Log a message.
                    allocationSize = new Integer(50);
                }
                
                Integer initialValue = tableGenerator.getInitialValue();
                if (initialValue == null) {
                    // Default value, same as annotation default.
                    // TODO: Log a message.
                    initialValue = new Integer(0);
                }
                
                TableSequence sequence = new TableSequence(seqName, allocationSize, initialValue);
                sequences.put(tableGeneratorName, sequence);

                // Get the database table from the table generator.
                sequence.setTable(tableGenerator.getDatabaseTable());
                
                if (tableGenerator.getPkColumnName() != null && (! tableGenerator.getPkColumnName().equals(""))) {
                    sequence.setNameFieldName(tableGenerator.getPkColumnName());
                }
                    
                if (tableGenerator.getValueColumnName() != null && (! tableGenerator.getValueColumnName().equals(""))) {
                    sequence.setCounterFieldName(tableGenerator.getValueColumnName());
                }

                if (tableGeneratorName.equals(DEFAULT_AUTO_GENERATOR)) {
                    // TableGenerator defined with DEFAULT_AUTO_GENERATOR.
                    // The sequence it defines will be used as a defaultSequence.
                    defaultAutoSequence = sequence;
                } else if (tableGeneratorName.equals(DEFAULT_TABLE_GENERATOR)) {
                    // SequenceGenerator defined with DEFAULT_TABLE_GENERATOR. 
                    // All sequences of GenerationType TABLE referencing non-
                    // defined generators will use a clone of the sequence 
                    // defined by this generator.
                    defaultTableSequence = sequence;
                }
            }

            // Finally loop through descriptors and set sequences as required 
            // into Descriptors and Login
            boolean usesAuto = false;
            for (Class entityClass : m_generatedValues.keySet()) {
                MetadataDescriptor descriptor = m_allAccessors.get(entityClass.getName()).getDescriptor();
                GeneratedValueMetadata generatedValue = m_generatedValues.get(entityClass);
                String generatorName = generatedValue.getGenerator();
                
                if (generatorName == null) {
                    // Value was loaded from XML (and it wasn't specified) so
                    // assign it the annotation default of ""
                    generatorName = "";
                }
                
                Sequence sequence = null;
                if (! generatorName.equals("")) {
                    sequence = sequences.get(generatorName);
                }
                
                if (sequence == null) {
                    Enum strategy = generatedValue.getStrategy();
                    
                    // A null strategy will default to AUTO.
                    if (strategy == null || strategy.name().equals(GenerationType.AUTO.name())) {
                        usesAuto = true;
                    } else if (strategy.name().equals(GenerationType.TABLE.name())) {
                        if (generatorName.equals("")) {
                            sequence = defaultTableSequence;
                        } else {
                            sequence = (Sequence)defaultTableSequence.clone();
                            sequence.setName(generatorName);
                        }
                    } else if (strategy.name().equals(GenerationType.SEQUENCE.name())) {
                        if (generatorName.equals("")) {
                            sequence = defaultObjectNativeSequence;
                        } else {
                            sequence = (Sequence)defaultObjectNativeSequence.clone();
                            sequence.setName(generatorName);
                        }
                    } else if (strategy.name().equals(GenerationType.IDENTITY.name())) {
                        if (generatorName.equals("")) {
                            sequence = defaultIdentityNativeSequence;
                        } else {
                            sequence = (Sequence)defaultIdentityNativeSequence.clone();
                            sequence.setName(generatorName);
                        }
                    }
                }

                if (sequence != null) {
                    descriptor.setSequenceNumberName(sequence.getName());
                    login.addSequence(sequence);
                } else {
                    String seqName;
                    
                    if (generatorName.equals("")) {
                        if (defaultAutoSequence != null) {
                            seqName = defaultAutoSequence.getName();
                        } else {
                            seqName = DEFAULT_AUTO_GENERATOR; 
                        }
                    } else {
                        seqName = generatorName;
                    }
                    
                    descriptor.setSequenceNumberName(seqName);
                }
            }
            
            if (usesAuto) {
                if (defaultAutoSequence != null) {
                    login.setDefaultSequence(defaultAutoSequence);
                }
            }
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
        // Mapped superclasses and embeddables are pre-processed through the 
        // entity pre-processing.
        for (EntityAccessor entity : getEntityAccessors()) {
            if (! entity.isPreProcessed()) {
                entity.preProcess();
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
        // Mapped superclasses and embeddables are processed through the entity 
        // processing.
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
        // being complete as well.
        processAccessorsWithDerivedIDs();

        // 2 - Process all the direct collection accessors we found. This list
        // does not include direct collections to an embeddable class.
        processDirectCollectionAccessors();
        
        // 3 - Process the sequencing metadata now that every entity has a 
        // validated primary key.
        processSequencingAccessors();
        
        // 4 - Process the relationship accessors now that every entity has a 
        // validated primary key and we can process join columns.
        processRelationshipAccessors();
        
        // 5 - Process the interface accessors which will iterate through all 
        // the entities in the PU and check if we should add them to a variable 
        // one to one mapping that was either defined (incompletely) or 
        // defaulted.
        processInterfaceAccessors();
        
        // 6 - Process the embeddable mapping accessors. These are the
        // embedded, embedded id and element collection accessors that map
        // to an embeddable class. We must hold off on their processing till
        // now to ensure their relationship accessors have been processed and
        // we can therefore process any association overrides correctly.
        processEmbeddableMappingAccessors();
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
        
        // Process the unique constraints
        table.processUniqueConstraints();
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
     * This flag represents dynamic weaving state for 1-1, many-1, fetch groups 
     * and change tracking.  
     * @param weavingEnabled (false = weaving disabled)
     */
    public void setWeavingEnabled(boolean weavingEnabled) {
        m_weavingEnabled = weavingEnabled;
    }
    
    /**
     * Return if the project should use indirection for eager relationships.
     */
    public boolean weaveEager() {
        return m_weaveEager;
    }
}

