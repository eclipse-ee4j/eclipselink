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
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes 
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnitReader;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_QUALIFIER;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_QUALIFIER_POSITION;

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
    
    protected ProcessingEnvironment processingEnv;
    protected SEPersistenceUnitInfo persistenceUnitInfo;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnit(SEPersistenceUnitInfo puInfo, MetadataMirrorFactory mirrorFactory) throws IOException {
        factory = mirrorFactory;
        persistenceUnitInfo = puInfo;
        
        // Ask the factory for the project and processing environment
        processingEnv = factory.getProcessingEnvironment();
        project = factory.getMetadataProject(persistenceUnitInfo);

        initXMLEntityMappings();
    }
    
    /**
     * INTERNAL:
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, but accessor loaded from XML (it's a new accessor then) - don't touch it
     * 4 - Existing element, but accessor loaded from Annotations - add new accessor overridding the old.
     */
    public void addEmbeddableAccessor(Element element) {
        MetadataClass cls = factory.getMetadataClass(element);
        
        if (project.hasEmbeddable(cls)) {
            EmbeddableAccessor embeddableAccessor = project.getEmbeddableAccessor(cls);
            
            // Don't touch it if it was loaded from XML.
            if (! embeddableAccessor.loadedFromXML()) {
                if (excludeUnlistedClasses(cls)) {
                    // remove it!
                } else {
                    // override it!
                    project.addEmbeddableAccessor(new EmbeddableAccessor(cls.getAnnotation(Embeddable.class), cls, project));
                }
            }
        } else if (! excludeUnlistedClasses(cls)) {
            // add it!
            project.addEmbeddableAccessor(new EmbeddableAccessor(cls.getAnnotation(Embeddable.class), cls, project));
        }
    }
    
    /**
     * INTERNAL:
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, loaded from XML - don't touch it (it's already a new un-processed accessor)
     * 4 - Existing element, loaded from Annotations - add new accessor overridding the old.
     */
    public void addEntityAccessor(Element element) {
        MetadataClass cls = factory.getMetadataClass(element);
        
        if (project.hasEntity(cls)) {
            EntityAccessor entityAccessor = project.getEntityAccessor(cls);
            
            // Don't touch it if it was loaded from XML.
            if (! entityAccessor.loadedFromXML()) {
                if (excludeUnlistedClasses(cls)) {
                    // remove it!
                } else {
                    // override it!
                    project.addEntityAccessor(new EntityAccessor(cls.getAnnotation(Entity.class), cls, project));
                }
            }
        } else if (! excludeUnlistedClasses(cls)) {
            // add it!
            project.addEntityAccessor(new EntityAccessor(cls.getAnnotation(Entity.class), cls, project));
        }
    }
    
    /**
     * INTERNAL:
     * The possibilities here:
     * 1 - New element and not exclude unlisted classes - add it
     * 2 - New element but exclude unlisted classes - ignore it.
     * 3 - Existing element, but accessor loaded from XML (it's a new accessor then) - don't touch it
     * 4 - Existing element, but accessor loaded from Annotations - add new accessor overridding the old.
     */
    public void addMappedSuperclassAccessor(Element element) {
        MetadataClass cls = factory.getMetadataClass(element);
        
        if (project.hasMappedSuperclass(cls)) {
            MappedSuperclassAccessor mappedSuperclassAccessor = project.getMappedSuperclass(cls);
            
            // Don't touch it if it was loaded from XML.
            if (! mappedSuperclassAccessor.loadedFromXML()) {
                if (excludeUnlistedClasses(cls)) {
                    // remove it!
                } else {
                    // override it!
                    project.addMappedSuperclass(element.toString(), new MappedSuperclassAccessor(cls.getAnnotation(MappedSuperclass.class), cls, project));
                }
            }
        } else if (! excludeUnlistedClasses(cls)) {
            // add it!
            project.addMappedSuperclass(element.toString(), new MappedSuperclassAccessor(cls.getAnnotation(MappedSuperclass.class), cls, project));
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addXMLEntityMappings(FileObject fileObject, XMLContext context) throws IOException {
        InputStream in = null;
        try {
            in = fileObject.openInputStream();
            xmlEntityMappings.add((XMLEntityMappings) XMLEntityMappingsReader.getEclipseLinkOrmProject().createUnmarshaller().unmarshal(in));
        } catch (XMLMarshalException e) {
            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addXMLEntityMappings(String mappingFile, boolean validate) throws IOException {
        FileObject fileObject = PersistenceUnitReader.getFileObject(mappingFile, StandardLocation.CLASS_OUTPUT, processingEnv);
        
        if (fileObject != null) {
            try {
                // Try eclipselink project
                //APTHelper.printNOTE("Trying eclipselink context ... ", m_processingEnv);
                addXMLEntityMappings(fileObject, XMLEntityMappingsReader.getEclipseLinkOrmProject());
            } catch (XMLMarshalException e) {
                try {
                    // Try JPA 2.0 project
                    //APTHelper.printNOTE("Trying JPA 2.0 context ... ", m_processingEnv);
                    addXMLEntityMappings(fileObject, XMLEntityMappingsReader.getOrm2Project());
                } catch (XMLMarshalException ee) {
                    //APTHelper.printNOTE("Trying JPA 1.0 context ... ", m_processingEnv);
                    // Try JPA 1.0 project, don't catch any exception at this point and throw it.
                    addXMLEntityMappings(fileObject, XMLEntityMappingsReader.getOrm1Project());
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * TODO: If the accessor is no longer valid it should be removed from the
     * project (could cause a memory leak)
     */
    public boolean containsElement(Element element) {
        MetadataClass cls = factory.getMetadataClass(element);
        
        if (project.hasEntity(cls)) {
            return isValidAccessor(project.getEntityAccessor(cls), element.getAnnotation(javax.persistence.Entity.class));
        }
        
        if (project.hasEmbeddable(cls)) {
            return isValidAccessor(project.getEmbeddableAccessor(cls), element.getAnnotation(javax.persistence.Embeddable.class));
        }
        
        if (project.hasMappedSuperclass(cls)) {
            return isValidAccessor(project.getMappedSuperclass(cls), element.getAnnotation(javax.persistence.MappedSuperclass.class));
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
    public String getCanonicalName(String name) {
        return persistenceUnitInfo.getCanonicalName(name, getCanonicalQualifierOption(), getCanonicalQualifierPositionOption());
    }
    
    /**
     * INTERNAL:
     */
    protected String getCanonicalQualifierOption() {
        return processingEnv.getOptions().get(CANONICAL_MODEL_QUALIFIER);
    }
    
    /**
     * INTERNAL:
     */
    protected String getCanonicalQualifierPositionOption() {
        return processingEnv.getOptions().get(CANONICAL_MODEL_QUALIFIER_POSITION);
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor getClassAccessor(Element element) {
        String elementString = element.toString();
        
        if (project.hasEntity(elementString)) {
            return project.getEntityAccessor(elementString);
        }
        
        if (project.hasEmbeddable(elementString)) {
            return project.getEmbeddableAccessor(elementString);
        }
        
        if (project.hasMappedSuperclass(elementString)) {
            return project.getMappedSuperclass(elementString);
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     */
    protected void initXMLEntityMappings() throws IOException {
        xmlEntityMappings = new ArrayList<XMLEntityMappings>();
        
        // Load the orm.xml if it exists.
        addXMLEntityMappings("META-INF/orm.xml", false);
        
        // Load the listed mapping files.
        for (String mappingFile : persistenceUnitInfo.getMappingFileNames()) {
            if (! mappingFile.equals("META-INF/orm.xml")) {
                addXMLEntityMappings(mappingFile, true);
            }
        }
    
        // 1 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        for (XMLEntityMappings entityMappings : xmlEntityMappings) {
            entityMappings.setLoader(factory.getLoader());
            entityMappings.setProject(project);
            entityMappings.setMetadataFactory(factory);
        
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
            project.addEmbeddableAccessor(embeddable);
            
            // This will override any global settings.
            embeddable.getEntityMappings().processEntityMappingsDefaults(embeddable);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected boolean isValidAccessor(ClassAccessor accessor, Annotation annotation) {
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
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing entity: " + entityAccessor.getJavaClassName());
                entityAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 2 - Pre-Process the list of mapped superclasses.
        for (MappedSuperclassAccessor mappedSuperclassAccessor : project.getMappedSuperclasses()) {
            if (shouldPreProcess(mappedSuperclassAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing mapped-superclass: " + mappedSuperclassAccessor.getJavaClassName());
                mappedSuperclassAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 3 - Pre-process the list of root embeddable accessors. This list will
        // have been build from step 1. Root embeddable accessors will have
        // an owning descriptor (used to determine access type).
        for (EmbeddableAccessor embeddableAccessor : project.getRootEmbeddableAccessors()) {
            if (shouldPreProcess(embeddableAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing embeddable: " + embeddableAccessor.getJavaClassName());
                embeddableAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 4 - Go through the whole list of embeddables and process any that 
        // were not pre-processed. Only way this is true is if the embeddable
        // was not referenced from an entity (be it root or nested in a root
        // embeddable)
        for (EmbeddableAccessor embeddableAccessor : project.getEmbeddableAccessors()) {
            if (shouldPreProcess(embeddableAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing embeddable: " + embeddableAccessor.getJavaClassName());
                // Must set the owning descriptor to be itself at this point
                // since this embeddable is not 'owned' by an entity.
                embeddableAccessor.setOwningDescriptor(embeddableAccessor.getOwningDescriptor());
                embeddableAccessor.preProcessForCanonicalModel();
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected boolean shouldPreProcess(ClassAccessor accessor) {
        return ! accessor.isPreProcessed() && factory.isRoundElement((MetadataClass) accessor.getAccessibleObject());
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return persistenceUnitInfo.getPersistenceUnitName();
    }
}

