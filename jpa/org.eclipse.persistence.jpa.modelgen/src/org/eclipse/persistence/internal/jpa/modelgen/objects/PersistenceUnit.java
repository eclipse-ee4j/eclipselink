/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes 
 *     11/20/2009-2.0 Guy Pelletier/Mitesh Meswani 
 *       - 295376: Improve usability of MetaModel generator
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 *     11/23/2010-2.2 Guy Pelletier 
 *       - 330660: Canonical model generator throws ClassCastException when using package-info.java
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitProperty;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnitReader;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_SUB_PACKAGE;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PREFIX;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_SUFFIX;

/**
 * A representation of a persistence unit definition. 
 * 
 * @author Guy Pelletier, Doug Clarke
 * @since EclipseLink 1.2
 */
public class PersistenceUnit {    
    protected List<XMLEntityMappings> xmlEntityMappings;
    
    protected MetadataProject project;
    protected MetadataMirrorFactory factory; 
    protected PersistenceUnitReader persistenceUnitReader;
    protected ProcessingEnvironment processingEnv;
    protected SEPersistenceUnitInfo persistenceUnitInfo;
    protected HashMap<String, String> persistenceUnitProperties;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnit(SEPersistenceUnitInfo puInfo, MetadataMirrorFactory mirrorFactory, PersistenceUnitReader reader) {
        factory = mirrorFactory;
        persistenceUnitInfo = puInfo;
        persistenceUnitReader = reader;
        
        // Ask the factory for the project and processing environment
        processingEnv = factory.getProcessingEnvironment();
        project = factory.getMetadataProject(persistenceUnitInfo);
        
        // Init our OX mapped properties into a map.
        initPersistenceUnitProperties();
        
        // Init our mapping files. This method relies on properties being
        // initialized first.
        initXMLEntityMappings();
    }
    
    /**
     * INTERNAL:
     * Add an element decorated with an Embeddable annotation.
     * 
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, but accessor loaded from XML - don't touch it
     * 4 - Existing element, but accessor loaded from Annotations - add new accessor overriding the old.
     */
    public void addEmbeddableAccessor(Element element) {
        MetadataClass metadataClass = factory.getMetadataClass(element);
        
        // Remove the accessor from other maps if the type changed.
        removeEntityAccessor(metadataClass);
        removeMappedSuperclassAccessor(metadataClass);
        
        if (project.hasEmbeddable(metadataClass)) {
            EmbeddableAccessor embeddableAccessor = project.getEmbeddableAccessor(metadataClass);
            
            // If it was loaded from XML, reset the pre-processed flag.
            if (embeddableAccessor.loadedFromXML()) {
                embeddableAccessor.clearPreProcessed();
            } else {
                // Was not loaded from XML and existed in the project.
                if (excludeUnlistedClasses(metadataClass)) {
                    // Exclude unlisted classes is now false, remove it!
                    removeEmbeddableAccessor(metadataClass);
                } else {
                    // Otherwise, override the existing accessor!
                    addEmbeddableAccessor(new EmbeddableAccessor(metadataClass.getAnnotation(Embeddable.class), metadataClass, project));
                }
            }
        } else if (! excludeUnlistedClasses(metadataClass)) {
            // add it!
            addEmbeddableAccessor(new EmbeddableAccessor(metadataClass.getAnnotation(Embeddable.class), metadataClass, project));
        }
    }
    
