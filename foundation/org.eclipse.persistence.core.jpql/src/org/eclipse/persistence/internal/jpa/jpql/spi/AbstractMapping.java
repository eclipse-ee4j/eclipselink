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

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IMappingType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The abstract definition of a {@link IMapping}.
 *
 * @see JavaMapping
 * @see JavaQueryKey
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
abstract class AbstractMapping implements IMapping {

	/**
	 * The mapping type of the given mapping.
	 */
	private int mappingType;

	/**
	 * The parent of this mapping.
	 */
	final IManagedType parent;

	/**
	 * Creates a new <code>AbstractMapping</code>.
	 *
	 * @param parent The parent of this mapping
	 */
	AbstractMapping(IManagedType parent) {
		super();
		this.parent      = parent;
		this.mappingType = -1;
	}

	/**
	 * Calculates the type of the mapping represented by this external form.
	 *
	 * @return The mapping type
	 */
	abstract int calculateMappingType();

	/**
	 * {@inheritDoc}
	 */
	public final int getMappingType() {
		if (mappingType == -1) {
			mappingType = calculateMappingType();
		}
		return mappingType;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IManagedType getParent() {
		return parent;
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	final ITypeRepository getTypeRepository() {
		return getParent().getProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransient() {
		return getMappingType() == IMappingType.TRANSIENT;
	}
}