/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *       - 218084: Implement metadata merging functionality between mapping file
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFile;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * Object to hold onto the XML entity mappings metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappings extends ORMetadata {
    private boolean m_isEclipseLinkORMFile;
    
    private ClassLoader m_loader;
    
    private List<EntityAccessor> m_entities;
    private List<ConverterMetadata> m_converters;
    private List<EmbeddableAccessor> m_embeddables;
    private List<MappedSuperclassAccessor> m_mappedSuperclasses;
    private List<NamedNativeQueryMetadata> m_namedNativeQueries;
    private List<NamedQueryMetadata> m_namedQueries;
    private List<NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries;
    private List<ObjectTypeConverterMetadata> m_objectTypeConverters;
    private List<SequenceGeneratorMetadata> m_sequenceGenerators;
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings;
    private List<StructConverterMetadata> m_structConverters;
    private List<TableGeneratorMetadata> m_tableGenerators;
    private List<TypeConverterMetadata> m_typeConverters;
    
    private MetadataFactory m_factory;
    private MetadataFile m_file;
    private MetadataProject m_project;
    
    private String m_access;
    private String m_catalog;
    private String m_description; // Currently don't do anything with this.
    private String m_package;
    private String m_schema;
    private String m_version;
    
    private String m_mappingFileNameOrURL;
    private XMLPersistenceUnitMetadata m_persistenceUnitMetadata;
    
    /**
     * INTERNAL:
     */
    public XMLEntityMappings() {
        super("<entity-mappings>");
        m_isEclipseLinkORMFile = false;
    }
    
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
     * INTERNAL: XMLEntityMappings calls this one
     * Load a class from a given class name.
     */
    protected Class getClassForName(String classname, ClassLoader loader) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class) AccessController.doPrivileged(new PrivilegedClassForName(classname, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(classname, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.getClassForName(classname, true, loader);
            }
        } catch (Exception exception) {
            throw ValidationException.unableToLoadClass(classname, exception);
        }
    }
    
    /**
     * INTERNAL:
     * This will initialize the given classname, and append the default
     * package if necessary. If the className is null or blank, this method 
     * will return "void".
     */
    public String getFullClassName(String className) {
        if (className == null || className.equals("")) {
            return "void";
        } else if (className.equalsIgnoreCase("Boolean")) {
            return "java.lang.Boolean";
        } else if (className.equalsIgnoreCase("Byte")) {
            return "java.lang.Byte";
        } else if (className.equalsIgnoreCase("Character")) {
            return "java.lang.Character";
        } else if (className.equalsIgnoreCase("Double")) {
            return "java.lang.Double";
        } else if (className.equalsIgnoreCase("Float")) {
            return "java.lang.Float";
        } else if (className.equalsIgnoreCase("Integer")) {
            return "java.lang.Integer";
        } else if (className.equalsIgnoreCase("Long")) {
            return "java.lang.Long";
        } else if (className.equalsIgnoreCase("Number")) {
            return "java.lang.Number";
        } else if (className.equalsIgnoreCase("Short")) {
            return "java.lang.Short";
        } else if (className.equalsIgnoreCase("String")) {
            return "java.lang.String";
        } else {
            return getFullyQualifiedClassName(className);
        }
    }
    
    /**
     * INTERNAL:
     * This will initialize the given classname, and append the default
     * package if necessary. If the className is null or blank, this method 
     * will return void.class.
     */
    public Class getClassForName(String className) {
        return getClassForName(getFullyQualifiedClassName(className), m_loader);
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
    public List<EntityAccessor> getEntities() {
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
        if (m_package == null || m_package.equals("")) {
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
     * Return the logger from the project. 
     */
    public MetadataLogger getLogger() {
        return m_project.getLogger();
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
    public String getMappingFileOrURL() {
        return m_mappingFileNameOrURL;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataFactory getMetadataFactory() {
        return m_factory;
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
    public List<NamedStoredProcedureQueryMetadata> getNamedStoredProcedureQueries() {
        return m_namedStoredProcedureQueries;
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
     */
    public MetadataProject getProject() {
        return m_project;
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
     * Assumes the correct class loader has been set before calling this
     * method.
     */
    public void initPersistenceUnitClasses(HashMap<String, EntityAccessor> allEntities, HashMap<String, EmbeddableAccessor> allEmbeddables) {
        // Process the entities
        for (EntityAccessor entity : getEntities()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass entityClass = getMetadataClass(getFullClassName(entity.getClassName()));
            
            // Initialize the entity with its metadata descriptor and 
            // project.
            entity.initXMLClassAccessor(entityClass, new MetadataDescriptor(entityClass, entity), m_project, this);
            
            if (allEntities.containsKey(entityClass.getName())) {
                // Merge this entity with the existing one.
                allEntities.get(entityClass.getName()).merge(entity);
            } else {
                // Add this entity to the map.
                allEntities.put(entityClass.getName(), entity);
            }
        }
        
        // Process the embeddables.
        for (EmbeddableAccessor embeddable : getEmbeddables()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass embeddableClass = getMetadataClass(getFullClassName(embeddable.getClassName()));
            
            // Initialize the embeddable with its metadata descriptor and
            // project.
            embeddable.initXMLClassAccessor(embeddableClass, new MetadataDescriptor(embeddableClass, embeddable), m_project, this);
            
            if (allEmbeddables.containsKey(embeddableClass.getName())) {
                // Merge this embeddable with the existing one.
                allEmbeddables.get(embeddableClass.getName()).merge(embeddable);
            } else {    
                // Add this embeddable to the map.
                allEmbeddables.put(embeddableClass.getName(), embeddable);
            }
        }
        
        // Process the mapped superclasses
        for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
            // Initialize the class with the package from entity mappings.
            MetadataClass mappedSuperclassClass = getMetadataClass(getFullClassName(mappedSuperclass.getClassName()));
            
            // Just set the accessible object on the mapped superclass for now.
            // Mapped superclasses are reloaded for each entity that inherits
            // from it. After each reload, the initXMLObjects is called and
            // ready to be processed there after.
            mappedSuperclass.setAccessibleObject(mappedSuperclassClass);
            mappedSuperclass.setEntityMappings(this);
            
            if (m_project.hasMappedSuperclass(mappedSuperclassClass)) {
                // Merge this mapped superclass with the existing one.
                m_project.getMappedSuperclassAccessor(mappedSuperclassClass).merge(mappedSuperclass);
            } else {
                // Add this mapped superclass to the project.
                m_project.addMappedSuperclass(mappedSuperclass);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean isEclipseLinkORMFile() {
        return m_isEclipseLinkORMFile;
    }
    
    /**
     * INTERNAL:
     * Return a new XMLEntityMappings instance. Used for reloading entities
     * and mapped superclasses.
     */
    protected XMLEntityMappings newXMLEntityMappingsObject() {
        XMLEntityMappings entityMappingsOut = new XMLEntityMappings();
        entityMappingsOut.setVersion(getVersion());
        return entityMappingsOut;
    }
    
    /**
     * INTERNAL:
     * Process the metadata from the <entity-mappings> level except for the
     * classes themselves. They will be processed afterwards and controlled
     * by the MetadataProcessor. Note: this method does a few things of
     * interest. It not only adds metadata to the project but it will also
     * override (that is EclipseLink-ORM-XML-->JPA-XML && JPA-XML-->Annotation)
     * the necessary metadata and log messages to the user. A validation 
     * exception could also be thrown. See the related processing methods for 
     * more details.
     * 
     * Any XML metadata of the types processed below should call these methods.
     * That is, as an example, a converter can be found at the entity-mappings 
     * and entity level. Therefore you must ensure that those from levels other
     * than the entity-mappings call these methods as well to ensure consistent
     * metadata processing (and behavior).
     */
    public void process() {
        // Add the XML converters to the project.
        for (ConverterMetadata converter : m_converters) {
            converter.initXMLObject(m_file, this);
            m_project.addConverter(converter);
        }
        
        // Add the XML type converters to the project.
        for (TypeConverterMetadata typeConverter : m_typeConverters) {
            typeConverter.initXMLObject(m_file, this);
            m_project.addConverter(typeConverter);
        }
        
        // Add the XML object type converters to the project.
        for (TypeConverterMetadata objectTypeConverter : m_objectTypeConverters) {
            objectTypeConverter.initXMLObject(m_file, this);
            m_project.addConverter(objectTypeConverter);
        }
        
        // Add the XML struct converters to the project.
        for (StructConverterMetadata structConverter : m_structConverters) {
            structConverter.initXMLObject(m_file, this);
            m_project.addConverter(structConverter);
        }
        
        // Add the XML table generators to the project.
        for (TableGeneratorMetadata tableGenerator : m_tableGenerators) {
            tableGenerator.initXMLObject(m_file, this);
            m_project.addTableGenerator(tableGenerator, getDefaultCatalog(), getDefaultSchema());
        }
            
        // Add the XML sequence generators to the project.
        for (SequenceGeneratorMetadata sequenceGenerator : m_sequenceGenerators) {
            sequenceGenerator.initXMLObject(m_file, this);
            m_project.addSequenceGenerator(sequenceGenerator, getDefaultCatalog(), getDefaultSchema());
        }
            
        // Add the XML named queries to the project.
        for (NamedQueryMetadata namedQuery : m_namedQueries) {
            namedQuery.initXMLObject(m_file, this);
            m_project.addQuery(namedQuery);
        }
        
        // Add the XML named native queries to the project.
        for (NamedNativeQueryMetadata namedNativeQuery : m_namedNativeQueries) {
            namedNativeQuery.initXMLObject(m_file, this);
               m_project.addQuery(namedNativeQuery);
           }
        
        // Add the XML named stored procedure queries to the project.
        for (NamedStoredProcedureQueryMetadata namedStoredProcedureQuery : m_namedStoredProcedureQueries) {
            namedStoredProcedureQuery.initXMLObject(m_file, this);
            m_project.addQuery(namedStoredProcedureQuery);
        }
            
        // Add the XML sql result set mappings to the project.
        for (SQLResultSetMappingMetadata sqlResultSetMapping : m_sqlResultSetMappings) {
            sqlResultSetMapping.initXMLObject(m_file, this);
            m_project.addSQLResultSetMapping(sqlResultSetMapping);
        }
    }
    
    /**
     * INTERNAL:
     * Set any entity-mappings defaults if specified. Do not blindly set them
     * since a global setting from the persistence-unit-metadata/defaults may
     * have been set and you do not want to risk removing the global value.
     */
    public void processEntityMappingsDefaults(ClassAccessor accessor) {
        MetadataDescriptor descriptor = accessor.getDescriptor();
        
        // Set the entity-mappings access if specified.
        if (m_access != null) {
            descriptor.setDefaultAccess(m_access);
        }
        
        // Set the entity-mappings catalog if specified.                
        if (m_catalog != null) {
            descriptor.setDefaultCatalog(m_catalog);
        }
        
        // Set the entity-mappings schema if specified.
        if (m_schema != null) {
            descriptor.setDefaultSchema(m_schema);
        }
    }

    /**
     * INTERNAL:
     * Process the persistence metadata if specified. Any conflicts in elements 
     * defined in multiple documents will cause an exception to be thrown 
     * (unless an override needs to occur from an eclipselink-orm.xml file). The 
     * one exception to this rule is default listeners: all default listeners 
     * found will be added to a list in the order that they are read from the 
     * instance document(s). 
     */
    public void processPersistenceUnitMetadata() {
        m_file = new MetadataFile(this);
        
        if (m_persistenceUnitMetadata != null) {
            // Set the accessible object for persistence unit metadata.
            m_persistenceUnitMetadata.initXMLObject(m_file, this);
            
            // This method will take care of any merging that needs to happen
            // and/or throw any conflict exceptions.
            m_project.setPersistenceUnitMetadata(m_persistenceUnitMetadata);

            // Process the default entity-listeners. No conflict checking will 
            // be done, that is, any and all default listeners specified across
            // the persistence unit will be added to the project.
            for (EntityListenerMetadata defaultListener : m_persistenceUnitMetadata.getDefaultListeners()) {
                // Set the accessible object for persistence unit metadata.
                defaultListener.initXMLObject(m_file, this);
                m_project.addDefaultListener(defaultListener);
            }
        }
    }
    
    /**
     * INTERNAL:
     * We clone/reload an entity class by writing it out to XML and reload it 
     * through OX.
     */
    public EntityAccessor reloadEntity(EntityAccessor accessor, MetadataDescriptor descriptor) {
        // Create entity mappings object to write out.
        XMLEntityMappings xmlEntityMappings = newXMLEntityMappingsObject();
            
        ArrayList list = new ArrayList();
        list.add(accessor);
        xmlEntityMappings.setEntities(list);
            
        // Reload the xml entity mappings object
        xmlEntityMappings = reloadXMLEntityMappingsObject(xmlEntityMappings);
            
        // Initialize the newly loaded/built entity
        EntityAccessor entity = xmlEntityMappings.getEntities().get(0);
        MetadataClass metadataClass = getMetadataFactory().getMetadataClass(getFullClassName(entity.getClassName()));
        entity.initXMLClassAccessor(metadataClass, descriptor, m_project, this);
        
        return entity;
    }
    
    /**
     * INTERNAL:
     * We clone/reload a mapped-superclass by writing it out to XML and 
     * reload it through OX.
     */
    public MappedSuperclassAccessor reloadMappedSuperclass(MappedSuperclassAccessor accessor, MetadataDescriptor descriptor) {
        // Create entity mappings object to write out.
        XMLEntityMappings xmlEntityMappings = newXMLEntityMappingsObject();

        ArrayList list = new ArrayList();
        list.add(accessor);
        xmlEntityMappings.setMappedSuperclasses(list);

        // Reload the xml entity mappings object
        xmlEntityMappings = reloadXMLEntityMappingsObject(xmlEntityMappings);
        
        // Initialize the newly loaded/built mapped superclass
        MappedSuperclassAccessor mappedSuperclass = xmlEntityMappings.getMappedSuperclasses().get(0);
        MetadataClass metadataClass = getMetadataFactory().getMetadataClass(getFullClassName(mappedSuperclass.getClassName()));
        mappedSuperclass.initXMLClassAccessor(metadataClass, descriptor, m_project, this);
        
        return mappedSuperclass;
    }
    
    /**
     * INTERNAL:
     */
    protected XMLEntityMappings reloadXMLEntityMappingsObject(XMLEntityMappings xmlEntityMappings) {
        ByteArrayOutputStream outputStream = null;
        StringReader reader1 = null;
        StringReader reader2 = null;
        StringReader reader3 = null;
        try {
            outputStream = new ByteArrayOutputStream();
            XMLEntityMappingsWriter.write(xmlEntityMappings, outputStream);
            reader1 = new StringReader(outputStream.toString());
            reader2 = new StringReader(outputStream.toString());
            reader3 = new StringReader(outputStream.toString());
            XMLEntityMappings newXMLEntityMappings = XMLEntityMappingsReader.read("tempStream", reader1, reader2, reader3, m_loader, null);
            return newXMLEntityMappings;
        } catch (Exception e) {
            throw new RuntimeException(e);
            // TODO: Throw an EclipseLink exception.
        } finally {
            if (outputStream != null){
                try{
                    outputStream.close();
                }catch (IOException ex){}
            }   
            if (reader1 != null) {
                reader1.close();
            }
            if (reader2 != null) {
                reader2.close();
            }
            if (reader3 != null) {
                reader3.close();
            }
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
    public void setEntities(List<EntityAccessor> entities) {
        m_entities = entities;
    }
    
    /**
     * INTERNAL:
     */
    public void setIsEclipseLinkORMFile(boolean isEclipseLinkORMFile) {
        m_isEclipseLinkORMFile = isEclipseLinkORMFile;
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
    public void setMappingFile(String mappingFileNameOrURL) {
        m_mappingFileNameOrURL = mappingFileNameOrURL;
    }
    
    /**
     * INTERNAL:
     */
    public void setMetadataFactory(MetadataFactory factory) {
        m_factory = factory;
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
    public void setNamedStoredProcedureQueries(List<NamedStoredProcedureQueryMetadata> namedStoredProcedureQueries) {
        m_namedStoredProcedureQueries = namedStoredProcedureQueries;
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
     * Used for OX mapping.
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
     * Set the project reference for this EntityMappings object.
     */
    public void setProject(MetadataProject project) {
        m_project = project;
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