    /**
     * INTERNAL:
     * Add an embeddable accessor to the project, preserving any previous
     * owning descriptors set if applicable.
     */
    protected void addEmbeddableAccessor(EmbeddableAccessor embeddableAccessor) {
        // We need to preserve owning descriptors to ensure we process
        // an embeddable accessor in the correct context. That is, if the
        // user changed only the embeddable class then we won't process
        // the owning entity (which ensures the owning descriptor, itself,
        // is set on the embeddable accessor).
        // If ownership changed, then those entities involved will be
        // in the round elements and we will correctly set the owning
        // descriptor in the pre-process stage of those entities, overriding 
        // this setting)
        if (project.hasEmbeddable(embeddableAccessor.getJavaClass())) {
            EmbeddableAccessor existingEmbeddableAccessor = project.getEmbeddableAccessor(embeddableAccessor.getJavaClass());
            embeddableAccessor.addEmbeddingAccessors(existingEmbeddableAccessor.getEmbeddingAccessors());
            embeddableAccessor.addOwningDescriptors(existingEmbeddableAccessor.getOwningDescriptors());
        }
        
        project.addEmbeddableAccessor(embeddableAccessor);
    }
    
    /**
     * INTERNAL:
     * Add an element decorated with an Entity annotation.
     * 
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, loaded from XML - don't touch it
     * 4 - Existing element, loaded from Annotations - add new accessor overriding the old.
     */
    public void addEntityAccessor(Element element) {
        MetadataClass metadataClass = factory.getMetadataClass(element);
        
        // Remove the accessor from other maps if the type changed.
        removeEmbeddableAccessor(metadataClass);
        removeMappedSuperclassAccessor(metadataClass);
        
        if (project.hasEntity(metadataClass)) {
            EntityAccessor entityAccessor = project.getEntityAccessor(metadataClass);
            
            // If it was loaded from XML, reset the pre-processed flag.
            if (entityAccessor.loadedFromXML()) {
                entityAccessor.clearPreProcessed();
            } else {
                // Was not loaded from XML and existed in the project. 
                if (excludeUnlistedClasses(metadataClass)) {
                    // Exclude unlisted classes is now false, remove it!
                    removeEntityAccessor(metadataClass);
                } else {
                    // Otherwise, override the existing accessor!
                    project.addEntityAccessor(new EntityAccessor(metadataClass.getAnnotation(Entity.class), metadataClass, project));
                }
            }
        } else if (! excludeUnlistedClasses(metadataClass)) {
            // add it!
            project.addEntityAccessor(new EntityAccessor(metadataClass.getAnnotation(Entity.class), metadataClass, project));
        }
    }
    
