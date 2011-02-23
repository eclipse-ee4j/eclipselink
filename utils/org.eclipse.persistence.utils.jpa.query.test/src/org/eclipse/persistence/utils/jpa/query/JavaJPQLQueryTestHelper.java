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
package org.eclipse.persistence.utils.jpa.query;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * This helper gives to the unit-tests the access to the application metadata through EclipseLink API.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaJPQLQueryTestHelper implements JPQLQueryTestHelper {

	/**
	 * The EclipseLink implementation of the {@link EntityManager}.
	 */
	private JpaEntityManager entityManager;

	/**
	 * The EclipseLink version of the {@link EntityManagerFactory}.
	 */
	private EntityManagerFactory factory;

	/**
	 * Flag used to prevent the project metadata to be created more than once.
	 */
	private boolean initialized;

	/**
	 * The cached {@link IORMConfiguration IORMConfigurations} to be reused for faster access.
	 */
	private Map<String, IORMConfiguration> ormConfigurations;

	/**
	 * The cached {@link IManagedTypeProvider} to be reused for faster access.
	 */
	private JavaManagedTypeProvider persistenceUnit;

	/**
	 * A fake JDBC driver so EclipseLink doesn't have to connect to a real database.
	 */
	private TestDriver testDriver;

	/**
	 * {@inheritDoc}
	 */
	public IQuery buildNamedQuery(String query) throws Exception {
		return new JavaQuery(getPersistenceUnit(), query);
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
			ormConfiguration = new EclipseLinkORMConfiguration(session());
			ormConfigurations.put(ormXmlFileName, ormConfiguration);
		}

		return ormConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaManagedTypeProvider getPersistenceUnit() throws Exception {
		if (persistenceUnit == null) {
			persistenceUnit = new JavaManagedTypeProvider(session());
		}
		return persistenceUnit;
	}

	private AbstractSession session() {
		return entityManager.unwrap(AbstractSession.class);
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

		if (initialized) {
			return;
		}

		initialized = true;

		// Register a fake JDBC driver so EclipseLink doesn't have to connect to a real database
		testDriver = new TestDriver();
		DriverManager.registerDriver(testDriver);

		Properties properties = new Properties();
		properties.put(PersistenceUnitProperties.VALIDATION_ONLY_PROPERTY, Boolean.TRUE.toString());

		// Create the EntityManager
		factory = Persistence.createEntityManagerFactory("jpql-parser", properties);
		EntityManager jpaEntityManager = factory.createEntityManager();
		entityManager = jpaEntityManager.unwrap(JpaEntityManager.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	public void tearDownAfter() throws Exception {

		initialized = false;

		DriverManager.deregisterDriver(testDriver);
		entityManager.close();
		factory.close();
	}

	private class EclipseLinkORMConfiguration extends JavaManagedTypeProvider
	                                          implements IORMConfiguration {

		EclipseLinkORMConfiguration(AbstractSession session) {
			super(session);
		}

		/**
		 * {@inheritDoc}
		 */
		public IQuery getNamedQuery(String queryName) {
			DatabaseQuery query = getSession().getQuery(queryName);
			return (query == null) ? null : new JavaQuery(this, query.getEJBQLString());
		}
	}
}