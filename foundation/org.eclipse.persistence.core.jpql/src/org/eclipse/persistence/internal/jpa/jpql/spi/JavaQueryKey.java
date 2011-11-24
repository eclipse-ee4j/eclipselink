/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql.spi;

import java.lang.annotation.Annotation;
import org.eclipse.persistence.jpa.jpql.spi.IEclipseLinkMappingType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaTypeDeclaration;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * The concrete implementation of {@link IMapping} that is wrapping the runtime representation of a
 * query key.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaQueryKey extends AbstractMapping {

	/**
	 * The alias of a database field.
	 */
	private final QueryKey queryKey;

	/**
	 * The cached {@link ITypeDeclaration} of this query key.
	 */
	private ITypeDeclaration typeDeclaration;

	/**
	 * Creates a new <code>JavaQueryKey</code>.
	 *
	 * @param parent The parent of this mapping
	 * @param queryKey The alias of a database field
	 */
	JavaQueryKey(IManagedType parent, QueryKey queryKey) {
		super(parent);
		this.queryKey = queryKey;
	}

	private IType buildType() {

		Class<?> type = getPropertyType();

		if (type != null) {
			return getTypeRepository().getType(type);
		}

		return getTypeRepository().getTypeHelper().objectType();
	}

	private ITypeDeclaration buildTypeDeclaration() {

		// ForeignReferenceQueryKey
		if (queryKey.isForeignReferenceQueryKey()) {
			return new JavaTypeDeclaration(getTypeRepository(), buildType(), getPropertyType(), false);
		}

		// DirectQueryKey
		return new JavaTypeDeclaration(getTypeRepository(), buildType(), null, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	int calculateMappingType() {

		// Basic
		if (queryKey.isDirectQueryKey()) {
			return IEclipseLinkMappingType.BASIC;
		}

		// Element Collection
		// Basic Collection
		if (queryKey.isDirectCollectionQueryKey()) {
			return IEclipseLinkMappingType.ELEMENT_COLLECTION;
		}

		// M:M
		if (queryKey.isManyToManyQueryKey()) {
			return IEclipseLinkMappingType.MANY_TO_MANY;
		}

		// 1:M
		if (queryKey.isOneToManyQueryKey()) {
			return IEclipseLinkMappingType.ONE_TO_MANY;
		}

		// 1:1
		if (queryKey.isOneToOneQueryKey()) {
			return IEclipseLinkMappingType.ONE_TO_ONE;
		}

		return IEclipseLinkMappingType.TRANSIENT;
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
	public String getName() {
		return queryKey.getName();
	}

	/**
	 * Returns the type of the mapping derived from the field or bean method return type represented
	 * by the mapping.
	 *
	 * @return The Java type representing the property type
	 */
	private Class<?> getPropertyType() {

		// ForeignReferenceQueryKey
		if (queryKey.isForeignReferenceQueryKey()) {
			ForeignReferenceQueryKey foreignReferenceQueryKey = (ForeignReferenceQueryKey) queryKey;
			return foreignReferenceQueryKey.getReferenceClass();
		}

		// DirectQueryKey
		DirectQueryKey key = (DirectQueryKey) queryKey;
		return key.getField().getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getTypeDeclaration().getType();
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

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollection() {
		return queryKey.isCollectionQueryKey();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProperty() {
		return queryKey.isDirectQueryKey();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRelationship() {
		return queryKey.isForeignReferenceQueryKey();
	}
}