/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

import static org.junit.Assert.*;

/**
 * This unit-test tests {@link AbstractQueryHelper} when the queries are global
 * and owned by an ORM configuration.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ORMQueryHelperTest<T> extends AbstractQueryHelperTest<T>
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IQuery namedQuery(String entityName, String queryName) throws Exception
	{
		IORMConfiguration ormConfiguration = ormConfiguration();
		IQuery namedQuery = ormConfiguration.getNamedQuery(queryName);
		assertNotNull("The named query could not be found", namedQuery);
		return namedQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String ormXmlFileName()
	{
		return "orm2.xml";
	}
}