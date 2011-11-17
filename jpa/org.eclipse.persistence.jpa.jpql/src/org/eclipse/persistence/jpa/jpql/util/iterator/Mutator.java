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
package org.eclipse.persistence.jpa.jpql.util.iterator;

/**
 * Used by {@link CloneIterator} to remove elements from the original collection; since the iterator
 * does not have direct access to the original collection.
 *
 * @version 2.4
 * @since 2.4
 */
public interface Mutator<E> {

	/**
	 * Removes the specified object from the original collection.
	 *
	 * @param item The item to remove from the original collection
	 */
	void remove(E item);
}