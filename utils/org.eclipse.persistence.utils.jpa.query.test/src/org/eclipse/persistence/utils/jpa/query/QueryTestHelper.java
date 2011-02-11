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

/**
 * This helper is responsible to create and initialize the ORM Configuration and
 * the Persistence Configuration required to execute the unit-tests.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface QueryTestHelper
{
	/**
	 * Retrieves the external form of the ORM configuration with the given name.
	 *
	 * @param ormXmlFileName The name of the orm.xml
	 * @return The external form of the ORM configuration.
	 * @throws Exception If an error was encountered during the creation of the ORM configuration
	 */
	IORMConfiguration getORMConfiguration(String ormXmlFileName) throws Exception;

	/**
	 * Returns the persistence unit that manages some entities.
	 *
	 * @return The external form of a persistence unit that will be used to retrieve the
	 * entities used for testing
	 * @throws Exception If an error was encountered during the creation of the persistence unit
	 */
	IManagedTypeProvider getPersistenceUnit() throws Exception;

	/**
	 * Notifies this helper before a test is invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	void setUp() throws Exception;

	/**
	 * Notifies this helper before the tests of one class are invoked.
	 *
	 * @throws Exception If an error was encountered during the initialization
	 */
	void setUpBefore() throws Exception;

	/**
	 * Notifies this helper after a test was invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	void tearDown() throws Exception;

	/**
	 * Notifies this helper after the tests of one class were invoked.
	 *
	 * @throws Exception If an error was encountered during execution
	 */
	void tearDownAfter() throws Exception;
}