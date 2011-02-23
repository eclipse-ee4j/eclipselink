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
import java.util.Properties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This unit-test tests the {@link JPQLQueryParser} and validation settings for a persistence unit
 * at runtime. The <code>"jpql-builder"</code> persistence unit is used for all tests.
 *
 * @version 2.3
 * @since 2.3
 * @author John Bracken
 */
@SuppressWarnings("nls")
public final class JPQLQueryParserTest {

	/**
	 * The mock JDBC driver to use with the {@link DriverManager} in these tests.
	 */
	private TestDriver driver = new TestDriver();

	/**
	 * Tests that the correct type of {@link JPQLQueryParser} is used at runtime for a persistence
	 * unit based on the type specified.
	 *
	 * @param builderClassToUse The {@link JPQLQueryParser} type to specify for the persistence unit
	 * @param expectedParserClass The expected type of {@link JPQLQueryParser} to be associated with
	 * the persistence unit
	 */
//	private void _testQueryParser(JPQLQueryParser builderToUse, Class<?> expectedParserClass) {
//
//		Properties properties = buildDefaultProperties();
//
//		JPQLQueryParser queryParser = buildQueryParser(properties, builderToUse);
//		assertSame(expectedParserClass, queryParser.getClass());
//	}

	/**
	 * Constructs a default set of persistence unit {@link Properties} to be used in these tests.
	 *
	 * @return Default set of persistence unit properties
	 */
	private Properties buildDefaultProperties() {

		Properties properties = new Properties();
		properties.put(PersistenceUnitProperties.VALIDATION_ONLY_PROPERTY, Boolean.TRUE.toString());

		return properties;
	}

	/**
	 * Builds a {@link JPQLQueryParser} given a set of persistence unit properties and the
	 * persistence unit <code>"jpql-builder"</code>.
	 *
	 * @param properties The persistence unit properties to use.
	 * @param builderToUse The query builder instance to use.
	 * @return A {@link JPQLQueryParser} derived from the persistence unit's entity manager
	 */
//	private JPQLQueryParser buildQueryParser(Properties properties, JPQLQueryParser builderToUse) {
//
//		JPQLQueryParserManager.setQueryParser(builderToUse);
//		EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpql-builder", properties);
//		EntityManager jpaEntityManager = factory.createEntityManager();
//		JPQLQueryParser queryParser = JPQLQueryParserManager.getQueryParser();
//		jpaEntityManager.close();
//		factory.close();
//
//		return queryParser;
//	}

	/**
	 * Sets the {@link TestDriver} on the {@link DriverManager}prior to each test.
	 */
	@Before
	public void setUp() throws Exception {
		DriverManager.registerDriver(this.driver);
	}

	/**
	 * Removes the {@link TestDriver} from the {@link DriverManager} following each test.
	 */
	@After
	public void tearDown() throws Exception {
		DriverManager.deregisterDriver(this.driver);
	}

	/**
	 * Tests that a third-party or custom {@link JPQLQueryParser} implementation is loaded correctly
	 * at runtime.
	 *
	 * @see CustomQueryParser
	 */
	@Test
	public void testCustomQueryParser() {
//		_testQueryParser(new CustomQueryParser(), CustomQueryParser.class);
	}

	/**
	 * Tests that the default {@link JPQLQueryParser} implementation is loaded correctly at runtime.
	 *
	 * @see ANTLRQueryParser
	 */
	@Test
	public void testDefaultQueryParser() {
//		_testQueryParser(null, ANTLRQueryParser.class);
	}

	/**
	 * Tests that the {@link EclipseLinkQueryParser} is loaded correctly at runtime when specified.
	 *
	 * @see HermesJPQLQueryParser
	 */
	@Test
	public void testEclipseLinkQueryParser() {
//		_testQueryParser(new HermesParser(false), HermesParser.class);
	}
}