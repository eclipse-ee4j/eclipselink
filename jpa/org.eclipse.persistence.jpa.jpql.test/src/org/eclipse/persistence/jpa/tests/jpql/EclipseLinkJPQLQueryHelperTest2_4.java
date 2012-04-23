/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkJPQLQueryHelperTest2_4 extends AbstractJPQLQueryHelperTest {

	protected IEntity entity(String entityName) throws Exception {
		IEntity entity = getPersistenceUnit().getEntityNamed(entityName);
		assertNotNull("The entity " + entityName + " could not be found", entity);
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IQuery namedQuery(String entityName, String queryName) throws Exception {
		IEntity entity = entity(entityName);
		IQuery namedQuery = entity.getNamedQuery(queryName);
		assertNotNull("The named query " + queryName + " could not be found on " + entityName, namedQuery);
		return namedQuery;
	}

	@Test
	public void test_ParameterType_FromSubquery_1() throws Exception {

		// Select e3.salary from Employee e, (Select count(e2), e2.department from Employee e2 group by e2.department) e3 where e3.department = :dept
		IQuery namedQuery = namedQuery("Employee", "employee.fromSubquery1");

		AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
		IType type = helper.getParameterType(":dept");
		assertNotNull("The type of :dept should have been found", type);

		assertEquals(
			"The type for :dept was incorrectly calculated",
			getType(namedQuery, String.class),
			type
		);
	}

	@Test
	public void test_ResultType_FromSubquery_1() throws Exception {

		// Select e3.salary from Employee e, (Select count(e2), e2.department from Employee e2 group by e2.department) e3 where e.department = e3.department
		IQuery namedQuery = namedQuery("Employee", "employee.fromSubquery2");

		AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();
		assertNotNull("The result type should have been found", type);

		assertEquals(
			"The result type was not calculated correctly",
			getType(namedQuery, Object.class),
			type
		);
	}

	@Test
	public void test_ResultType_FromSubquery_2() throws Exception {

		// Select e3.count from Employee e, (Select count(e2) as count, e2.department from Employee e2 group by e2.department) e3 where e.department = e3.department
		IQuery namedQuery = namedQuery("Employee", "employee.fromSubquery3");

		AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();
		assertNotNull("The result type should have been found", type);

		assertEquals(
			"The result type was not calculated correctly",
			getType(namedQuery, Long.class),
			type
		);
	}

	@Test
	public void test_ResultType_FromSubquery_4() throws Exception {

		// Select e3.result from Employee e, (Select count(e2) + 2.2 as result, e2.department from Employee e2 group by e2.department) e3 where e.department = e3.department
		IQuery namedQuery = namedQuery("Employee", "employee.fromSubquery4");

		AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
		IType type = helper.getResultType();
		assertNotNull("The result type should have been found", type);

		assertEquals(
			"The result type was not calculated correctly",
			getType(namedQuery, Double.class),
			type
		);
	}
}