/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.DefaultSemanticValidator;
import org.eclipse.persistence.jpa.jpql.GenericSemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;

/**
 * The base definition of a test class used for testing a JPQL query semantically.
 *
 * @see AbstractSemanticValidator
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractSemanticValidatorTest extends AbstractValidatorTest {

	/**
	 * The instance of {@link JPQLQueryContext} that is used when running the unit-tests defined by
	 * the subclass. The instance is shared between each the unit-test.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * Creates a new external form for the given JPQL query.
	 *
	 * @param jpqlQuery The JPQL query to wrap with a {@link IQuery}
	 * @return The external form for the JPQL query
	 * @throws Exception If a problem was encountered while access the application metadata
	 */
	protected IQuery buildQuery(String jpqlQuery) throws Exception {
		return new JavaQuery(getPersistenceUnit(), jpqlQuery);
	}

	/**
	 * Creates a new {@link JPQLQueryContext}.
	 *
	 * @return A instance of {@link JPQLQueryContext} that is used to cache information about the
	 * JPQL query being manipulated
	 */
	protected abstract JPQLQueryContext buildQueryContext();

	/**
	 * Creates a new {@link SemanticValidatorHelper}, which is used by {@link AbstractSemanticValidator}
	 * in order to access the JPA metadata. The unit-tests uses {@link GenericSemanticValidatorHelper},
	 * which simply accesses Hermes SPI and delegates the calls to {@link JPQLQueryContext}.
	 *
	 * @return A new instance of {@link GenericSemanticValidatorHelper}
	 */
	protected SemanticValidatorHelper buildSemanticValidatorHelper() {
		return new GenericSemanticValidatorHelper(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSemanticValidator buildValidator() {
		return new DefaultSemanticValidator(buildSemanticValidatorHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSemanticValidator getValidator() {
		return (AbstractSemanticValidator) super.getValidator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUpClass() throws Exception {
		queryContext = buildQueryContext();
		super.setUpClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		queryContext.dispose();
		super.tearDown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDownClass() throws Exception {
		queryContext = null;
		super.tearDownClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<JPQLQueryProblem> validate(String jpqlQuery, JPQLExpression jpqlExpression) throws Exception {

		// Update JPQLQueryContext, set JPQLExpression first since it was already parsed
		queryContext.setJPQLExpression(jpqlExpression);
		queryContext.setQuery(buildQuery(jpqlQuery));

		// Now validate semantically the JPQL query
		return super.validate(jpqlQuery, jpqlExpression);
	}
}