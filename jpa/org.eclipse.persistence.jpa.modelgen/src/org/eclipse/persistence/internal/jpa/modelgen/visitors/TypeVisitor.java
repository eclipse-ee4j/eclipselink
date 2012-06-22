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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.modelgen.visitors;

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
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;

/**
 * A type visitor. 
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class TypeVisitor<R, P> extends SimpleTypeVisitor6<MetadataAnnotatedElement, MetadataAnnotatedElement> {
    public static String GENERIC_TYPE = "? extends Object";
    
    /**
     * INTERNAL:
     */
    public TypeVisitor() {}
    
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
     * Visit a declared field or Class.
     */
    @Override
    public MetadataAnnotatedElement visitDeclared(DeclaredType declaredType, MetadataAnnotatedElement annotatedElement) {
        // Get the metadata class of the declared type from the factory.
        MetadataMirrorFactory factory = (MetadataMirrorFactory) annotatedElement.getMetadataFactory();
        MetadataClass cls = factory.getMetadataClass(declaredType);
        
        // Set the type, which is the class name.
        annotatedElement.setType(cls.getName());
        
        // Set the generic types. Internally EclipseLink wants the class name 
        // in the 0 position of the generic list.
        annotatedElement.addGenericType(cls.getName());
        
        for (TypeMirror typeArgument : declaredType.getTypeArguments()) { 
            // Set the type from the metadata class as it may be a generic and
            // we don't want to set the letter type, rather our default GENERIC_TYPE.
            annotatedElement.addGenericType(factory.getMetadataClass(typeArgument).getType());
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
        annotatedElement.setType(GENERIC_TYPE);
        return annotatedElement;
    }

    /**
     * INTERNAL:
     * Visit a method.
     */
    @Override
    public MetadataAnnotatedElement visitExecutable(ExecutableType executableType, MetadataAnnotatedElement annotatedElement) {
        MetadataMirrorFactory factory = ((MetadataMirrorFactory) annotatedElement.getMetadataFactory());
        MetadataMethod method = (MetadataMethod) annotatedElement;
        
        // Set the parameters.
        for (TypeMirror parameter : executableType.getParameterTypes()) {
            method.addParameter(factory.getMetadataClass(parameter).getType());
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
        // Should this be Void.class?
        annotatedElement.setType(GENERIC_TYPE);
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
        annotatedElement.setType(GENERIC_TYPE);
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
        annotatedElement.setType(GENERIC_TYPE);
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitWildcard(WildcardType wildcardType, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(wildcardType.toString());
        return annotatedElement;
    }
}
