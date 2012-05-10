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
package org.eclipse.persistence.jpa.jpql.util.iterator;

/**
 * Used by {@link CloneListIterator} to remove elements from the original list; since the list
 * iterator does not have direct access to the original list.
 *
 * @version 2.4
 * @since 2.4
 */
public interface ListMutator<E> {

	/**
	 * Adds the specified object to the original list.
	 *
	 * @param index The index of insertion
	 * @param item The element to insert into the list
	 */
	void add(int index, E item);

	/**
	 * Removes the specified object from the original list.
	 *
	 * @param index The index of the element to remove
	 */
	void remove(int index);

	/**
	 * Sets the specified object in the original list.
	 *
	 * @param index The index of replacement
	 * @param item The element to replace the existing one
	 */
	void set(int index, E item);
}