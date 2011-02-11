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

import org.eclipse.persistence.utils.jpa.query.spi.IEntity;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

import static org.junit.Assert.*;

/**
 * This unit-test tests {@link AbstractQueryHelper} when the queries are on
 * entities managed by an ORM configuration.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ORMEntityQueryHelperTest<T> extends AbstractQueryHelperTest<T>
{
	private IEntity entity(String entityName) throws Exception
	{
		IEntity entity = (IEntity) ormConfiguration().getManagedType(entityName);
		assertNotNull("The named query could not be found", entity);
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IQuery namedQuery(String entityName, String queryName) throws Exception
	{
		IEntity entity = entity(entityName);
		IQuery namedQuery = entity.getNamedQuery(queryName);
		assertNotNull("The named query could not be found", namedQuery);
		return namedQuery;
	}
}