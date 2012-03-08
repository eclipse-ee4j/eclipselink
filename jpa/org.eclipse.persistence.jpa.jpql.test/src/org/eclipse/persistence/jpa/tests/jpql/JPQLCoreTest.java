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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * The abstract definition of a unit-test responsible to test various API that access the
 * JPQL parsed tree.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class JPQLCoreTest extends JPQLBasicTest {

	/**
	 * The helper that gives access to the application's JPA metadata.
	 */
	@JPQLQueryTestHelperTestHelper
	private JPQLQueryTestHelper jpqlQueryTestHelper;

	/**
	 * Creates a new <code>AbstractJPQLQueryTest</code>.
	 */
	protected JPQLCoreTest() {
		super();
	}

	/**
	 * Retrieves the external form of the ORM configuration with the name retrieved from
	 * {@link #ormXmlFileName()}.
	 *
	 * @param ormXmlFileName
	 * @return The external form of the ORM configuration.
	 * @throws Exception If an error was encountered during the creation of the ORM configuration
	 */
	protected IORMConfiguration getORMConfiguration(String ormXmlFileName) throws Exception {
		return jpqlQueryTestHelper.getORMConfiguration(ormXmlFileName);
	}

	/**
	 * Returns the persistence unit that manages some entities.
	 *
	 * @return The external form of a persistence unit that will be used to retrieve the entities
	 * used for testing
	 * @throws Exception If an error was encountered during the creation of the persistence unit
	 */
	protected IManagedTypeProvider getPersistenceUnit() throws Exception {
		return jpqlQueryTestHelper.getPersistenceUnit();
	}

	/**
	 * Returns the helper used to complete the behavior of this unit-test.
	 *
	 * @return This test's helper
	 */
	protected JPQLQueryTestHelper getQueryTestHelper() {
		return jpqlQueryTestHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (jpqlQueryTestHelper != null) {
			jpqlQueryTestHelper.setUp();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUpClass() throws Exception {
		super.setUpClass();
		if (jpqlQueryTestHelper != null) {
			jpqlQueryTestHelper.setUpBefore();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (jpqlQueryTestHelper != null) {
			jpqlQueryTestHelper.tearDown();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDownClass() throws Exception {
		super.tearDownClass();
		if (jpqlQueryTestHelper != null) {
			jpqlQueryTestHelper.tearDownAfter();
			jpqlQueryTestHelper = null;
		}
	}
}