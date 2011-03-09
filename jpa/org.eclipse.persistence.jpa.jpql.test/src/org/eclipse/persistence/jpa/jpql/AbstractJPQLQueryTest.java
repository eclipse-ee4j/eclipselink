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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.junit.After;
import org.junit.Before;

/**
 * The abstract definition of a unit-test responsible to test various API that access the
 * JPQL parsed tree.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJPQLQueryTest {

	/**
	 * The helper that gives access to the application's JPA metadata.
	 */
	private static JPQLQueryTestHelper jpqlQueryTestHelper;

	/**
	 * Creates a new <code>AbstractJPQLQueryTest</code>.
	 */
	protected AbstractJPQLQueryTest() {
		super();
		initialize();
	}

	/**
	 * Sets the helper that is reponsible to give access to the application's JPA metadata.
	 * <p>
	 * Note: This has to be set prior of executing the unit-tests since {@link #setUpClass()} and
	 * {@link #tearDownClass()} have to be static.
	 *
	 * @param jpqlQueryTestHelper The helper that completes the behavior of this unit-test
	 */
	public static void setJPQLQueryTestHelper(JPQLQueryTestHelper jpqlQueryTestHelper) {
		AbstractJPQLQueryTest.jpqlQueryTestHelper = jpqlQueryTestHelper;
	}

	/**
	 * Notifies this helper before the tests of one class are invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	public static void setUpClass() throws Exception {
		jpqlQueryTestHelper.setUpBefore();
	}

	/**
	 * Notifies this helper after the tests of one class were invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	public static void tearDownClass() throws Exception {
		jpqlQueryTestHelper.tearDownAfter();
	}

	/**
	 * Initializes this unit-tests.
	 */
	protected void initialize() {
	}

	/**
	 * Retrieves the external form of the ORM configuration with the name retrieved from
	 * {@link #ormXmlFileName()}.
	 *
	 * @return The external form of the ORM configuration.
	 * @throws Exception If an error was encountered during the creation of the ORM configuration
	 */
	protected IORMConfiguration ormConfiguration() throws Exception {
		return jpqlQueryTestHelper.getORMConfiguration(ormXmlFileName());
	}

	/**
	 * Retrieves the name of the orm.xml file.
	 *
	 * @return The short name of the orm.xml
	 */
	protected String ormXmlFileName() {
		return "orm1.xml";
	}

	/**
	 * Returns the persistence unit that manages some entities.
	 *
	 * @return The external form of a persistence unit that will be used to retrieve the entities
	 * used for testing
	 * @throws Exception If an error was encountered during the creation of the persistence unit
	 */
	protected IManagedTypeProvider persistenceUnit() throws Exception {
		return jpqlQueryTestHelper.getPersistenceUnit();
	}

	/**
	 * Returns the helper used to complete the behavior of this unit-test.
	 *
	 * @return This test's helper
	 */
	protected JPQLQueryTestHelper queryTestHelper() {
		return jpqlQueryTestHelper;
	}

	/**
	 * Notifies this helper before a test is invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	@Before
	public void setUp() throws Exception {
		jpqlQueryTestHelper.setUp();
	}

	/**
	 * Notifies this helper after a test was invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	@After
	public void tearDown() throws Exception {
		jpqlQueryTestHelper.tearDown();
	}
}