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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * The abstract definition of a unit-test responsible to test various API that access the
 * JPQL parsed tree.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractQueryTest
{
	/**
	 * The helper that gives access to the application's JPA metadata.
	 */
	private static QueryTestHelper queryTestHelper;

	/**
	 * Creates a new <code>AbstractQueryTest</code>.
	 */
	protected AbstractQueryTest()
	{
		super();
		initialize();
	}

	/**
	 * Sets the helper that is reponsible to give access to the application's JPA
	 * metadata.
	 * <p>
	 * Note: This has to be set prior of executing the unit-tests since
	 * {@link #setUpClass()} and {@link #tearDownClass()} have to be static.
	 *
	 * @param queryTestHelper The helper that completes the behavior of this
	 * unit-test
	 */
	public static void setQueryTestHelper(QueryTestHelper queryTestHelper)
	{
		AbstractQueryTest.queryTestHelper = queryTestHelper;
	}

	/**
	 * Notifies this helper before the tests of one class are invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	@BeforeClass
	public static void setUpClass() throws Exception
	{
		queryTestHelper.setUpBefore();
	}

	/**
	 * Notifies this helper after the tests of one class were invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	@AfterClass
	public static void tearDownClass() throws Exception
	{
		queryTestHelper.tearDownAfter();
	}

	/**
	 * Creates a new {@link QueryTestHelper}, which is responsible to give access
	 * to the application's JPA metadata.
	 *
	 * @return A new {@link QueryTestHelper}
	 */
	protected abstract QueryTestHelper buildTestHelper();

	/**
	 * Initializes this unit-tests.
	 */
	protected void initialize()
	{
	}

	/**
	 * Retrieves the external form of the ORM configuration with the name retrieved from
	 * {@link #ormXmlFileName()}.
	 *
	 * @return The external form of the ORM configuration.
	 * @throws Exception If an error was encountered during the creation of the ORM configuration
	 */
	protected IORMConfiguration ormConfiguration() throws Exception
	{
		return queryTestHelper.getORMConfiguration(ormXmlFileName());
	}

	/**
	 * Retrieves the name of the orm.xml file.
	 *
	 * @return The short name of the orm.xml
	 */
	protected String ormXmlFileName()
	{
		return "orm1.xml";
	}

	/**
	 * Returns the persistence unit that manages some entities.
	 *
	 * @return The external form of a persistence unit that will be used to retrieve the
	 * entities used for testing
	 * @throws Exception If an error was encountered during the creation of the persistence unit
	 */
	protected IManagedTypeProvider persistenceUnit() throws Exception
	{
		return queryTestHelper.getPersistenceUnit();
	}

	/**
	 * Returns the helper used to complete the behavior of this unit-test.
	 *
	 * @return This test's helper
	 */
	protected QueryTestHelper queryTestHelper()
	{
		return queryTestHelper;
	}

	/**
	 * Notifies this helper before a test is invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	@Before
	public void setUp() throws Exception
	{
		queryTestHelper.setUp();
	}

	/**
	 * Notifies this helper after a test was invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	@After
	public void tearDown() throws Exception
	{
		queryTestHelper.tearDown();
	}
}