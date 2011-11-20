/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This unit-tests tests {@link DefaultActualJPQLQueryFormatter}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultActualJPQLQueryFormatterTest extends AbstractStateObjectTest {

	/**
	 * Exact match, using info from Expression, which is valid.
	 */
	@Test
	public void testAbsExpression_ExactMatch1() throws Exception {

		String jpqlQuery = "SeLeCt AbS(e.age) FrOm Employee e";
		StateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SeLeCt AbS(e.age) FrOm Employee e", formatter.toString(stateObject));
	}

	/**
	 * Exact match, using info from Expression, which is invalid.
	 */
	@Test
	public void testAbsExpression_ExactMatch2() throws Exception {

		String jpqlQuery = "SELECT aBs FROM Employee e";
		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT aBs FROM Employee e", formatter.toString(stateObject));
	}

	/**
	 * Exact match, using info from Expression, which is invalid.
	 */
	@Test
	public void testAbsExpression_ExactMatch3() throws Exception {

		String jpqlQuery = "SELECT aBs(e.age FROM Employee e";
		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT aBs(e.age FROM Employee e", formatter.toString(stateObject));
	}

	/**
	 * Exact match, using info from Expression, which is valid.
	 */
	@Test
	public void testAbsExpression_ExactMatch4() throws Exception {

		String jpqlQuery = "SELECT abS(e.age) FROM Employee e";
		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT abS(e.age) FROM Employee e", formatter.toString(stateObject));
	}

	/**
	 * Exact match, no Expression available.
	 */
	@Test
	public void testAbsExpression_ExactMatch5() throws Exception {

		String jpqlQuery = "SELECT FROM Employee e";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
		SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
		select.addSelectItem(new AbsExpressionStateObject(select, new StateFieldPathExpressionStateObject(select, "e.age")));

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT ABS(e.age) FROM Employee e", formatter.toString(stateObject));
	}

	/**
	 * No exact match, should not use info from Expression, which is invalid.
	 */
	@Test
	public void testAbsExpression_NoExactMatch1() throws Exception {

		String jpqlQuery = "SeLeCt AbS FrOm Employee e";
		StateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("SeLeCt AbS() FrOm Employee e", formatter.toString(stateObject));
	}

	/**
	 * No exact match, should not use info from Expression, which is invalid.
	 */
	@Test
	public void testAbsExpression_NoExactMatch2() throws Exception {

		String jpqlQuery = "SeLeCt AbS( FrOm Employee e";
		StateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("SeLeCt AbS() FrOm Employee e", formatter.toString(stateObject));
	}

	/**
	 * No exact match, no Expression available.
	 */
	@Test
	public void testAbsExpression_NoExactMatch3() throws Exception {

		String jpqlQuery = "SELECT FROM Employee e";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
		SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
		select.addSelectItem(new AbsExpressionStateObject(select, new StateFieldPathExpressionStateObject(select, "e.age")));

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("SELECT ABS(e.age) FROM Employee e", formatter.toString(stateObject));
	}

	/**
	 * Exact match, Expression available, which is invalid.
	 */
	@Test
	public void testWhereClause_ExactMatch1() throws Exception {

		String jpqlQuery = "SELECT e fRom Employee e wHeRe";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT e fRom Employee e wHeRe", formatter.toString(stateObject));
	}

	/**
	 * Exact match, Expression available, which is invalid.
	 */
	@Test
	public void testWhereClause_ExactMatch2() throws Exception {

		String jpqlQuery = "SELECT e fRom Employee e wHeRe ";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT e fRom Employee e wHeRe ", formatter.toString(stateObject));
	}

	/**
	 * Exact match, Expression available, which invalid.
	 */
	@Test
	public void testWhereClause_ExactMatch3() throws Exception {

		String jpqlQuery = "SELECT e fRom Employee e wHeRe e.name = 'JPQL'";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT e fRom Employee e wHeRe e.name = 'JPQL'", formatter.toString(stateObject));
	}

	/**
	 * Exact match, no Expression available.
	 */
	@Test
	public void testWhereClause_ExactMatch4() throws Exception {

		String jpqlQuery = "SELECT e fRom Employee e";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
		SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
		select.addWhereClause();

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT e fRom Employee e WHERE", formatter.toString(stateObject));
	}

	/**
	 * Exact match, no Expression available.
	 */
	@Test
	public void testWhereClause_ExactMatch5() throws Exception {

		String jpqlQuery = "SELECT e fRom Employee e";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
		SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
		select.addWhereClause("e.name = 'JPQL'");

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals("SELECT e fRom Employee e WHERE e.name = 'JPQL'", formatter.toString(stateObject));
	}

	/**
	 * No exact match.
	 */
	@Test
	public void testWhereClause_NoExactMatch1() throws Exception {

		String jpqlQuery = "sELect e fRom Employee e WheRe e.name = 'JPQL'";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("sELect e fRom Employee e WheRe e.name = 'JPQL'", formatter.toString(stateObject));
	}

	/**
	 * No exact match.
	 */
	@Test
	public void testWhereClause_NoExactMatch2() throws Exception {

		String jpqlQuery = "sELect e fRom Employee e WheRe";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("sELect e fRom Employee e WheRe", formatter.toString(stateObject));
	}

	/**
	 * No exact match.
	 */
	@Test
	public void testWhereClause_NoExactMatch3() throws Exception {

		String jpqlQuery = "sELect e fRom Employee e WheRe ";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("sELect e fRom Employee e WheRe", formatter.toString(stateObject));
	}

	/**
	 * No exact match.
	 */
	@Test
	public void testWhereClause_NoExactMatch4() throws Exception {

		String jpqlQuery = "sELect e fRom Employee e";

		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
		SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
		select.addWhereClause("e.name = 'JPQL'");

		DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("sELect e fRom Employee e WHERE e.name = 'JPQL'", formatter.toString(stateObject));
	}
}