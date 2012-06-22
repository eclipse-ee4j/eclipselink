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
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * The external representation of a JPQL query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IQuery extends IExternalForm {

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return A non-<code>null</code> string representation of the JPQL query
	 */
	String getExpression();

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	IManagedTypeProvider getProvider();
}