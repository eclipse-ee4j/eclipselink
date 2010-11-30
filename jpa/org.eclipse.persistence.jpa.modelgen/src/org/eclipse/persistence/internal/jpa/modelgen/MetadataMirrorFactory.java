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
 *     11/23/2010-2.2 Guy Pelletier 
 *       - 330660: Canonical model generator throws ClassCastException when using package-info.java
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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
    
    // This is a map of element/metadata classes built per compile round. This 
    // map is cleared for each compile round.
    private Map<Element, MetadataClass> roundElements;
    
    // This is a hash set of metadata classes per compile round. This set is
    // cleared for each compile round.
    private HashSet<MetadataClass> roundMetadataClasses;
    
    private ProcessingEnvironment processingEnv;

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
        roundElements = new HashMap<Element, MetadataClass>();
        roundMetadataClasses = new HashSet<MetadataClass>();
        persistenceUnits = new HashMap<String, PersistenceUnit>();
        metadataProjects = new HashMap<String, MetadataProject>();
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
        MetadataClass metadataClass = roundElements.get(element);
        
        if (metadataClass == null) {
            // As a performance gain, avoid visiting this class if it is not a 
            // round element. We must re-visit round elements.
            if (isRoundElement(element)) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "Building metadata class for round element: " + element);
                metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
                roundElements.put(element, metadataClass);
                roundMetadataClasses.add(metadataClass);
                
                // Kick off the visiting of elements.
                element.accept(elementVisitor, metadataClass);
              
                // The name of the metadata class is a qualified name from a 
                // type element. Set this name on the MetadataFactory map. We 
                // can't call addMetadataClass till we have visited the class.
                addMetadataClass(metadataClass);
            } else {
                // So we are not a round element, the outcome is as follows:
                //  - TypeParameterElement: they are stored by simple name, so 
                //    if it exists in our map return it, otherwise visit it.
                //  - TypeElement, and not in the existing class map, return 
                //    simple MetadataClass with only a name.
                //  - TypeElement, in existing class map, return it.
                //  - Everything else ... look up the metadata class using the
                //    toString value.
                if (element instanceof TypeParameterElement) {
                    // You could in theory just visit it since we don't do much with it.
                    String simpleName = element.getSimpleName().toString();
                    
                    if (metadataClassExists(simpleName)) {
                        metadataClass = getMetadataClass(simpleName);
                    } else {
                        metadataClass = new MetadataClass(MetadataMirrorFactory.this, "");
                        element.accept(elementVisitor, metadataClass);
                        addMetadataClass(metadataClass);
                    }
                } else {
                    //processingEnv.getMessager().printMessage(Kind.NOTE, "Element: " + element.toString());
                    metadataClass = getMetadataClass((element instanceof TypeElement) ? ((TypeElement) element).getQualifiedName().toString() : element.toString());    
                }
            }
        }
        
        return metadataClass;
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
    public Map<Element, MetadataClass> getRoundElements() {
        return roundElements;
    }
    
    /**
     * INTENAL:
     */
    public boolean isRoundElement(Element element) {
        return roundElements.containsKey(element);
    }
    
    /**
     * INTENAL:
     */
    public boolean isRoundElement(MetadataClass cls) {
        return roundMetadataClasses.contains(cls);
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
        roundElements.clear();
        roundMetadataClasses.clear();
        
        // Initialize the element visitor if it is null.
        if (elementVisitor == null) {
            elementVisitor = new ElementVisitor<MetadataClass, MetadataClass>(processingEnv);
        }
        
        // Go through the root elements and gather the round elements that
        // we care about. It is crucial to not call getMetadataClass(element)
        // before we gather our list of round elements. Calling this method will
        // trigger the visiting of elements which has a dependency on round
        // elements.
        for (Element element : roundEnvironment.getRootElements()) {
            // Look at only class elements.
            if (element.getKind().isClass()) {
                // Don't look at the generated classes. We must look at all the
                // classes and not only those decorated with @Entity, 
                // @MappedSuperclass or @Embeddable, since it may be a class
                // defined solely in XML and we want to make sure we look at
                // the changes for those classes as well.
                if (element.getAnnotation(javax.annotation.Generated.class) == null) { 
                    roundElements.put(element, null);
                }
            }
        }
        
        // Visit all the round elements now. These may be new elements or 
        // existing elements that were changed. We must build or re-build the 
        // class metadata for that element to be re-used with new accessors
        // needing to pre-processed.
        for (Element element : roundElements.keySet()) {
            getMetadataClass(element);
        }
    }
}

