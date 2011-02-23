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
package org.eclipse.persistence.utils.jpa.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * The abstract definition of {@link IManagedType} defined for wrapping the runtime mapped
 * class object.
 *
 * @version 2.3
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
	}

	private IMapping buildMapping(DatabaseMapping mapping) {
		return new JavaMapping(this, mapping);
	}

	private IMapping buildMapping(QueryKey queryKey) {
		return new JavaQueryKey(this, queryKey);
	}

	private Map<String, IMapping> buildMappings() {

		Map<String, IMapping> mappings = new HashMap<String, IMapping>();

		// Mappings
		for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
			mappings.put(databaseMapping.getAttributeName(), buildMapping(databaseMapping));
		}

		// Query Keys
		for (QueryKey queryKey : descriptor.getQueryKeys().values()) {
			mappings.put(queryKey.getName(), buildMapping(queryKey));
		}

		return mappings;
	}

	private IType buildType() {
		return provider.getTypeRepository().getType(descriptor.getJavaClass());
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
		initializeMappings();
		return mappings.get(name);
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
		if (mappings == null) {
			mappings = buildMappings();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final Iterator<IMapping> mappings() {
		initializeMappings();
		return mappings.values().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type=");
		sb.append(descriptor.getJavaClassName());
		return sb.toString();
	}
}