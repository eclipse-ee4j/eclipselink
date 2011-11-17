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
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

/**
 * The concrete implementation of {@link IManagedTypeProvider} that is wrapping the runtime
 * representation of a provider of a managed type.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JavaManagedTypeProvider implements IManagedTypeProvider {

	/**
	 * The filtered collection of managed types that are {@link IEmbeddable}.
	 */
	private Collection<IEmbeddable> embeddables;

	/**
	 * The filtered collection of managed types that are {@link IEntity}.
	 */
	private Collection<IEntity> entities;

	/**
	 * The cached {@link IManagedType managed types}.
	 */
	private Map<String, IManagedType> managedTypes;

	/**
	 * The filtered collection of managed types that are {@link IMappedSuperclass}.
	 */
	private Collection<IMappedSuperclass> mappedSuperclasses;

	/**
	 * The builder that is responsible to create the {@link IMapping} wrapping a persistent attribute
	 * or property.
	 */
	private IMappingBuilder<Member> mappingBuilder;

	/**
	 * The external form of a type repository.
	 */
	private JavaTypeRepository typeRepository;

	/**
	 * Creates a new <code>JavaManagedTypeProvider</code>.
	 *
	 * @param mappingBuilder The builder that is responsible to create the {@link IMapping} wrapping
	 * a persistent attribute or property
	 * @exception NullPointerException The {@link IMappingBuilder} cannot be <code>null</code>
	 */
	public JavaManagedTypeProvider(IMappingBuilder<Member> mappingBuilder) {
		super();
		initialize(mappingBuilder);
	}

	/**
	 * Adds the given Java class that is a JPA embeddable.
	 *
	 * @param type The embeddable class
	 * @return The external form wrapping the given Java type
	 */
	public IEmbeddable addEmbeddable(Class<?> type) {
		IEmbeddable embeddable = buildEmbeddable(type);
		embeddables.add(embeddable);
		managedTypes.put(type.getName(), embeddable);
		return embeddable;
	}

	/**
	 * Adds the given Java class that is a JPA entity.
	 *
	 * @param type The entity class
	 * @return The external form wrapping the given Java type
	 */
	public IEntity addEntity(Class<?> type) {
		IEntity entity = buildEntity(type);
		entities.add(entity);
		managedTypes.put(type.getName(), entity);
		return entity;
	}

	/**
	 * Adds the given Java class that is a JPA mapped superclass.
	 *
	 * @param type The mapped superclass class
	 * @return The external form wrapping the given Java type
	 */
	public IMappedSuperclass addMappedSuperclass(Class<?> type) {
		IMappedSuperclass mappedSuperclass = buildMappedSuperclass(type);
		mappedSuperclasses.add(mappedSuperclass);
		managedTypes.put(type.getName(), mappedSuperclass);
		return mappedSuperclass;
	}

	protected IEmbeddable buildEmbeddable(Class<?> type) {
		return new JavaEmbeddable(this, getTypeRepository().getType(type), mappingBuilder);
	}

	protected IEntity buildEntity(Class<?> type) {
		return new JavaEntity(this, getTypeRepository().getType(type), mappingBuilder);
	}

	protected IMappedSuperclass buildMappedSuperclass(Class<?> type) {
		return new JavaMappedSuperclass(this, getTypeRepository().getType(type), mappingBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IEntity> entities() {
		return new CloneIterator<IEntity>(entities);
	}

	/**
	 * {@inheritDoc}
	 */
	public IEmbeddable getEmbeddable(IType type) {
		return getManagedType(embeddables, type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IEmbeddable getEmbeddable(String typeName) {
		return getManagedType(embeddables, typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getEntity(IType type) {
		return getEntity(type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getEntity(String typeName) {
		return getManagedType(entities, typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getEntityNamed(String entityName) {

		for (IEntity entity : entities) {
			if (entity.getName().equals(entityName)) {
				return entity;
			}
		}

		return null;
	}

	protected <T extends IManagedType> T getManagedType(Collection<T> managedTypes,
	                                                    String typeName) {

		for (T managedType : managedTypes) {
			if (managedType.getType().getName().equals(typeName)) {
				return managedType;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(IType type) {
		return getManagedType(managedTypes.values(), type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(String typeName) {
		return managedTypes.get(typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappedSuperclass getMappedSuperclass(IType type) {
		return getManagedType(mappedSuperclasses, type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappedSuperclass getMappedSuperclass(String typeName) {
		return getManagedType(mappedSuperclasses, typeName);
	}

	protected IMappingBuilder<Member> getMappingBuilder() {
		return mappingBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaTypeRepository getTypeRepository() {
		if (typeRepository == null) {
			typeRepository = new JavaTypeRepository(getClass().getClassLoader());
		}
		return typeRepository;
	}

	/**
	 * Initializes this provider.
	 */
	protected void initialize() {
	}

	private void initialize(IMappingBuilder<Member> mappingBuilder) {

		Assert.isNotNull(mappingBuilder, "The IMappingBuilder cannot be null");

		this.mappingBuilder     = mappingBuilder;
		this.entities           = new LinkedList<IEntity>();
		this.embeddables        = new LinkedList<IEmbeddable>();
		this.managedTypes       = new HashMap<String, IManagedType>();
		this.mappedSuperclasses = new LinkedList<IMappedSuperclass>();

		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IManagedType> managedTypes() {
		return new CloneIterator<IManagedType>(managedTypes.values());
	}
}