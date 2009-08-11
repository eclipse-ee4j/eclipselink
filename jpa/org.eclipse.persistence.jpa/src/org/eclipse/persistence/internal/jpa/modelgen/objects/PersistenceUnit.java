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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.tools.FileObject;

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
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * A representation of a persistence unit definition. 
 * 
 * @author Guy Pelletier, Doug Clarke
 * @since Eclipselink 2.0
 */
public class PersistenceUnit {    
    protected List<XMLEntityMappings> m_xmlEntityMappings;
    
    protected MetadataProject m_project;
    protected MetadataMirrorFactory m_factory; 
    
    protected ProcessingEnvironment m_processingEnv;
    protected SEPersistenceUnitInfo m_puInfo;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnit(SEPersistenceUnitInfo puInfo, MetadataMirrorFactory factory) throws IOException {
        m_puInfo = puInfo;
        m_processingEnv = factory.getProcessingEnvironment();
        m_xmlEntityMappings = new ArrayList<XMLEntityMappings>();
        
        m_factory = factory;
        m_project = new MetadataProject(m_puInfo, new ServerSession(new Project(new DatabaseLogin())), false, false);
        
        initXMLEntityMappings();
    }
    
    /**
     * INTERNAL:
     */
    public void addEntityAccessor(Element element) {
        String elementString = element.toString();
        // If it does contain the entity accessor already and we are not
        // excluding unlisted classes then add a new EntityAccessor.
        
        if (! m_project.hasEntity(elementString) && ! excludeUnlistedClasses()) {
            MetadataClass entityClass = m_factory.getMetadataClass(element);
            EntityAccessor entityAccessor = new EntityAccessor(entityClass.getAnnotation(Entity.class), entityClass, m_project);
            m_project.addEntityAccessor(entityAccessor);
        } 
    }
    
