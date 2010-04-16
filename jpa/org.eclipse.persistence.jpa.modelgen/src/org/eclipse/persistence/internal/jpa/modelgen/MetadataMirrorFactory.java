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
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.ElementVisitor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * This metadata factory employs java mirrors to create MetadataClass and is
 * used with the canonical model processor.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class MetadataMirrorFactory extends MetadataFactory {
    private ElementVisitor<MetadataClass, MetadataClass> elementVisitor;
    private HashSet<MetadataClass> roundElements;
    
    // Per persistence unit.
    private Map<String, MetadataProject> metadataProjects;
    private Map<Element, MetadataClass> metadataClassesFromElements;
    
    private ProcessingEnvironment processingEnv;
    private RoundEnvironment roundEnv;

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
        roundElements = new HashSet<MetadataClass>();
        metadataProjects = new HashMap<String, MetadataProject>();
        metadataClassesFromElements = new HashMap<Element, MetadataClass>();
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataClass buildMetadataClass(Element element) {
        MetadataClass metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
        
        // We keep our own map of classes for a couple reasons:
        // 1- Most importantly, we use this map to avoid an infinite loop. Once
        // we kick off the visiting of this class, it snow balls into visiting
        // the other classes.
        // 2- For our own safety we cache metadata class keyed on the Element we 
        // built it for. This ensures we are always dealing with the correct 
        // related metadata class.
        // 3- we can't call addMetadataClass here since it adds the class to the
        // map keyed on its name. We won't know the class name till we visit it.
        metadataClassesFromElements.put(element, metadataClass);
        
        // Kick off the visiting of elements.
        element.accept(elementVisitor, metadataClass);
        
        // The name off the metadata class is a qualified name from a type
        // element. Set this on the MetadataFactory map.
        addMetadataClass(metadataClass);
        
        return metadataClass;
    }
    
    /**
     * INTERNAL:
     * If the element doesn't exist we will build it and add it to our list of
     * MetadataClasses before returning it.
     */
    public MetadataClass getMetadataClass(Element element) {
        if (metadataClassesFromElements.containsKey(element)) {
            return metadataClassesFromElements.get(element);
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
     * else kaboom, null pointer!
     */
    @Override
    public MetadataClass getMetadataClass(String className) {
        if (className == null) {
            return null;
        } else {
        	if (! metadataClassExists(className)) {
        		// By the time this method is called we should have built a 
        		// MetadataClass for all the model elements (and then some) which 
        		// are the only classes we really care about. This is acting like a 
        		// catch all for any jdk classes we didn't visit and just returns a 
        		// MetadataClass with the same class name.
        		addMetadataClass(new MetadataClass(this, className));
        	}
        	
        	return getMetadataClasses().get(className);
        }
    }
    
    /**
     * INTERNAL:
     * If the adds a new element will build it and add it to our list of
     * MetadataClasses.
     */
    public MetadataClass getMetadataClass(TypeMirror typeMirror) {
        Element element = processingEnv.getTypeUtils().asElement(typeMirror);
        
        if (element == null) {
            // This case hits when we are passed a TypeMirror of <none>. Not
            // 100% on the whole story here, either way we create a metadata 
            // class with that name and carry on. The case also hits when we 
            // ask for a metadata class for array types.  
            return getMetadataClass(typeMirror.toString());
        } else {
            return getMetadataClass(element);
        }
    }
    
    /**
     * INTERNAL:
     * We preserve state from each processor run by holding static references
     * to projects. 
     */
    public MetadataProject getMetadataProject(SEPersistenceUnitInfo puInfo) {
        if (! metadataProjects.containsKey(puInfo.getPersistenceUnitName())) {
            MetadataProject project = new MetadataProject(puInfo, new ServerSession(new Project(new DatabaseLogin())), false, false);
            metadataProjects.put(puInfo.getPersistenceUnitName(), project);
            return project;
        } else {
            return metadataProjects.get(puInfo.getPersistenceUnitName());
        }
    }
    
    /**
     * INTERNAL:
     */
    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnv;
    }

    /**
     * INTENAL:
     */
    public boolean isRoundElement(Element element) {
        return roundEnv.getRootElements().contains(element);
    }
    
    /**
     * INTENAL:
     */
    public boolean isRoundElement(MetadataClass cls) {
        return roundElements.contains(cls);
    }
    
    /**
     * INTERNAL:
     * Our processor will not visit generated elements, there is no need for
     * us to do this.
     */
    public void setEnvironments(ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        processingEnv = processingEnvironment;
        roundEnv = roundEnvironment;
        roundElements.clear();
        
        // Initialize the element visitor if it is null.
        if (elementVisitor == null) {
            elementVisitor = new ElementVisitor<MetadataClass, MetadataClass>(processingEnv);
        }
        
        // Visit all the root elements now. These may be new elements or 
        // existing elements that were changed. We must build or re-build the 
        // class metadata for that element to be re-used with new accessors
        // needing to pre-processed.
        for (Element element : roundEnvironment.getRootElements()) {
            if (element.getAnnotation(javax.annotation.Generated.class) == null) { 
                processingEnv.getMessager().printMessage(Kind.NOTE, "Building metadata class for round element: " + element);
                roundElements.add(buildMetadataClass(element));
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void resolveGenericTypes(MetadataClass child, List<String> genericTypes, MetadataClass parent, MetadataDescriptor descriptor) {
        // Our metadata factory does not and can not resolve the types since
        // we are writing static attributes on our generated class. This 
        // factory will use types of "? extends Object". So don't need to
        // resolve anything here. No work is good work!
    }
}

