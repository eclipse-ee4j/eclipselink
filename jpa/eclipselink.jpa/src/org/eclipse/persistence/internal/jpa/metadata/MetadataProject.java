/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.GenerationType;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.annotations.Direction;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.RelationshipAccessor;

import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.queries.EntityResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FieldResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryHintMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.StoredProcedureParameterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;

import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.EJBQLPlaceHolderQuery;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.StoredProcedureCall;

import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.NativeSequence;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatasourceLogin;

/**
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataProject {
    // Sequencing constants.
    private static final String DEFAULT_AUTO_GENERATOR = "SEQ_GEN";
    private static final String DEFAULT_TABLE_GENERATOR = "SEQ_GEN_TABLE";
    private static final String DEFAULT_SEQUENCE_GENERATOR = "SEQ_GEN_SEQUENCE";
    private static final String DEFAULT_IDENTITY_GENERATOR = "SEQ_GEN_IDENTITY";
    
    // Persistence unit info that is represented by this project.
    private PersistenceUnitInfo m_persistenceUnitInfo;
    
    // A list of all the entity mappinds (XML file representation)
    private List<XMLEntityMappings> m_entityMappings;

    // The session we are currently processing for.
    private AbstractSession m_session;

    // The logger for the project.
    private MetadataLogger m_logger;

    // Boolean to specify if we should weave for value holders.
    private boolean m_enableLazyForOneToOne;

    // Persistence unit metadata for this project.
    private XMLPersistenceUnitMetadata m_persistenceUnitMetadata;

    // List of mapped-superclasses found in XML for this project/persistence unit.
    private HashMap<String, MappedSuperclassAccessor> m_mappedSuperclasses;

    // All the class accessors for this project (Entities and Embeddables).
    private HashMap<String, ClassAccessor> m_allAccessors;
    
    // The class accessors for this project
    private HashMap<String, ClassAccessor> m_classAccessors;
    
    // The embeddable accessors for this project
    private HashMap<String, EmbeddableAccessor> m_embeddableAccessors;

    // Class accessors that have relationships.
    private HashSet<ClassAccessor> m_accessorsWithRelationships;
    
    // Class accessors that have a customizer.
    private HashSet<ClassAccessor> m_accessorsWithCustomizer;

    // Query metadata
    private HashMap<String, NamedQueryMetadata> m_namedQueries;
    private HashMap<String, NamedNativeQueryMetadata> m_namedNativeQueries;
    private HashMap<String, NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries;

    // Sequencing metadata.
    private HashMap<Class, GeneratedValueMetadata> m_generatedValues;
    private HashMap<String, TableGeneratorMetadata> m_tableGenerators;
    private HashMap<String, SequenceGeneratorMetadata> m_sequenceGenerators;

    // Default listeners that need to be applied to each entity in the
    // persistence unit (unless they exclude them).
    private HashMap<String, EntityListenerMetadata> m_defaultListeners;
    
    // Metadata converters, that is, EclipseLink converters.
    private HashMap<String, AbstractConverterMetadata> m_converters;
    
    // Accessors that use an EclipseLink converter.
    private HashSet<DirectAccessor> m_convertAccessors;

    // MetadataStructConverters, these are StructConverters that get added to 
    // the session.
    private HashMap<String, StructConverterMetadata> m_structConverters;
    
    /**
     * INTERNAL:
     */
    public MetadataProject(PersistenceUnitInfo puInfo, AbstractSession session, boolean enableLazyForOneToOne) {
    	m_persistenceUnitInfo = puInfo;
        m_session = session;
        m_logger = new MetadataLogger(session);
        m_enableLazyForOneToOne = enableLazyForOneToOne;
        
        m_entityMappings = new ArrayList<XMLEntityMappings>();
        m_defaultListeners = new HashMap<String, EntityListenerMetadata>();

        m_namedQueries = new HashMap<String, NamedQueryMetadata>();
        m_namedNativeQueries = new HashMap<String, NamedNativeQueryMetadata>();
        m_namedStoredProcedureQueries = new HashMap<String, NamedStoredProcedureQueryMetadata>();

        m_mappedSuperclasses = new HashMap<String, MappedSuperclassAccessor>();
        m_allAccessors = new HashMap<String, ClassAccessor>();
        m_classAccessors = new HashMap<String, ClassAccessor>();
        m_embeddableAccessors = new HashMap<String, EmbeddableAccessor>();
        m_accessorsWithCustomizer = new HashSet<ClassAccessor>();
        m_accessorsWithRelationships = new HashSet<ClassAccessor>();

        m_generatedValues = new HashMap<Class, GeneratedValueMetadata>();
        m_tableGenerators = new HashMap<String, TableGeneratorMetadata>();
        m_sequenceGenerators = new HashMap<String, SequenceGeneratorMetadata>();
        
        m_converters = new HashMap<String, AbstractConverterMetadata>();
        m_convertAccessors = new HashSet<DirectAccessor>();
        m_structConverters = new HashMap<String, StructConverterMetadata>();
    }

    /**
     * INTERNAL:
     * This method will add the descriptor to the actual EclipseLink project,
     * if it has not already been added. This method if called for entities
     * and embeddable classes (which are both weavable classes)
     */
    protected void addAccessor(ClassAccessor accessor) {
    	MetadataDescriptor descriptor = accessor.getDescriptor();
    	
        // Set the persistence unit meta data (if there is any) on the descriptor.
        if (m_persistenceUnitMetadata != null) {
        	descriptor.setIgnoreAnnotations(m_persistenceUnitMetadata.isXMLMappingMetadataComplete());
        	
        	// Set the persistence unit defaults (if there are any) on the descriptor.
        	XMLPersistenceUnitDefaults persistenceUnitDefaults = m_persistenceUnitMetadata.getPersistenceUnitDefaults();
        	
        	if (persistenceUnitDefaults != null) {
        		descriptor.setXMLAccess(persistenceUnitDefaults.getAccess());
        		descriptor.setXMLSchema(persistenceUnitDefaults.getSchema());
        		descriptor.setXMLCatalog(persistenceUnitDefaults.getCatalog());
        		descriptor.setIsCascadePersist(persistenceUnitDefaults.isCascadePersist());
        	}
        }

        Project project = getSession().getProject();
        ClassDescriptor descriptorOnProject = MetadataHelper.findDescriptor(project, descriptor.getJavaClass());

        if (descriptorOnProject == null) {
            project.addDescriptor(descriptor.getClassDescriptor());
        } else {
            descriptor.setDescriptor(descriptorOnProject);
        }

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
    public void addAccessorWithRelationships(ClassAccessor accessor) {
        m_accessorsWithRelationships.add(accessor);
    }
    
    /**
     * INTERNAL:
     * Add a ClassAccessor to this project.
     */
    public void addClassAccessor(ClassAccessor accessor) {
    	m_classAccessors.put(accessor.getJavaClassName(), accessor);
    	
    	addAccessor(accessor);
    }
 
    /**
     * INTERNAL:
     */
    public void addConvertAccessor(DirectAccessor accessor) {
        m_convertAccessors.add(accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addConverter(AbstractConverterMetadata converter) {
        m_converters.put(converter.getName(), converter);
    }
    
    /**
     * INTERNAL:
     */
    public void addDefaultListener(EntityListenerMetadata defaultListener) {
    	m_defaultListeners.put(defaultListener.getClassName(), defaultListener);
    }

    /**
     * INTERNAL:
     * Add an embeddable accessor to this project.
     */
    public void addEmbeddableAccessor(EmbeddableAccessor accessor) {
    	m_embeddableAccessors.put(accessor.getJavaClassName(), accessor);
    	addAccessor(accessor);
    	accessor.getDescriptor().setIsEmbeddable();
    }
    
    /**
     * INTERNAL:
     */
    public void addEntityMappings(XMLEntityMappings entityMappings) {
    	m_entityMappings.add(entityMappings);
    }
    
    /**
     * INTERNAL:
     */
    public void addGeneratedValue(GeneratedValueMetadata generatedvalue, Class entityClass) {
        m_generatedValues.put(entityClass, generatedvalue);
    }
    
    /**
     * INTERNAL:
     * Add a mapped-superclass to the project. The mapped-superclasses that
     * are added here are those that are defined in XML only!
     */
    public void addMappedSuperclass(String className, MappedSuperclassAccessor mappedSuperclass) {
        m_mappedSuperclasses.put(className, mappedSuperclass);
    }

    /**
     * INTERNAL:
     */
    public void addNamedNativeQuery(NamedNativeQueryMetadata namedNativeQuery) {
        m_namedNativeQueries.put(namedNativeQuery.getName(), namedNativeQuery);
    }

    /**
     * INTERNAL:
     */
    public void addNamedQuery(NamedQueryMetadata namedQuery) {
        m_namedQueries.put(namedQuery.getName(), namedQuery);
    }
    
    /**
     * INTERNAL:
     */
    public void addNamedStoredProcedureQuery(NamedStoredProcedureQueryMetadata namedStoredProcedureQuery) {
        m_namedStoredProcedureQueries.put(namedStoredProcedureQuery.getName(), namedStoredProcedureQuery);
    }
    
    /**
     * INTERNAL:
     */
    public void addSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
        m_sequenceGenerators.put(sequenceGenerator.getName(), sequenceGenerator);
    }
    
    /**
     * INTERNAL:
     */
    public void addStructConverter(StructConverterMetadata converter) {
        m_structConverters.put(converter.getName(), converter);
    }

    /**
     * INTERNAL:
     */
    public void addTableGenerator(TableGeneratorMetadata tableGenerator) {
        m_tableGenerators.put(tableGenerator.getGeneratorName(), tableGenerator);
    }

    /**
     * INTERNAL:
     */
    public boolean enableLazyForOneToOne() {
        return m_enableLazyForOneToOne;
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
    public Set<ClassAccessor> getAccessorsWithRelationships() {
        return m_accessorsWithRelationships;
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
    public Collection<ClassAccessor> getClassAccessors() {
        return m_classAccessors.values();
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
    public HashMap<String, EntityListenerMetadata> getDefaultListeners() {
        return m_defaultListeners;
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddableAccessor getEmbeddableAccessor(String className) {
    	return m_embeddableAccessors.get(className);
    }
    
    /**
     * INTERNAL:
     */
    public List<XMLEntityMappings> getEntityMappings() {
    	return m_entityMappings;
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
    public SequenceGeneratorMetadata getSequenceGenerator(String name) {
        return m_sequenceGenerators.get(name);
    }

    /**
     * INTERNAL:
     */
    public Collection<SequenceGeneratorMetadata> getSequenceGenerators() {
        return m_sequenceGenerators.values();
    }
    
    /**
     * INTERNAL:
     */
    public AbstractSession getSession() {
        return m_session;
    }
        
    /**
     * INTERNAL:
     */
    public StructConverterMetadata getStructConverter(String name) {
        return m_structConverters.get(name);
    }

    /**
     * INTERNAL:
     */
    public HashMap<String, StructConverterMetadata> getStructConverters(){
        return m_structConverters;
    }
    
    /**
     * INTERNAL:
     */
    public TableGeneratorMetadata getTableGenerator(String generatorName) {
        return m_tableGenerators.get(generatorName);
    }

    /**
     * INTERNAL:
     */
    public Collection<TableGeneratorMetadata> getTableGenerators() {
        return m_tableGenerators.values();
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
    public boolean hasConflictingSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
    	SequenceGeneratorMetadata existingSequenceGenerator = getSequenceGenerator(sequenceGenerator.getName());
    	
        if (existingSequenceGenerator == null) {
        	return false;
        } else {
            return ! existingSequenceGenerator.equals(sequenceGenerator);
        }
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
        return m_classAccessors.containsKey(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappedSuperclass(Class cls) {
        return m_mappedSuperclasses.containsKey(cls.getName());
    }

    /**
     * INTERNAL:
     */
    public boolean hasStructConverter(String name) {
        return m_structConverters.containsKey(name);
    }
    
    /**
     * INTERNAL:
     * Stage 2 processing. That is, it does all the extra processing that 
     * couldn't be completed in the original metadata accessor processing.
	 */
	public void process() {
        processConvertAccessors();
        
        processSequencing();
        
        processAccessorsWithRelationships();
    }
    
    /**
     * INTERNAL:
     * Process the related descriptors.
     */
    protected void processAccessorsWithRelationships() {
        for (ClassAccessor classAccessor : getAccessorsWithRelationships()) {
            for (RelationshipAccessor accessor : classAccessor.getDescriptor().getRelationshipAccessors()) {
                accessor.processRelationship();
            }
        }
    }

    /**
     * INTERNAL:
     * Process those accessors that have a convert value. A convert value is 
     * used to process an EclipseLink converter (Converter, TypeConverter and 
     * ObjectTypeConverter) for an accessor's database mapping.
     */
    protected void processConvertAccessors() {
        for (DirectAccessor accessor : m_convertAccessors) {
            accessor.processConvert();
        }
    }
    
    /**
     * INTERNAL:
     * Process an AbstractMetadataConverter and add it to the project. 
     */
    public void processConverter(AbstractConverterMetadata converter) {
    	// Check for a struct converter with the same name.
    	StructConverterMetadata existingStructConverter = m_structConverters.get(converter.getName());
    	if (existingStructConverter != null) {
    		throw ValidationException.multipleConvertersOfTheSameName(converter.getName(), existingStructConverter.getLocation(), converter.getLocation());
    	}
    	
    	// Check for another converter with the same name.
    	AbstractConverterMetadata existingConverter = m_converters.get(converter.getName());
    	if (existingConverter == null || existingConverter.loadedFromAnnotation() && converter.loadedFromXML()) {
    		if (existingConverter != null) {
    			// XML -> Annotation override, log a warning.
				getLogger().logWarningMessage(MetadataLogger.IGNORE_CONVERTER_ANNOTATION, existingConverter.getName(), existingConverter.getLocation(), converter.getLocation());
    		}
    		
    		addConverter(converter);
    	} else if (! existingConverter.equals(converter)) {
    		throw ValidationException.multipleConvertersOfTheSameName(converter.getName(), existingConverter.getLocation(), converter.getLocation());
    	}
    }
    
    /**
     * INTERNAL:
     * Process the named native queries we found and add them to the given
     * session.
     */
    public void processNamedNativeQueries(ClassLoader loader) {
        for (NamedNativeQueryMetadata namedNativeQuery : m_namedNativeQueries.values()) {
            HashMap<String, String> hints = processQueryHints(namedNativeQuery.getHints(), namedNativeQuery.getName());

            Class resultClass = namedNativeQuery.getResultClass();

            if (resultClass != void.class) {
                resultClass = MetadataHelper.getClassForName(resultClass.getName(), loader);
                m_session.addQuery(namedNativeQuery.getName(), EJBQueryImpl.buildSQLDatabaseQuery(resultClass, namedNativeQuery.getQuery(), hints));
            } else { 
                String resultSetMapping = namedNativeQuery.getResultSetMapping();

                if (! resultSetMapping.equals("")) {
                    m_session.addQuery(namedNativeQuery.getName(), EJBQueryImpl.buildSQLDatabaseQuery(resultSetMapping, namedNativeQuery.getQuery(), hints));
                } else {
                    // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                    m_session.addQuery(namedNativeQuery.getName(), EJBQueryImpl.buildSQLDatabaseQuery(namedNativeQuery.getQuery(), hints));
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Process a MetadataNamedNativeQuery. The actual query processing isn't 
     * done till addNamedQueriesToSession is called.
     */
    public void processNamedNativeQuery(NamedNativeQueryMetadata namedNativeQuery) {
    	NamedNativeQueryMetadata existingNamedNativeQuery = m_namedNativeQueries.get(namedNativeQuery.getName());
    	
    	if (existingNamedNativeQuery == null || existingNamedNativeQuery.loadedFromAnnotation() && namedNativeQuery.loadedFromXML()) {
    		if (existingNamedNativeQuery != null) {
    			// XML -> Annotation override, log a warning.
				getLogger().logWarningMessage(MetadataLogger.IGNORE_NAMED_NATIVE_QUERY_ANNOTATION, existingNamedNativeQuery.getName(), existingNamedNativeQuery.getLocation(), namedNativeQuery.getLocation());
    		}
    		
    		addNamedNativeQuery(namedNativeQuery);
    	} else if (! existingNamedNativeQuery.equals(namedNativeQuery)) {
    		throw ValidationException.multipleNamedNativeQueriesWithSameName(namedNativeQuery.getName(), namedNativeQuery.getLocation(), existingNamedNativeQuery.getLocation());
    	}
    }
    
    /**
     * INTERNAL:
     * Process the named queries we found and add them to the given session.
     */
    public void processNamedQueries() {
        for (NamedQueryMetadata namedQuery : m_namedQueries.values()) {
            try {
                HashMap<String, String> hints = processQueryHints(namedQuery.getHints(), namedQuery.getName());
                m_session.addEjbqlPlaceHolderQuery(new EJBQLPlaceHolderQuery(namedQuery.getName(), namedQuery.getQuery(), hints));
            } catch (Exception exception) {
            	throw ValidationException.errorProcessingNamedQuery(namedQuery.getClass(), namedQuery.getName(), exception);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Add a metadata named query to the project. The actual query processing 
     * isn't done till addNamedQueriesToSession is called.
     */
    public void processNamedQuery(NamedQueryMetadata namedQuery) {
    	NamedQueryMetadata existingNamedQuery = m_namedQueries.get(namedQuery.getName());
    	
        if (existingNamedQuery == null || existingNamedQuery.loadedFromAnnotation() && namedQuery.loadedFromXML()) {
        	if (existingNamedQuery != null) {
        		// XML -> Annotation override, log a warning.
				getLogger().logWarningMessage(MetadataLogger.IGNORE_NAMED_QUERY_ANNOTATION, existingNamedQuery.getName(), existingNamedQuery.getLocation(), namedQuery.getLocation());
        	}
        	
        	addNamedQuery(namedQuery);
        } else if (! existingNamedQuery.equals(namedQuery)) {
        	throw ValidationException.multipleNamedQueriesWithSameName(namedQuery.getName(), namedQuery.getLocation(), existingNamedQuery.getLocation());
        }
    }
    
    /**
     * INTERNAL:
     * Process the named stored procedure queries we found and add them to the given session.
     */
    public void processNamedStoredProcedureQueries(ClassLoader loader) {
        for (NamedStoredProcedureQueryMetadata query : m_namedStoredProcedureQueries.values()) {
            // Build the stored procedure call.
            StoredProcedureCall call = new StoredProcedureCall();
            
            // Process the stored procedure parameters.
            List<String> queryArguments = processStoredProcedureParameters(query, call);
            
            // Process the procedure name
            call.setProcedureName(query.getProcedureName());
            
            // Process the returns result set.
            call.setReturnsResultSet(query.returnsResultSet());
            
            // Process the query hints.
            HashMap<String, String> hints = processQueryHints(query.getHints(), query.getName());
            
            // Process the result class.
            Class resultClass = query.getResultClass();
            
            if (resultClass != void.class) {
                resultClass = MetadataHelper.getClassForName(resultClass.getName(), loader);
                m_session.addQuery(query.getName(), EJBQueryImpl.buildStoredProcedureQuery(resultClass, call, queryArguments, hints));
            } else {
                // Process the result set mapping name.
                String resultSetMapping = query.getResultSetMapping();

                if (! resultSetMapping.equals("")) {
                    m_session.addQuery(query.getName(), EJBQueryImpl.buildStoredProcedureQuery(resultSetMapping, call, queryArguments, hints));
                } else {
                    // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                    m_session.addQuery(query.getName(), EJBQueryImpl.buildStoredProcedureQuery(call, queryArguments, hints));
                }    
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a MetadataNamedStoredProcedureQuery. The actual query 
     * processing isn't done till addNamedQueriesToSession is called.
     */
    public void processNamedStoredProcedureQuery(NamedStoredProcedureQueryMetadata namedStoredProcedureQuery) {
    	NamedStoredProcedureQueryMetadata existingNamedStoredProcedureQuery = m_namedStoredProcedureQueries.get(namedStoredProcedureQuery.getName());
    	
        if (existingNamedStoredProcedureQuery == null || existingNamedStoredProcedureQuery.loadedFromAnnotation() && namedStoredProcedureQuery.loadedFromXML()) {
        	if (existingNamedStoredProcedureQuery != null) {
        		// XML -> Annotation override, log a warning.
				getLogger().logWarningMessage(MetadataLogger.IGNORE_NAMED_STORED_PROCEDURE_QUERY_ANNOTATION, existingNamedStoredProcedureQuery.getName(), existingNamedStoredProcedureQuery.getLocation(), namedStoredProcedureQuery.getLocation());
        	}
        	
        	addNamedStoredProcedureQuery(namedStoredProcedureQuery);
        } else {
            // Future: Should implement a equals method like the other query processing.
        	throw ValidationException.multipleNamedStoredProcedureQueriesWithSameName(namedStoredProcedureQuery.getName(), namedStoredProcedureQuery.getLocation(), existingNamedStoredProcedureQuery.getLocation());
        }
    }

    /**
     * INTERNAL:
     * Process a list of MetadataQueryHint.
     */	
    protected HashMap<String, String> processQueryHints(List<QueryHintMetadata> hints, String queryName) {
    	HashMap<String, String> hm = new HashMap<String, String>();
    	
    	if (hints != null) {
    		for (QueryHintMetadata hint : hints) {
    			QueryHintsHandler.verify(hint.getName(), hint.getValue(), queryName, m_session);
    			hm.put(hint.getName(), hint.getValue());
    		}
    	}
		
        return hm;
    } 
    
    /**
     * INTERNAL:
     * Process a MetadataSequenceGenerator and add it to the project.
     */
    public void processSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
        // Check if the sequence generator name uses a reserved name.
        String name = sequenceGenerator.getName();
        
         if (name.equals(DEFAULT_TABLE_GENERATOR)) {
        	 throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_TABLE_GENERATOR, sequenceGenerator.getLocation());
        } else if (name.equals(DEFAULT_IDENTITY_GENERATOR)) {
        	throw ValidationException.sequenceGeneratorUsingAReservedName(DEFAULT_IDENTITY_GENERATOR, sequenceGenerator.getLocation());
        }
            
        // Conflicting means that they do not have all the same values.
        if (hasConflictingSequenceGenerator(sequenceGenerator)) {
        	SequenceGeneratorMetadata otherSequenceGenerator = getSequenceGenerator(name);
            if (sequenceGenerator.loadedFromAnnotations() && otherSequenceGenerator.loadedFromXML()) {
                // Future: Log a warning that we are ignoring this table generator.
                return;
            } else {
            	throw ValidationException.conflictingSequenceGeneratorsSpecified(name, sequenceGenerator.getLocation(), otherSequenceGenerator.getLocation());
            }
        }
            
        TableGeneratorMetadata tableGenerator = getTableGenerator(name);
        if (tableGenerator != null) {
        	throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(name, sequenceGenerator.getLocation(), tableGenerator.getLocation());        	
        }
            
        for (TableGeneratorMetadata otherTableGenerator : getTableGenerators()) {
            if (otherTableGenerator.getPkColumnValue().equals(sequenceGenerator.getSequenceName())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if (otherTableGenerator.getPkColumnValue().length() > 0) {
                	throw ValidationException.conflictingSequenceNameAndTablePkColumnValueSpecified(sequenceGenerator.getSequenceName(), sequenceGenerator.getLocation(), otherTableGenerator.getLocation());
                }
            }
        }
        
        addSequenceGenerator(sequenceGenerator);
    }
    
    /**
     * INTERNAL:
     * Process the sequencing information.
     */
    protected void processSequencing() {
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
                	// Future: Log a message.
                	seqName = sequenceGeneratorName;
                }
                
                Integer allocationSize = sequenceGenerator.getAllocationSize();
                if (allocationSize == null) {
                	// Default value, same as annotation default.
                	// Future: Log a message.
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
                	// Future: Log a message.
                	seqName = tableGeneratorName;
                }
                
                Integer allocationSize = tableGenerator.getAllocationSize();
                if (allocationSize == null) {
                	// Default value, same as annotation default.
                	// Future: Log a message.
                	allocationSize = new Integer(50);
                }
                
                Integer initialValue = tableGenerator.getInitialValue();
                if (initialValue == null) {
                	// Default value, same as annotation default.
                	// Future: Log a message.
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
                	if (strategy == null || strategy.equals(GenerationType.AUTO)) {
                		usesAuto = true;
                	} else if (strategy.equals(GenerationType.TABLE)) {
                        if (generatorName.equals("")) {
                            sequence = defaultTableSequence;
                        } else {
                            sequence = (Sequence)defaultTableSequence.clone();
                            sequence.setName(generatorName);
                        }
                    } else if (strategy.equals(GenerationType.SEQUENCE)) {
                        if (generatorName.equals("")) {
                            sequence = defaultObjectNativeSequence;
                        } else {
                            sequence = (Sequence)defaultObjectNativeSequence.clone();
                            sequence.setName(generatorName);
                        }
                    } else if (strategy.equals(GenerationType.IDENTITY)) {
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
     * Process an sql result set mapping metadata into a EclipseLink 
     * SqlResultSetMapping and store it on the session.
     */
    public void processSqlResultSetMapping(SQLResultSetMappingMetadata sqlResultSetMapping) {        
        // Initialize a new SqlResultSetMapping (with the metadata name)
        org.eclipse.persistence.queries.SQLResultSetMapping mapping = new org.eclipse.persistence.queries.SQLResultSetMapping(sqlResultSetMapping.getName());
        
        // Process the entity results.
        if (sqlResultSetMapping.hasEntityResults()) {
        	for (EntityResultMetadata eResult : sqlResultSetMapping.getEntityResults()) {
        		EntityResult entityResult = new EntityResult(eResult.getEntityClass().getName());
        
        		// Process the field results.
        		if (eResult.hasFieldResults()) {
        			for (FieldResultMetadata fResult : eResult.getFieldResults()) {
        				entityResult.addFieldResult(new FieldResult(fResult.getName(), fResult.getColumn()));
        			}
        		}
        
        		// Process the discriminator value;
        		entityResult.setDiscriminatorColumn(eResult.getDiscriminatorColumn());
        
        		// Add the result to the SqlResultSetMapping.
        		mapping.addResult(entityResult);
        	}
        }
        
        // Process the column results.
        if (sqlResultSetMapping.hasColumnResults()) {
        	for (String columnResult : sqlResultSetMapping.getColumnResults()) {
        		mapping.addResult(new ColumnResult(columnResult));
        	}
        }
            
        getSession().getProject().addSQLResultSetMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Process the stored procedure parameters on the given stored procedure
     * query.
     */
    protected List<String> processStoredProcedureParameters(NamedStoredProcedureQueryMetadata query, StoredProcedureCall call) {
    	List<String> queryArguments = new ArrayList<String>();
    	
    	if (query.hasProcedureParameters()) {
    		for (StoredProcedureParameterMetadata parameter : query.getProcedureParameters()) {
    			// Process the query parameter
    			String argumentFieldName = parameter.getQueryParameter();
                    
    			// Process the procedure parameter name, defaults to the 
    			// argument field name.
	            String procedureParameterName = parameter.getName();
	            if (procedureParameterName.equals("")) {
	                // Future: Log a message.
	                procedureParameterName = argumentFieldName;
	            }
	                    
	            // Process the parameter direction
	            String direction = parameter.getDirection();
	            if (direction.equals(Direction.IN.name())) {
	                if (parameter.hasType()) {
	                    call.addNamedArgument(procedureParameterName, argumentFieldName, parameter.getType());
	                } else if (parameter.hasJdbcType() && parameter.hasJdbcTypeName()) {
	                    call.addNamedArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType(), parameter.getJdbcTypeName());
	                } else if (parameter.hasJdbcType()) {
	                    call.addNamedArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType());
	                } else {
	                    call.addNamedArgument(procedureParameterName, argumentFieldName);
	                }
	                        
	                queryArguments.add(argumentFieldName);
	            } else if (direction.equals(Direction.OUT.name())) {
	                if (parameter.hasType()) {
	                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getType());
	                } else if (parameter.hasJdbcType() && parameter.hasJdbcTypeName()) {
	                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType(), parameter.getJdbcTypeName());
	                } else if (parameter.hasJdbcType()) {
	                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType());
	                } else {
	                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName);
	                }
	            } else if (direction.equals(Direction.IN_OUT.name())) {
	                if (parameter.hasType()) {
	                    call.addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName, parameter.getType());
	                } else if (parameter.hasJdbcType() && parameter.hasJdbcTypeName()) {
	                    call.addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName, parameter.getJdbcType(), parameter.getJdbcTypeName());
	                } else if (parameter.hasJdbcType()) {
	                    call.addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName, parameter.getJdbcType());
	                } else {
	                    call.addNamedInOutputArgument(procedureParameterName, argumentFieldName);
	                }
	                        
	                queryArguments.add(argumentFieldName);
	            } else { // must be MetadataConstants.OUT_CURSOR.
	                call.useNamedCursorOutputAsResultSet(argumentFieldName);
	            }
	        }
    	}
      
    	return queryArguments;
    }
    
    /**
     * INTERNAL:
     * Process a StructConverterMetadata. 
     */
    public void processStructConverter(StructConverterMetadata structConverter) {
    	// Check for a mapping converter with the same name.
    	AbstractConverterMetadata existingConverter = m_converters.get(structConverter.getName());
    	if (existingConverter != null) {
    		throw ValidationException.multipleConvertersOfTheSameName(structConverter.getName(), existingConverter.getLocation(), structConverter.getLocation());
    	}
    	
    	// Check for a struct converter with the same name.
    	StructConverterMetadata existingStructConverter = m_structConverters.get(structConverter.getName());
    	if (existingStructConverter == null || existingConverter.loadedFromAnnotation() && structConverter.loadedFromXML()) {
    		if (existingConverter != null) {
    			// XML -> Annotation override, log a warning.
				getLogger().logWarningMessage(MetadataLogger.IGNORE_STRUCT_CONVERTER_ANNOTATION, existingConverter.getName(), existingConverter.getLocation(), structConverter.getLocation());
    		}
    		
    		addStructConverter(structConverter);
    	} else if (! existingStructConverter.equals(structConverter)) {
    		throw ValidationException.multipleConvertersOfTheSameName(structConverter.getName(), existingStructConverter.getLocation(), structConverter.getLocation());
    	}
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
        table.setFullyQualifiedTableName(MetadataHelper.getFullyQualifiedTableName(name, catalog, schema));
        
        // Process the unique constraints
        table.processUniqueConstraints();
    }
    
    /**
     * INTERNAL:
     * Process a MetadataTableGenerator and add it to the project.
     */     
    public void processTableGenerator(TableGeneratorMetadata tableGenerator, String defaultCatalog, String defaultSchema) {
    	// Process the default values.
    	processTable(tableGenerator, TableSequence.defaultTableName, defaultCatalog, defaultSchema);
    	
        // Check if the table generator name uses a reserved name.
        String generatorName = tableGenerator.getGeneratorName();
        
        if (generatorName.equals(DEFAULT_SEQUENCE_GENERATOR)) {
        	throw ValidationException.tableGeneratorUsingAReservedName(DEFAULT_SEQUENCE_GENERATOR, tableGenerator.getLocation());
        } else if (generatorName.equals(DEFAULT_IDENTITY_GENERATOR)) {
        	throw ValidationException.tableGeneratorUsingAReservedName(DEFAULT_IDENTITY_GENERATOR, tableGenerator.getLocation());
        }

        TableGeneratorMetadata otherTableGenerator = getTableGenerator(generatorName);
        if (otherTableGenerator != null && ! otherTableGenerator.equals(tableGenerator)) {
        	if (tableGenerator.loadedFromAnnotations() && otherTableGenerator.loadedFromXML()) {
                // Future: Log a warning that we are ignoring this table generator.
                return;
            } else {
            	// Two generators found and they do not have all the same values.
            	throw ValidationException.conflictingTableGeneratorsSpecified(generatorName, tableGenerator.getLocation(), otherTableGenerator.getLocation());
            }        	
        }

        SequenceGeneratorMetadata otherSequenceGenerator = getSequenceGenerator(generatorName);
        if (otherSequenceGenerator != null) {
        	throw ValidationException.conflictingSequenceAndTableGeneratorsSpecified(generatorName, otherSequenceGenerator.getLocation(), tableGenerator.getLocation());        	
        }
            
        for (SequenceGeneratorMetadata sequenceGenerator : getSequenceGenerators()) {
            if (sequenceGenerator.getSequenceName().equals(tableGenerator.getPkColumnValue())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if (sequenceGenerator.getSequenceName().length() > 0) {
                	throw ValidationException.conflictingSequenceNameAndTablePkColumnValueSpecified(otherSequenceGenerator.getSequenceName(), otherSequenceGenerator.getLocation(), tableGenerator.getLocation());
                }
            }
        }
            
        // Add the table generator to the descriptor metadata.
        addTableGenerator(tableGenerator);    
    }

     /** 
      * INTERNAL:
      */
     public void setPersistenceUnitMetadata(XMLPersistenceUnitMetadata persistenceUnitMetadata) {
    	 // Future will require some for of merging/overlaying. Right now
    	 // will check for conflicts.
    	 if (m_persistenceUnitMetadata == null) {
    		 m_persistenceUnitMetadata = persistenceUnitMetadata;
    	 } else { 
    		 if (! m_persistenceUnitMetadata.equals(persistenceUnitMetadata)) {
    			 throw ValidationException.persistenceUnitMetadataConflict(m_persistenceUnitMetadata.getConflict());
    		 }
    	 }
     }
}

