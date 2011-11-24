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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;
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
public final class JavaManagedTypeProvider implements IManagedTypeProvider {

	/**
	 * The filtered collection of managed types that are {@link IEmbeddable}.
	 */
	private Map<String, IEmbeddable> embeddables;

	/**
	 * The filtered collection of managed types that are an {@link IEntity}.
	 */
	private Map<String, IEntity> entities;

	/**
	 * Flag used to determine whether only the entire collection have been created or only the
	 * requested managed types have been created.
	 */
	private boolean initialized;

	/**
	 * The cached {@link IManagedType IManagedTypes}.
	 */
	private Map<String, IManagedType> managedTypes;

	/**
	 * The EclipseLink {@link AbstractSession}.
	 */
	private AbstractSession session;

	/**
	 * The external form of a type repository.
	 */
	private JavaTypeRepository typeRepository;

	/**
	 * Creates a new <code>JavaManagedTypeProvider</code>.
	 *
	 * @param session The EclipseLink {@link AbstractSession} that type information is derived from
	 */
	public JavaManagedTypeProvider(AbstractSession session) {
		super();
		initialize(session);
	}

	/**
	 * Adds the {@link ClassDescriptor} by wrapping it with its corresponding external form.
	 *
	 * @param descriptor The {@link ClassDescriptor} to add, which can be <code>null</code>
	 * @param returnEntity <code>true</code> to return the new {@link IEntity} if the {@link
	 * ClassDescriptor} represents an entity; <code>false</code> to return the new {@link IEmbeddable}
	 * if it represents an aggregate; <code>null</code> to return the new object regardless of its
	 * type
	 * @return The external form wrapping the given {@link ClassDescriptor} or null if the given
	 * descriptor is <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	private <T extends IManagedType> T addManagedType(ClassDescriptor descriptor, Boolean returnEntity) {

		if (descriptor == null) {
			return null;
		}

		String typeName = descriptor.getJavaClassName();

		if (descriptor.isAggregateDescriptor()) {
			JavaEmbeddable embeddable = new JavaEmbeddable(this, descriptor);
			embeddables .put(typeName, embeddable);
			managedTypes.put(typeName, embeddable);
			return (returnEntity == null) || !returnEntity ? (T) embeddable : null;
		}
		else {
			JavaEntity entity = new JavaEntity(this, descriptor);
			entities    .put(typeName, entity);
			managedTypes.put(typeName, entity);
			return (returnEntity == null) || returnEntity ? (T) entity : null;
		}
	}

	/**
	 * Adds the {@link ClassDescriptor} that describes the given type name.
	 *
	 * @param typeName The fully qualified class name of the {@link ClassDescriptor} to add
	 * @param returnEntity <code>true</code> to return the new {@link IEntity} if the {@link
	 * ClassDescriptor} represents an entity; <code>false</code> to return the new {@link IEmbeddable}
	 * if it represents an aggregate; <code>null</code> to return the new object regardless of its
	 * type
	 */
	private <T extends IManagedType> T addManagedType(String typeName, Boolean returnEntity) {
		return addManagedType(getDescriptor(typeName), returnEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IEntity> entities() {
		initializeManagedTypes();
		return new CloneIterator<IEntity>(entities.values());
	}

	/**
	 * Returns the {@link ClassLoader} used by EclipseLink to load the application's classes.
	 *
	 * @return The application's {@link ClassLoader}
	 */
	ClassLoader getClassLoader() {
		return getSession().getDatasourcePlatform().getConversionManager().getLoader();
	}

	@SuppressWarnings("rawtypes")
	private ClassDescriptor getDescriptor(String typeName) {

		for (Map.Entry<Class, ClassDescriptor> entry : session.getDescriptors().entrySet()) {
			if (entry.getKey().getName().equals(typeName)) {
				return entry.getValue();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IEmbeddable getEmbeddable(IType type) {
		return getEmbeddable(type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IEmbeddable getEmbeddable(String typeName) {

		IEmbeddable embeddable = embeddables.get(typeName);

		if (embeddable == null) {
			embeddable = addManagedType(typeName, Boolean.FALSE);
		}

		return embeddable;
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

		IEntity entity = entities.get(typeName);

		if (entity == null) {
			entity = addManagedType(typeName, Boolean.TRUE);
		}

		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getEntityNamed(String entityName) {

		ClassDescriptor descriptor = session.getDescriptorForAlias(entityName);

		if (descriptor != null) {

			IEntity entity = entities.get(descriptor.getJavaClassName());

			if (entity == null) {
				entity = addManagedType(descriptor, Boolean.TRUE);
			}

			return entity;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(IType type) {
		return getManagedType(type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(String typeName) {

		IManagedType managedType = managedTypes.get(typeName);

		if (managedType == null) {
			managedType = addManagedType(typeName, null);
		}

		return managedType;
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappedSuperclass getMappedSuperclass(IType type) {
		// Not supported, it is merged with the IEntity
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappedSuperclass getMappedSuperclass(String typeName) {
		// Not supported, it is merged with the IEntity
		return null;
	}

	/**
	 * Returns the EclipseLink {@link AbstractSession} that type information is derived from.
	 *
	 * @return The current session from which JPA artifacts can be retrieved
	 */
	AbstractSession getSession() {
		return session;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeRepository getTypeRepository() {
		if (typeRepository == null) {
			typeRepository = new JavaTypeRepository(getClassLoader());
		}
		return typeRepository;
	}

	private void initialize(AbstractSession session) {

		this.session      = session;
		this.managedTypes = new HashMap<String, IManagedType>();
		this.embeddables  = new HashMap<String, IEmbeddable>();
		this.entities     = new HashMap<String, IEntity>();
	}

	private void initializeManagedTypes() {

		if (!initialized) {
			initialized = true;

			for (ClassDescriptor descriptor : session.getDescriptors().values()) {
				IManagedType managedType = managedTypes.get(descriptor.getJavaClassName());
				if (managedType == null) {
					addManagedType(descriptor, true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IManagedType> managedTypes() {
		initializeManagedTypes();
		return new CloneIterator<IManagedType>(managedTypes.values());
	}
}