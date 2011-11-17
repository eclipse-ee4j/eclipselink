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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An <code>ArrayListIterator</code> provides a {@link ListIterator} for an array of objects.
 * <p>
 * The name might be a bit confusing: this is a {@link ListIterator} for an {@link Array};
 * <em>not</em> an {@link Iterator} for an {@link ArrayList}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public class ArrayListIterator<E> extends ArrayIterator<E>
                                  implements IterableListIterator<E> {

	/**
	 * The position from which the cursor cannot go lower.
	 */
	private int minIndex;

	/**
	 * Creates a new <code>ArrayListIterator</code> for the specified array.
	 *
	 * @param array The object containing the items to iterate over
	 */
	public <T extends E> ArrayListIterator(T... array)
	{
		this(array, 0, array.length);
	}

	/**
	 * Creates a new <code>ArrayListIterator</code> for the specified array, starting at the
	 * specified start index and continuing for the specified length.
	 *
	 * @param array The object containing the items to iterate over
	 * @param start The beginning of the iteration
	 * @param length The length of the iteration
	 */
	public <T extends E> ArrayListIterator(T[] array, int start, int length) {
		super(array, start, length);
		this.minIndex = start;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(E item) {
		throw new UnsupportedOperationException("An ArrayListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasPrevious() {
		return nextIndex > minIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<E> iterator() {
		return (ListIterator<E>) super.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextIndex() {
		return nextIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public E previous() {

		if (hasPrevious()) {
			return array[--nextIndex];
		}

		throw new NoSuchElementException();
	}

	/**
	 * {@inheritDoc}
	 */
	public int previousIndex() {
		return nextIndex - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(E item) {
		throw new UnsupportedOperationException("An ArrayListIterator is read-only.");
	}
}