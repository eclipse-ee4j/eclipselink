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
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * The external representation of a managed type, which is a JPA persistent object.
 *
 * @see IEmbeddable
 * @see IEntity
 * @see IMappedSuperclass
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IManagedType extends IExternalForm,
                                      Comparable<IManagedType> {

	/**
	 * Visits this managed type with the given visitor.
	 *
	 * @param visitor The visitor to visit this managed type object
	 */
	void accept(IManagedTypeVisitor visitor);

	/**
	 * Returns the {@link IMapping} with the given name.
	 *
	 * @param name The name of the mapping to retrieve
	 * @return Either the {@link IMapping} or <code>null</code> if it could not
	 * be found
	 */
	IMapping getMappingNamed(String name);

	/**
	 * Retrieves the owner of this managed type.
	 *
	 * @return The external form holding onto the JPA managed types.
	 */
	IManagedTypeProvider getProvider();

	/**
	 * Returns the external representation of the class used by this managed type.
	 *
	 * @return The external representation of the class used by this managed type
	 */
	IType getType();

	/**
	 * Returns the collection of {@link IMapping mappings} defined in this managed type.
	 *
	 * @return The collection of persistent fields and properties of this managed type
	 */
	Iterable<IMapping> mappings();
}