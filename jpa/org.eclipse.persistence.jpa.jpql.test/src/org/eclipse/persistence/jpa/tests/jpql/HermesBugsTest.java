package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.DefaultJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
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
	 * missing space before WHERE statement
	 */
	@Test
	public void testAddWhereClauseMissingSpace() throws Exception {

		String jpql = "SELECT a FROM Artifact a";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, true);

		SelectStatementStateObject selectSO = (SelectStatementStateObject) so.getQueryStatement();
		assertFalse(selectSO.hasWhereClause());
		selectSO.addWhereClause("a.relativeURI LIKE '%bar'");

		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals("SELECT a FROM Artifact a WHERE a.relativeURI LIKE '%bar'", formatter.toString(so));
		// instead we get
		// SELECT a FROM Artifact aWHERE a.relativeURI LIKE '%bar'
		// this only happens with DefaultActualJPQLQueryFormatter with exactMatch=false.
		// doesn't happen for DefaultJPQLQueryFormatter
	}

	/**
	 * WhereClauseStateObject.andParse() doesn't preserve order of operations. I think it should?
	 */
	@Test
	public void testAndParseOrderOfOperations() throws Exception {

		String jpql = "SELECT r FROM AnyRelationshipType r WHERE r.name = a OR r.name = b";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);

		SelectStatementStateObject selectSO = (SelectStatementStateObject) so.getQueryStatement();
		selectSO.getWhereClause().andParse("r.name = c OR r.name = d");

		IJPQLQueryFormatter formatter = new DefaultJPQLQueryFormatter(IdentifierStyle.UPPERCASE);
		assertEquals("SELECT r FROM AnyRelationshipType r WHERE (r.name = a OR r.name = b) AND (r.name = c OR r.name = d)", formatter.toString(so));

		// instead formatter.toString(so) returns:
		// SELECT r FROM AnyRelationshipType r WHERE r.name = a OR r.name = b
		// AND r.name = c OR r.name = d
		// which is logically different
	}

	/**
	 * formatter throws StackOverflowError on "IS NULL"
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
	 * Default formatter still throws StackOverflowError on "IS NULL"
	 */
	@Test
	public void testFormatterIsNullStackOverflow() throws Exception {
		String jpql = "SELECT r FROM AnyRelationshipType r WHERE r.groupUUID IS NULL";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, true);
		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
		assertEquals(jpql, formatter.toString(so)); // passes
		formatter = new DefaultJPQLQueryFormatter(IdentifierStyle.UPPERCASE);
		assertEquals(jpql, formatter.toString(so)); // fails with StackOverflowError
	}

	/**
	 * DefaultJPQLQueryFormatter omits important space after LIKE
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
	 * Not causes StackOverflow
	 */
	@Test
	public void testFormatterNot() throws Exception {
		String jpql = "SELECT a FROM Artifact a WHERE NOT (a.name = 'fred')";
		JPQLQueryStateObject so = queryBuilder.buildStateObject(getPersistenceUnit(), jpql, false);
		IJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
		assertEquals(jpql, formatter.toString(so));
		// fails with DefaultJPQLQueryFormatter too
	}

	/**
	 * DefaultActualJPQLQueryFormatter with exactMatch=true omits important space before ORDER BY
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
}