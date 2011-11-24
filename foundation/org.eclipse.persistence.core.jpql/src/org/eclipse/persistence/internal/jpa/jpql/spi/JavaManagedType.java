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
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * The abstract definition of {@link IManagedType} defined for wrapping the runtime mapped
 * class object.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
abstract class JavaManagedType implements IManagedType {

	/**
	 * The EclipseLink descriptor wrapping the actual managed type.
	 */
	private ClassDescriptor descriptor;

	/**
	 * Flag used to determine whether only the entire collection have been created or only the
	 * requested mappings have been created.
	 */
	private boolean initialized;

	/**
	 * The cached {@link IMapping} mapped by their property name.
	 */
	private Map<String, IMapping> mappings;

	/**
	 * The provider of JPA managed types.
	 */
	private JavaManagedTypeProvider provider;

	/**
	 * The cached {@link IType} representing the managed type.
	 */
	private IType type;

	/**
	 * Creates a new <code>JavaManagedType</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param descriptor The EclipseLink descriptor wrapping the actual managed type
	 */
	JavaManagedType(JavaManagedTypeProvider provider, ClassDescriptor descriptor) {
		super();
		this.provider   = provider;
		this.descriptor = descriptor;
		this.mappings   = new HashMap<String, IMapping>();
	}

	private IType buildType() {
		return provider.getTypeRepository().getType(descriptor.getJavaClass());
	}

	/**
	 * {@inheritDoc}
	 */
	public final int compareTo(IManagedType managedType) {
		return getType().getName().compareTo(managedType.getType().getName());
	}

	/**
	 * Returns the encapsulated {@link ClassDescriptor}, which is the actual mapping.
	 *
	 * @return The actual EclipseLink descriptor wrapped by this external form
	 */
	final ClassDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IMapping getMappingNamed(String name) {

		IMapping mapping = mappings.get(name);

		if (mapping == null) {

			DatabaseMapping databaseMapping = descriptor.getMappingForAttributeName(name);

			if (databaseMapping != null) {
				mapping = new JavaMapping(this, databaseMapping);
				mappings.put(name, mapping);
			}

			if (mapping == null) {
				QueryKey queryKey = descriptor.getQueryKeyNamed(name);

				if (queryKey != null) {
					mapping = new JavaQueryKey(this, queryKey);
					mappings.put(name, mapping);
				}
			}
		}

		return mapping;
	}

	/**
	 * {@inheritDoc}
	 */
	public final JavaManagedTypeProvider getProvider() {
		return provider;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IType getType() {
		if (type == null) {
			type = buildType();
		}
		return type;
	}

	private void initializeMappings() {

		if (!initialized) {
			initialized = true;

			// Mappings
			for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
				IMapping mapping = new JavaMapping(this, databaseMapping);
				mappings.put(databaseMapping.getAttributeName(), mapping);
			}

			// Query Keys
			for (QueryKey queryKey : descriptor.getQueryKeys().values()) {
				IMapping mapping = new JavaQueryKey(this, queryKey);
				mappings.put(queryKey.getName(), mapping);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IMapping> mappings() {
		initializeMappings();
		return new CloneIterator<IMapping>(mappings.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("type=");
		sb.append(descriptor.getJavaClassName());
		return sb.toString();
	}
}