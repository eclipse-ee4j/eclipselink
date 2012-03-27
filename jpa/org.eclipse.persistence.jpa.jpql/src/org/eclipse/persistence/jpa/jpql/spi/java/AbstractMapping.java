/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

import static org.eclipse.persistence.jpa.jpql.spi.IMappingType.*;

/**
 * The abstract implementation of {@link IMapping} that is wrapping the runtime representation
 * of a persistent attribute.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractMapping implements IMapping {

	/**
	 * The type of the persistent attribute.
	 *
	 * @see org.eclipse.persistence.jpa.jpql.spi.IMappingType IMappingType
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
	 * @return The mapping type, which is one of the constants defined in {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMappingType IMappingType} when the provider is generic JPA
	 */
	protected int calculateMappingType() {
		return calculateMappingType(getMemberAnnotations());
	}

	/**
	 * Calculates the type of the mapping represented by this external form.
	 *
	 * @param annotations The {@link Annotation Annotations} that are present on the member
	 * @return The mapping type, which is one of the constants defined in {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMappingType IMappingType} when the provider is generic JPA
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

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IMapping mapping) {
		return getName().compareTo(mapping.getName());
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return member.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		if (type == null) {
			type = getTypeRepository().getType(getMemberType());
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollection() {
		switch (getMappingType()) {
			case ELEMENT_COLLECTION:
			case MANY_TO_MANY:
			case ONE_TO_MANY: return true;
			default:          return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProperty() {
		switch (getMappingType()) {
			case BASIC:
			case ID:
			case VERSION: return true;
			default:      return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransient() {
		return getMappingType() == TRANSIENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}
}