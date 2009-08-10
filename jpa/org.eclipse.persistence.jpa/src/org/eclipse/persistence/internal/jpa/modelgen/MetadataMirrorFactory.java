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
package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.ElementVisitor;

/**
 * The metadata factory that employs java mirrors to create MetadataClass.
 * This factory is used for Mirror elements, any JDK classes run through the
 * existing ASM factory that we use for regular metadata processing. 
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.0
 */
public class MetadataMirrorFactory extends MetadataFactory {
    private ProcessingEnvironment m_processingEnv;
    private HashSet<String> m_roundElements;
    
    private Map<String, MetadataClass> m_metadataClasses;

    /**
     * INTERNAL:
     * The factory is kept as a static object to the persistence unit. The first
     * time the factory is initialized, we will get a full list of root 
     * elements. Build MetadataClass for them right away. We don't want to
     * rebuild the factory every time otherwise we lose already built metadata
     * classes and may not be able to rebuild till individual elements are
     * 'touched' or if the project is rebuilt as a whole.
     */
    protected MetadataMirrorFactory(MetadataLogger logger, ClassLoader loader) {
        super(logger, loader);
        m_roundElements = new HashSet<String>();
        m_metadataClasses = new HashMap<String, MetadataClass>();
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataClass buildClassMetadata(Element element) {
        MetadataClass metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
                
        // Kick off the visiting of elements.
        ElementVisitor<MetadataClass, MetadataClass> visitor = new ElementVisitor<MetadataClass, MetadataClass>(m_processingEnv);
        element.accept(visitor, metadataClass);
            
        // The name off the metadata class is a qualified name from a type
        // element. Set this on the MetadataFactory map.
        getMetadata().put(metadataClass.getName(), metadataClass);
        
        // For our own safety we cache another map of metadata class keyed on 
        // the toString value the Element we built it for. This ensures we are 
        // always dealing with the correct related metadata class.
        m_metadataClasses.put(element.toString(), metadataClass);
            
        return metadataClass;
    }
    
    /**
     * INTERNAL:
     * If the adds a new element will build it and add it to our list of
     * MetadataClasses.
     */
    public MetadataClass getClassMetadata(Element element) {
        if (m_metadataClasses.containsKey(element.toString())) {
            return m_metadataClasses.get(element.toString());
        } else {
            return buildClassMetadata(element);
        }
    }
    
    /**
     * INTERNAL:
     * If the adds a new element will build it and add it to our list of
     * MetadataClasses.
     */
    public MetadataClass getClassMetadata(TypeMirror typeMirror) {
        Element element = m_processingEnv.getTypeUtils().asElement(typeMirror);
        
        if (element == null) {
            // TODO: Humm ... bit of a hack? Can't explain it though. Sometimes
            // getting a typeMirror for <none> ...
            String name = typeMirror.toString();
            
            if (m_metadataClasses.containsKey(name)) {
                return m_metadataClasses.get(name);
            } else {
                MetadataClass metadataClass = new MetadataClass(MetadataMirrorFactory.this, name);
                m_metadataClasses.put(name, metadataClass);
                return metadataClass;
            }
        } else {
            return getClassMetadata(element);
        }
    }
    
    /**
     * INTERNAL:
     */
    public ProcessingEnvironment getProcessingEnvironment() {
        return m_processingEnv;
    }

    /**
     * INTENAL:
     */
    public boolean isRoundElement(MetadataClass cls) {
        return m_roundElements.contains(cls.getName());
    }
    
    /**
     * INTERNAL:
     */
    public void setEnvironments(ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        m_processingEnv = processingEnvironment;
        m_roundElements.clear();
        
        // Visit all the root elements now. These may be new elements or 
        // existing elements that were changed. We must build or re-build the 
        // class metadata for that element. Also we will only pre-process
        // the root elements MetadataClasses
        for (Element element : roundEnvironment.getRootElements()) {
            // Ignore generated classes, we are not going to visit them
            // or do anything with them at this point.
            if (! element.getSimpleName().toString().endsWith("_")) {
                m_roundElements.add(buildClassMetadata(element).getName());
            }
        }
    }
}

