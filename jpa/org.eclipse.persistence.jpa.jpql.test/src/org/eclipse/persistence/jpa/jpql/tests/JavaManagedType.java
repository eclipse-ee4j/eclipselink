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
package org.eclipse.persistence.jpa.jpql.tests;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;

/**
 * The abstract definition of {@link IManagedType} defined for wrapping the runtime mapped class
 * object.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class JavaManagedType implements IManagedType {

	/**
	 *
	 */
	private Map<String, IMapping> mappings;

	/**
	 * The provider of JPA managed types.
	 */
	private final IManagedTypeProvider provider;

	/**
	 * The cached {@link IType} representing the managed type.
	 */
	private final JavaType type;
	
	/**
	 * Creates a new <code>JavaManagedType</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param type The {@link IType} wrapping the Java type
	 */
	JavaManagedType(IManagedTypeProvider provider, JavaType type) {
		super();
		this.type     = type;
		this.provider = provider;
	}

	/**
	 * {@inheritDoc}
	 */
	public final int compareTo(IManagedType managedType) {
		return getType().getName().compareTo(managedType.getType().getName());
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
	public final IManagedTypeProvider getProvider() {
		return provider;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaType getType() {
		return type;
	}

	private void initializeMappings() {
		if (mappings == null) {
			mappings = new HashMap<String, IMapping>();
			for (Field field : type.getType().getDeclaredFields()) {
				mappings.put(field.getName(), new JavaMapping(this, field));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final Iterable<IMapping> mappings() {
		initializeMappings();
		return Collections.unmodifiableCollection(mappings.values());
	}
}