package org.eclipse.persistence.jpa.tests.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.DefaultJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.eclipse.persistence.jpa.tests.jpql.model.IJPQLQueryBuilderTestHelper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Illustrates possible bugs in the Hermes 2.0 parser  */
@SuppressWarnings("nls")
public class HermesBugsTest extends JPQLCoreTest {

	@IJPQLQueryBuilderTestHelper
	private IJPQLQueryBuilder queryBuilder;

	/**
	 * formatter throws StackOverflowError on "IS NULL"
	 *
	 */
	@Test
	public void testFormatterIsNullOrderStackOverflow() throws Exception {
		String jpql = "SELECT r FROM AnyRelationshipType r WHERE r.groupUUID IS NULL";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, true);

		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals(jpql, formatter.toString(so)); // fails with
													// StackOverflowError
	}

	/**
	 * DefaultJPQLQueryFormatter omits important space after LIKE
	 *
	 */
	@Test
	public void testFormatterLikeMissingSpace() throws Exception  {
		String jpql = "SELECT a FROM Asset a WHERE a.name LIKE a.address";

		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);

		IJPQLQueryFormatter formatter = new DefaultJPQLQueryFormatter(IdentifierStyle.UPPERCASE);
		assertEquals(jpql, formatter.toString(so));
		// instead formatter.toString(so) returns:
		// SELECT a FROM Asset a WHERE a.name LIKEa.address'
	}

	/**
	 * lowercase 'true" is formatted as NULL
	 *
	 */
	@Test
	public void testFormatterLowercaseTrue() throws Exception  {

		String jpql = "SELECT a FROM Asset a WHERE a.good = TRUE";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);
		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals(jpql, formatter.toString(so)); // this works

		jpql = "SELECT a FROM Asset a WHERE a.good = true";
		so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);
		formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals(jpql, formatter.toString(so));
		// instead formatter.toString(so) returns:
		// SELECT a FROM Asset a WHERE a.good = NULL
	}

	/**
	 * DefaultActualJPQLQueryFormatter with exactMatch=true omits important
	 * space before ORDER BY
	 *
	 */
	@Test
	public void testFormatterOrderByMissingSpace() throws Exception  {
		String jpql = "SELECT a FROM Asset a WHERE a.good = TRUE ORDER BY a.size";

		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);

		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals(jpql, formatter.toString(so));
		// instead formatter.toString(so) returns: TRUEORDER BY a.size
	}

	/**
	 * "IS EMPTY" is formatted as "null"
	 *
	 */
	@Test
	public void testFormatterOrderStackOverflow() throws Exception  {
		String jpql = "SELECT r FROM AnyRelationshipType r WHERE r.groupUUID IS EMPTY";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);

		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals(jpql, formatter.toString(so));
		// instead formatter.toString(so) returns:
		// SELECT r FROM AnyRelationshipType r WHERE r.groupUUID null
	}

	/**
	 * UPPER() on a string literal causes validateGrammar() to fail
	 *
	 */
	@Test
	public void testValidateUpperOnLiteral() throws Exception  {

		String jpql = "SELECT b FROM AnyEntityType b WHERE UPPER(b.name) LIKE 'PortType/TimeProcess'";
		IQuery query = new JavaQuery(getPersistenceUnit(), jpql);
		DefaultJPQLQueryHelper helper = new DefaultJPQLQueryHelper(queryBuilder.getGrammar());
		helper.setQuery(query);
		List<JPQLQueryProblem> problems = helper.validateGrammar();
		assertTrue(problems.isEmpty()); // this passes

//		jpql = "SELECT b FROM AnyEntityType b WHERE UPPER(b.name) LIKE UPPER('PortType/TimeProcess')";
//		query = new JavaQuery(getPersistenceUnit(), jpql);
//		helper = new DefaultJPQLQueryHelper(queryBuilder.getGrammar());
//		helper.setQuery(query);
//		problems = helper.validateGrammar();
//		assertTrue(problems.isEmpty()); // fails here with validation problems
	}
}