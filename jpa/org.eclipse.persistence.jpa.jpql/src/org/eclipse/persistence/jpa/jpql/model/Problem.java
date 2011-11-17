/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * A problem describes an issue found in a JPQL query because it is either grammatically or
 * semantically incorrect.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface Problem {

	/**
	 * Returns the arguments associate with the problem's message.
	 *
	 * @return A non-<code>null</code> list of arguments that can be used to format the localized
	 * message
	 */
	String[] getMessageArguments();

	/**
	 * Returns the key used to retrieve the localized message describing the problem found in the
	 * {@link StateObject}.
	 *
	 * @return The key used to retrieve the localized message
	 */
	String getMessageKey();

	/**
	 * Returns the {@link StateObject} where the problem was found.
	 *
	 * @return The {@link StateObject} where the problem was found
	 */
	StateObject getStateObject();
}