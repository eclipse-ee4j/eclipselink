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

/**
 * This interface makes an {@link ListIterator} {@link Iterable} so it can be used in an enhanced
 * for loop.
 *
 * @version 2.4
 * @since 2.4
 */
public interface IterableListIterator<T> extends IterableIterator<T>,
                                                 ListIterator<T> {

	/**
	 * {@inheritDoc}
	 */
	ListIterator<T> iterator();
}