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
package org.eclipse.persistence.jpa.jpql.util.filter;

/**
 * A filter is used to determine if a value can be "accepted" or "rejected".
 *
 * @version 2.3
 * @since 2.3
 */
public interface Filter<T> {

	/**
	 * Determines whether the specified object is "accepted" by the filter. The semantics of "accept"
	 * is determined by the contract between the client and the server.
	 *
	 * @param value The value to filter
	 * @return <code>true</code> if the given value is "accepted" by this filter; <code>false</code>
	 * if it was "rejected"
	 */
	boolean accept(T value);
}