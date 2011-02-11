/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.spi;

/**
 * The external representation of a Java Persistence query.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface IQuery
{
	/**
	 * Returns the string representation of the Java Persistence query.
	 *
	 * @return A non-<code>null</code> string representation of the Java Persistence query
	 */
	String getExpression();

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	IManagedTypeProvider getProvider();
}