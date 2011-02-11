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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface IORMConfiguration extends IManagedTypeProvider
{
	/**
	 * Returns the external form of the given named query;
	 *
	 * @param queryName The name of the JPQL query to retrieve
	 * @return The {@link IQuery} representing the JPQL query named with the
	 * given name; or <code>null</code> if none could be found
	 */
	IQuery getNamedQuery(String queryName);
}