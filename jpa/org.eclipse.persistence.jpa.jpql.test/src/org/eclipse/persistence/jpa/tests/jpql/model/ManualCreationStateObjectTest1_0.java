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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.IConditionalExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.ISelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.query.AdditionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AndExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CountFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ExistsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LikeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LocateExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MaxFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MinFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NumericLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ObjectExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhereClauseStateObject;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

/**
 * This tests the manual creation of a {@link StateObject} that can be parsed by the JPQL grammar
 * defined in JPA 1.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ManualCreationStateObjectTest1_0 extends AbstractStateObjectTest1_0 {

	protected JPQLQueryStateObject buildJPQLQueryStateObject() throws Exception {
		return new JPQLQueryStateObject(getQueryBuilder(), getPersistenceUnit());
	}

	@Test
	public void test_Query_001() throws Exception {

		// SELECT e FROM Employee e
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		test(stateObject_001(), jpqlStateObject, query_001());
	}

	@Test
	public void test_Query_002() throws Exception {

		// SELECT e\nFROM Employee e
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		test(stateObject_002(), jpqlStateObject, query_002());
	}

	@Test
	public void test_Query_003_a() throws Exception {

		// SELECT e
      // FROM Employee e
      // WHERE e.department.name = 'NA42' AND
      //       e.address.state IN ('NY', 'CA')
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.path("e.department.name").equal("'NA42'")
			.and(
				builder.path("e.address.state").in("'NY'", "'CA'")
			)
		.commit();

		test(stateObject_003(), jpqlStateObject, query_003());
	}

	@Test
	public void test_Query_003_b() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department.name = 'NA42' AND
		//       e.address.state IN ('NY', 'CA')
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");
		select.addWhereClause().parse("e.department.name = 'NA42' AND e.address.state IN ('NY', 'CA')");

		test(stateObject_003(), jpqlStateObject, query_003());
	}

	@Test
	public void test_Query_003_c() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department.name = 'NA42' AND
		//       e.address.state IN ('NY', 'CA')
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		WhereClauseStateObject whereClause = select.addWhereClause();

		// e.department.name = 'NA42'
		StateFieldPathExpressionStateObject left = new StateFieldPathExpressionStateObject(select, "e.department.name");
		StringLiteralStateObject right = new StringLiteralStateObject(select, "'NA42'");
		ComparisonExpressionStateObject comparison = new ComparisonExpressionStateObject(select, left, EQUAL, right);

		// e.address.state IN ('NY', 'CA')
		InExpressionStateObject in = new InExpressionStateObject(select);
		in.setStateObject(new StateFieldPathExpressionStateObject(in, "e.address.state"));
		in.addItem(new StringLiteralStateObject(in, "'NY'"));
		in.addItem(new StringLiteralStateObject(in, "'CA'"));

		// x AND y
		whereClause.setConditional(new AndExpressionStateObject(select, comparison, in));

		test(stateObject_003(), jpqlStateObject, query_003());
	}

	@Test
	public void test_Query_004_a() throws Exception {

		// SELECT p.number
		// FROM Employee e, Phone p
		// WHERE     e = p.employee
		//       AND e.department.name = 'NA42'
		//       AND p.type = 'Cell'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addRangeDeclaration("Phone",    "p");
		select.addSelectItem("p.number");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.variable("e").equal(builder.path("p.employee"))
			.and(
				builder.path("e.department.name").equal("'NA42'")
			)
			.and(
				builder.path("p.type").equal("'Cell'")
			)
		.commit();

		test(stateObject_004(), jpqlStateObject, query_004());
	}

	@Test
	public void test_Query_004_b() throws Exception {

		// SELECT p.number
		// FROM Employee e, Phone p
		// WHERE     e = p.employee
		//       AND e.department.name = 'NA42'
		//       AND p.type = 'Cell'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addRangeDeclaration("Phone",    "p");
		select.addSelectItem("p.number");
		select.addWhereClause().parse("e = p.employee AND e.department.name = 'NA42' AND p.type = 'Cell'");

		test(stateObject_004(), jpqlStateObject, query_004());
	}

	@Test
	public void test_Query_005_a() throws Exception {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("d");
		select.addSelectItem(new CountFunctionStateObject(select, "e"));
		select.addSelectItem(new MaxFunctionStateObject(select, "e.salary"));
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));
		select.addRangeDeclaration("Department", "d").
			addJoin("d.employees", "e");
		select.addGroupByClause().addGroupByItem("d");
		select.addHavingClause().getBuilder().count("e").greaterThanOrEqual(5).commit();

		test(stateObject_005(), jpqlStateObject, query_005());
	}

	@Test
	public void test_Query_005_b() throws Exception {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").
			parseJoin("JOIN d.employees e");
		select.addGroupByClause().addGroupByItem("d");
		select.addHavingClause().parse("COUNT(e) >= 5");

		SelectClauseStateObject selectClause = select.getSelectClause();
		selectClause.parse("d");
		selectClause.parse("COUNT(e)");
		selectClause.parse("MAX(e.salary)");
		selectClause.parse("AVG(e.salary)");

		test(stateObject_005(), jpqlStateObject, query_005());
	}

	@Test
	public void test_Query_005_c() throws Exception {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").
			parseJoin("JOIN d.employees e");
		select.addGroupByClause().addGroupByItem("d");
		select.addHavingClause().parse("COUNT(e) >= 5");

		ISelectExpressionStateObjectBuilder builder = select.getSelectBuilder();
			builder.variable("d")
			.append().count("e")
			.append().max("e.salary")
			.append().avg("e.salary").
		commit();

		test(stateObject_005(), jpqlStateObject, query_005());
	}

	@Test
	public void test_Query_005_d() throws Exception {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.parseSelect("d, COUNT(e), MAX(e.salary), AVG(e.salary)");
		select.addRangeDeclaration("Department", "d").
			parseJoin("JOIN d.employees e");
		select.addGroupByClause().addGroupByItem("d");
		select.addHavingClause().parse("COUNT(e) >= 5");

		test(stateObject_005(), jpqlStateObject, query_005());
	}

	@Test
	public void test_Query_006() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = ?1
		//       AND e.salary > ?2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.path("e.deparment").equal("?1")
			.and(
				builder.path("e.salary").greaterThan("?2")
			)
		.commit();

		test(stateObject_006(), jpqlStateObject, query_006());
	}

	@Test
	public void test_Query_007() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = :dept
		//       AND e.salary > :base

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.path("e.deparment").equal(":dept")
			.and(
				builder.path("e.salary").greaterThan(":base")
			)
		.commit();

		test(stateObject_007(), jpqlStateObject, query_007());
	}

	@Test
	public void test_Query_008() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = 'NA65'
		//       AND e.name = 'UNKNOWN'' OR e.name = ''Roberts'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.path("e.deparment").equal("'NA65'")
			.and(
				builder.path("e.name").equal(builder.string("'UNKNOWN'' OR e.name = ''Roberts'"))
			)
		.commit();

		test(stateObject_008(), jpqlStateObject, query_008());
	}

	@Test
	public void test_Query_009() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.startDate BETWEEN ?1 AND ?2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder
				.path("e.startDate")
			.between(
				builder.parameter("?1"),
				builder.parameter("?2")
			)
		.commit();

		test(stateObject_009(), jpqlStateObject, query_009());
	}

	@Test
	public void test_Query_010() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = :dept AND
		//      e.salary = (SELECT MAX(e.salary)
		//                  FROM Employee e
		//                  WHERE e.department = :dept)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new MaxFunctionStateObject(subquery, "e.salary"));
		subquery.addRangeDeclaration("Employee", "e");
		IConditionalExpressionStateObjectBuilder subBuilder = subquery.addWhereClause().getBuilder();
		subBuilder.path("e.department").equal(subBuilder.parameter(":dept")).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("e.department").equal(builder.parameter(":dept"))
			.and(
				builder.path("e.salary").equal(builder.sub(subquery))
			)
		.commit();

		test(stateObject_010(), jpqlStateObject, query_010());
	}

	@Test
	public void test_Query_011() throws Exception {

		// SELECT e
		// FROM Project p JOIN p.employees e
		// WHERE p.name = ?1
		// ORDER BY e.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Project", "p").
			addJoin("p.employees", "e");
		select.addSelectItem("e");
		select.addWhereClause().getBuilder().path("p.name").equal("?1").commit();
		select.addOrderByClause().addItem("e.name");

		test(stateObject_011(), jpqlStateObject, query_011());
	}

	@Test
	public void test_Query_012() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS EMPTY";

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");
		select.addWhereClause().getBuilder().isEmpty("e.projects").commit();

		test(stateObject_012(), jpqlStateObject, query_012());
	}

	@Test
	public void test_Query_013() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS NOT EMPTY";

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");
		select.addWhereClause().getBuilder().isNotEmpty("e.projects").commit();

		test(stateObject_013(), jpqlStateObject, query_013());
	}

	@Test
	public void test_Query_014() throws Exception {

		// UPDATE Employee e
		// SET e.manager = ?1
		// WHERE e.department = ?2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem("e.manager", "?1");
		update.addWhereClause().getBuilder().path("e.department").equal("?1").commit();

		test(stateObject_014(), jpqlStateObject, query_014());
	}

	@Test
	public void test_Query_015() throws Exception {

		// DELETE FROM Project p
      // WHERE p.employees IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Project", "p");
		delete.addWhereClause().getBuilder().isEmpty("p.employees").commit();

		test(stateObject_015(), jpqlStateObject, query_015());
	}

	@Test
	public void test_Query_016() throws Exception {

		// DELETE FROM Department d
		// WHERE d.name IN ('CA13', 'CA19', 'NY30')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Department", "d");
		IConditionalExpressionStateObjectBuilder builder = delete.addWhereClause().getBuilder();
		builder.path("d.name").in("'CA13'", "'CA19'", "'NY30'").commit();

		test(stateObject_016(), jpqlStateObject, query_016());
	}

	@Test
	public void test_Query_017() throws Exception {

		// UPDATE Employee e
		// SET e.department = null
		// WHERE e.department.name IN ('CA13', 'CA19', 'NY30')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem("e.department", new KeywordExpressionStateObject(update, NULL));
		update.addWhereClause().getBuilder().
			path("e.department.name").in("'CA13'", "'CA19'", "'NY30'")
		.commit();

		test(stateObject_017(), jpqlStateObject, query_017());
	}

	@Test
	public void test_Query_018() throws Exception {

		// SELECT d
		// FROM Department d
		// WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d");
		select.addSelectItem("d");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("d.name").like(builder.string("'QA\\_%'"), "'\\'").commit();

		test(stateObject_018(), jpqlStateObject, query_018());
	}

	@Test
	public void test_Query_019() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new MaxFunctionStateObject(subquery, "e2.salary"));
		subquery.addRangeDeclaration("Employee", "e2");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("e.salary").equal(builder.sub(subquery)).commit();

		test(stateObject_019(), jpqlStateObject, query_019());
	}

	@Test
	public void test_Query_020() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("p");
		subquery.addRangeDeclaration("Phone", "p");

		IConditionalExpressionStateObjectBuilder subBuilder = subquery.addWhereClause().getBuilder();
				subBuilder.path("p.employee").equal(subBuilder.variable("e"))
			.and(
				subBuilder.path("p.type").equal("'Cell'")
			)
		.commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_020(), jpqlStateObject, query_020());
	}

	@Test
	public void test_Query_021() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("p");
		subquery.addDerivedPathDeclaration("e.phones", "p");
		subquery.addWhereClause().getBuilder().path("p.type").equal("'Cell'").commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_021(), jpqlStateObject, query_021());
	}

	@Test
	public void test_Query_022() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department IN (SELECT DISTINCT d
		//                        FROM Department d JOIN d.employees de JOIN de.projects p
		//                        WHERE p.name LIKE 'QA%')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.getSelectClause().setDistinct(true);
		subquery.setSelectItem("d");
		subquery.addRangeDeclaration("Department", "d")
			.addJoin("d.employees", "de").getParent()
			.addJoin("de.projects", "p");
		subquery.addWhereClause().getBuilder().path("p.name").like("'QA%'").commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("e.department").in(subquery).commit();

		test(stateObject_022(), jpqlStateObject, query_022());
	}

	@Test
	public void test_Query_023() throws Exception {

		// SELECT p
		// FROM Phone p
		// WHERE p.type NOT IN ('Office', 'Home')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Phone", "p");
		select.addSelectItem("p");
		select.addWhereClause().getBuilder().path("p.type").notIn("'Office'", "'Home'").commit();

		test(stateObject_023(), jpqlStateObject, query_023());
	}

	@Test
	public void test_Query_024() throws Exception {

		// SELECT m
		// FROM Employee m
		// WHERE (SELECT COUNT(e)
		//        FROM Employee e
		//        WHERE e.manager = m) > 0

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "m");
		select.addSelectItem("m");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new CountFunctionStateObject(subquery, "e"));
		subquery.addRangeDeclaration("Employee", "e");
		IConditionalExpressionStateObjectBuilder subBuilder = subquery.addWhereClause().getBuilder();
		subBuilder.path("e.manager").equal(subBuilder.variable("m")).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.sub(subquery).greaterThan(0).commit();

		test(stateObject_024(), jpqlStateObject, query_024());
	}

	@Test
	public void test_Query_025() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e MEMBER OF e.directs

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");
		select.addWhereClause().getBuilder().variable("e").memberOf("e.directs").commit();

		test(stateObject_025(), jpqlStateObject, query_025());
	}

	@Test
	public void test_Query_026() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE NOT EXISTS (SELECT p
		//                   FROM e.phones p
		//                   WHERE p.type = 'Cell')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("p");
		subquery.addDerivedPathDeclaration("e.phones", "p");
		subquery.addWhereClause().getBuilder().path("p.type").equal("'Cell'").commit();

		select.addWhereClause().getBuilder().notExists(subquery).commit();

		test(stateObject_026(), jpqlStateObject, query_026());
	}

	@Test
	public void test_Query_027() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.directs IS NOT EMPTY AND
		//       e.salary < ALL (SELECT d.salary FROM e.directs d)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("d.salary");
		subquery.addDerivedPathDeclaration("e.directs", "d");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.isNotEmpty("e.directs")
			.and(
				builder.path("e.salary").lowerThan(builder.all(subquery))
			)
		.commit();

		test(stateObject_027(), jpqlStateObject, query_027());
	}

	@Test
	public void test_Query_028() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p
		//                           WHERE p.name LIKE 'QA%')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.getSelectClause().toggleDistinct();
		subquery.setSelectItem("d");
		subquery.addRangeDeclaration("Department", "d").
			addJoin("d.employees", "de").getParent().
			addJoin("de.projects", "p");
		subquery.addWhereClause().getBuilder().path("p.name").like("'QA%'").commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
			builder.path("e.department").equal(builder.any(subquery))
		.commit();

		test(stateObject_028(), jpqlStateObject, query_028());
	}

	@Test
	public void test_Query_029() throws Exception {

		// SELECT d
		// FROM Department d
		// WHERE SIZE(d.employees) = 2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d");
		select.addSelectItem("d");
		select.addWhereClause().getBuilder().size("d.employees").equal(2).commit();

		test(stateObject_029(), jpqlStateObject, query_029());
	}

	@Test
	public void test_Query_030() throws Exception {

		// SELECT d
      // FROM Department d
      // WHERE (SELECT COUNT(e)
      //        FROM d.employees e) = 2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d");
		select.addSelectItem("d");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new CountFunctionStateObject(subquery, "e"));
		subquery.addDerivedPathDeclaration("d.employees", "e");

		select.addWhereClause().getBuilder().sub(subquery).equal(2).commit();

		test(stateObject_030(), jpqlStateObject, query_030());
	}

	@Test
	public void test_Query_031() throws Exception {

		// SELECT e
		// FROM Employee e
		// ORDER BY e.name DESC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");
		select.addOrderByClause().addItemDesc("e.name");

		test(stateObject_031(), jpqlStateObject, query_031());
	}

	@Test
	public void test_Query_032() throws Exception {

		// SELECT e
      // FROM Employee e JOIN e.department d
      // ORDER BY d.name, e.name DESC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").addJoin("e.department", "d");
		select.addSelectItem("e");
		select.addOrderByClause().
			addItem("d.name").getParent().
			addItemDesc("e.name");

		test(stateObject_032(), jpqlStateObject, query_032());
	}

	@Test
	public void test_Query_033_a() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));

		test(stateObject_033(), jpqlStateObject, query_033());
	}

	@Test
	public void test_Query_033_b() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.getSelectClause().parse("AVG(e.salary");

		test(stateObject_033(), jpqlStateObject, query_033());
	}

	@Test
	public void test_Query_033_c() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.getSelectClause().getBuilder().avg("e.salary").commit();

		test(stateObject_033(), jpqlStateObject, query_033());
	}

	@Test
	public void test_Query_034_a() throws Exception {

		// SELECT d.name, AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").addJoin("d.employees", "e");
		select.addSelectItem("d.name");
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));
		select.addGroupByClause().addGroupByItem("d.name");

		test(stateObject_034(), jpqlStateObject, query_034());
	}

	@Test
	public void test_Query_034_b() throws Exception {

		// SELECT d.name, AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").addJoin("d.employees", "e");
		select.addGroupByClause().addGroupByItem("d.name");

		ISelectExpressionStateObjectBuilder builder = select.getSelectClause().getBuilder();
		builder.path("d.name").append().avg("e.salary").commit();

		test(stateObject_034(), jpqlStateObject, query_034());
	}

	@Test
	public void test_Query_035_a() throws Exception {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").addJoin("d.employees", "e");
		select.addSelectItem("d.name");
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));
		select.addWhereClause().getBuilder().isEmpty("e.directs").commit();
		select.addGroupByClause().addGroupByItem("d.name");

		test(stateObject_035(), jpqlStateObject, query_035());
	}

	@Test
	public void test_Query_036() throws Exception {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name
      // HAVING AVG(e.salary) > 50000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").addJoin("d.employees", "e");
		select.addSelectItem("d.name");
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));
		select.addWhereClause().getBuilder().isEmpty("e.directs").commit();
		select.addGroupByClause().addGroupByItem("d.name");
		select.addHavingClause().getBuilder().avg("e.salary").greaterThan(50000).commit();

		test(stateObject_036(), jpqlStateObject, query_036());
	}

	@Test
	public void test_Query_037_a() throws Exception {

		// SELECT e, COUNT(p), COUNT(DISTINCT p.type)
		// FROM Employee e JOIN e.phones p
		// GROUP BY e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").addJoin("e.phones", "p");
		select.addSelectItem("e");
		select.addSelectItem(new CountFunctionStateObject(select, "p"));
		select.addSelectItem(new CountFunctionStateObject(select, true, "p.type"));
		select.addGroupByClause().addGroupByItem("e");

		test(stateObject_037(), jpqlStateObject, query_037());
	}

	@Test
	public void test_Query_037_b() throws Exception {

		// SELECT e, COUNT(p), COUNT(DISTINCT p.type)
		// FROM Employee e JOIN e.phones p
		// GROUP BY e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").addJoin("e.phones", "p");
		select.addGroupByClause().addGroupByItem("e");

		select.getSelectBuilder()
			.variable("e").append()
			.count("p").append()
			.countDistinct("p.type")
		.commit();

		test(stateObject_037(), jpqlStateObject, query_037());
	}

	@Test
	public void test_Query_038() throws Exception {

		// SELECT d.name, e.salary, COUNT(p)
		// FROM Department d JOIN d.employees e JOIN e.projects p
		// GROUP BY d.name, e.salary

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Department", "d").
			addJoin("d.employees", "e").getParent().
			addJoin("e.projects",  "p");
		select.addSelectItem("d.name");
		select.addSelectItem("e.salary");
		select.addSelectItem(new CountFunctionStateObject(select, "p"));
		select.addGroupByClause().addGroupByItem("d.name");
		select.getGroupByClause().addGroupByItem("e.salary");

		test(stateObject_038(), jpqlStateObject, query_038());
	}

	@Test
	public void test_Query_039() throws Exception {

		// SELECT e, COUNT(p)
		// FROM Employee e JOIN e.projects p
		// GROUP BY e
		// HAVING COUNT(p) >= 2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").addJoin("e.projects", "p");
		select.addSelectItem("e");
		select.addSelectItem(new CountFunctionStateObject(select, "p"));
		select.addHavingClause().getBuilder().count("p").greaterThanOrEqual(2).commit();
		select.addGroupByClause().addGroupByItem("e");

		test(stateObject_039(), jpqlStateObject, query_039());
	}

	@Test
	public void test_Query_040() throws Exception {

		// UPDATE Employee e
		// SET e.salary = 60000
		// WHERE e.salary = 55000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem("e.salary", new NumericLiteralStateObject(update, "60000"));
		update.addWhereClause().getBuilder().path("e.salary").equal(55000).commit();

		test(stateObject_040(), jpqlStateObject, query_040());
	}

	@Test
	public void test_Query_041() throws Exception {

		// UPDATE Employee e
		// SET e.salary = e.salary + 5000
		// WHERE EXISTS (SELECT p
		//               FROM e.projects p
		//               WHERE p.name = 'Release1')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem(
			"e.salary",
			new AdditionExpressionStateObject(
				update,
				new StateFieldPathExpressionStateObject(update, "e.projects"),
				new NumericLiteralStateObject(update, 5000)
			)
		);

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(update);
		subquery.setSelectItem("p");
		subquery.addDerivedPathDeclaration("e.projects", "p");
		subquery.addWhereClause().getBuilder().path("p.name").equal("'Release1'").commit();

		update.addWhereClause().getBuilder().exists(subquery).commit();

		test(stateObject_041(), jpqlStateObject, query_041());
	}

	@Test
	public void test_Query_042() throws Exception {

		// UPDATE Phone p
		// SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)),
		//     p.type = 'Business'
		// WHERE p.employee.address.city = 'New York' AND p.type = 'Office'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		List<StateObject> stateObjects = new ArrayList<StateObject>();
		stateObjects.add(new StringLiteralStateObject(jpqlStateObject, "'288'"));
		stateObjects.add(new SubstringExpressionStateObject(
			jpqlStateObject,
			new StateFieldPathExpressionStateObject(jpqlStateObject, "p.number"),
			new LocateExpressionStateObject(
				jpqlStateObject,
				new StateFieldPathExpressionStateObject(jpqlStateObject, "p.number"),
				new StringLiteralStateObject(jpqlStateObject, "'-'")
			),
			new NumericLiteralStateObject(jpqlStateObject, 4)
		));

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Phone", "p");
		update.addItem("p.number", new ConcatExpressionStateObject(update, stateObjects));
		update.addItem("p.type",   "'Business'");

		IConditionalExpressionStateObjectBuilder builder = update.addWhereClause().getBuilder();
		builder.
				path("p.employee.address.city").equal("'New York'")
			.and(
				builder.path("p.type").equal("'Office'")
			)
		.commit();

		test(stateObject_042(), jpqlStateObject, query_042());
	}

	@Test
	public void test_Query_043() throws Exception {

		// DELETE FROM Employee e
		// WHERE e.department IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Employee", "e");
		delete.addWhereClause().getBuilder().isNull("e.department").commit();

		test(stateObject_043(), jpqlStateObject, query_043());
	}

	@Test
	public void test_Query_044() throws Exception {

		// Select Distinct object(c)
		// From Customer c, In(c.orders) co
		// Where co.totalPrice >= Some (Select o.totalPrice
		//                              From Order o, In(o.lineItems) l
		//                              Where l.quantity = 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").equal(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("co.totalPrice")
			.greaterThanOrEqual(
				builder.some(subquery))
		.commit();

		test(stateObject_044(), jpqlStateObject, query_044());
	}

	@Test
	public void test_Query_045() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= SOME (Select o.totalPrice
		//                              FROM Order o, IN(o.lineItems) l
		//                              WHERE l.quantity = 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").equal(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("co.totalPrice")
			.lowerThanOrEqual(
				builder.some(subquery))
		.commit();

		test(stateObject_045(), jpqlStateObject, query_045());
	}

	@Test
	public void test_Query_046() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new MaxFunctionStateObject(subquery, "o.totalPrice"));
		subquery.addRangeDeclaration("Order", "o");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").equal(builder.any(subquery)).commit();

		test(stateObject_046(), jpqlStateObject, query_046());
	}

	@Test
	public void test_Query_047() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").equal(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").lowerThan(builder.any(subquery)).commit();

		test(stateObject_047(), jpqlStateObject, query_047());
	}

	@Test
	public void test_Query_048() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").equal(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").greaterThan(builder.any(subquery)).commit();

		test(stateObject_048(), jpqlStateObject, query_048());
	}

	@Test
	public void test_Query_049() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new MinFunctionStateObject(select, "o.totalPrice"));
		subquery.addRangeDeclaration("Order", "o");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").different(builder.all(subquery)).commit();

		test(stateObject_049(), jpqlStateObject, query_049());
	}

	@Test
	public void test_Query_050() throws Exception {

		// SELECT Distinct object(c)
      // FROM Customer c, IN(c.orders) co
      // WHERE co.totalPrice >= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity >= 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").greaterThanOrEqual(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").greaterThanOrEqual(builder.all(subquery)).commit();

		test(stateObject_050(), jpqlStateObject, query_050());
	}

	@Test
	public void test_Query_051() throws Exception {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity > 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").greaterThanOrEqual(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").lowerThanOrEqual(builder.all(subquery)).commit();

		test(stateObject_051(), jpqlStateObject, query_051());
	}

	@Test
	public void test_Query_052() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new MinFunctionStateObject(subquery, "o.totalPrice"));
		subquery.addRangeDeclaration("Order", "o");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").equal(builder.all(subquery)).commit();

		test(stateObject_052(), jpqlStateObject, query_052());
	}

	@Test
	public void test_Query_053() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").greaterThan(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").lowerThan(builder.all(subquery)).commit();

		test(stateObject_053(), jpqlStateObject, query_053());
	}

	@Test
	public void test_Query_054() throws Exception {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addSelectItem(new ObjectExpressionStateObject(select, "c"));
		select.addRangeDeclaration("Customer", "c");
		select.addCollectionDeclaration("c.orders", "co");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o.totalPrice");
		subquery.addRangeDeclaration("Order", "o");
		subquery.addCollectionDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").greaterThan(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("co.totalPrice").greaterThan(builder.all(subquery)).commit();

		test(stateObject_054(), jpqlStateObject, query_054());
	}

	@Test
	public void test_Query_055() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT l
		//               FROM o.lineItems l
		//               where l.quantity > 3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addRangeDeclaration("Customer", "c").addJoin("c.orders", "o");
		select.addSelectItem("c");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new IdentificationVariableStateObject(subquery, "l"));
		subquery.addDerivedPathDeclaration("o.lineItems", "l");
		subquery.addWhereClause().getBuilder().path("l.quantity").greaterThan(3).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_055(), jpqlStateObject, query_055());
	}

	@Test
	public void test_Query_056() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice BETWEEN 1000 AND 1200)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().toggleDistinct();
		select.addRangeDeclaration("Customer", "c").addJoin("c.orders", "o");
		select.addSelectItem("c");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new IdentificationVariableStateObject(subquery, "o"));
		subquery.addDerivedPathDeclaration("c.orders", "o");
		IConditionalExpressionStateObjectBuilder subBuilder = subquery.addWhereClause().getBuilder();
		subBuilder.path("o.totalPrice").between(subBuilder.numeric(1000), subBuilder.numeric(1200)).commit();

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_056(), jpqlStateObject, query_056());
	}

	@Test
	public void test_Query_057_a() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");
		select.addWhereClause("c.home.state IN(Select distinct w.state from c.work w where w.state = :state)");

		test(stateObject_057(), jpqlStateObject, query_057());
	}

	@Test
	public void test_Query_057_b() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(jpqlStateObject);
		subquery.getSelectClause().setDistinct(true);
		subquery.setSelectItem("w.state");
		subquery.addDerivedPathDeclaration("c.work", "w");
		subquery.addWhereClause().getBuilder().path("w.state").equal(":state").commit();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("c.home.state").in(subquery).commit();

		test(stateObject_057(), jpqlStateObject, query_057());
	}

	@Test
	public void test_Query_058_a() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(jpqlStateObject);
		subquery.setSelectItem(new IdentificationVariableStateObject(subquery, "c"));

		WhereClauseStateObject where = subquery.addWhereClause();
		where.setConditional(new LikeExpressionStateObject(
			where,
			new StateFieldPathExpressionStateObject(where, "c.name"),
			new StringLiteralStateObject(where, "'%Caruso'")
		));

		DerivedPathIdentificationVariableDeclarationStateObject derivedPath = subquery.addDerivedPathDeclaration();
		derivedPath.setRootPath("o.customer");
		derivedPath.setIdentificationVariable("c");

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o");
		select.addSelectItem(new ObjectExpressionStateObject(select, "o"));
		where = select.addWhereClause();
		where.setConditional(new ExistsExpressionStateObject(where, subquery));

		test(stateObject_058(), jpqlStateObject, query_058());
	}

	@Test
	public void test_Query_058_b() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("Object(o)");
		select.getFromClause().parse("Order o");
		select.addWhereClause("EXISTS (Select c From o.customer c WHERE c.name LIKE '%Caruso')");

		test(stateObject_058(), jpqlStateObject, query_058());
	}

	@Test
	public void test_Query_058_c() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("Object(o)");
		select.addRangeDeclaration("Order", "o");
		select.addWhereClause().parse("EXISTS (Select c From o.customer c WHERE c.name LIKE '%Caruso')");

		test(stateObject_058(), jpqlStateObject, query_058());
	}

	@Test
	public void test_Query_058_d() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("OBJECT(o)");
		select.getFromClause().parse("Order o");

		ExistsExpressionStateObject exists = new ExistsExpressionStateObject(select);
		exists.parse("Select c From o.customer c WHERE c.name LIKE '%Caruso'");
		select.addWhereClause().setConditional(exists);

		test(stateObject_058(), jpqlStateObject, query_058());
	}

	@Test
	public void test_Query_059_a() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");
		select.addWhereClause("EXISTS (SELECT o FROM c.orders o where o.totalPrice > 1500)");

		test(stateObject_059(), jpqlStateObject, query_059());
	}

	@Test
	public void test_Query_059_b() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o");
		subquery.addDerivedPathDeclaration("c.orders", "o");
		subquery.addWhereClause().getBuilder().path("o.totalPrice").greaterThan(1500).commit();

		select.addWhereClause().getBuilder().exists(subquery).commit();

		test(stateObject_059(), jpqlStateObject, query_059());
	}

	@Test
	public void test_Query_060() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("o1");
		subquery.addDerivedPathDeclaration("c.orders", "o1");

		select.addWhereClause().getBuilder().notExists(subquery).commit();

		test(stateObject_060(), jpqlStateObject, query_060());
	}

	@Test
	public void test_Query_061() throws Exception {

		// select object(o)
		// FROM Order o
		// Where SQRT(o.totalPrice) > :doubleValue

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o");
		select.addSelectItem(new ObjectExpressionStateObject(select, "o"));

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.sqrt(builder.path("o.totalPrice")).greaterThan(builder.parameter(":doubleValue")).commit();

		test(stateObject_061(), jpqlStateObject, query_061());
	}

	@Test
	public void test_Query_062() throws Exception {

		// select sum(o.totalPrice)
		// FROM Order o
		// GROUP BY o.totalPrice
		// HAVING ABS(o.totalPrice) = :doubleValue

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectBuilder().sum("o.totalPrice").commit();
		select.addRangeDeclaration("Order", "o");
		select.addGroupByClause().addGroupByItem("o.totalPrice");

		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.abs(builder.path("o.totalPrice")).greaterThan(builder.parameter(":doubleValue")).commit();

		test(stateObject_062(), jpqlStateObject, query_062());
	}

	@Test
	public void test_Query_063() throws Exception {

		// select c.name
		// FROM Customer c
		// Group By c.name
		// HAVING trim(TRAILING from c.name) = ' David R. Vincent'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
				builder.trim(Specification.TRAILING, builder.path("c.name"))
			.equal(
				builder.string("' David R. Vincent'")
			)
		.commit();

		test(stateObject_063(), jpqlStateObject, query_063());
	}

	@Test
	public void test_Query_064() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// Having trim(LEADING from c.name) = 'David R. Vincent '

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
				builder.trim(Specification.LEADING, builder.path("c.name"))
			.equal(
				builder.string("'David R. Vincent '")
			)
		.commit();

		test(stateObject_064(), jpqlStateObject, query_064());
	}

	@Test
	public void test_Query_065() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group by c.name
		// HAVING trim(BOTH from c.name) = ' David R. Vincent'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
				builder.trim(Specification.BOTH, builder.path("c.name"))
			.equal(
				builder.string("'David R. Vincent'")
			)
		.commit();

		test(stateObject_065(), jpqlStateObject, query_065());
	}

	@Test
	public void test_Query_066() throws Exception {

		// select c.name
		// FROM  Customer c
		// GROUP BY c.name
		// HAVING LOCATE('Frechette', c.name) > 0

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");

		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.locate(builder.string("'Frechette'"), builder.path("c.name")).greaterThan(0).commit();

		test(stateObject_066(), jpqlStateObject, query_066());
	}

	@Test
	public void test_Query_067() throws Exception {

		// select a.city
		// FROM  Customer c JOIN c.home a
		// GROUP BY a.city
		// HAVING LENGTH(a.city) = 10

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.home", "a");
		select.addSelectItem("a.city");
		select.addGroupByClause("a.city");

		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.length(builder.path("a.city")).equal(10).commit();

		test(stateObject_067(), jpqlStateObject, query_067());
	}

	@Test
	public void test_Query_068_a() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.country", "cc");
		select.addSelectItem(new CountFunctionStateObject(select, "cc.country"));
		select.addGroupByClause("cc.country");
		select.addHavingClause("UPPER(cc.country) = 'ENGLAND'");

		test(stateObject_068(), jpqlStateObject, query_068());
	}

	@Test
	public void test_Query_068_b() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.country", "cc");
		select.addSelectItem(new CountFunctionStateObject(select, "cc.country"));
		select.addGroupByClause("cc.country");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.upper(builder.path("cc.country")).equal(builder.string("'ENGLAND'")).commit();

		test(stateObject_068(), jpqlStateObject, query_068());
	}

	@Test
	public void test_Query_069() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING LOWER(cc.code) = 'gbr'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem(new CountFunctionStateObject(select, "cc.code"));
		select.addRangeDeclaration("Customer", "c").addJoin("c.country", "cc");
		select.addGroupByClause("cc.code");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.lower(builder.path("cc.code")).equal(builder.string("'gbr'")).commit();

		test(stateObject_069(), jpqlStateObject, query_069());
	}

	@Test
	public void test_Query_070_a() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		select.addHavingClause("c.name = concat(:fmname, :lname)");

		test(stateObject_070(), jpqlStateObject, query_070());
	}

	@Test
	public void test_Query_070_b() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
				builder.path("c.name")
			.equal(
				builder.concat(builder.parameter(":fmname"), builder.parameter(":lname"))
			)
		.commit();

		test(stateObject_070(), jpqlStateObject, query_070());
	}

	@Test
	public void test_Query_071_a() throws Exception {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.aliases", "a");
		select.addSelectItem(new CountFunctionStateObject(select, "c"));
		select.addGroupByClause().addGroupByItem("a.alias");
		select.addHavingClause("a.alias = SUBSTRING(:string1, :int1, :int2)");

		test(stateObject_071(), jpqlStateObject, query_071());
	}

	@Test
	public void test_Query_071_b() throws Exception {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.aliases", "a");
		select.addSelectItem(new CountFunctionStateObject(select, "c"));
		select.addGroupByClause().addGroupByItem("a.alias");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
				builder.path("a.alias")
			.equal(
				builder.substring(
					builder.parameter(":string1"),
					builder.parameter(":int1"),
					builder.parameter(":int2")
				)
			)
		.commit();

		test(stateObject_071(), jpqlStateObject, query_071());
	}

	@Test
	public void test_Query_072_a() throws Exception {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.country.country");
		select.addGroupByClause("c.country.country");

		test(stateObject_072(), jpqlStateObject, query_072());
	}

	@Test
	public void test_Query_072_b() throws Exception {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c.country.country");
		select.addGroupByClause().addGroupByItem("c.country.country");

		test(stateObject_072(), jpqlStateObject, query_072());
	}

	@Test
	public void test_Query_073_a() throws Exception {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.country", "cc");
		select.addSelectItem("Count(c)");
		select.addGroupByClause("cc.code");
		select.addHavingClause("cc.code IN ('GBR', 'CHA')");

		test(stateObject_073(), jpqlStateObject, query_073());
	}

	@Test
	public void test_Query_073_b() throws Exception {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.country", "cc");
		select.addSelectItem(new CountFunctionStateObject(select, "c"));
		select.addGroupByClause().addGroupByItem("cc.code");
		IConditionalExpressionStateObjectBuilder builder = select.addHavingClause().getBuilder();
		builder.path("cc.code").in(builder.string("'GBR'"), builder.string("'CHA'")).commit();

		test(stateObject_073(), jpqlStateObject, query_073());
	}

	@Test
	public void test_Query_074_a() throws Exception {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.orders", "o");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		select.addWhereClause("o.totalPrice BETWEEN 90 AND 160");

		test(stateObject_074(), jpqlStateObject, query_074());
	}

	@Test
	public void test_Query_074_b() throws Exception {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").addJoin("c.orders", "o");
		select.addSelectItem("c.name");
		select.addGroupByClause().addGroupByItem("c.name");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.totalPrice")
			.between(
				builder.numeric(90),
				builder.numeric(160)
			)
		.commit();

		test(stateObject_074(), jpqlStateObject, query_074());
	}

	@Test
	public void test_Query_075_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.id = '1001' OR o.totalPrice > 10000");

		test(stateObject_075(), jpqlStateObject, query_075());
	}

	@Test
	public void test_Query_075_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.id").equal(builder.string("'1001'"))
			.or(
				builder.path("o.totalPrice").greaterThan(10000)
			)
		.commit();

		test(stateObject_075(), jpqlStateObject, query_075());
	}

	@Test
	public void test_Query_076_a() throws Exception {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.id = '1001' OR o.totalPrice < 1000");

		test(stateObject_076(), jpqlStateObject, query_076());
	}

	@Test
	public void test_Query_076_b() throws Exception {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.id").equal(builder.string("'1001'"))
			.or(
				builder.path("o.totalPrice").lowerThan(1000)
			)
		.commit();

		test(stateObject_076(), jpqlStateObject, query_076());
	}

	@Test
	public void test_Query_077_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000");

		test(stateObject_077(), jpqlStateObject, query_077());
	}

	@Test
	public void test_Query_077_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.name").equal(builder.string("'Karen R. Tegan'"))
			.or(
				builder.path("o.totalPrice").lowerThan(10000)
			)
		.commit();

		test(stateObject_077(), jpqlStateObject, query_077());
	}

	@Test
	public void test_Query_078_a() throws Exception {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("o");
		select.addWhereClause("o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000");

		test(stateObject_078(), jpqlStateObject, query_078());
	}

	@Test
	public void test_Query_078_b() throws Exception {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.getSelectClause().setDistinct(true);
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("o");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("o.customer.name").equal(builder.string("'Karen R. Tegan'"))
			.or(
				builder.path("o.totalPrice").greaterThan(5000)
			)
		.commit();

		test(stateObject_078(), jpqlStateObject, query_078());
	}

	@Test
	public void test_Query_079_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.id = '1001' AND o.totalPrice > 10000");

		test(stateObject_079(), jpqlStateObject, query_079());
	}

	@Test
	public void test_Query_079_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.id").equal(builder.string("'1001'"))
			.and(
				builder.path("o.totalPrice").greaterThan(10000)
			)
		.commit();

		test(stateObject_079(), jpqlStateObject, query_079());
	}

	@Test
	public void test_Query_080_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.id = '1001' AND o.totalPrice < 1000");

		test(stateObject_080(), jpqlStateObject, query_080());
	}

	@Test
	public void test_Query_080_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.id").equal(builder.string("'1001'"))
			.and(
				builder.path("o.totalPrice").lowerThan(1000)
			)
		.commit();

		test(stateObject_080(), jpqlStateObject, query_080());
	}

	@Test
	public void test_Query_081_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000");

		test(stateObject_081(), jpqlStateObject, query_081());
	}

	@Test
	public void test_Query_081_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.name").equal(builder.string("'Karen R. Tegan'"))
			.and(
				builder.path("o.totalPrice").greaterThan(10000)
			)
		.commit();

		test(stateObject_081(), jpqlStateObject, query_081());
	}

	@Test
	public void test_Query_082_a() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		select.addWhereClause("o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500");

		test(stateObject_082(), jpqlStateObject, query_082());
	}

	@Test
	public void test_Query_082_b() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o").getRangeVariableDeclaration().setAs(true);
		select.addSelectItem("Object(o)");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("o.customer.name").equal(builder.string("'Karen R. Tegan'"))
			.and(
				builder.path("o.totalPrice").greaterThan(500)
			)
		.commit();

		test(stateObject_082(), jpqlStateObject, query_082());
	}

	//@Test
	public void test_Query_083() throws Exception {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_083(), jpqlStateObject, query_083());
	}

	//@Test
	public void test_Query_084() throws Exception {

		// SELECT DISTINCT o
		// From Order o
		// where o.totalPrice NOT BETWEEN 1000 AND 1200

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_084(), jpqlStateObject, query_084());
	}

	//@Test
	public void test_Query_085() throws Exception {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate BETWEEN :date1 AND :date6

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_085(), jpqlStateObject, query_085());
	}

	//@Test
	public void test_Query_086() throws Exception {

		// SELECT DISTINCT a
		// from Alias a LEFT JOIN FETCH a.customers
		// where a.alias LIKE 'a%'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_086(), jpqlStateObject, query_086());
	}

	//@Test
	public void test_Query_087() throws Exception {

		// select Object(o)
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.name LIKE '%Caruso'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_087(), jpqlStateObject, query_087());
	}

	//@Test
	public void test_Query_088() throws Exception {

		// select o
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.home.city='Lawrence'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_088(), jpqlStateObject, query_088());
	}

	//@Test
	public void test_Query_089() throws Exception {

		// SELECT DISTINCT c
		// from Customer c LEFT JOIN FETCH c.orders
		// where c.home.state IN('NY','RI')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_089(), jpqlStateObject, query_089());
	}

	//@Test
	public void test_Query_090() throws Exception {

		// SELECT c
		// from Customer c JOIN FETCH c.spouse

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");



		test(stateObject_090(), jpqlStateObject, query_090());
	}

	//@Test
	public void test_Query_091() throws Exception {

		// SELECT Object(c)
		// from Customer c INNER JOIN c.aliases a
		// where a.alias = :aName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_091(), jpqlStateObject, query_091());
	}

	//@Test
	public void test_Query_092() throws Exception {

		// SELECT Object(o)
		// from Order o INNER JOIN o.customer cust
		// where cust.name = ?1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_092(), jpqlStateObject, query_092());
	}

	//@Test
	public void test_Query_093() throws Exception {

		// SELECT DISTINCT object(c)
		// from Customer c INNER JOIN c.creditCards cc
		// where cc.type='VISA'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_093(), jpqlStateObject, query_093());
	}

	//@Test
	public void test_Query_094() throws Exception {

		// SELECT c
		// from Customer c INNER JOIN c.spouse s

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_094(), jpqlStateObject, query_094());
	}

	//@Test
	public void test_Query_095() throws Exception {

		// select cc.type
		// FROM CreditCard cc JOIN cc.customer cust
		// GROUP BY cc.type

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_095(), jpqlStateObject, query_095());
	}

	public void test_Query_096() throws Exception {

		// select cc.code
		// FROM Customer c JOIN c.country cc
		// GROUP BY cc.code

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_096(), jpqlStateObject, query_096());
	}

	public void test_Query_097() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where LOWER(a.alias)='sjc'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_097(), jpqlStateObject, query_097());
	}

	public void test_Query_098() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where UPPER(a.alias)='SJC'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_098(), jpqlStateObject, query_098());
	}

	public void test_Query_099() throws Exception {

		// SELECT c.id, a.alias
		// from Customer c LEFT OUTER JOIN c.aliases a
		// where c.name LIKE 'Ste%'
		// ORDER BY a.alias, c.id

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_099(), jpqlStateObject, query_099());
	}

	public void test_Query_100() throws Exception {

		// SELECT o.id, cust.id
		// from Order o LEFT OUTER JOIN o.customer cust
		// where cust.name=?1
		// ORDER BY o.id

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_100(), jpqlStateObject, query_100());
	}

	public void test_Query_101() throws Exception {

		// SELECT DISTINCT c
		// from Customer c LEFT OUTER JOIN c.creditCards cc
		// where c.name LIKE '%Caruso'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_101(), jpqlStateObject, query_101());
	}

	public void test_Query_102() throws Exception {

		// SELECT Sum(p.quantity)
		// FROM Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_102(), jpqlStateObject, query_102());
	}

	public void test_Query_103() throws Exception {

		// Select Count(c.home.city)
		// from Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_103(), jpqlStateObject, query_103());
	}

	public void test_Query_104() throws Exception {

		// SELECT Sum(p.price)
		// FROM Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_104(), jpqlStateObject, query_104());
	}

	public void test_Query_105() throws Exception {

		// SELECT AVG(o.totalPrice)
		// FROM Order o

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_105(), jpqlStateObject, query_105());
	}

	public void test_Query_106() throws Exception {

		// SELECT DISTINCT MAX(l.quantity)
		// FROM LineItem l

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_106(), jpqlStateObject, query_106());
	}

	public void test_Query_107() throws Exception {

		// SELECT DISTINCT MIN(o.id)
		// FROM Order o
		// where o.customer.name = 'Robert E. Bissett'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_107(), jpqlStateObject, query_107());
	}

	public void test_Query_108() throws Exception {

		// SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
		// FROM Customer c
		// where c.work.city = :workcity

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_108(), jpqlStateObject, query_108());
	}

	public void test_Query_109() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) > 100

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_109(), jpqlStateObject, query_109());
	}

	public void test_Query_110() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) >= 2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_110(), jpqlStateObject, query_110());
	}

	public void test_Query_111() throws Exception {

		// select Distinct c
		// FROM Customer c LEFT OUTER JOIN c.work workAddress
		// where workAddress.zip IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_111(), jpqlStateObject, query_111());
	}

	public void test_Query_112() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c, IN(c.orders) o

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_112(), jpqlStateObject, query_112());
	}

	public void test_Query_113() throws Exception {

		// Select Distinct Object(c)
		// from Customer c
		// where c.name is null

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_113(), jpqlStateObject, query_113());
	}

	public void test_Query_114() throws Exception {

		// Select c.name
		// from Customer c
		// where c.home.street = '212 Edgewood Drive'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_114(), jpqlStateObject, query_114());
	}

	public void test_Query_115() throws Exception {

		// Select s.customer
		// from Spouse s
		// where s.id = '6'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_115(), jpqlStateObject, query_115());
	}

	public void test_Query_116() throws Exception {

		// Select c.work.zip
		// from Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_116(), jpqlStateObject, query_116());
	}

	public void test_Query_117() throws Exception {

		// SELECT Distinct Object(c)
		// From Customer c, IN(c.home.phones) p
		// where p.area LIKE :area

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_117(), jpqlStateObject, query_117());
	}

	public void test_Query_118() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c, in(c.aliases) a
		// where NOT a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_118(), jpqlStateObject, query_118());
	}

	public void test_Query_119() throws Exception {

		// select distinct object(c)
		// fRoM Customer c, IN(c.aliases) a
		// where c.name = :cName OR a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_119(), jpqlStateObject, query_119());
	}

	public void test_Query_120() throws Exception {

		// select Distinct Object(c)
		// from Customer c, in(c.aliases) a
		// where c.name = :cName AND a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_120(), jpqlStateObject, query_120());
	}

	public void test_Query_121() throws Exception {

		// sElEcT Distinct oBJeCt(c)
		// FROM Customer c, IN(c.aliases) a
		// WHERE a.customerNoop IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_121(), jpqlStateObject, query_121());
	}

	public void test_Query_122() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE '%\\_%' escape '\\'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_122(), jpqlStateObject, query_122());
	}

	public void test_Query_123() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_123(), jpqlStateObject, query_123());
	}

	public void test_Query_124() throws Exception {

		// Select Distinct o.creditCard.balance
		// from Order o
		// ORDER BY o.creditCard.balance ASC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_124(), jpqlStateObject, query_124());
	}

	public void test_Query_125() throws Exception {

		// Select c.work.zip
		// from Customer c
		// where c.work.zip IS NOT NULL
		// ORDER BY c.work.zip ASC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_125(), jpqlStateObject, query_125());
	}

	public void test_Query_126() throws Exception {

		// SELECT a.alias
		// FROM Alias AS a
		// WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_126(), jpqlStateObject, query_126());
	}

	public void test_Query_127() throws Exception {

		// Select Object(c)
		// from Customer c
		// where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_127(), jpqlStateObject, query_127());
	}

	public void test_Query_128() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where p.name = ?1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_128(), jpqlStateObject, query_128());
	}

	public void test_Query_129() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_129(), jpqlStateObject, query_129());
	}

	public void test_Query_130() throws Exception {

		// Select Distinct Object(o)
		// from Order o
		// where o.customer.name IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_130(), jpqlStateObject, query_130());
	}

	public void test_Query_131() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where (p.quantity < 10) OR (p.quantity > 20)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_131(), jpqlStateObject, query_131());
	}

	public void test_Query_132() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where p.quantity NOT BETWEEN 10 AND 20

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_132(), jpqlStateObject, query_132());
	}

	public void test_Query_133() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where (p.quantity >= 10) AND (p.quantity <= 20)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_133(), jpqlStateObject, query_133());
	}

	public void test_Query_134() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where p.quantity BETWEEN 10 AND 20

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_134(), jpqlStateObject, query_134());
	}

	public void test_Query_135() throws Exception {

		// Select Distinct OBJECT(c)
		// from Customer c, IN(c.creditCards) b
		// where SQRT(b.balance) = :dbl

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_135(), jpqlStateObject, query_135());
	}

	public void test_Query_136() throws Exception {

		// Select Distinct OBJECT(c)
		// From Product p
		// where MOD(550, 100) = p.quantity

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_136(), jpqlStateObject, query_136());
	}

	public void test_Query_137() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_137(), jpqlStateObject, query_137());
	}

	public void test_Query_138() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// where c.home.state IN('NH', 'RI')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_138(), jpqlStateObject, query_138());
	}

	public void test_Query_140() throws Exception {

		// SELECT c
		// from Customer c
		// where c.home.city IN(:city)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_140(), jpqlStateObject, query_140());
	}

	public void test_Query_141() throws Exception {

		// Select Distinct Object(o)
		// from Order o, in(o.lineItems) l
		// where l.quantity NOT IN (1, 5)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_141(), jpqlStateObject, query_141());
	}

	public void test_Query_142() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE o.sampleLineItem MEMBER OF o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_142(), jpqlStateObject, query_142());
	}

	public void test_Query_143() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE :param NOT MEMBER o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_143(), jpqlStateObject, query_143());
	}

	public void test_Query_144() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o, LineItem l
		// WHERE l MEMBER o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_144(), jpqlStateObject, query_144());
	}

	public void test_Query_145() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE 'sh\\_ll' escape '\\'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_145(), jpqlStateObject, query_145());
	}

	public void test_Query_146() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop NOT MEMBER OF a.customersNoop

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_146(), jpqlStateObject, query_146());
	}

	public void test_Query_147() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop MEMBER OF a.customersNoop

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_147(), jpqlStateObject, query_147());
	}

	public void test_Query_148() throws Exception {

		// Select Distinct Object(a)
		// from Alias a
		// where LOCATE('ev', a.alias) = 3

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_148(), jpqlStateObject, query_148());
	}

	public void test_Query_149() throws Exception {

		// Select DISTINCT Object(o)
		// From Order o
		// WHERE o.totalPrice > ABS(:dbl)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_149(), jpqlStateObject, query_149());
	}

	public void test_Query_150() throws Exception {

		// Select Distinct OBjeCt(a)
		// From Alias a
		// WHERE LENGTH(a.alias) > 4

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_150(), jpqlStateObject, query_150());
	}

	public void test_Query_151() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_151(), jpqlStateObject, query_151());
	}

	public void test_Query_152() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = CONCAT('ste', 'vie')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_152(), jpqlStateObject, query_152());
	}

	public void test_Query_153() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.work.zip IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_153(), jpqlStateObject, query_153());
	}

	public void test_Query_154() throws Exception {

		// sELEct dIsTiNcT oBjEcT(c)
		// FROM Customer c
		// WHERE c.work.zip IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_154(), jpqlStateObject, query_154());
	}

	public void test_Query_155() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS NOT EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_155(), jpqlStateObject, query_155());
	}

	public void test_Query_156() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_156(), jpqlStateObject, query_156());
	}

	public void test_Query_157() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip not like '%44_'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_157(), jpqlStateObject, query_157());
	}

	public void test_Query_158() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip LIKE '%77'"

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_158(), jpqlStateObject, query_158());
	}

	public void test_Query_159() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c Left Outer Join c.home h
		// WHERE h.city Not iN ('Swansea', 'Brookline')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_159(), jpqlStateObject, query_159());
	}

	public void test_Query_160() throws Exception {

		// select distinct c
		// FROM Customer c
		// WHERE c.home.city IN ('Lexington')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_160(), jpqlStateObject, query_160());
	}

	public void test_Query_161() throws Exception {

		// sElEcT c
		// FROM Customer c
		// Where c.name = :cName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_161(), jpqlStateObject, query_161());
	}

	public void test_Query_162() throws Exception {

		// select distinct Object(o)
		// From Order o
		// WHERE o.creditCard.approved = FALSE

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_161(), jpqlStateObject, query_161());
	}

	public void test_Query_163() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice NOT bETwEeN 1000 AND 1200

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_163(), jpqlStateObject, query_163());
	}

	public void test_Query_164() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice BETWEEN 1000 AND 1200

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_164(), jpqlStateObject, query_164());
	}

	public void test_Query_165() throws Exception {

		// SELECT DISTINCT Object(o)
		// FROM Order o, in(o.lineItems) l
		// WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_165(), jpqlStateObject, query_165());
	}

	public void test_Query_166() throws Exception {

		// select distinct Object(o)
		// FROM Order AS o, in(o.lineItems) l
		// WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_166(), jpqlStateObject, query_166());
	}

	public void test_Query_167() throws Exception {

		// SeLeCt DiStInCt oBjEcT(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_167(), jpqlStateObject, query_167());
	}

	public void test_Query_168() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE NOT o.totalPrice < 4500

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_168(), jpqlStateObject, query_168());
	}

	public void test_Query_169() throws Exception {

		// Select DISTINCT Object(P)
		// From Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_169(), jpqlStateObject, query_169());
	}

	public void test_Query_170() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_170(), jpqlStateObject, query_170());
	}

	public void test_Query_171() throws Exception {

		// SELECT c
		// from Customer c
		// WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_171(), jpqlStateObject, query_171());
	}

	public void test_Query_172() throws Exception {

		// SELECT c
		// from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_172(), jpqlStateObject, query_172());
	}

	public void test_Query_173() throws Exception {

		// Select Distinct Object(c)
		// FrOm Customer c, In(c.aliases) a
		// WHERE a.alias = :aName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_173(), jpqlStateObject, query_173());
	}

	public void test_Query_174() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_174(), jpqlStateObject, query_174());
	}

	public void test_Query_175() throws Exception {

		// Select Distinct o
		// from Order AS o
		// WHERE o.customer.name = :name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_175(), jpqlStateObject, query_175());
	}

	public void test_Query_176() throws Exception {

		// UPDATE Customer c SET c.name = 'CHANGED'
		// WHERE c.orders IS NOT EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_176(), jpqlStateObject, query_176());
	}

	public void test_Query_177() throws Exception {

		// UPDATE DateTime SET date = CURRENT_DATE

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setEntityName("DateTime");



		test(stateObject_177(), jpqlStateObject, query_177());
	}

	public void test_Query_178() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.firstName = :first AND
		//     c.lastName = :last

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_178(), jpqlStateObject, query_178());
	}

	public void test_Query_179() throws Exception {

		// SELECT OBJECT ( c ) FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_179(), jpqlStateObject, query_179());
	}

	public void test_Query_180() throws Exception {

		// SELECT c.firstName, c.lastName
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_180(), jpqlStateObject, query_180());
	}

	public void test_Query_181() throws Exception {

		// SELECT c.address.city
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_181(), jpqlStateObject, query_181());
	}

	public void test_Query_182() throws Exception {

		// SELECT new com.titan.domain.Name(c.firstName, c.lastName)
		// FROM Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_182(), jpqlStateObject, query_182());
	}

	public void test_Query_183() throws Exception {

		// SELECT cbn.ship
		// FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_183(), jpqlStateObject, query_183());
	}

	public void test_Query_184() throws Exception {

		// Select c.firstName, c.lastName, p.number
		// From Customer c Left Join c.phoneNumbers p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_184(), jpqlStateObject, query_184());
	}

	public void test_Query_185() throws Exception {

		// SELECT r
		// FROM Reservation AS r
		// WHERE (r.amountPaid * .01) > 300.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_185(), jpqlStateObject, query_185());
	}

	public void test_Query_186() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_186(), jpqlStateObject, query_186());
	}

	public void test_Query_187() throws Exception {

		// SELECT r
		// FROM Reservation r, IN ( r.customers ) AS cust
		// WHERE cust = :specificCustomer

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_187(), jpqlStateObject, query_187());
	}

	public void test_Query_188() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_188(), jpqlStateObject, query_188());
	}

	public void test_Query_189() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_189(), jpqlStateObject, query_189());
	}

	public void test_Query_190() throws Exception {

		// SELECT c
		// FROM Customer AS c
		//  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_190(), jpqlStateObject, query_190());
	}

	public void test_Query_191() throws Exception {

		// SELECT cab
		// FROM Cabin AS cab
		// WHERE cab.deckLevel IN (1,3,5,7)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_191(), jpqlStateObject, query_191());
	}

	public void test_Query_192() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_192(), jpqlStateObject, query_192());
	}

	public void test_Query_193() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_193(), jpqlStateObject, query_193());
	}

	public void test_Query_194() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state = 'TX' AND
		//       c.lastName = 'Smith' AND
		//       c.firstName = 'John'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_194(), jpqlStateObject, query_194());
	}

	public void test_Query_195() throws Exception {

		// SELECT crs
		// FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust
		// WHERE
		//  cust = :myCustomer
		//  AND
		//  cust MEMBER OF res.customers

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_196(), jpqlStateObject, query_196());
	}

	public void test_Query_196() throws Exception {

		// SELECT c
		// FROM Customer AS c
		// WHERE    LENGTH(c.lastName) > 6
		//       AND
		//          LOCATE( c.lastName, 'Monson' ) > -1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_196(), jpqlStateObject, query_196());
	}

	public void test_Query_197() throws Exception {

		// SELECT c
		// FROM Customer AS C
		// ORDER BY c.lastName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_197(), jpqlStateObject, query_197());
	}

	public void test_Query_198() throws Exception {

		// SELECT c
		// FROM Customer AS C
      // WHERE c.address.city = 'Boston' AND c.address.state = 'MA'
		// ORDER BY c.lastName DESC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_198(), jpqlStateObject, query_198());
	}

	public void test_Query_199() throws Exception {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_199(), jpqlStateObject, query_199());
	}

	public void test_Query_200() throws Exception {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name
		// HAVING count(res) > 10

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_200(), jpqlStateObject, query_200());
	}

	//@Test
	public void test_Query_201() throws Exception {

		// SELECT COUNT (res)
		// FROM Reservation res
		// WHERE res.amountPaid >
		//       (SELECT avg(r.amountPaid) FROM Reservation r)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		test(stateObject_201(), jpqlStateObject, query_201());
	}

	@Test
	public void test_Query_202() throws Exception {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 100000 < (
		//    SELECT SUM(res.amountPaid) FROM cr.reservations res
		// )

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Cruise", "cr");
		select.addSelectItem("cr");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem(new SumFunctionStateObject(subquery, "res.amountPaid"));
		subquery.addDerivedPathDeclaration("cr.reservations", "res");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.numeric(100000).lowerThan(builder.sub(subquery)).commit();

		test(stateObject_202(), jpqlStateObject, query_202());
	}

	@Test
	public void test_Query_203() throws Exception {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 0 < ALL (
		//   SELECT res.amountPaid from cr.reservations res
		// )

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Cruise", "cr");
		select.addSelectItem("cr");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("res.amountPaid");
		subquery.addDerivedPathDeclaration("cr.reservations", "res");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.numeric(0).lowerThan(builder.all(subquery)).commit();

		test(stateObject_203(), jpqlStateObject, query_203());
	}

	@Test
	public void test_Query_204() throws Exception {

		// UPDATE Reservation res
		// SET res.name = 'Pascal'
		// WHERE EXISTS (
		//    SELECT c
		//    FROM res.customers c
		//    WHERE c.firstName = 'Bill' AND c.lastName='Burke'
		// )

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Reservation", "res");
		update.addItem("res.name", "'Pascal'");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(update);
		subquery.setSelectItem("c");
		subquery.addDerivedPathDeclaration("res.customers", "c");
		IConditionalExpressionStateObjectBuilder builder = subquery.addWhereClause().getBuilder();
		builder.
				path("c.firstName").equal("'Bill'")
			.and(
				builder.path("c.lastName").equal("'Burke'")
			)
		.commit();

		update.addWhereClause().getBuilder().exists(subquery).commit();

		test(stateObject_204(), jpqlStateObject, query_204());
	}

	@Test
	public void test_Query_215() throws Exception {

		// SELECT o
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity DESC, o.totalcost

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();
		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("o");

		IdentificationVariableDeclarationStateObject range = select.addRangeDeclaration("Customer", "c");
		range.addJoin("c.orders",  "o");
		range.addJoin("c.address", "a");

		select.addWhereClause().getBuilder().path("a.state").equal("'CA'").commit();
		select.addOrderByClause().addItemDesc("o.quantity");
		select.getOrderByClause().addItem("o.totalcost");

		test(stateObject_215(), jpqlStateObject, query_215());
	}

	@Test
	public void test_Query_216() throws Exception {

		// SELECT o.quantity, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity, a.zipcode

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("o.quantity");
		select.addSelectItem("a.zipcode");

		IdentificationVariableDeclarationStateObject range = select.addRangeDeclaration("Customer", "c");
		range.addJoin("c.orders",  "o");
		range.addJoin("c.address", "a");

		select.addWhereClause().getBuilder().path("a.state").equal("'CA'").commit();
		select.addOrderByClause().
			addItem("o.quantity").getParent().
			addItem("a.zipcode");

		test(stateObject_216(), jpqlStateObject, query_216());
	}

	@Test
	public void test_Query_219() throws Exception {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Customer", "c");
		IConditionalExpressionStateObjectBuilder builder = delete.addWhereClause().getBuilder();
		builder.path("c.status").equal("'inactive'").commit();

		test(stateObject_219(), jpqlStateObject, query_219());
	}

	@Test
	public void test_Query_220_a() throws Exception {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'
		//       AND
		//       c.orders IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Customer", "c");

		IConditionalExpressionStateObjectBuilder builder = delete.addWhereClause().getBuilder();
				builder.path("c.status").equal("'inactive'")
			.and(
				builder.isEmpty("c.orders")
			)
		.commit();

		test(stateObject_220(), jpqlStateObject, query_220());
	}

	@Test
	public void test_Query_220_b() throws Exception {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'
		//       AND
		//       c.orders IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Customer", "c");
		WhereClauseStateObject where = delete.addWhereClause();
		where.parse("c.status = 'inactive'");
		where.andParse("c.orders IS EMPTY");

		test(stateObject_220(), jpqlStateObject, query_220());
	}

	@Test
	public void test_Query_221_a() throws Exception {

		// UPDATE customer c
		// SET c.status = 'outstanding'
		// WHERE c.balance < 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("customer", "c");
		update.addItem("c.status", new StringLiteralStateObject(update, "'outstanding'"));

		IConditionalExpressionStateObjectBuilder builder = update.addWhereClause().getBuilder();
		builder.path("c.balance").lowerThan(10000).commit();

		test(stateObject_221(), jpqlStateObject, query_221());
	}

	@Test
	public void test_Query_221_b() throws Exception {

		// UPDATE customer c
		// SET c.status = 'outstanding'
		// WHERE c.balance < 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("customer", "c");
		update.addItem("c.status", "'outstanding'");
		update.addWhereClause().parse("c.balance < 10000");

		test(stateObject_221(), jpqlStateObject, query_221());
	}

	@Test
	public void test_Query_228_a() throws Exception {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.phoneNumbers", "p");
		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
				builder.path("e.firstName").equal("'Bob'")
			.and(
				builder.path("e.lastName").like("'Smith%'")
			)
			.and(
				builder.path("e.address.city").equal("'Toronto'")
			)
			.and(
				builder.path("p.areaCode").different("'2'")
			)
		.commit();

		test(stateObject_228(), jpqlStateObject, query_228());
	}

	@Test
	public void test_Query_228_b() throws Exception {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.phoneNumbers", "p");
		select.addWhereClause("e.firstName = 'Bob' and e.lastName like 'Smith%' and e.address.city = 'Toronto' and p.areaCode <> '2'");

		test(stateObject_228(), jpqlStateObject, query_228());
	}

	@Test
	public void test_Query_228_c() throws Exception {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.phoneNumbers", "p");
		select.addWhereClause().parse("e.firstName = 'Bob' and e.lastName like 'Smith%' and e.address.city = 'Toronto' and p.areaCode <> '2'");

		test(stateObject_228(), jpqlStateObject, query_228());
	}

	@Test
	public void test_Query_228_d() throws Exception {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.phoneNumbers", "p");

		WhereClauseStateObject where = select.addWhereClause();
		where.parse("e.firstName = 'Bob'");
		where.andParse("e.lastName like 'Smith%'");
		where.andParse("e.address.city = 'Toronto'");
		where.andParse("p.areaCode <> '2'");

		test(stateObject_228(), jpqlStateObject, query_228());
	}

	@Test
	public void test_Query_229() throws Exception {

		// Select e
		// From Employee e
		// Where Exists(Select a From e.address a Where a.zipCode = 27519)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("a");
		subquery.addDerivedPathDeclaration("e.address", "a");
		subquery.addWhereClause("a.zipCode = 27519");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_229(), jpqlStateObject, query_229());
	}

	@Test
	public void test_Query_230() throws Exception {

		// Select e
		// From Employee e
		// Where Exists(Where Exists(Select e.name From In e.phoneNumbers Where e.zipCode = 27519))

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e");

		SimpleSelectStatementStateObject subquery = new SimpleSelectStatementStateObject(select);
		subquery.setSelectItem("e.name");
		subquery.addDerivedCollectionDeclaration("e.phoneNumbers");
		subquery.addWhereClause("e.zipCode = 27519");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		test(stateObject_230(), jpqlStateObject, query_230());
	}
}