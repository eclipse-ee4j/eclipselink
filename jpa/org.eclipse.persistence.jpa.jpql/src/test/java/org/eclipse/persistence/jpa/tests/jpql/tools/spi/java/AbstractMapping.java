/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;
import static org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType.*;

/**
 * The abstract implementation of {@link IMapping} that is wrapping the runtime representation
 * of a persistent attribute.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractMapping implements IMapping {

    /**
     * The type of the persistent attribute.
     *
     * @see org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType IMappingType
     */
    private int mappingType;

    /**
     * The Java {@link Member} wrapped by this mapping, which represents a persistent attribute.
     */
    private Member member;

    /**
     * The parent of this {@link IMapping}.
     */
    private IManagedType parent;

    /**
     * The external form representing the type of the persistent attribute.
     */
    private IType type;

    /**
     * The external form representing the type declaration of the persistent attribute
     */
    private ITypeDeclaration typeDeclaration;

    /**
     * Creates a new <code>AbstractMapping</code>.
     *
     * @param parent The parent of this mapping
     * @param member The Java {@link Member} wrapped by this mapping, which represents a persistent
     * attribute
     */
    protected AbstractMapping(IManagedType parent, Member member) {
        super();
        this.parent      = parent;
        this.member      = member;
        this.mappingType = -1;
    }

    protected ITypeDeclaration buildTypeDeclaration() {
        return new JavaTypeDeclaration(
            parent.getProvider().getTypeRepository(),
            getType(),
            getMemberGenericType(),
            getMemberType().isArray()
        );
    }

    /**
     * Calculates the type of the persistent attribute represented by this external form.
     *
     * @return The mapping type, which is one of the constants defined in
     * {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType IMappingType} when the provider is generic JPA
     */
    protected int calculateMappingType() {
        return calculateMappingType(getMemberAnnotations());
    }

    /**
     * Calculates the type of the mapping represented by this external form.
     *
     * @param annotations The {@link Annotation Annotations} that are present on the member
     * @return The mapping type, which is one of the constants defined in
     * {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType IMappingType} when the provider is generic JPA
     */
    protected int calculateMappingType(Annotation[] annotations) {

        if (hasAnnotation(annotations, ElementCollection.class)) {
            return ELEMENT_COLLECTION;
        }

        if (hasAnnotation(annotations, Embedded.class)) {
            return EMBEDDED;
        }

        if (hasAnnotation(annotations, EmbeddedId.class)) {
            return EMBEDDED_ID;
        }

        if (hasAnnotation(annotations, Id.class)) {
            return ID;
        }

        if (hasAnnotation(annotations, ManyToMany.class)) {
            return MANY_TO_MANY;
        }

        if (hasAnnotation(annotations, ManyToOne.class)) {
            return MANY_TO_ONE;
        }

        if (hasAnnotation(annotations, OneToMany.class)) {
            return ONE_TO_MANY;
        }

        if (hasAnnotation(annotations, OneToOne.class)) {
            return ONE_TO_ONE;
        }

        if (hasAnnotation(annotations, Transient.class)) {
            return TRANSIENT;
        }

        if (hasAnnotation(annotations, Version.class)) {
            return VERSION;
        }

        // Default
        IType type = getType();
        TypeHelper typeHelper = getTypeRepository().getTypeHelper();

        // M:M
        if (typeHelper.isCollectionType(type) ||
            typeHelper.isMapType(type)) {

            return ONE_TO_MANY;
        }

        // 1:1
        if (parent.getProvider().getEntity(type) != null) {
            return ONE_TO_ONE;
        }

        // Embedded
        if (parent.getProvider().getEmbeddable(type) != null) {
            return EMBEDDED;
        }

        // Basic
        return BASIC;
    }

    @Override
    public int compareTo(IMapping mapping) {
        return getName().compareTo(mapping.getName());
    }

    @Override
    public int getMappingType() {
        if (mappingType == -1) {
            mappingType = calculateMappingType();
        }
        return mappingType;
    }

    /**
     * Returns the Java {@link Member} wrapped by this mapping, which represents a persistent attribute.
     *
     * @return The Java {@link Member}
     */
    public Member getMember() {
        return member;
    }

    protected abstract Annotation[] getMemberAnnotations();

    protected abstract Type getMemberGenericType();

    protected abstract Class<?> getMemberType();

    @Override
    public String getName() {
        return member.getName();
    }

    @Override
    public IManagedType getParent() {
        return parent;
    }

    @Override
    public IType getType() {
        if (type == null) {
            type = getTypeRepository().getType(getMemberType());
        }
        return type;
    }

    @Override
    public ITypeDeclaration getTypeDeclaration() {
        if (typeDeclaration == null) {
            typeDeclaration = buildTypeDeclaration();
        }
        return typeDeclaration;
    }

    protected ITypeRepository getTypeRepository() {
        return parent.getProvider().getTypeRepository();
    }

    protected boolean hasAnnotation(Annotation[] annotations,
                                    Class<? extends Annotation> annotationType) {

        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationType) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasAnnotation(Annotation[] annotations, String annotationType) {

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCollection() {
        switch (getMappingType()) {
            case ELEMENT_COLLECTION:
            case MANY_TO_MANY:
            case ONE_TO_MANY: return true;
            default:          return false;
        }
    }

    @Override
    public boolean isEmbeddable() {
        switch (getMappingType()) {
            case EMBEDDED:
            case EMBEDDED_ID: return true;
            default:          return false;
        }
    }

    @Override
    public boolean isProperty() {
        switch (getMappingType()) {
            case BASIC:
            case ID:
            case VERSION: return true;
            default:      return false;
        }
    }

    @Override
    public boolean isRelationship() {
        switch (getMappingType()) {
            case ELEMENT_COLLECTION:
            case EMBEDDED_ID:
            case MANY_TO_MANY:
            case MANY_TO_ONE:
            case ONE_TO_MANY:
            case ONE_TO_ONE: return true;
            default:         return false;
        }
    }

    @Override
    public boolean isTransient() {
        return getMappingType() == TRANSIENT;
    }

    @Override
    public String toString() {
        return getName() + " : " + getTypeDeclaration();
    }
}
