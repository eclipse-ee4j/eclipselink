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
package org.eclipse.persistence.internal.jpa.modelgen.visitors;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractElementVisitor6;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.AnnotationValueVisitor;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.TypeVisitor;

/**
 * An element visitor. 
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.0
 */
public class ElementVisitor<R, P> extends AbstractElementVisitor6<MetadataAnnotatedElement, MetadataClass> {
    private ProcessingEnvironment m_processingEnv;
    
    /**
     * INTERNAL:
     */
    public ElementVisitor(ProcessingEnvironment processingEnv) {
        m_processingEnv = processingEnv;
    }
    
    /**
     * INTERNAL:
     * The pre-processing stages requires some knowledge of the annotation 
     * values (e.g. targetEntity) so we will visit the annotation values
     * and build complete MetadataAnnotation from the mirrors.
     */
    protected void buildMetadataAnnotations(MetadataAnnotatedElement annotatedElement, List<? extends AnnotationMirror> annotationMirrors) {
        AnnotationValueVisitor<Object, Object> visitor = new AnnotationValueVisitor<Object, Object>();
        
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            annotatedElement.addAnnotation((MetadataAnnotation) visitor.visitAnnotation(annotationMirror, null));
        }
    }
    
    /**
     * INTERNAL:
     */
    protected int getModifiers(Set<Modifier> modifiers) {
        int mods = 0;
        
        for (Modifier modifier : modifiers) {
            if (modifier.equals(Modifier.ABSTRACT)) {
                mods += java.lang.reflect.Modifier.ABSTRACT;
            } 
            
            if (modifier.equals(Modifier.FINAL)) {
                mods += java.lang.reflect.Modifier.FINAL;
            }
            
            if (modifier.equals(Modifier.NATIVE)) {
                mods += java.lang.reflect.Modifier.NATIVE;
            }
            
            if (modifier.equals(Modifier.PRIVATE)) {
                mods += java.lang.reflect.Modifier.PRIVATE;
            }
            
            if (modifier.equals(Modifier.PROTECTED)) {
                mods += java.lang.reflect.Modifier.PROTECTED;   
            }
            
            if (modifier.equals(Modifier.PUBLIC)) {
                mods += java.lang.reflect.Modifier.PUBLIC;   
            }
            
            if (modifier.equals(Modifier.STATIC)) {
                mods += java.lang.reflect.Modifier.STATIC;   
            }
            
            if (modifier.equals(Modifier.STRICTFP)) {
                mods += java.lang.reflect.Modifier.STRICT;
            }
            
            if (modifier.equals(Modifier.SYNCHRONIZED)) {
                mods += java.lang.reflect.Modifier.SYNCHRONIZED;    
            }
            
            if (modifier.equals(Modifier.TRANSIENT)) {
                mods += java.lang.reflect.Modifier.TRANSIENT;   
            }
            
            if (modifier.equals(Modifier.VOLATILE)) {
                mods += java.lang.reflect.Modifier.VOLATILE;    
            }
        }
        
        return mods;
    } 
    
    /**
     * INTERNAL:
     * Visit an executable and create a MetadataMethod object.
     */
    @Override
    public MetadataMethod visitExecutable(ExecutableElement executableElement, MetadataClass metadataClass) {
        MetadataMethod method = new MetadataMethod(metadataClass.getMetadataFactory(), metadataClass);
        
        // Set the name.
        method.setName(executableElement.getSimpleName().toString());
        
        // Set the attribute name.
        method.setAttributeName(Helper.getAttributeNameFromMethodName(method.getName()));
        
        // Set the modifiers.
        method.setModifiers(getModifiers(executableElement.getModifiers()));

        // Visit executable element for the parameters, return type and generic type.
        TypeVisitor<MetadataMethod, MetadataMethod> visitor = new TypeVisitor<MetadataMethod, MetadataMethod>();
        executableElement.asType().accept(visitor, method);
        
        // Set the annotations.
        buildMetadataAnnotations(method, executableElement.getAnnotationMirrors());

        // Handle multiple methods with the same name.
        MetadataMethod existing = metadataClass.getMethods().get(method.getName());
        if (existing == null) {
            metadataClass.addMethod(method);
        } else {
            while (existing.getNext() != null) {
                existing = existing.getNext();
            }
            existing.setNext(method);
        }
        
        return method;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataClass visitPackage(PackageElement arg0, MetadataClass metadataClass) {
        m_processingEnv.getMessager().printMessage(Kind.NOTE, "ElementVisitor Package NOT IMPLEMENTED : " + arg0);
        return null;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataClass visitType(TypeElement typeElement, MetadataClass metadataClass) {
        //m_processingEnv.getMessager().printMessage(Kind.NOTE, "Visiting class: " + typeElement);
        
        MetadataMirrorFactory factory = ((MetadataMirrorFactory) metadataClass.getMetadataFactory());
        
        // Set the qualified name.
        metadataClass.setName(typeElement.getQualifiedName().toString());
        
        // Set the modifiers.
        metadataClass.setModifiers(getModifiers(typeElement.getModifiers()));
        
        // Visit the enclosed elements.
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind().isClass()) {
                metadataClass.addEnclosedClass(factory.getMetadataClass(enclosedElement));
            } else {
                enclosedElement.accept(this, metadataClass);
            }
        }
        
        // Add the interfaces.
        for (TypeMirror interfaceCls : typeElement.getInterfaces()) {
            metadataClass.addInterface(factory.getMetadataClass(interfaceCls).getName());
        }
        
        // Set the superclass name (if there is one) 
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass != null) {
            metadataClass.setSuperclassName(factory.getMetadataClass(superclass).getName());
        }
        
        // Visit the type element for type and generic type.
        TypeVisitor<MetadataClass, MetadataClass> visitor = new TypeVisitor<MetadataClass, MetadataClass>();
        typeElement.asType().accept(visitor, metadataClass);
        
        // Set the annotations.
        buildMetadataAnnotations(metadataClass, typeElement.getAnnotationMirrors());
        
        return metadataClass;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataClass visitTypeParameter(TypeParameterElement arg0, MetadataClass metadataClass) {
        m_processingEnv.getMessager().printMessage(Kind.NOTE, "ElementVisitor TypeParameter NOT IMPLEMENTED : " + arg0);
        return null;
    }

    /**
     * INTERNAL:
     * Visit a variable and create a MetadataField object.
     */
    @Override
    public MetadataField visitVariable(VariableElement variableElement, MetadataClass metadataClass) {
        MetadataField field = new MetadataField(metadataClass.getMetadataFactory());
        
        // Set the name.
        field.setName(variableElement.getSimpleName().toString());
        
        // Set the attribute name (same as name in this case)
        field.setAttributeName(field.getName());
        
        // Visit the variable element for type and generic type.
        TypeVisitor<MetadataField, MetadataField> visitor = new TypeVisitor<MetadataField, MetadataField>();
        variableElement.asType().accept(visitor, field);
        
        // Set the modifiers.
        field.setModifiers(getModifiers(variableElement.getModifiers()));

        // Set the annotations.
        buildMetadataAnnotations(field, variableElement.getAnnotationMirrors());
        
        // Add the field to the class and return the field.
        metadataClass.addField(field);
        return field;
    }
}
