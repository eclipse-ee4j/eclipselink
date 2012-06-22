/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tests;

import org.eclipse.persistence.jpa.jpql.JPQLQueryHelper;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.internal.jpql.VirtualQuery;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The default implementation of {@link JPQLQueryTestHelper} used by the unit-tests.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaJPQLQueryTestHelper implements JPQLQueryTestHelper {

	/**
	 * The external forms of the ORM configurations mapped to the file name.
	 */
	private Map<String, IORMConfiguration> ormConfigurations;

	/**
	 * The external form of the persistence unit.
	 */
	private JavaManagedTypeProvider persistenceUnit;

	/**
	 * The single instance of the helper that gives access to the parser API.
	 */
	private JPQLQueryHelper queryHelper;

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryHelper buildJPQLQueryHelper(IQuery query) {

		if (queryHelper == null) {
			queryHelper = new JPQLQueryHelper();
		}

		queryHelper.setQuery(query);
		return queryHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery buildNamedQuery(String query) throws Exception {
		return new VirtualQuery(getPersistenceUnit(), query);
	}

	private IORMConfiguration buildORMConfiguration(String ormXmlFileName) {
		return new JavaORMConfiguration(ormXmlFileName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IORMConfiguration getORMConfiguration(String ormXmlFileName) throws Exception {

		if (ormConfigurations == null) {
			ormConfigurations = new HashMap<String, IORMConfiguration>();
		}

		IORMConfiguration ormConfiguration = ormConfigurations.get(ormXmlFileName);

		if (ormConfiguration == null) {
			ormConfiguration = buildORMConfiguration(ormXmlFileName);
			ormConfigurations.put(ormXmlFileName, ormConfiguration);
		}

		return ormConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedTypeProvider getPersistenceUnit() throws Exception {
		if (persistenceUnit == null) {
			persistenceUnit = new JavaManagedTypeProvider(IJPAVersion.DEFAULT_VERSION);
		}
		return persistenceUnit;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUp() throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUpBefore() throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	public void tearDown() throws Exception {
		if (queryHelper != null) {
			queryHelper.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void tearDownAfter() throws Exception {
	}
}