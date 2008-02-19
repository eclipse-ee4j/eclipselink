/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MappedSuperclassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.queries.EntityResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

/**
 * Object to hold onto the XML entity mappings metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappings {
	private ClassLoader m_loader;
	
	private List<ClassAccessor> m_entities;
	private List<ConverterMetadata> m_converters;
	private List<EmbeddableAccessor> m_embeddables;
	private List<MappedSuperclassAccessor> m_mappedSuperclasses;
	private List<NamedNativeQueryMetadata> m_namedNativeQueries;
	private List<NamedQueryMetadata> m_namedQueries;
	private List<ObjectTypeConverterMetadata> m_objectTypeConverters;
	private List<SequenceGeneratorMetadata> m_sequenceGenerators;
	private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings;
	private List<StructConverterMetadata> m_structConverters;
	private List<TableGeneratorMetadata> m_tableGenerators;
	private List<TypeConverterMetadata> m_typeConverters;
	
	private MetadataProject m_project;
	
	private String m_access;
	private String m_catalog;
	private String m_description; // Currently don't do anything with this.
	private String m_package;
	private String m_schema;
	private String m_version;
	
	private URL m_mappingFileURL;
	private XMLPersistenceUnitMetadata m_persistenceUnitMetadata;
	
	/**
	 * INTERNAL:
	 */
	public XMLEntityMappings() {}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getAccess() {
		return m_access;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getCatalog() {
		return m_catalog;
	}
	
	/**
     * INTERNAL:
     * This will initialize the given classname, and append the default
     * package if necessary. If the className is null or blank, this method 
     * will return void.class.
     */
	public Class getClassForName(String className) {
		if (className == null || className.equals("")) {
			return void.class;
		} else if (className.equalsIgnoreCase("Boolean")) {
			return Boolean.class;
		} else if (className.equalsIgnoreCase("Byte")) {
			return Byte.class;
		} else if (className.equalsIgnoreCase("Character")) {
			return Character.class;
		} else if (className.equalsIgnoreCase("Double")) {
			return Double.class;
		} else if (className.equalsIgnoreCase("Float")) {
			return Float.class;
		} else if (className.equalsIgnoreCase("Integer")) {
			return Integer.class;
		} else if (className.equalsIgnoreCase("Long")) {
			return Long.class;
		} else if (className.equalsIgnoreCase("Number")) {
			return Number.class;
		} else if (className.equalsIgnoreCase("Short")) {
			return Short.class;
		} else if (className.equalsIgnoreCase("String")) {
			return String.class;
		} else {
			return MetadataHelper.getClassForName(getFullyQualifiedClassName(className), m_loader);
		}
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public List<ConverterMetadata> getConverters() {
		return m_converters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getDescription() {
		return m_description;
	}
	
	/**
	 * INTERNAL:
	 * Returns the default catalog. Either from entity-mappings or persistence
	 * unit defaults. Note, null could be returned.
	 */
	protected String getDefaultCatalog() {
		if (m_catalog == null) {
			if (m_project.getPersistenceUnitMetadata() == null) {
				return null;
			} else {
				return m_project.getPersistenceUnitMetadata().getCatalog();
			}
		} else {
			return m_catalog;
		}
	}
	
	/**
	 * INTERNAL:
	 * Returns the default schema. Either from entity-mappings or persistence
	 * unit defaults. Note, null could be returned.
	 */
	protected String getDefaultSchema() {
		if (m_schema == null) {
			if (m_project.getPersistenceUnitMetadata() == null) {
				return null;
			} else {
				return m_project.getPersistenceUnitMetadata().getSchema();
			}
		} else {
			return m_schema;
		}
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<EmbeddableAccessor> getEmbeddables() {
		return m_embeddables;
	}
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<ClassAccessor> getEntities() {
		return m_entities;
	}
	
    /**
     * INTERNAL:
     * This convenience method will attempt to fully qualify a class name if 
     * required. This assumes that the className value is non-null, and a 
     * "qualified" class name contains at least one '.'
     * Future: What about Enum support? Employee.Enum currently would not
     * qualify with the package since there is a dot.
     */
    public String getFullyQualifiedClassName(String className) {
        // If there is no global package defined or the class name is qualified, 
    	// return className
        if (m_package.equals("")) {
            return className;
        } else if (className.indexOf(".") > -1) {
        	return className;
        } else {
        	// Prepend the package to the class name
        	// Format of global package is "foo.bar."
        	if (m_package.endsWith(".")) {
        		return m_package + className;
        	} else {
        		// Format of global package is "foo.bar"
        		return m_package + "." + className;
        	}
        }
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<MappedSuperclassAccessor> getMappedSuperclasses() {
		return m_mappedSuperclasses;
	}
	
	/**
	 * INTERNAL:
	 */
	public URL getMappingFile() {
		return m_mappingFileURL;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getMappingFileName() {
		return m_mappingFileURL.toString();
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<NamedNativeQueryMetadata> getNamedNativeQueries() {
		return m_namedNativeQueries;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<NamedQueryMetadata> getNamedQueries() {
		return m_namedQueries;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public List<ObjectTypeConverterMetadata> getObjectTypeConverters() {
		return m_objectTypeConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getPackage() {
		return m_package;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public XMLPersistenceUnitMetadata getPersistenceUnitMetadata() {
		return m_persistenceUnitMetadata;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getSchema() {
		return m_schema;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<SequenceGeneratorMetadata> getSequenceGenerators() {
		return m_sequenceGenerators;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<SQLResultSetMappingMetadata> getSqlResultSetMappings() {
		return m_sqlResultSetMappings;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<StructConverterMetadata> getStructConverters() {
		return m_structConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<TableGeneratorMetadata> getTableGenerators() {
		return m_tableGenerators;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public List<TypeConverterMetadata> getTypeConverters() {
		return m_typeConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getVersion() {
		return m_version;
	}
	
	/**
	 * INTERNAL:
	 */
	public void initPersistenceUnitClasses(MetadataProject project) {
    	// Process the entities
    	for (ClassAccessor entity : getEntities()) {
    		// Initialize the class with the package from entity mappings.
    		Class entityClass = getClassForName(entity.getClassName());
    		entity.init(new MetadataClass(entityClass), new MetadataDescriptor(entityClass, entity), project, this);
    		
    		// Add it to the project.
    		project.addClassAccessor(entity);
    		
    		// Set any entity-mappings default if available. This must be 
    		// done after the accessor has been added to the project since
    		// global PU metadata defaults get applied then and we'll need 
    		// to override them where necessary.
    		processEntityMappingsDefaults(entity);
    	}
        
    	// Process the embeddables.
		for (EmbeddableAccessor embeddable : getEmbeddables()) {
			// Initialize the class with the package from entity mappings.
			Class embeddableClass = getClassForName(embeddable.getClassName());
			embeddable.init(new MetadataClass(embeddableClass), new MetadataDescriptor(embeddableClass, embeddable), project, this);
			
			// Add it to the project.
			project.addEmbeddableAccessor(embeddable);
			
			// Set any entity-mappings default if available. This must be 
    		// done after the accessor has been added to the project since
    		// global PU metadata defaults get applied then and we'll need 
    		// to override them where necessary.
			processEntityMappingsDefaults(embeddable);
		}
		
		// Process the mapped superclasses
		for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
			// Set the mapping file name (used in error/logging messages)
			mappedSuperclass.setMappingFile(getMappingFileName());
			
			// Set a reference back to this entity-mappings on the class
			// accessor. It may need to load some classes at some point
			// and we want to ensure any loading is done via the 
			// entity-mappings to ensure package is taken into 
			// consideration.
			mappedSuperclass.setEntityMappings(this);
			
			// Initialize the class with the package from entity mappings.
			String mappedSuperclassClassName = getClassForName(mappedSuperclass.getClassName()).getName();
			
			// Add it to the project.
			project.addMappedSuperclass(mappedSuperclassClassName, mappedSuperclass);
		}
	}
	
	/**
	 * INTERNAL:
	 */
	public void process(MetadataProject project) { 
		m_project = project;
		
		// Process the XML converters
		processConverters(m_converters);
		
		// Process the XML type converters
		processTypeConverters(m_typeConverters);
		
		// Process the XML object type converters
		processObjectTypeConverters(m_objectTypeConverters);
		
		// Process the XML struct converters
		processStructConverters(m_structConverters);
		
        // Process the XML table generators.
		for (TableGeneratorMetadata tableGenerator : m_tableGenerators) {
			processTableGenerator(tableGenerator, getDefaultCatalog(), getDefaultSchema(), getMappingFileName());
		}
            
        // Process the XML sequence generators.
		for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators) {
			processSequenceGenerator(sequenceGenerator, getMappingFileName());
    	}
            
        // Process the XML named queries.
    	processNamedQueries(m_namedQueries, getMappingFileName());

        // Process the XML named native queries.
    	processNamedNativeQueries(m_namedNativeQueries, getMappingFileName());
            
        // Process the XML sql result set mappings.
    	processSqlResultSetMappings(m_sqlResultSetMappings);

        // Process the entities from this mapping file.
        for (ClassAccessor entity : m_entities) {
        	// It may have been fast tracked ...
        	if (! entity.isProcessed()) {
        		// Tell the entity to process itself ...
        		entity.process();
        		
        		// Once it's done, set the flag to processed to avoid multiple
        		// processing of the same entity.
        		entity.setIsProcessed();
        	}
        }
        
        // Mapped super classes and embeddables are processed through discovery
        // when processing the entities.
	}
    	
	/**
	 * INTERNAL:
	 * Process the converters. MetadataAccessor will also call this method 
	 * which will ensure we initialize our converters in a common way.
	 */
    public void processConverters(List<ConverterMetadata> converters) {
    	for (ConverterMetadata converter : converters) {
    		// Set the location of this converter.
    		converter.setLocation(getMappingFileName());

    		// Set the fully qualified class name on the converter. 
    		converter.setClassName(getFullyQualifiedClassName(converter.getClassName()));
    		
    		// Ask the project to process what we found.
    		m_project.processConverter(converter);
    	}
    }
	
	/**
	 * INTERNAL:
	 */
	protected void processEntityMappingsDefaults(ClassAccessor accessor) {
		MetadataDescriptor descriptor = accessor.getDescriptor();
		
		// Set the access type if specified. Look for one set on the class
		// accessor first, else set the entity-mappings access if specified.
		if (accessor.getAccess() != null) {
			descriptor.setXMLAccess(accessor.getAccess());
		} else if (m_access != null) {
			descriptor.setXMLAccess(m_access);
		} else {
			// If there is an access specified in the persistence unit
			// default then it should already be set.
		}
		
		// Set the entity-mappings catalog if specified.        		
		if (m_catalog != null) {
			descriptor.setXMLCatalog(m_catalog);
		}
		
		// Set the entity-mappings schema if specified.
		if (m_schema != null) {
			descriptor.setXMLSchema(m_schema);
		}
		
		// Set the metadata-complete value from the accessor if set.
		// You must do the check as to not un-due a global default setting.
		if (accessor.isMetadataComplete()) {
			descriptor.setIgnoreAnnotations(true);
		}
		
		// Set the mapping file name (used in error/logging messages)
		accessor.setMappingFile(getMappingFileName());

		// Set a reference back to this entity-mappings on the class
		// accessor. It may need to load some classes at some point
		// and we want to ensure any loading is done via the 
		// entity-mappings to ensure package is taken into 
		// consideration.
		accessor.setEntityMappings(this);
	}
	
	/**
	 * INTERNAL:
	 * Process the named native queries. ClassAccessor will also call this 
	 * method which will ensure we initialize our named native queries in a 
	 * common way.
	 */
    public void processNamedNativeQueries(List<NamedNativeQueryMetadata> namedNativeQueries, String location) {
    	for (NamedNativeQueryMetadata namedNativeQuery : namedNativeQueries) {
   			// Set the location of this named query.
   			namedNativeQuery.setLocation(location);
    			
   			// Initialize the result class if specified (defaults to void.class)
   			namedNativeQuery.setResultClass(getClassForName(namedNativeQuery.getResultClassName()));
    		
   			// Ask the project to process what we found.
   			m_project.processNamedNativeQuery(namedNativeQuery);
   		}
    }
    
	/**
	 * INTERNAL:
	 * Process the named queries. ClassAccessor will also call this method which 
	 * will ensure we initialize our named queries in a common way.
	 */
    public void processNamedQueries(List<NamedQueryMetadata> namedQueries, String location) {
    	for (NamedQueryMetadata namedQuery : namedQueries) {
    		// Set the location of this named query.
    		namedQuery.setLocation(location);
    		
    		// Ask the project to process what we found.
    		m_project.processNamedQuery(namedQuery);
    	}
    }
    
	/**
	 * INTERNAL:
	 * Process the object type converters. MetadataAccessor will also 
	 * call this method which will ensure we initialize our converters in a 
	 * common way.
	 */
    public void processObjectTypeConverters(List<ObjectTypeConverterMetadata> objectTypeConverters) {
    	for (TypeConverterMetadata objectTypeConverter : objectTypeConverters) {
    		// Set the location of this object type converter.
    		objectTypeConverter.setLocation(getMappingFileName());

    		// Initialize the data and object types if specified (both default 
    		// to void.class otherwise)
    		objectTypeConverter.setDataType(getClassForName(objectTypeConverter.getDataTypeName()));
    		objectTypeConverter.setObjectType(getClassForName(objectTypeConverter.getObjectTypeName()));
    		
    		// Ask the project to process what we found.
    		m_project.processConverter(objectTypeConverter);
    	}
    }
    
	/**
	 * INTERNAL:
	 * Process the persistence metadata if specified. Any conflicts in elements 
     * defined in multiple documents will cause an exception to be thrown. The 
     * first instance encountered wins, i.e. any conflicts between PU metadata 
     * definitions in multiple instance documents will cause an exception to be 
     * thrown. The one exception to this rule is default listeners: all default 
     * listeners found will be added to a list in the order that they are read 
     * from the instance document(s). 
	 */
	public void processPersistenceUnitMetadata(MetadataProject project) {
		if (m_persistenceUnitMetadata != null) {
			// This method will take care of any merging or conflicts and
			// throw an exception if necessary.
			project.setPersistenceUnitMetadata(m_persistenceUnitMetadata);

			// Process the default entity-listeners. No conflict checking will 
			// be done, that is, any and all default listeners specified across
			// the persistence unit will be added to the project.
			for (EntityListenerMetadata defaultListener : m_persistenceUnitMetadata.getDefaultListeners()) {
				project.addDefaultListener(defaultListener);
			}
		}
	}
    
    /**
     * INTERNAL: 
	 * Process the sequence generator. Called from NonRelationshipAccessor to 
	 * ensure we have common initialization of sequence generators.
     */
    public void processSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator, String location) {
    	// Set the location of this sequence generator.
        sequenceGenerator.setLocation(location);
        	
        // Ask the project to process what we found.
        m_project.processSequenceGenerator(sequenceGenerator);
    }
    
	/**
	 * INTERNAL:
	 * Process the SqlResultSetMapping. ClassAccessor will also call this method 
	 * since we need to initialize the entity class for every entity result.
	 */
    public void processSqlResultSetMappings(List<SQLResultSetMappingMetadata> sqlResultSetMappings) {
    	for (SQLResultSetMappingMetadata sqlResultSetMapping : sqlResultSetMappings) {
    		// Initialize the entity class for every entity result.
    		if (sqlResultSetMapping.hasEntityResults()) {
    			for (EntityResultMetadata entityResult : sqlResultSetMapping.getEntityResults()) {
    				entityResult.setEntityClass(getClassForName(entityResult.getEntityClassName()));
    			}
    		}
    			
    		// Current processing is last one in win, may want to 
    		// enhance that like we do with named queries.
    		// Ask the project to process what we found.
    		m_project.processSqlResultSetMapping(sqlResultSetMapping);
    	}
    }
    
	/**
	 * INTERNAL:
	 * Process the struct converters. MetadataAccessor will also call this 
	 * method which will ensure we initialize our converters in a common way.
	 */
    public void processStructConverters(List<StructConverterMetadata> structConverters) {
    	for (StructConverterMetadata structConverter : structConverters) {
    		// Set the location of this struct converter.
    		structConverter.setLocation(getMappingFileName());
    		
    		// Ask the project to process what we found.
    		m_project.processStructConverter(structConverter);
    	}
    }
    
    /**
     * INTERNAL: 
	 * Process the table generators. Called from NonRelationshipAccessor to 
	 * ensure we have common initialization of table generators.
	 */
    public void processTableGenerator(TableGeneratorMetadata tableGenerator, String defaultCatalog, String defaultSchema, String location) {
    	// Set the location of this sequence generator.
		tableGenerator.setLocation(location);
		    	
		// Ask the project to process what we found.
		m_project.processTableGenerator(tableGenerator, defaultCatalog, defaultSchema);
    }
    
	/**
	 * INTERNAL:
	 * Process the type converters. MetadataAccessor will also call this method 
	 * which will ensure we initialize our converters in a common way.
	 */
    public void processTypeConverters(List<TypeConverterMetadata> typeConverters) {
    	for (TypeConverterMetadata typeConverter : typeConverters) {
    		// Set the location of this type converter.
    		typeConverter.setLocation(getMappingFileName());

   			// Initialize the data and object types if specified (both default 
    		// to void.class otherwise)
    		typeConverter.setDataType(getClassForName(typeConverter.getDataTypeName()));
    		typeConverter.setObjectType(getClassForName(typeConverter.getObjectTypeName()));
    		
    		// Ask the project to process what we found.
    		m_project.processConverter(typeConverter);
    	}
    }
    
    /**
     * INTERNAL:
     * We clone/reload a mapped-superclass by writing it out to XML and 
     * reload it through OX.
     */
    public MappedSuperclassAccessor reloadMappedSuperclass(MappedSuperclassAccessor accessor, MetadataDescriptor descriptor, ClassLoader loader) {
    	try {
    		// Create entity mappings object to write out.
    		XMLEntityMappings entityMappingsOut = new XMLEntityMappings();
    		entityMappingsOut.setVersion(getVersion());
    		ArrayList list = new ArrayList();
    		list.add(accessor);
    		entityMappingsOut.setMappedSuperclasses(list);

    		// Create a temp file, write it out, read it back in and delete.
    		File file = new File("tempToDelete.xml");
    		XMLEntityMappingsWriter.write(entityMappingsOut, file.toURI());
    		XMLEntityMappings entityMappings = XMLEntityMappingsReader.read(file.toURL(), loader);
    		file.delete();
        	
    		// Initialize the newly loaded/built mapped superclass
    		MappedSuperclassAccessor mappedSuperclass = entityMappings.getMappedSuperclasses().get(0);
   			Class mappedSuperclassClass = getClassForName(mappedSuperclass.getClassName());   
    		mappedSuperclass.setMappingFile(getMappingFileName());
    		mappedSuperclass.setEntityMappings(this);
    		mappedSuperclass.init(new MetadataClass(mappedSuperclassClass), descriptor, m_project, this);
    		
    		return mappedSuperclass;
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    		// Future: Throw an EclipseLink exception.
    	}
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setAccess(String access) {
		m_access = access;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setCatalog(String catalog) {
		m_catalog = catalog;
	}

	/**
     * INTERNAL:
     * Used for OX mapping
     */
	public void setConverters(List<ConverterMetadata> converters) {
		m_converters = converters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setDescription(String description) {
		m_description = description;
	}

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setEmbeddables(List<EmbeddableAccessor> embeddables) {
		m_embeddables = embeddables;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setEntities(List<ClassAccessor> entities) {
		m_entities = entities;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setLoader(ClassLoader loader) {
		m_loader = loader;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setMappedSuperclasses(List<MappedSuperclassAccessor> mappedSuperclasses) {
		m_mappedSuperclasses = mappedSuperclasses;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setMappingFile(URL mappingFileURL) {
		m_mappingFileURL = mappingFileURL;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setNamedNativeQueries(List<NamedNativeQueryMetadata> namedNativeQueries) {
		m_namedNativeQueries = namedNativeQueries;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setNamedQueries(List<NamedQueryMetadata> namedQueries) {
		m_namedQueries = namedQueries;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public void setObjectTypeConverters(List<ObjectTypeConverterMetadata> objectTypeConverters) {
		m_objectTypeConverters = objectTypeConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping. Null value will set "".
	 */
	public void setPackage(String pkg) {
		m_package = pkg;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setPersistenceUnitMetadata(XMLPersistenceUnitMetadata persistenceUnitMetadata) {
		m_persistenceUnitMetadata = persistenceUnitMetadata;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setSchema(String schema) {
		m_schema = schema;
	}

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setSequenceGenerators(List<SequenceGeneratorMetadata> sequenceGenerators) {
		m_sequenceGenerators = sequenceGenerators;
	}

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setSqlResultSetMappings(List<SQLResultSetMappingMetadata> sqlResultSetMappings) {
		m_sqlResultSetMappings = sqlResultSetMappings;
	}

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setStructConverters(List<StructConverterMetadata> structConverters) {
		m_structConverters = structConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setTableGenerators(List<TableGeneratorMetadata> tableGenerators) {
		m_tableGenerators = tableGenerators;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public void setTypeConverters(List<TypeConverterMetadata> typeConverters) {
		m_typeConverters = typeConverters;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setVersion(String version) {
		m_version = version;
	}
}