    /**
     * INTERNAL:
     */
    public void addMappedSuperclassAccessor(Element element) {
        String elementString = element.toString();
        
        // If it does contain the mapped superclass accessor already and we are 
        // not excluding unlisted classes then add a new MappedSuperclassAccessor.
        if (! m_project.hasMappedSuperclass(elementString) && ! excludeUnlistedClasses()) {
            MetadataClass mappedSuperclassClass = m_factory.getMetadataClass(element);
            MappedSuperclassAccessor mappedSuperclassAccessor = new MappedSuperclassAccessor(mappedSuperclassClass.getAnnotation(MappedSuperclass.class), mappedSuperclassClass, m_project);
            m_project.addMappedSuperclass(element.toString(), mappedSuperclassAccessor);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addEmbeddableAccessor(Element element) {
        String elementString = element.toString();
        
        // If it does contain the mapped superclass accessor already and we are 
        // not excluding unlisted classes then add a new MappedSuperclassAccessor.
        if (! m_project.hasEmbeddable(elementString) && ! excludeUnlistedClasses()) {
            MetadataClass embeddableClass = m_factory.getMetadataClass(element);
            EmbeddableAccessor embeddableAccessor = new EmbeddableAccessor(embeddableClass.getAnnotation(Embeddable.class), embeddableClass, m_project); 
            m_project.addEmbeddableAccessor(embeddableAccessor);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addXMLEntityMappings(FileObject fileObject, XMLContext context) throws IOException {
        InputStream in = null;
        try {
            in = fileObject.openInputStream();
            XMLEntityMappings xmlEntityMappings = (XMLEntityMappings) XMLEntityMappingsReader.getEclipseLinkOrmProject().createUnmarshaller().unmarshal(in);
            m_xmlEntityMappings.add(xmlEntityMappings);
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
        FileObject fileObject = PersistenceUnitReader.getFileObject(mappingFile, m_processingEnv);
        
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
     */
    public boolean containsElement(Element element) {
        String elementString = element.toString();
        
        if (m_project.hasEntity(elementString)) {
            return true;
        }
        
        if (m_project.hasEmbeddable(elementString)) {
            return true;
        }
        
        return m_project.hasMappedSuperclass(elementString);
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor getClassAccessor(Element element) {
        String elementString = element.toString();
        
        if (m_project.hasEntity(elementString)) {
            return m_project.getEntityAccessor(elementString);
        }
        
        if (m_project.hasEmbeddable(elementString)) {
            return m_project.getEmbeddableAccessor(elementString);
        }
        
        if (m_project.hasMappedSuperclass(elementString)) {
            return m_project.getMappedSuperclass(elementString);
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean excludeUnlistedClasses() {
        return m_puInfo.excludeUnlistedClasses();
    }
    
    /**
     * INTERNAL:
     */
    protected void initXMLEntityMappings() throws IOException {
        // Load the orm.xml if it exists.
        addXMLEntityMappings("META-INF/orm.xml", false);
        
        // Load the listed mapping files.
        for (String mappingFile : m_puInfo.getMappingFileNames()) {
            if (! mappingFile.equals("META-INF/orm.xml")) {
                addXMLEntityMappings(mappingFile, true);
            }
        }
    
        // 1 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        for (XMLEntityMappings entityMappings : m_xmlEntityMappings) {
            entityMappings.setLoader(m_factory.getLoader());
            entityMappings.setProject(m_project);
            entityMappings.setMetadataFactory(m_factory);
        
            // Process the persistence unit metadata if defined.
            entityMappings.processPersistenceUnitMetadata();
        }
        
        // 2 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        HashMap<String, EntityAccessor> entities = new HashMap<String, EntityAccessor>();
        HashMap<String, EmbeddableAccessor> embeddables = new HashMap<String, EmbeddableAccessor>();
        
        for (XMLEntityMappings entityMappings : m_xmlEntityMappings) {
            entityMappings.initPersistenceUnitClasses(entities, embeddables);
        }
        
        // 3 - Iterate through all the XML entities and add them to the project 
        // and apply any persistence unit defaults.
        for (EntityAccessor entity : entities.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEntityAccessor(entity);
            
            // This will override any global settings.
            entity.getEntityMappings().processEntityMappingsDefaults(entity);
        }

        // 4 - Iterate though all the XML embeddables and add them to the 
        // project and apply any persistence unit defaults.
        for (EmbeddableAccessor embeddable : embeddables.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEmbeddableAccessor(embeddable);
            
            // This will override any global settings.
            embeddable.getEntityMappings().processEntityMappingsDefaults(embeddable);
        }
    }
    
    /**
     * INTERNAL:
     * Steps are important here, so don't change them. 
     */
    public void preProcessForCanonicalModel() {
        // 1 - Pre-Process the list of entities first. This will discover/build 
        // the list of embeddable accessors.
        for (EntityAccessor entityAccessor : m_project.getEntityAccessors()) {
            // Some entity accessors can be fast tracked for pre-processing.
            // That is, an inheritance subclass will tell its parents to 
            // pre-process. So don't pre-process it again.
            if (shouldPreProcess(entityAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing entity: " + entityAccessor.getJavaClassName());
                entityAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 2 - Pre-Process the list of mapped superclasses.
        for (MappedSuperclassAccessor mappedSuperclassAccessor : m_project.getMappedSuperclasses()) {
            if (shouldPreProcess(mappedSuperclassAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing mapped-superclass: " + mappedSuperclassAccessor.getJavaClassName());
                mappedSuperclassAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 3 - Pre-process the list of root embeddable accessors. This list will
        // have been build from step 1. Root embeddable accessors will have
        // an owning descriptor (used to determine access type).
        for (EmbeddableAccessor embeddableAccessor : m_project.getRootEmbeddableAccessors()) {
            if (shouldPreProcess(embeddableAccessor)) {
                //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Pre-processing embeddable: " + embeddableAccessor.getJavaClassName());
                embeddableAccessor.preProcessForCanonicalModel();
            }
        }
        
        // 4 - Go through the whole list of embeddables and process any that 
        // were not pre-processed. Only way this is true is if the embeddable
        // was not referenced from an entity (be it root or nested in a root
        // embeddable)
        for (EmbeddableAccessor embeddableAccessor : m_project.getEmbeddableAccessors()) {
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
        return ! accessor.isPreProcessed() && m_factory.isRoundElement((MetadataClass) accessor.getAccessibleObject());
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return m_puInfo.getPersistenceUnitName();
    }
}

