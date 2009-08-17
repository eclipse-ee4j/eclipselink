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
    public static String GENERIC_TYPE = "? extends Object";
    private StringTypeVisitor<String, Object> stringTypeVisitor;
    
    /**
     * INTERNAL:
     */
    public TypeVisitor() {
        stringTypeVisitor = new StringTypeVisitor<String, Object>();
    }
    
    /**
     * INTERNAL:
     */
    private String getRawClass(String type) {
        // This seems ridiculous ... there must be API to extract just the 
        // raw class??? Using simpleName hacks off the package ...
        if (type.indexOf("<") > -1) {
            return type.substring(0, type.indexOf("<"));
        }
        
        return type;
    }
    
    /**
     * INTERNAL:
     * Visit a declared array field.
     */
    @Override
    public MetadataAnnotatedElement visitArray(ArrayType arrayType, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(arrayType.accept(stringTypeVisitor, null));
        return annotatedElement;
    }
    
    /**
     * INTERNAL:
     * Visit a declared field.
     */
    @Override
    public MetadataAnnotatedElement visitDeclared(DeclaredType declaredType, MetadataAnnotatedElement annotatedElement) {
        // Set the type, which is the raw class.
        annotatedElement.setType(getRawClass(declaredType.accept(stringTypeVisitor, null)));
        // Internally, Eclipselink also wants this (raw class in the 0 position of the generic list).
        annotatedElement.addGenericType(annotatedElement.getType());
        
        for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
            annotatedElement.addGenericType(typeArgument.accept(stringTypeVisitor, null));
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
        annotatedElement.setType(errorType.accept(stringTypeVisitor, null));
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
            method.addParameter(getRawClass(parameter.accept(stringTypeVisitor, null)));
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
        annotatedElement.setType(noType.accept(stringTypeVisitor, null));
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
        annotatedElement.setType(nullType.accept(stringTypeVisitor, null));
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
        annotatedElement.setType(typeVariable.accept(stringTypeVisitor, null));
        return annotatedElement;
    }

    /**
     * INTERNAL:
     */
    @Override
    public MetadataAnnotatedElement visitWildcard(WildcardType wildcardType, MetadataAnnotatedElement annotatedElement) {
        annotatedElement.setType(wildcardType.accept(stringTypeVisitor, null));
        return annotatedElement;
    }
    
    /**
     * A generic type visitor. The main purpose of this visitor is to allow 
     * finer grain settings to be handled. For example, A generic type T. For 
     * all generic types we return/set the type of "? extends Object". 
     */
    class StringTypeVisitor<E, A> extends SimpleTypeVisitor6<String, Object> {
        /**
         * INTERNAL:
         */
        public StringTypeVisitor() {}
        
        /**
         * INTERNAL:
         */
        @Override
        public String visitArray(ArrayType arrayType, Object obj) {
            return arrayType.toString();
        }
        
        /**
         * INTERNAL:
         * Visit a declared field.
         */
        @Override
        public String visitDeclared(DeclaredType declaredType, Object obj) {
            return declaredType.toString();
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitError(ErrorType errorType, Object obj) {
            return GENERIC_TYPE;
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitExecutable(ExecutableType executableType, Object obj) {
            return executableType.toString();
        }
        
        /**
         * INTERNAL:
         */
        @Override
        public String visitNoType(NoType noType, Object obj) {
            return GENERIC_TYPE;
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitNull(NullType nullType, Object obj) {
            return GENERIC_TYPE;
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitPrimitive(PrimitiveType primitiveType, Object obj) {
            return primitiveType.toString();
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitTypeVariable(TypeVariable typeVariable, Object obj) {
            return GENERIC_TYPE;
        }

        /**
         * INTERNAL:
         */
        @Override
        public String visitWildcard(WildcardType wildcardType, Object obj) {
            return wildcardType.toString();
        }
    }
}
