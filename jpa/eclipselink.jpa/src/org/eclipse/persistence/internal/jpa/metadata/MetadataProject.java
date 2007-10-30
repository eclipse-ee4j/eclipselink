/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.NativeSequence;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.queries.EJBQLPlaceHolderQuery;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.RelationshipAccessor;

import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataAbstractConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataStructConverter;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataQueryHint;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataNamedQuery;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataNamedNativeQuery;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataNamedStoredProcedureQuery;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataStoredProcedureParameter;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataGeneratedValue;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataTableGenerator;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataSequenceGenerator;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;

import org.eclipse.persistence.queries.StoredProcedureCall;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

/**
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataProject {
    // persistence unit that is represented by this project
    protected PersistenceUnitInfo m_PUInfo;

    // Names of all the 'weavable' classes for this project.
    protected Collection<String> m_weavableClassNames = new HashSet<String>();

    // URL to Document object map of all the mapping files for this PU.
    // The reason for using URL instead of name is that name is not unique.
    protected Map<URL, Document> m_mappingFiles = new HashMap<URL, Document>();

    // The session we are currently processing for.
    protected AbstractSession m_session;

    // Boolean to specify if we should weave for value holders.
    protected boolean m_enableLazyForOneToOne;

    // Persistence unit metadata for this project.
    protected MetadataPersistenceUnit m_persistenceUnit;

    // List of mapped-superclasses found in XML for this project/persistence unit.
    protected HashMap<String, Node> m_mappedSuperclassNodes;
    protected HashMap<String, XMLHelper> m_mappedSuperclasses;

    // List of embeddables found in XML for this project/persistence unit.
    protected HashMap<String, Node> m_embeddableNodes;
    protected HashMap<String, XMLHelper> m_embeddables;

    // All the descriptors for this project.
    protected HashMap<String, MetadataDescriptor> m_allDescriptors;

    // Descriptors that have relationships.
    protected HashSet<MetadataDescriptor> m_descriptorsWithRelationships;
    
    // Descriptors that have a customizer.
    protected HashSet<MetadataDescriptor> m_descriptorsWithCustomizer;

    // Named queries for this project.
    protected HashMap<String, MetadataNamedQuery> m_namedQueries;

    // NamedNativeQueries for this project.
    protected HashMap<String, MetadataNamedNativeQuery> m_namedNativeQueries;
    
    // NamedStoredProcedureQueries for this project.
    protected HashMap<String, MetadataNamedStoredProcedureQuery> m_namedStoredProcedureQueries;

    // Sequencing metadata.
    protected HashMap<Class, MetadataGeneratedValue> m_generatedValues;
    protected HashMap<String, MetadataTableGenerator> m_tableGenerators;
    protected HashMap<String, MetadataSequenceGenerator> m_sequenceGenerators;

    // Default listeners that need to be applied to each entity in the
    // persistence unit (unless they exclude them).
    protected HashMap<XMLHelper, NodeList> m_defaultListeners;
    
    // Metadata converters, that is, TopLink converters.
    protected HashMap<String, MetadataAbstractConverter> m_converters;
    
    // Accessors that use a TopLink converter.
    protected HashSet<MetadataAccessor> m_convertAccessors;

    // MetadataStructConverters, these are StructConverters that get added to the session
    protected HashMap<String, MetadataStructConverter> m_structConverters;
    
    /**
     * INTERNAL:
     */
    public MetadataProject(PersistenceUnitInfo puInfo, AbstractSession session, boolean enableLazyForOneToOne) {
        m_PUInfo = puInfo;
        m_session = session;
        m_enableLazyForOneToOne = enableLazyForOneToOne;

        m_defaultListeners = new HashMap<XMLHelper, NodeList>();

        m_namedQueries = new HashMap<String, MetadataNamedQuery>();
        m_namedNativeQueries = new HashMap<String, MetadataNamedNativeQuery>();
        m_namedStoredProcedureQueries = new HashMap<String, MetadataNamedStoredProcedureQuery>();

        m_mappedSuperclassNodes = new HashMap<String, Node>();
        m_mappedSuperclasses = new HashMap<String, XMLHelper>();

        m_embeddableNodes = new HashMap<String, Node>();
        m_embeddables = new HashMap<String, XMLHelper>();

        m_allDescriptors = new HashMap<String, MetadataDescriptor>();
        m_descriptorsWithCustomizer = new HashSet<MetadataDescriptor>();
        m_descriptorsWithRelationships = new HashSet<MetadataDescriptor>();

        m_generatedValues = new HashMap<Class, MetadataGeneratedValue>();
        m_tableGenerators = new HashMap<String, MetadataTableGenerator>();
        m_sequenceGenerators = new HashMap<String, MetadataSequenceGenerator>();
        
        m_converters = new HashMap<String, MetadataAbstractConverter>();
        m_convertAccessors = new HashSet<MetadataAccessor>();
        m_structConverters = new HashMap<String, MetadataStructConverter>();
    }

    /**
     * INTERNAL:
     */
    public void addConverter(MetadataAbstractConverter converter) {
        m_converters.put(converter.getName(), converter);
    }
    
    /**
     * INTERNAL:
     */
    public void addConvertAccessor(MetadataAccessor accessor) {
        m_convertAccessors.add(accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addDefaultListeners(NodeList nodes, XMLHelper helper) {
        m_defaultListeners.put(helper, nodes);
    }

    /**
     * INTERNAL:
     * This method will add the descriptor to the actual TopLink project as
     * well if it has not already been added.
     */
    public void addDescriptor(MetadataDescriptor descriptor) {
        // Set the persistence unit defaults (if there are any) on the descriptor.

        if (m_persistenceUnit != null) {
            descriptor.setAccess(m_persistenceUnit.getAccess());
            descriptor.setSchema(m_persistenceUnit.getSchema());
            descriptor.setCatalog(m_persistenceUnit.getCatalog());
            descriptor.setIsCascadePersist(m_persistenceUnit.isCascadePersist());
            descriptor.setIgnoreAnnotations(m_persistenceUnit.isMetadataComplete());
        }

        Project project = getSession().getProject();
        ClassDescriptor descriptorOnProject = MetadataHelper.findDescriptor(project, descriptor.getJavaClass());

        if (descriptorOnProject == null) {
            project.addDescriptor(descriptor.getClassDescriptor());
        } else {
            descriptor.setDescriptor(descriptorOnProject);
        }

        m_allDescriptors.put(descriptor.getJavaClassName(), descriptor);
    }
    
    /**
     * INTERNAL:
     */
    public void addDescriptorWithCustomizer(MetadataDescriptor descriptor) {
        m_descriptorsWithCustomizer.add(descriptor);
    }

    /**
     * INTERNAL:
     */
    public void addGeneratedValue(MetadataGeneratedValue metadatageneratedvalue, Class entityClass) {
        m_generatedValues.put(entityClass, metadatageneratedvalue);
    }

    /**
     * INTERNAL:
     * Add a mapped-superclass that we found in an XML document.
     */
    public void addMappedSuperclass(Class mappedSuperclass, Node node, XMLHelper helper) {
        m_mappedSuperclasses.put(mappedSuperclass.getName(), helper);
        m_mappedSuperclassNodes.put(mappedSuperclass.getName(), node);
    }

    /**
     * INTERNAL:
     * Add an embeddable that we found in an XML document.
     */
    public void addEmbeddable(Class embeddable, Node node, XMLHelper helper) {
        m_embeddables.put(embeddable.getName(), helper);
        m_embeddableNodes.put(embeddable.getName(), node);
    }

    /**
     * INTERNAL:
     */
    public void addNamedNativeQuery(MetadataNamedNativeQuery namedNativeQuery) {
        m_namedNativeQueries.put(namedNativeQuery.getName(), namedNativeQuery);
    }

    /**
     * INTERNAL:
     */
    public void addNamedQuery(MetadataNamedQuery namedQuery) {
        m_namedQueries.put(namedQuery.getName(), namedQuery);
    }
    
    /**
     * INTERNAL:
     */
    public void addNamedStoredProcedureQuery(MetadataNamedStoredProcedureQuery namedStoredProcedureQuery) {
        m_namedStoredProcedureQueries.put(namedStoredProcedureQuery.getName(), namedStoredProcedureQuery);
    }

    /**
     * INTERNAL:
     */
    public void addRelationshipDescriptor(MetadataDescriptor descriptor) {
        m_descriptorsWithRelationships.add(descriptor);
    }
    
    /**
     * INTERNAL:
     */
    public void addStructConverter(MetadataStructConverter converter) {
        m_structConverters.put(converter.getName(), converter);
    }
    
    /**
     * INTERNAL:
     */
    public void addSequenceGenerator(MetadataSequenceGenerator sequenceGenerator) {
        m_sequenceGenerators.put(sequenceGenerator.getName(), sequenceGenerator);
    }

    /**
     * INTERNAL:
     */
    public void addTableGenerator(MetadataTableGenerator tableGenerator) {
        m_tableGenerators.put(tableGenerator.getName(), tableGenerator);
    }
    
    /**
     * INTERNAL:
     * This method frees up resources acquired by this object.
     */
    public void cleanup() {
        // get rid of the DOM trees.
        m_mappingFiles.clear();
    }

    /**
     * INTERNAL:
     */
    public boolean containsDescriptor(Class cls) {
        return m_allDescriptors.containsKey(cls.getName());
    }

    /**
     * INTERNAL:
     */
    public boolean enableLazyForOneToOne() {
        return m_enableLazyForOneToOne;
    }

    /**
     * INTERNAL:
     */
    public MetadataAbstractConverter getConverter(String name) {
        return m_converters.get(name);
    }
    
    /**
     * INTERNAL:
     */
    public HashMap<XMLHelper, NodeList> getDefaultListeners() {
        return m_defaultListeners;
    }

    /**
     * INTERNAL:
     */
    public MetadataDescriptor getDescriptor(Class cls) {
        if (cls == null) {
            return null;
        } else {
            MetadataDescriptor descriptor = m_allDescriptors.get(cls.getName());
            if (descriptor == null) {
                throw ValidationException.classNotListedInPersistenceUnit(cls.getName());
            } else {
                return descriptor;
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public Collection<MetadataDescriptor> getDescriptors() {
        return m_allDescriptors.values();
    }
    
    /**
     * INTERNAL:
     */
    public Set<MetadataDescriptor> getDescriptorsWithCustomizer() {
        return m_descriptorsWithCustomizer;
    }
    
    /**
     * INTERNAL:
     */
    public XMLHelper getMappedSuperclassHelper(Class cls) {
        return m_mappedSuperclasses.get(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public Node getMappedSuperclassNode(Class cls) {
        return m_mappedSuperclassNodes.get(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public Map<URL, Document> getMappingFiles() {
        return Collections.unmodifiableMap(m_mappingFiles);
    }
    
    /**
     * INTERNAL:
     */
    public XMLHelper getEmbeddableHelper(Class cls) {
        return m_embeddables.get(cls.getName());
    }

    /**
     * INTERNAL:
     */
    public Node getEmbeddableNode(Class cls) {
        return m_embeddableNodes.get(cls.getName());
    }

    /**
     * INTERNAL:
     */
    public MetadataNamedNativeQuery getNamedNativeQuery(String name) {
        return m_namedNativeQueries.get(name);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataNamedQuery getNamedQuery(String name) {
        return m_namedQueries.get(name);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataNamedStoredProcedureQuery getNamedStoredProcedureQuery(String name) {
        return m_namedStoredProcedureQueries.get(name);
    }
    
    /** 
     * INTERNAL:
     * Set the classes for processing.
     */
    public MetadataPersistenceUnit getPersistenceUnit() {
        return m_persistenceUnit;
    }
    
    /**
     * INTERNAL:
     */
    public PersistenceUnitInfo getPUInfo() {
        return m_PUInfo;
    }
    
    /**
     * INTERNAL:
     */
    public HashSet<MetadataDescriptor> getRelationshipDescriptors() {
        return m_descriptorsWithRelationships;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataSequenceGenerator getSequenceGenerator(String name) {
        return m_sequenceGenerators.get(name);
    }

    /**
     * INTERNAL:
     */
    public Collection<MetadataSequenceGenerator> getSequenceGenerators() {
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
    public MetadataStructConverter getStructConverter(String name) {
        return m_structConverters.get(name);
    }

    /**
     * INTERNAL:
     */
    public HashMap<String, MetadataStructConverter> getStructConverters(){
        return m_structConverters;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataTableGenerator getTableGenerator(String name) {
        return m_tableGenerators.get(name);
    }

    /**
     * INTERNAL:
     */
    public Collection<MetadataTableGenerator> getTableGenerators() {
        return m_tableGenerators.values();
    }
    
    /**
     * INTERNAL:
     * Returns all those classes in this project that are available for 
     * weaving. This list currently includes entity and embeddables classes.
     */
    public Collection<String> getWeavableClassNames() {
        return Collections.unmodifiableCollection(m_weavableClassNames);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasConflictingSequenceGenerator(MetadataSequenceGenerator sequenceGenerator) {
        if (hasSequenceGenerator(sequenceGenerator.getName())) {
            return ! getSequenceGenerator(sequenceGenerator.getName()).equals(sequenceGenerator);
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     */
    public boolean hasConflictingTableGenerator(MetadataTableGenerator tableGenerator) {
        if (hasTableGenerator(tableGenerator.getName())) {
            return ! getTableGenerator(tableGenerator.getName()).equals(tableGenerator);
        } else {
            return false;
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
    public boolean hasDescriptors() {
        return ! m_allDescriptors.isEmpty();
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
    public boolean hasEmbeddable(Class cls) {
        return m_embeddables.containsKey(cls.getName());
    }

    /**
     * INTERNAL:
     */
    public boolean hasNamedNativeQuery(String name) {
        return m_namedNativeQueries.containsKey(name);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasNamedQuery(String name) {
        return m_namedQueries.containsKey(name);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasNamedStoredProcedureQuery(String name) {
        return m_namedStoredProcedureQueries.containsKey(name);
    }
    
    /** 
     * INTERNAL:
     * Set the classes for processing.
     */
    public boolean hasPersistenceUnit() {
        return m_persistenceUnit != null;
    }

    /**
     * INTERNAL:
     */
    public boolean hasSequenceGenerator(String name) {
        return getSequenceGenerator(name) != null;
    }

    /**
     * INTERNAL:
     */
    public boolean hasStructConverter(String name) {
        return m_structConverters.containsKey(name);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasTableGenerator(String name) {
        return getTableGenerator(name) != null;
    }

    /**
     * INTERNAL:
     * 
     * Stage 2 processing. That is, it does all the extra processing that 
     * couldn't be completed in the original metadata accessor processing.
	 */
	public void process() {
        processConvertAccessors();
        
        processSequencing();
        
        processRelationshipDescriptors();
    }

    /**
     * INTERNAL:
     * Process those accessors that are decorated with a @Convert annotation. 
     * A @Convert is used to process a TopLink defined annotation converter 
     * (@Converter, @TypeConverter and @ObjectTypeConverter) for an accessor's 
     * database mapping.
     */
    protected void processConvertAccessors() {
        for (MetadataAccessor accessor : m_convertAccessors) {
            accessor.processConvert();
        }
    }
    
    /**
     * INTERNAL:
     * Process the named native queries we found and add them to the given
     * session.
     */
    public void processNamedNativeQueries(ClassLoader loader) {
        for (MetadataNamedNativeQuery query : m_namedNativeQueries.values()) {
            HashMap<String, String> hints = processQueryHints(query.getHints(), query.getName());

            Class resultClass = query.getResultClass();

            if (resultClass != void.class) {
                resultClass = MetadataHelper.getClassForName(resultClass.getName(), loader);
                m_session.addQuery(query.getName(), EJBQueryImpl.buildSQLDatabaseQuery(resultClass, query.getEJBQLString(), hints));
            } else { 
                String resultSetMapping = query.getResultSetMapping();

                if (! resultSetMapping.equals("")) {
                    m_session.addQuery(query.getName(), EJBQueryImpl.buildSQLDatabaseQuery(resultSetMapping, query.getEJBQLString(), hints));
                } else {
                    // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                    m_session.addQuery(query.getName(), EJBQueryImpl.buildSQLDatabaseQuery(query.getEJBQLString(), hints));
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Process the named queries we found and add them to the given session.
     */
    public void processNamedQueries(MetadataValidator validator) {
        for (MetadataNamedQuery query : m_namedQueries.values()) {
            try {
                HashMap<String, String> hints = processQueryHints(query.getHints(), query.getName());
                m_session.addEjbqlPlaceHolderQuery(new EJBQLPlaceHolderQuery(query.getName(), query.getEJBQLString(), hints));
            } catch (Exception exception) {
                validator.throwErrorProcessingNamedQueryAnnotation(query.getClass(), query.getName(), exception);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the named stored procedure queries we found and add them to the given session.
     */
    public void processNamedStoredProcedureQueries(MetadataValidator validator, ClassLoader loader) {
        for (MetadataNamedStoredProcedureQuery query : m_namedStoredProcedureQueries.values()) {
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
     * Process a list of MetadataQueryHint.
     */	
    protected HashMap<String, String> processQueryHints(List<MetadataQueryHint> hints, String queryName) {
        HashMap<String, String> hm = new HashMap<String, String>();

        for (MetadataQueryHint hint : hints) {
            QueryHintsHandler.verify(hint.getName(), hint.getValue(), queryName, m_session);
            hm.put(hint.getName(), hint.getValue());
        } 
		
        return hm;
    } 
    
    /**
     * INTERNAL:
     * Process the related descriptors.
     */
    protected void processRelationshipDescriptors() {
        for (MetadataDescriptor descriptor : getRelationshipDescriptors()) {
            for (RelationshipAccessor accessor : descriptor.getRelationshipAccessors()) {
                accessor.processRelationship();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the sequencing information.
     */
    protected void processSequencing() {
        if (! m_generatedValues.isEmpty()) {
            DatasourceLogin login = m_session.getProject().getLogin();
            
            // Generators referenced from Id should have correct type
            for (MetadataGeneratedValue generatedValue : m_generatedValues.values()) {
                String type = generatedValue.getStrategy();
                String generatorName = generatedValue.getGenerator();
                
                if (type.equals(MetadataConstants.TABLE)) {
                    MetadataSequenceGenerator sequenceGenerator = m_sequenceGenerators.get(generatorName);
                    
                    if (sequenceGenerator != null) {
                        // WIP
                    }
                } else if (type.equals(MetadataConstants.SEQUENCE) || type.equals(MetadataConstants.IDENTITY)) {
                    MetadataTableGenerator tableGenerator = m_tableGenerators.get(generatorName);
                    
                    if (tableGenerator != null) {
                        // WIP
                    }
                }
            }
    
            Sequence defaultAutoSequence = null;
            TableSequence defaultTableSequence = new TableSequence(MetadataConstants.DEFAULT_TABLE_GENERATOR);
            NativeSequence defaultNativeSequence = new NativeSequence(MetadataConstants.DEFAULT_SEQUENCE_GENERATOR);
            
            // Sequences keyed on generator names.
            Hashtable<String, Sequence> sequences = new Hashtable<String, Sequence>();
            
            for (MetadataSequenceGenerator sequenceGenerator : m_sequenceGenerators.values()) {
                String sequenceGeneratorName = sequenceGenerator.getName();
                String seqName = (sequenceGenerator.getSequenceName().equals("")) ? sequenceGeneratorName : sequenceGenerator.getSequenceName();
                NativeSequence sequence = new NativeSequence(seqName, sequenceGenerator.getAllocationSize());
                sequences.put(sequenceGeneratorName, sequence);
                
                if (sequenceGeneratorName.equals(MetadataConstants.DEFAULT_AUTO_GENERATOR)) {
                    // SequenceGenerator defined with DEFAULT_AUTO_GENERATOR.
                    // The sequence it defines will be used as a defaultSequence.
                    defaultAutoSequence = sequence;
                } else if (sequenceGeneratorName.equals(MetadataConstants.DEFAULT_SEQUENCE_GENERATOR)) {
                    // SequenceGenerator deinfed with DEFAULT_SEQUENCE_GENERATOR.
                    // All sequences of GeneratorType SEQUENCE and IDENTITY 
                    // referencing non-defined generators will use a clone of 
                    // the sequence defined by this generator.
                    defaultNativeSequence = sequence;
                }
            }

            for (MetadataTableGenerator tableGenerator : m_tableGenerators.values()) {
                String tableGeneratorName = tableGenerator.getName();
                String seqName = (tableGenerator.getPkColumnValue().equals("")) ? tableGeneratorName : tableGenerator.getPkColumnValue();
                TableSequence sequence = new TableSequence(seqName, tableGenerator.getAllocationSize(), tableGenerator.getInitialValue());
                sequences.put(tableGeneratorName, sequence);

                //bug 2647: pull schema and catalog defaults from the persistence Unit if they are not defined.  
                String catalogName = tableGenerator.getCatalog();
                String schemaName = tableGenerator.getSchema();
                if (this.getPersistenceUnit()!=null){
                    catalogName = catalogName.length()>0? catalogName: this.getPersistenceUnit().getCatalog();
                    schemaName = schemaName.length()>0? schemaName: this.getPersistenceUnit().getSchema();
                }

                // Get the database table from the @TableGenerator values.
                // In case tableGenerator.table().equals("") default sequence 
                // table name will be extracted from sequence and used, see 
                // TableSequence class.
                sequence.setTable(new DatabaseTable(MetadataHelper.getFullyQualifiedTableName(tableGenerator.getTable(), sequence.getTableName(), catalogName, schemaName)));
                
                // Process the @UniqueConstraints for this table.
                for (String[] uniqueConstraint : tableGenerator.getUniqueConstraints()) {
                    sequence.getTable().addUniqueConstraints(uniqueConstraint);
                }
                
                if (! tableGenerator.getPkColumnName().equals("")) {
                    sequence.setNameFieldName(tableGenerator.getPkColumnName());
                }
                    
                if (! tableGenerator.getValueColumnName().equals("")) {
                    sequence.setCounterFieldName(tableGenerator.getValueColumnName());
                }

                if (tableGeneratorName.equals(MetadataConstants.DEFAULT_AUTO_GENERATOR)) {
                    // TableGenerator defined with DEFAULT_AUTO_GENERATOR.
                    // The sequence it defines will be used as a defaultSequence.
                    defaultAutoSequence = sequence;
                } else if (tableGeneratorName.equals(MetadataConstants.DEFAULT_TABLE_GENERATOR)) {
                    // SequenceGenerator defined with DEFAULT_TABLE_GENERATOR. 
                    // All sequences of GenerationType TABLE referencing non-
                    // defined generators will use a clone of the sequence 
                    // defined by this generator.
                    defaultTableSequence = sequence;
                }
            }

            // Finally loop through descriptors and set sequences as required into 
            // Descriptors and Login
            for (Class entityClass : m_generatedValues.keySet()) {
                MetadataDescriptor descriptor = m_allDescriptors.get(entityClass.getName());
                MetadataGeneratedValue generatedValue = m_generatedValues.get(entityClass);
                String generatorName = generatedValue.getGenerator();
                Sequence sequence = null;

                if (! generatorName.equals("")) {
                    sequence = sequences.get(generatorName);
                }
                
                if (sequence == null) {
                    if (generatedValue.getStrategy().equals(MetadataConstants.TABLE)) {
                        if (generatorName.equals("")) {
                            sequence = defaultTableSequence;
                        } else {
                            sequence = (Sequence)defaultTableSequence.clone();
                            sequence.setName(generatorName);
                        }
                    } else if (generatedValue.getStrategy().equals(MetadataConstants.SEQUENCE) || generatedValue.getStrategy().equals(MetadataConstants.IDENTITY)) {
                        if (generatorName.equals("")) {
                            sequence = defaultNativeSequence;
                        } else {
                            sequence = (Sequence)defaultNativeSequence.clone();
                            sequence.setName(generatorName);
                        }
                    }
                }

                if (sequence != null) {
                    descriptor.setSequenceNumberName(sequence.getName());
                    login.addSequence(sequence);
                } else if (generatedValue.getStrategy().equals(MetadataConstants.AUTO)) {
                    if (defaultAutoSequence != null) {
                        descriptor.setSequenceNumberName(defaultAutoSequence.getName());
                        login.setDefaultSequence(defaultAutoSequence);
                    } else {
                        descriptor.setSequenceNumberName(MetadataConstants.DEFAULT_AUTO_GENERATOR);
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     *
     * Process the stored procedure parameters on the given stored procedure
     * query.
     */
     protected List<String> processStoredProcedureParameters(MetadataNamedStoredProcedureQuery query, StoredProcedureCall call) {
        List<String> queryArguments = new ArrayList<String>();
        
        for (MetadataStoredProcedureParameter parameter : query.getProcedureParameters()) {
            // Process the query parameter
            String argumentFieldName = parameter.getQueryParameter();
                    
            // Process the procedure parameter name, defaults to the 
            // argument field name.
            String procedureParameterName = parameter.getName();
            if (procedureParameterName.equals("")) {
                // WIP we should log a warning ...
                procedureParameterName = argumentFieldName;
            }
                    
            // Process the parameter direction
            String direction = parameter.getDirection();
            if (direction.equals(MetadataConstants.IN)) {
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
            } else if (direction.equals(MetadataConstants.OUT)) {
                if (parameter.hasType()) {
                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getType());
                } else if (parameter.hasJdbcType() && parameter.hasJdbcTypeName()) {
                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType(), parameter.getJdbcTypeName());
                } else if (parameter.hasJdbcType()) {
                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName, parameter.getJdbcType());
                } else {
                    call.addNamedOutputArgument(procedureParameterName, argumentFieldName);
                }
            } else if (direction.equals(MetadataConstants.IN_OUT)) {
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
        
        return queryArguments;
    }
    
    /**
     * INTERNAL:
     */
    public void setMappingFiles(Map<URL, Document> mappingFiles) {
        m_mappingFiles.clear();
        m_mappingFiles.putAll(mappingFiles);
    }

    /** 
     * INTERNAL:
     * Set the classes for processing.
     */
    public void setPersistenceUnit(MetadataPersistenceUnit persistenceUnit) {
        m_persistenceUnit = persistenceUnit;
    }
    
    /**
     * INTERNAL:
     */
    public void setWeavableClassNames(Collection<String> weavableClassNames) {
        m_weavableClassNames.clear();
        m_weavableClassNames.addAll(weavableClassNames);
    }
}

