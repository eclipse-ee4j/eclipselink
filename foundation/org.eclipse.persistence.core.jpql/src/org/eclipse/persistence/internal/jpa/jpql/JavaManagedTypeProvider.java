/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IPlatform;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link IManagedTypeProvider} that is wrapping the runtime
 * representation of a provider of a managed type.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
class JavaManagedTypeProvider implements IManagedTypeProvider {

	/**
	 * The filtered collection of managed types that are the abstract schema types.
	 */
	private Collection<IEntity> abstractSchemaTypes;

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
	JavaManagedTypeProvider(AbstractSession session) {
		super();
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterable<IEntity> abstractSchemaTypes() {
		if (abstractSchemaTypes == null) {
			initializeManagedTypes();
			EntityCollector visitor = new EntityCollector();
			for (IManagedType managedType : managedTypes.values()) {
				managedType.accept(visitor);
			}
			abstractSchemaTypes = visitor.entities;
		}
		return Collections.unmodifiableCollection(abstractSchemaTypes);
	}

	private IManagedType buildManagedType(ClassDescriptor descriptor) {

		if (descriptor.isAggregateDescriptor()) {
			return new JavaEmbeddable(this, descriptor);
		}

//		return new JavaMappedSuperclass(this, descriptor);
		return new JavaEntity(this, descriptor);
	}

	/**
	 * Returns the {@link ClassLoader} used by EclipseLink to load the application's classes.
	 *
	 * @return The application's {@link ClassLoader}
	 */
	ClassLoader getClassLoader() {
		return getSession().getDatasourcePlatform().getConversionManager().getLoader();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public IManagedType getManagedType(IType type) {

		JavaType eclipseLinkType = (JavaType) type;
		ClassDescriptor descriptor = eclipseLinkType.getDescriptor();

		if (descriptor == null) {
			descriptor = getSession().getClassDescriptor(eclipseLinkType.getType());
		}

		if (descriptor == null) {
			return null;
		}

		String abstractSchemaName = descriptor.getAlias();

		if (ExpressionTools.stringIsEmpty(abstractSchemaName)) {

			for (Object value : getSession().getAliasDescriptors().entrySet()) {
				Map.Entry<String, ClassDescriptor> entry = (Map.Entry<String, ClassDescriptor>) value;

				if (descriptor == entry.getValue()) {
					abstractSchemaName = entry.getKey();
					break;
				}
			}

			if (ExpressionTools.stringIsEmpty(abstractSchemaName)) {
				abstractSchemaName = descriptor.getJavaClass().getSimpleName();
			}
		}

		return getManagedType(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(String abstractSchemaName) {
		initializeManagedTypes();
		return managedTypes.get(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IPlatform getPlatform() {
		return IPlatform.ECLIPSE_LINK;
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

	/**
	 * {@inheritDoc}
	 */
	public IJPAVersion getVersion() {
		return IJPAVersion.DEFAULT_VERSION;
	}

	@SuppressWarnings("unchecked")
	private void initializeManagedTypes() {

		if (managedTypes == null) {
			managedTypes = new HashMap<String, IManagedType>();

			for (ClassDescriptor descriptor : getSession().getDescriptors().values()) {
				String abstractSchemaName = descriptor.getAlias();
				if (ExpressionTools.stringIsEmpty(abstractSchemaName)) {
					abstractSchemaName = descriptor.getJavaClass().getSimpleName();
				}
				managedTypes.put(abstractSchemaName, buildManagedType(descriptor));
			}

			for (Object value : getSession().getAliasDescriptors().entrySet()) {
				Map.Entry<String, ClassDescriptor> entry = (Map.Entry<String, ClassDescriptor>) value;

				managedTypes.put(entry.getKey(), buildManagedType(entry.getValue()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterable<IManagedType> managedTypes() {
		initializeManagedTypes();
		return Collections.unmodifiableCollection(managedTypes.values());
	}

	private static class EntityCollector implements IManagedTypeVisitor {

		/**
		 * The collection of {@link IEntity entities} that got visited.
		 */
		private final Collection<IEntity> entities;

		/**
		 * Creates a new <code>EntityCollector</code>.
		 */
		EntityCollector() {
			super();
			entities = new ArrayList<IEntity>();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEmbeddable embeddable) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEntity entity) {
			entities.add(entity);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IMappedSuperclass mappedSuperclass) {
		}
	}
}