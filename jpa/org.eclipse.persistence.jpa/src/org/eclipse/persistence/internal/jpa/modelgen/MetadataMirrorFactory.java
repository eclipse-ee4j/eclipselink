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
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.ElementVisitor;

/**
 * This metadata factory employs java mirrors to create MetadataClass and is
 * used with the canonical model processor.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.0
 */
public class MetadataMirrorFactory extends MetadataFactory {
    private ProcessingEnvironment m_processingEnv;
    private HashSet<String> m_roundElements;
    
    private Map<String, MetadataClass> m_metadataClassesFromElements;

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
        m_metadataClassesFromElements = new HashMap<String, MetadataClass>();
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataClass buildMetadataClass(Element element) {
        MetadataClass metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
                
        // Kick off the visiting of elements.
        ElementVisitor<MetadataClass, MetadataClass> visitor = new ElementVisitor<MetadataClass, MetadataClass>(m_processingEnv);
        element.accept(visitor, metadataClass);
            
        // The name off the metadata class is a qualified name from a type
        // element. Set this on the MetadataFactory map.
        addMetadataClass(metadataClass);
        
        // For our own safety we cache another map of metadata class keyed on 
        // the toString value the Element we built it for. This ensures we are 
        // always dealing with the correct related metadata class.
        m_metadataClassesFromElements.put(element.toString(), metadataClass);
            
        return metadataClass;
    }
    
    /**
     * INTERNAL:
     * If the adds a new element will build it and add it to our list of
     * MetadataClasses.
     */
    public MetadataClass getMetadataClass(Element element) {
        if (m_metadataClassesFromElements.containsKey(element.toString())) {
            return m_metadataClassesFromElements.get(element.toString());
        } else {
            return buildMetadataClass(element);
        }
    }
    
    /**
     * INTERNAL:
     * This assumes that every class that is accessed in the pre-process
     * methods will have a class metadata built for them already. That is,
     * our visitor must visit every class that the pre-process will want to
     * look at. All return types and field types need a metadata class or
     * else kaboom ... null pointer!
     */
    @Override
    public MetadataClass getMetadataClass(String className) {
        if (! metadataClassExists(className)) {
            // By the time this method is called we should have built a 
            // MetadataClass for all the model elements (and then some ... ) 
            // which are the only classes we really care about. This is acting 
            // like a catch all and for any jdk classes just return a 
            // MetadataClass with the same class name.
            addMetadataClass(new MetadataClass(this, className));
        }
        
        return getMetadataClasses().get(className);
    }
    
    /**
     * INTERNAL:
     * If the adds a new element will build it and add it to our list of
     * MetadataClasses.
     */
    public MetadataClass getMetadataClass(TypeMirror typeMirror) {
        Element element = m_processingEnv.getTypeUtils().asElement(typeMirror);
        
        if (element == null) {
            return getMetadataClass(typeMirror.toString());
        } else {
            return getMetadataClass(element);
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
                m_roundElements.add(buildMetadataClass(element).getName());
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void resolveGenericTypes(MetadataClass child, List<String> genericTypes, MetadataClass parent, MetadataDescriptor descriptor) {
        // If we have a generic parent we need to grab our generic types
        // that may be used (and therefore need to be resolved) to map
        // accessors correctly.
        /*
        if (genericTypes != null) { 
            
            List<String> parentGenericTypes = parent.getGenericType();
            if (parentGenericTypes != null) {
                List genericParentTemp = new ArrayList(genericTypes);
                genericParentTemp.removeAll(child.getInterfaces());
                
                int size = genericParentTemp.size();
                int parentIndex = 0;
                
                for (int index = genericTypes.indexOf(parent.getName()) + 1; index < size; index++) {
                    String actualTypeArgument = genericTypes.get(index);
                    String variable = parentGenericTypes.get(parentIndex);
                    parentIndex = parentIndex + 3;
                    
                    // We are building bottom up and need to link up any 
                    // TypeVariables with the actual class from the originating 
                    // entity.
                    if (actualTypeArgument.length() == 1) {
                        index++;
                        actualTypeArgument = genericTypes.get(index);
                        descriptor.addGenericType(variable, descriptor.getGenericType(actualTypeArgument));
                    } else {
                        descriptor.addGenericType(variable, actualTypeArgument);
                    }
                }
            }
        }
        */
    }
}