    /**
     * INTERNAL:
     * Add an element decorated with a MappedSuperclass annotation.
     * 
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, but accessor loaded from XML - don't touch it
     * 4 - Existing element, but accessor loaded from Annotations - add new accessor overridding the old.
     */
    public void addMappedSuperclassAccessor(Element element) {
        MetadataClass metadataClass = factory.getMetadataClass(element);
        
        // Remove the accessor from other maps if the type changed.
        removeEntityAccessor(metadataClass);
        removeEmbeddableAccessor(metadataClass);
        
        if (project.hasMappedSuperclass(metadataClass)) {
            MappedSuperclassAccessor mappedSuperclassAccessor = project.getMappedSuperclassAccessor(metadataClass);
            
            // If it was loaded from XML, reset the pre-processed flag.
            if (mappedSuperclassAccessor.loadedFromXML()) {
                mappedSuperclassAccessor.clearPreProcessed();
            } else {
                // Was not loaded from XML and existed in the project.
                if (excludeUnlistedClasses(metadataClass)) {
                    // Exclude unlisted classes is now false, remove it!
                    project.removeMappedSuperclassAccessor(metadataClass);
                } else {
                    // Otherwise, override the existing accessor!
                    project.addMappedSuperclass(new MappedSuperclassAccessor(metadataClass.getAnnotation(MappedSuperclass.class), metadataClass, project));
                }
            }
        } else if (! excludeUnlistedClasses(metadataClass)) {
            // add it!
            project.addMappedSuperclass(new MappedSuperclassAccessor(metadataClass.getAnnotation(MappedSuperclass.class), metadataClass, project));
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addPropertyFromOptions(String propertyName) {
        if (! persistenceUnitProperties.containsKey(propertyName) || persistenceUnitProperties.get(propertyName) == null) {
            persistenceUnitProperties.put(propertyName, processingEnv.getOptions().get(propertyName));
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addXMLEntityMappings(String mappingFile, XMLContext context) {
        // If the input stream is null, we didn't find the mapping file, so 
        // don't bother trying to read it.
        InputStream inputStream = persistenceUnitReader.getInputStream(mappingFile, false);
        
        if (inputStream != null) {
            try {
                XMLEntityMappings entityMappings = (XMLEntityMappings) context.createUnmarshaller().unmarshal(inputStream);
                // For eclipselink-orm merging and overriding these need to be set.
                entityMappings.setIsEclipseLinkORMFile(mappingFile.equals(MetadataHelper.ECLIPSELINK_ORM_FILE));
                entityMappings.setMappingFile(mappingFile);
                processingEnv.getMessager().printMessage(Kind.NOTE, "File loaded : " + mappingFile + ", is eclipselink-orm file: " + entityMappings.isEclipseLinkORMFile());
                xmlEntityMappings.add(entityMappings);
            } finally {
                persistenceUnitReader.closeInputStream(inputStream);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addXMLEntityMappings(String mappingFile) {
        try {
            // Try eclipselink project
            addXMLEntityMappings(mappingFile, XMLEntityMappingsReader.getEclipseLinkOrmProject());
        } catch (XMLMarshalException e) {
            try {
                // Try JPA 2.0 project
                addXMLEntityMappings(mappingFile, XMLEntityMappingsReader.getOrm2Project());
            } catch (XMLMarshalException ee) {
                // Try JPA 1.0 project (don't catch exceptions at this point)
                addXMLEntityMappings(mappingFile, XMLEntityMappingsReader.getOrm1Project());
            }
        }
    }
    
    /**
     * INTERNAL:
     * If the accessor is no longer valid it we should probably look into
     * removing the accessor from the project. For now I don't think it's 
     * a big deal.
     */
    public boolean containsClass(MetadataClass metadataClass) {
        if (project.hasEntity(metadataClass)) {
            return isValidAccessor(project.getEntityAccessor(metadataClass), metadataClass.getAnnotation(javax.persistence.Entity.class));
        }
        
        if (project.hasEmbeddable(metadataClass)) {
            return isValidAccessor(project.getEmbeddableAccessor(metadataClass), metadataClass.getAnnotation(javax.persistence.Embeddable.class));
        }
        
        if (project.hasMappedSuperclass(metadataClass)) {
            return isValidAccessor(project.getMappedSuperclassAccessor(metadataClass), metadataClass.getAnnotation(javax.persistence.MappedSuperclass.class));
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if the metadata class given is not a managed class and 
     * exclude-unlisted-classes is set to true for this PU.
     */
    protected boolean excludeUnlistedClasses(MetadataClass cls) {
        return (! persistenceUnitInfo.getManagedClassNames().contains(cls.getName())) && persistenceUnitInfo.excludeUnlistedClasses();
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor getClassAccessor(MetadataClass metadataClass) {
        if (project.hasEntity(metadataClass)) {
            return project.getEntityAccessor(metadataClass);
        }
        
        if (project.hasEmbeddable(metadataClass)) {
            return project.getEmbeddableAccessor(metadataClass);
        }
        
        if (project.hasMappedSuperclass(metadataClass)) {
            return project.getMappedSuperclassAccessor(metadataClass);
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     */
    public String getQualifiedCanonicalName(String qualifiedName) {
        return MetadataHelper.getQualifiedCanonicalName(qualifiedName, persistenceUnitProperties); 
    }
    
    /**
     * INTERNAL:
     */
    public void initPersistenceUnitProperties() {
        persistenceUnitProperties = new HashMap<String, String>();
        
        // Determine how much validation we want to do. For now, last one
        // in wins (in the case of multiple settings) and we ignore null
        // named properties.
        for (SEPersistenceUnitProperty property : persistenceUnitInfo.getPersistenceUnitProperties()) { 
            if (property.getName() != null) {
                //processingEnv.getMessager().printMessage(Kind.NOTE, "Key: " + property.getName() + " , value: " + property.getValue());
                persistenceUnitProperties.put(property.getName(), property.getValue());
            }
        }
        
        // Check for user specified options and add them to the properties
        // if they were not specified in the persistence.xml.
        addPropertyFromOptions(CANONICAL_MODEL_PREFIX);
        addPropertyFromOptions(CANONICAL_MODEL_SUFFIX);
        addPropertyFromOptions(CANONICAL_MODEL_SUB_PACKAGE);
    }
    
    /**
     * INTERNAL:
     */
    protected void initXMLEntityMappings() {
        xmlEntityMappings = new ArrayList<XMLEntityMappings>();
        
        // Load the orm.xml if it exists.
        addXMLEntityMappings(MetadataHelper.JPA_ORM_FILE);
        
        // Load the eclipselink-orm.xml if it exists and is not excluded.
        Boolean excludeEclipseLinkORM = false;
        if (persistenceUnitProperties.containsKey(PersistenceUnitProperties.EXCLUDE_ECLIPSELINK_ORM_FILE)) {
            excludeEclipseLinkORM = Boolean.valueOf((String) persistenceUnitProperties.get(PersistenceUnitProperties.EXCLUDE_ECLIPSELINK_ORM_FILE));
        }
        
        if (! excludeEclipseLinkORM) {
            addXMLEntityMappings(MetadataHelper.ECLIPSELINK_ORM_FILE);
        }
        
        // Load the listed mapping files.
        for (String mappingFile : persistenceUnitInfo.getMappingFileNames()) {
            if (! mappingFile.equals(MetadataHelper.JPA_ORM_FILE) && ! mappingFile.equals(MetadataHelper.ECLIPSELINK_ORM_FILE)) {
                addXMLEntityMappings(mappingFile);
            }
        }
    
        // 1 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        for (XMLEntityMappings entityMappings : xmlEntityMappings) {
            entityMappings.setLoader(factory.getLoader());
            entityMappings.setProject(project);
            entityMappings.setMetadataFactory(factory);
            entityMappings.setLoadedForCanonicalModel(true);
        
            // Process the persistence unit metadata if defined.
            entityMappings.processPersistenceUnitMetadata();
        }
        
        // 2 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        HashMap<String, EntityAccessor> entities = new HashMap<String, EntityAccessor>();
        HashMap<String, EmbeddableAccessor> embeddables = new HashMap<String, EmbeddableAccessor>();
        
        for (XMLEntityMappings entityMappings : xmlEntityMappings) {
            entityMappings.initPersistenceUnitClasses(entities, embeddables);
        }
        
        // 3 - Iterate through all the XML entities and add them to the project 
        // and apply any persistence unit defaults.
        for (EntityAccessor entity : entities.values()) {
            // This will apply global persistence unit defaults.
            project.addEntityAccessor(entity);
            
            // This will override any global settings.
            entity.getEntityMappings().processEntityMappingsDefaults(entity);
        }

        // 4 - Iterate though all the XML embeddables and add them to the 
        // project and apply any persistence unit defaults.
        for (EmbeddableAccessor embeddable : embeddables.values()) {
            // This will apply global persistence unit defaults.
            addEmbeddableAccessor(embeddable);
            
            // This will override any global settings.
            embeddable.getEntityMappings().processEntityMappingsDefaults(embeddable);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected boolean isValidAccessor(ClassAccessor accessor, MetadataAnnotation annotation) {
        if (! accessor.loadedFromXML()) {
            // If it wasn't loaded from XML, we need to look at it further. It
            // could have been deleted and brought back (without an annotation)
            // or simply have had its annotation removed. Check for an annotation.
            return annotation != null;
        }
        
        return true;
    }
    
    /**
     * INTERNAL:
     * Steps are important here, so don't change them.
     */
    public void preProcessForCanonicalModel() {
        // 1 - Pre-Process the list of entities first. This will discover/build 
        // the list of embeddable accessors.
        for (EntityAccessor entityAccessor : project.getEntityAccessors()) {
            // Some entity accessors can be fast tracked for pre-processing.
            // That is, an inheritance subclass will tell its parents to 
            // pre-process. So don't pre-process it again.
            if (shouldPreProcess(entityAccessor)) {
                // Update the accessible object in case it changed.
                entityAccessor.setAccessibleObject(factory.getMetadataClass(entityAccessor.getAccessibleObjectName()));
                
                // Pre-process the accessor now.
                entityAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 2 - Pre-Process the list of mapped superclasses.
        for (MappedSuperclassAccessor mappedSuperclassAccessor : project.getMappedSuperclasses()) {
            if (shouldPreProcess(mappedSuperclassAccessor)) {
                // Update the accessible object first, in case it changed.
                mappedSuperclassAccessor.setAccessibleObject(factory.getMetadataClass(mappedSuperclassAccessor.getAccessibleObjectName()));
                
                // Pre-process the accessor now.
                mappedSuperclassAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 3 - Pre-process the list of root embeddable accessors. This list will
        // have been built from step 1. Root embeddable accessors will have
        // an owning descriptor (used to determine access type).
        for (EmbeddableAccessor embeddableAccessor : project.getRootEmbeddableAccessors()) {
            if (shouldPreProcess(embeddableAccessor)) {
                // Update the accessible object first, in case it changed.
                embeddableAccessor.setAccessibleObject(factory.getMetadataClass(embeddableAccessor.getAccessibleObjectName()));
                
                // Pre-process the accessor now.
                embeddableAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 4 - Go through the whole list of embeddables and process any that 
        // were not pre-processed. Only way this is true is if the embeddable
        // was not referenced from an entity (be it root or nested in a root
        // embeddable)
        for (EmbeddableAccessor embeddableAccessor : project.getEmbeddableAccessors()) {
            if (shouldPreProcess(embeddableAccessor)) {
                // Update the accessible object first, in case it changed.
                embeddableAccessor.setAccessibleObject(factory.getMetadataClass(embeddableAccessor.getAccessibleObjectName()));
                
                // Pre-process the accessor now.
                embeddableAccessor.preProcessForCanonicalModel();
            }
        }
    }
    
    /**
     * INTERNAL:
     * If it is not already pre-processed or is included in the round elements
     * (presumably because something changed), then pre-process the accessor.
     * You may get a previously loaded and processed XML class accessor that
     * may have had a change made to its associated class (that does not have
     * either an Entity, Embeddable or MappedSuperclass annotation) with the 
     * load xml flag turned off. Therefore we must make sure we preProcess the 
     * accessor again.  
     */
    protected boolean shouldPreProcess(ClassAccessor accessor) {
        MetadataClass cls = (MetadataClass) accessor.getAccessibleObject();
        
        if (accessor.isPreProcessed() && factory.isRoundElement(cls)) {
            accessor.clearPreProcessed();
        }
        
        return ! accessor.isPreProcessed();
    }
    
    /**
     * INTERNAL:
     * Remove the entity accessor for the given metadata class if one exists.
     */
    protected void removeEntityAccessor(MetadataClass metadataClass) {
        if (project.hasEntity(metadataClass)) {
            project.removeEntityAccessor(metadataClass);
        }
    }
    
    /**
     * INTERNAL:
     * Remove the embeddable accessor for the given metadata class if one exists.
     */
    protected void removeEmbeddableAccessor(MetadataClass metadataClass) {
        if (project.hasEmbeddable(metadataClass)) {
            project.removeEmbeddableAccessor(metadataClass);
        }
    }
    
    /**
     * INTERNAL:
     * Remove the embeddable accessor for the given metadata class if one exists.
     */
    protected void removeMappedSuperclassAccessor(MetadataClass metadataClass) {
        if (project.hasMappedSuperclass(metadataClass)) {
            project.removeMappedSuperclassAccessor(metadataClass);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return persistenceUnitInfo.getPersistenceUnitName();
    }
}

