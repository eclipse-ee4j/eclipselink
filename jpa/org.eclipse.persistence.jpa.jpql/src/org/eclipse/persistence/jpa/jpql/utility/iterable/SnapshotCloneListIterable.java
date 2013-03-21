/*******************************************************************************
 * Copyright (c) 2012, 2013, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.utility.iterable;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.utility.iterator.CloneListIterator;

/**
 * A <code>SnapshotCloneListIterable</code> returns a list iterator on a
 * "snapshot" of a list, allowing for concurrent access to the original list.
 * A copy of the list is created when the list iterable is constructed.
 * As a result, the contents of the list will be the same with
 * every call to {@link #iterator()}, even if the original list is modified via
 * the list iterator's mutation methods.
 * <p>
 * The original list passed to the <code>SnapshotCloneListIterable</code>'s
 * constructor should be thread-safe (e.g. {@link java.util.Vector});
 * otherwise you run the risk of a corrupted list.
 * <p>
 * By default, the list iterator returned by a <code>SnapshotCloneListIterable</code> does not
 * support the {@link ListIterator} mutation operations; this is because it does not
 * have access to the original list. But if the <code>SnapshotCloneListIterable</code>
 * is supplied with a {@link org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator.Mutator}
 * it will delegate the
 * {@link ListIterator} mutation operations to the <code>Mutator</code>.
 * Alternatively, a subclass can override the list iterable's mutation
 * methods.
 * <p>
 * This list iterable is useful for multiple passes over a list that should not
 * be changed (e.g. by another thread) between passes.
 *
 * @param <E> the type of elements returned by the list iterable's list iterator
 *
 * @see CloneListIterator
 * @see LiveCloneListIterable
 * @see SnapshotCloneIterable
 */
public class SnapshotCloneListIterable<E>
	extends CloneListIterable<E>
{
	private final Object[] array;


	// ********** constructors **********

	/**
	 * Construct a "snapshot" list iterable for the specified list.
	 * The {@link ListIterator} modify operations will not be supported
	 * by the list iterator returned by {@link #iterator()}
	 * unless a subclass overrides the list iterable's modify
	 * method.
	 */
	public SnapshotCloneListIterable(List<? extends E> list) {
		super();
		this.array = list.toArray();
	}

	/**
	 * Construct a "snapshot" list iterable for the specified list.
	 * The specified mutator will be used by any generated list iterators to
	 * modify the original list.
	 */
	public SnapshotCloneListIterable(List<? extends E> list, CloneListIterator.Mutator<E> mutator) {
		super(mutator);
		this.array = list.toArray();
	}


	// ********** ListIterable implementation **********

	public ListIterator<E> iterator() {
		return new LocalCloneListIterator<E>(this.mutator, this.array);
	}

	@Override
	public String toString() {
		return Arrays.toString(this.array);
	}


	// ********** clone iterator **********

	/**
	 * provide access to "internal" constructor
	 */
	protected static class LocalCloneListIterator<E> extends CloneListIterator<E> {
		protected LocalCloneListIterator(Mutator<E> mutator, Object[] array) {
			super(mutator, array);
		}
	}
}