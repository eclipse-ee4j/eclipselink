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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor6;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

/**
 * A type visitor. 
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.0
 */
public class TypeVisitor<R, P> extends SimpleTypeVisitor6<MetadataAnnotatedElement, MetadataAnnotatedElement> {
    private ProcessingEnvironment m_processingEnv;
    
    /**
     * INTERNAL:
     */
    public TypeVisitor(ProcessingEnvironment processingEnv) {
        m_processingEnv = processingEnv;
    }
    
    /**
     * INTERNAL:
     * Visit a declared array field.
     */
    @Override
    public MetadataAnnotatedElement visitArray(ArrayType arrayType, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(arrayType.toString());
        return annotatedElement;
    }
    
    /**
     * INTERNAL:
     * Visit a declared field.
     */
    @Override
    public MetadataAnnotatedElement visitDeclared(DeclaredType declaredType, MetadataAnnotatedElement annotatedElement) {
        // Set the type, use the erasure.
        annotatedElement.setType(m_processingEnv.getTypeUtils().erasure(declaredType).toString());
        // Internally, Eclipselink also wants this.
        annotatedElement.addGenericType(annotatedElement.getType());
        
        for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
            annotatedElement.addGenericType(typeArgument.toString());
        }
        
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitError(ErrorType errorType, MetadataAnnotatedElement annotatedElement) {
        // We will hit this case when there exists a compile error on the model.
        // However our annotation processor will still be called and we 
        // therefore want to ensure our annotatedElement still has a type set
        // on it and not null. This will avoid exceptions when we go through 
        // the pre-processing of our metadata classes.
        annotatedElement.setType(String.class.toString());
        
        return annotatedElement;
    }

    /**
     * INTERNAL:
     * Visit a method.
     */
    @Override
    public MetadataAnnotatedElement visitExecutable(ExecutableType executableType, MetadataAnnotatedElement annotatedElement) {
        MetadataMethod method = (MetadataMethod) annotatedElement;
        
        // Set the parameters.
        for (TypeMirror parameter : executableType.getParameterTypes()) {
            method.addParameter(m_processingEnv.getTypeUtils().erasure(parameter).toString());
        }
        
        // Visit the return type (will set the type and generic types).
        executableType.getReturnType().accept(this, method);
        method.setReturnType(method.getType());
        
        return method;
    }
    
    /**
     * INTERNAL:
     * Method that returns void.
     */
    @Override
    public MetadataAnnotatedElement visitNoType(NoType noType, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(noType.toString());
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitNull(NullType nullType, MetadataAnnotatedElement annotatedElement) {
        // We will hit this case when there exists a compile error on the model??
        // However our annotation processor will still be called and we 
        // therefore want to ensure our annotatedElement still has a type set
        // on it and not null. This will avoid exceptions when we go through 
        // the pre-processing of our metadata classes.
        annotatedElement.setType(String.class.toString());
        return annotatedElement;
    }

    /**
     * INTERNAL:
     * Visit a declared primitive field.
     */
    @Override
    public MetadataAnnotatedElement visitPrimitive(PrimitiveType primitiveType, MetadataAnnotatedElement annotatedElement) {
        // We can not set the boxed type here. We must preserve the actual type
        // otherwise during accessor pre-process a validation exception will
        // occur under property access since we'll look for the equivalent
        // set method with the boxed class which will not be found. We deal with
        // boxing the type when generating the canonical model.
        annotatedElement.setPrimitiveType(primitiveType);
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitTypeVariable(TypeVariable typeVariable, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(typeVariable.toString());
        //m_processingEnv.getMessager().printMessage(Kind.NOTE, "upper bound: " + typeVariable.getLowerBound() + " lower bound: " + typeVariable.getLowerBound());
        //m_processingEnv.getMessager().printMessage(Kind.NOTE, "TypeVariable visit NOT IMPLEMENTED : " + typeVariable + " - " + annotatedElement);
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitWildcard(WildcardType wildcardType, MetadataAnnotatedElement annotatedElement) {
        //m_processingEnv.getMessager().printMessage(Kind.NOTE, "WildcardType visit NOT IMPLEMENTED : " + wildcardType);
        return null;
    }
}
