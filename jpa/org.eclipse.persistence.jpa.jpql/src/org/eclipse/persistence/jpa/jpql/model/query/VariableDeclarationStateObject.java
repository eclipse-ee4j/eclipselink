/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * This defines a variable declaration, which has a "root" object and an identification variable.
 *
 * @see CollectionMemberDeclarationStateObject
 * @see DerivedPathIdentificationVariableDeclarationStateObject
 * @see DerivedPathVariableDeclarationStateObject
 * @see IdentificationVariableDeclarationStateObject
 * @see RangeVariableDeclarationStateObject
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface VariableDeclarationStateObject extends StateObject {

	/**
	 * Returns the {@link IManagedType} for the given identification variable. The search does not
	 * traverse up the query hierarchy if this declaration is for a subquery.
	 * <p>
	 * <pre><code>SELECT e FROM Department d JOIN KEY(d.employees).addresses a</pre></code>
	 * In the above query, the managed type associated with the identification variable:
	 * <ul>
	 * <li>d is "Department"
	 * <li>a is "Address"
	 * </ul>
	 *
	 * @param stateObject The {@link StateObject} that should be an simple identification variable or
	 * an encapsulated identification variable with the identifier <code><b>KEY</b></code> or
	 * <code><b>VALUE</b></code>
	 * @return The {@link IManagedType} representing the domain object declared by the given
	 * identification variable
	 */
	IManagedType getManagedType(StateObject stateObject);

	/**
	 * Returns the {@link IdentificationVariableStateObject} that are used by this state object. It
	 * is possible more than one declaration exists, like a range variable declaration has also joins
	 * and join fetches.
	 *
	 * @return The list of {@link IdentificationVariableStateObject}
	 */
	IterableListIterator<IdentificationVariableStateObject> identificationVariables();
}