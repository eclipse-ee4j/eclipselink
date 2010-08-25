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
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 *     08/25/2010-2.2 Guy Pelletier 
 *       - 309445: CannonicalModelProcessor process all files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
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
    
    // Thing to note: persistence units can be reloaded. We do not however
    // reload their associated projects. Once the project is created, it remains 
    // around for the lifecycle of the compiler.
    private Map<String, PersistenceUnit> persistenceUnits;
    private Map<String, MetadataProject> metadataProjects;
    
    // This is a map of metadata classes built per compile round and used in
    // determining if it has been pre-processed.
    private Map<MetadataClass, Boolean> roundElements;
    
    // This is a map of metadata classes built per compile round.
    private Map<Element, MetadataClass> roundMetadataClasses;
    
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
        roundElements = new HashMap<MetadataClass, Boolean>();
        persistenceUnits = new HashMap<String, PersistenceUnit>();
        metadataProjects = new HashMap<String, MetadataProject>();
        roundMetadataClasses = new HashMap<Element, MetadataClass>();
    }
    
    /**
     * INTERNAL:
     */
    public void addPersistenceUnit(SEPersistenceUnitInfo puInfo, PersistenceUnit persistenceUnit) {
        persistenceUnits.put(puInfo.getPersistenceUnitName(), persistenceUnit);
    }
    
    /**
     * INTERNAL:
     * If the metadata class doesn't exist for the given element, we will build 
     * one and add it to our map of MetadataClasses before returning it. We keep 
     * our own map of metadata classes for each round element for a couple 
     * reasons:
     *   1- Most importantly, we use this map to avoid an infinite loop. Once we 
     *      kick off the visiting of a class, it snow balls into visiting and
     *      building other classes from the round (referenced from that class).
     *   2- For our own safety we cache metadata class keyed on the element we 
     *      built it for. This ensures we are always dealing with the correct 
     *      related metadata class.
     *   3- For each round we must update all metadata classes for each round
     *      element. 
     */
    public MetadataClass getMetadataClass(Element element) {
        if (roundMetadataClasses.containsKey(element)) {
            return roundMetadataClasses.get(element);
        } else {
            // As a performance gain, avoid visiting this class if, 1) it is not 
            // a round element and 2), we already have a metadata class for it.
            MetadataClass metadataClass = getMetadataClass(((TypeElement) element).getQualifiedName().toString());
            
            if (isRoundElement(element) || metadataClass == null) { 
                metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
                
                // Add it to the map of metadata classes from this round.
                roundMetadataClasses.put(element, metadataClass);
                
                // Kick off the visiting of elements.
                element.accept(elementVisitor, metadataClass);
              
                // The name of the metadata class is a qualified name from a 
                // type element. Set this name on the MetadataFactory map. We 
                // can't call addMetadataClass till we have visited the class.
                addMetadataClass(metadataClass);
            } 
            
            return metadataClass;
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
    public Collection<PersistenceUnit> getPersistenceUnits() {
        return persistenceUnits.values();
    }
    
    /**
     * INTERNAL:
     */
    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnv;
    }

    /**
     * INTERNAL:
     */
    public boolean isPreProcessedRoundElement(MetadataClass cls) {
        return isRoundElement(cls) && roundElements.get(cls);
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
        return roundElements.containsKey(cls);
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
    
    /**
     * INTERNAL:
     * Our processor will not visit generated elements, there is no need for
     * us to do this.
     */
    public void setEnvironments(ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        processingEnv = processingEnvironment;
        roundEnv = roundEnvironment;
        roundElements.clear();
        roundMetadataClasses.clear();
        
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
                roundElements.put(getMetadataClass(element), false);
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void setIsPreProcessedRoundElement(MetadataClass cls) {
        if (isRoundElement(cls)) {
            roundElements.put(cls, true);
        }
    }
}

