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
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
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
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

/**
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

		jpqlQuery(stateObject_001()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_002() throws Exception {

		// SELECT e\nFROM Employee e
		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		jpqlQuery(stateObject_002()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_003()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_003()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_003()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_004()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_004()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_005()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_005()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_005()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_005()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_006()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_007()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_008()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_009()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_010()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_011()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_012()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_013()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_014()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_015() throws Exception {

		// DELETE FROM Project p
      // WHERE p.employees IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Project", "p");
		delete.addWhereClause().getBuilder().isEmpty("p.employees").commit();

		jpqlQuery(stateObject_015()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_016()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_017()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_018()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_019()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_020()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_021()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_022()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_023()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_024()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_025()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_026()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_027()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_028()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_029()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_030()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_031()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_032()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_033_a() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem(new AvgFunctionStateObject(select, "e.salary"));

		jpqlQuery(stateObject_033()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_033_b() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.getSelectClause().parse("AVG(e.salary");

		jpqlQuery(stateObject_033()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_033_c() throws Exception {

		// SELECT AVG(e.salary) FROM Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.getSelectClause().getBuilder().avg("e.salary").commit();

		jpqlQuery(stateObject_033()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_034()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_034()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_035()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_036()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_037()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_037()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_038()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_039()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_040()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_041()).test(jpqlStateObject);
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


		jpqlQuery(stateObject_042()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_043() throws Exception {

		// DELETE FROM Employee e
		// WHERE e.department IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		DeleteStatementStateObject delete = jpqlStateObject.addDeleteStatement();
		delete.setDeclaration("Employee", "e");
		delete.addWhereClause().getBuilder().isNull("e.department").commit();

		jpqlQuery(stateObject_043()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_044()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_045()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_046()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_047()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_048()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_049()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_050()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_051()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_052()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_053()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_054()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_055()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_056()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_057() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_057()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_058() throws Exception {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_058()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_059() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_059()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_060() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");



		jpqlQuery(stateObject_060()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_061() throws Exception {

		// select object(o)
		// FROM Order o
		// Where SQRT(o.totalPrice) > :doubleValue

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Order", "o");
		select.addSelectItem("e");



		jpqlQuery(stateObject_061()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_062() throws Exception {

		// select sum(o.totalPrice)
		// FROM Order o
		// GROUP BY o.totalPrice
		// HAVING ABS(o.totalPrice) = :doubleValue

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_062()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_063() throws Exception {

		// select c.name
		// FROM Customer c
		// Group By c.name
		// HAVING trim(TRAILING from c.name) = ' David R. Vincent'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_063()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_064() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// Having trim(LEADING from c.name) = 'David R. Vincent '

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_064()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_065() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group by c.name
		// HAVING trim(BOTH from c.name) = 'David R. Vincent'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_065()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_066() throws Exception {

		// select c.name
		// FROM  Customer c
		// GROUP BY c.name
		// HAVING LOCATE('Frechette', c.name) > 0

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_066()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_067() throws Exception {

		// select a.city
		// FROM  Customer c JOIN c.home a
		// GROUP BY a.city
		// HAVING LENGTH(a.city) = 10

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_067()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_068() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_068()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_069() throws Exception {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING LOWER(cc.code) = 'gbr'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_069()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_070() throws Exception {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_070()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_071() throws Exception {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_071()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_072() throws Exception {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_072()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_073() throws Exception {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_073()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_074() throws Exception {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_074()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_075() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_075()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_076() throws Exception {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_076()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_077() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_077()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_078() throws Exception {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_078()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_079() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_079()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_080() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_080()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_081() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_081()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_082() throws Exception {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_082()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_083()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_084()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_085()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_086()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_087()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_088()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_089()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_090() throws Exception {

		// SELECT c
		// from Customer c JOIN FETCH c.spouse

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c");
		select.addSelectItem("c");



		jpqlQuery(stateObject_090()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_091()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_092()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_093()).test(jpqlStateObject);
	}

	//@Test
	public void test_Query_094() throws Exception {

		// SELECT c
		// from Customer c INNER JOIN c.spouse s

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_094()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_095()).test(jpqlStateObject);
	}

	public void test_Query_096() throws Exception {

		// select cc.code
		// FROM Customer c JOIN c.country cc
		// GROUP BY cc.code

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_096()).test(jpqlStateObject);
	}

	public void test_Query_097() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where LOWER(a.alias)='sjc'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_097()).test(jpqlStateObject);
	}

	public void test_Query_098() throws Exception {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where UPPER(a.alias)='SJC'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_098()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_099()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_100()).test(jpqlStateObject);
	}

	public void test_Query_101() throws Exception {

		// SELECT DISTINCT c
		// from Customer c LEFT OUTER JOIN c.creditCards cc
		// where c.name LIKE '%Caruso'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_101()).test(jpqlStateObject);
	}

	public void test_Query_102() throws Exception {

		// SELECT Sum(p.quantity)
		// FROM Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_102()).test(jpqlStateObject);
	}

	public void test_Query_103() throws Exception {

		// Select Count(c.home.city)
		// from Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_103()).test(jpqlStateObject);
	}

	public void test_Query_104() throws Exception {

		// SELECT Sum(p.price)
		// FROM Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_104()).test(jpqlStateObject);
	}

	public void test_Query_105() throws Exception {

		// SELECT AVG(o.totalPrice)
		// FROM Order o

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_105()).test(jpqlStateObject);
	}

	public void test_Query_106() throws Exception {

		// SELECT DISTINCT MAX(l.quantity)
		// FROM LineItem l

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_106()).test(jpqlStateObject);
	}

	public void test_Query_107() throws Exception {

		// SELECT DISTINCT MIN(o.id)
		// FROM Order o
		// where o.customer.name = 'Robert E. Bissett'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_107()).test(jpqlStateObject);
	}

	public void test_Query_108() throws Exception {

		// SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
		// FROM Customer c
		// where c.work.city = :workcity

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_108()).test(jpqlStateObject);
	}

	public void test_Query_109() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) > 100

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_109()).test(jpqlStateObject);
	}

	public void test_Query_110() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) >= 2

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_110()).test(jpqlStateObject);
	}

	public void test_Query_111() throws Exception {

		// select Distinct c
		// FROM Customer c LEFT OUTER JOIN c.work workAddress
		// where workAddress.zip IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_111()).test(jpqlStateObject);
	}

	public void test_Query_112() throws Exception {

		// SELECT DISTINCT c
		// FROM Customer c, IN(c.orders) o

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_112()).test(jpqlStateObject);
	}

	public void test_Query_113() throws Exception {

		// Select Distinct Object(c)
		// from Customer c
		// where c.name is null

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_113()).test(jpqlStateObject);
	}

	public void test_Query_114() throws Exception {

		// Select c.name
		// from Customer c
		// where c.home.street = '212 Edgewood Drive'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_114()).test(jpqlStateObject);
	}

	public void test_Query_115() throws Exception {

		// Select s.customer
		// from Spouse s
		// where s.id = '6'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_115()).test(jpqlStateObject);
	}

	public void test_Query_116() throws Exception {

		// Select c.work.zip
		// from Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_116()).test(jpqlStateObject);
	}

	public void test_Query_117() throws Exception {

		// SELECT Distinct Object(c)
		// From Customer c, IN(c.home.phones) p
		// where p.area LIKE :area

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_117()).test(jpqlStateObject);
	}

	public void test_Query_118() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c, in(c.aliases) a
		// where NOT a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_118()).test(jpqlStateObject);
	}

	public void test_Query_119() throws Exception {

		// select distinct object(c)
		// fRoM Customer c, IN(c.aliases) a
		// where c.name = :cName OR a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_119()).test(jpqlStateObject);
	}

	public void test_Query_120() throws Exception {

		// select Distinct Object(c)
		// from Customer c, in(c.aliases) a
		// where c.name = :cName AND a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_120()).test(jpqlStateObject);
	}

	public void test_Query_121() throws Exception {

		// sElEcT Distinct oBJeCt(c)
		// FROM Customer c, IN(c.aliases) a
		// WHERE a.customerNoop IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_121()).test(jpqlStateObject);
	}

	public void test_Query_122() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE '%\\_%' escape '\\'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_122()).test(jpqlStateObject);
	}

	public void test_Query_123() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.customerNoop IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_123()).test(jpqlStateObject);
	}

	public void test_Query_124() throws Exception {

		// Select Distinct o.creditCard.balance
		// from Order o
		// ORDER BY o.creditCard.balance ASC

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_124()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_125()).test(jpqlStateObject);
	}

	public void test_Query_126() throws Exception {

		// SELECT a.alias
		// FROM Alias AS a
		// WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_126()).test(jpqlStateObject);
	}

	public void test_Query_127() throws Exception {

		// Select Object(c)
		// from Customer c
		// where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_127()).test(jpqlStateObject);
	}

	public void test_Query_128() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where p.name = ?1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_128()).test(jpqlStateObject);
	}

	public void test_Query_129() throws Exception {

		// Select Distinct Object(p)
		// from Product p
		// where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_129()).test(jpqlStateObject);
	}

	public void test_Query_130() throws Exception {

		// Select Distinct Object(o)
		// from Order o
		// where o.customer.name IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_130()).test(jpqlStateObject);
	}

	public void test_Query_131() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where (p.quantity < 10) OR (p.quantity > 20)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_131()).test(jpqlStateObject);
	}

	public void test_Query_132() throws Exception {

		// Select DISTINCT Object(p)
		// From Product p
		// where p.quantity NOT BETWEEN 10 AND 20

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_132()).test(jpqlStateObject);
	}

	public void test_Query_133() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where (p.quantity >= 10) AND (p.quantity <= 20)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_133()).test(jpqlStateObject);
	}

	public void test_Query_134() throws Exception {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where p.quantity BETWEEN 10 AND 20

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_134()).test(jpqlStateObject);
	}

	public void test_Query_135() throws Exception {

		// Select Distinct OBJECT(c)
		// from Customer c, IN(c.creditCards) b
		// where SQRT(b.balance) = :dbl

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_135()).test(jpqlStateObject);
	}

	public void test_Query_136() throws Exception {

		// Select Distinct OBJECT(c)
		// From Product p
		// where MOD(550, 100) = p.quantity

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_136()).test(jpqlStateObject);
	}

	public void test_Query_137() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_137()).test(jpqlStateObject);
	}

	public void test_Query_138() throws Exception {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// where c.home.state IN('NH', 'RI')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_138()).test(jpqlStateObject);
	}

	public void test_Query_140() throws Exception {

		// SELECT c
		// from Customer c
		// where c.home.city IN(:city)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_140()).test(jpqlStateObject);
	}

	public void test_Query_141() throws Exception {

		// Select Distinct Object(o)
		// from Order o, in(o.lineItems) l
		// where l.quantity NOT IN (1, 5)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_141()).test(jpqlStateObject);
	}

	public void test_Query_142() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE o.sampleLineItem MEMBER OF o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_142()).test(jpqlStateObject);
	}

	public void test_Query_143() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE :param NOT MEMBER o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_143()).test(jpqlStateObject);
	}

	public void test_Query_144() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o, LineItem l
		// WHERE l MEMBER o.lineItems

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_144()).test(jpqlStateObject);
	}

	public void test_Query_145() throws Exception {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE 'sh\\_ll' escape '\\'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_145()).test(jpqlStateObject);
	}

	public void test_Query_146() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop NOT MEMBER OF a.customersNoop

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_146()).test(jpqlStateObject);
	}

	public void test_Query_147() throws Exception {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop MEMBER OF a.customersNoop

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_147()).test(jpqlStateObject);
	}

	public void test_Query_148() throws Exception {

		// Select Distinct Object(a)
		// from Alias a
		// where LOCATE('ev', a.alias) = 3

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_148()).test(jpqlStateObject);
	}

	public void test_Query_149() throws Exception {

		// Select DISTINCT Object(o)
		// From Order o
		// WHERE o.totalPrice > ABS(:dbl)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_149()).test(jpqlStateObject);
	}

	public void test_Query_150() throws Exception {

		// Select Distinct OBjeCt(a)
		// From Alias a
		// WHERE LENGTH(a.alias) > 4

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_150()).test(jpqlStateObject);
	}

	public void test_Query_151() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_151()).test(jpqlStateObject);
	}

	public void test_Query_152() throws Exception {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = CONCAT('ste', 'vie')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_152()).test(jpqlStateObject);
	}

	public void test_Query_153() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.work.zip IS NOT NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_153()).test(jpqlStateObject);
	}

	public void test_Query_154() throws Exception {

		// sELEct dIsTiNcT oBjEcT(c)
		// FROM Customer c
		// WHERE c.work.zip IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_154()).test(jpqlStateObject);
	}

	public void test_Query_155() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS NOT EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_155()).test(jpqlStateObject);
	}

	public void test_Query_156() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_156()).test(jpqlStateObject);
	}

	public void test_Query_157() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip not like '%44_'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_157()).test(jpqlStateObject);
	}

	public void test_Query_158() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip LIKE '%77'"

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_158()).test(jpqlStateObject);
	}

	public void test_Query_159() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer c Left Outer Join c.home h
		// WHERE h.city Not iN ('Swansea', 'Brookline')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_159()).test(jpqlStateObject);
	}

	public void test_Query_160() throws Exception {

		// select distinct c
		// FROM Customer c
		// WHERE c.home.city IN ('Lexington')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_160()).test(jpqlStateObject);
	}

	public void test_Query_161() throws Exception {

		// sElEcT c
		// FROM Customer c
		// Where c.name = :cName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_161()).test(jpqlStateObject);
	}

	public void test_Query_162() throws Exception {

		// select distinct Object(o)
		// From Order o
		// WHERE o.creditCard.approved = FALSE

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_162()).test(jpqlStateObject);
	}

	public void test_Query_163() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice NOT bETwEeN 1000 AND 1200

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_163()).test(jpqlStateObject);
	}

	public void test_Query_164() throws Exception {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice BETWEEN 1000 AND 1200

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_164()).test(jpqlStateObject);
	}

	public void test_Query_165() throws Exception {

		// SELECT DISTINCT Object(o)
		// FROM Order o, in(o.lineItems) l
		// WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_165()).test(jpqlStateObject);
	}

	public void test_Query_166() throws Exception {

		// select distinct Object(o)
		// FROM Order AS o, in(o.lineItems) l
		// WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_166()).test(jpqlStateObject);
	}

	public void test_Query_167() throws Exception {

		// SeLeCt DiStInCt oBjEcT(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_167()).test(jpqlStateObject);
	}

	public void test_Query_168() throws Exception {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE NOT o.totalPrice < 4500

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_168()).test(jpqlStateObject);
	}

	public void test_Query_169() throws Exception {

		// Select DISTINCT Object(P)
		// From Product p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_169()).test(jpqlStateObject);
	}

	public void test_Query_170() throws Exception {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_170()).test(jpqlStateObject);
	}

	public void test_Query_171() throws Exception {

		// SELECT c
		// from Customer c
		// WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_172()).test(jpqlStateObject);
	}

	public void test_Query_172() throws Exception {

		// SELECT c
		// from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_172()).test(jpqlStateObject);
	}

	public void test_Query_173() throws Exception {

		// Select Distinct Object(c)
		// FrOm Customer c, In(c.aliases) a
		// WHERE a.alias = :aName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_173()).test(jpqlStateObject);
	}

	public void test_Query_174() throws Exception {

		// Select Distinct Object(c)
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_174()).test(jpqlStateObject);
	}

	public void test_Query_175() throws Exception {

		// Select Distinct o
		// from Order AS o
		// WHERE o.customer.name = :name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_175()).test(jpqlStateObject);
	}

	public void test_Query_176() throws Exception {

		// UPDATE Customer c SET c.name = 'CHANGED'
		// WHERE c.orders IS NOT EMPTY

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_176()).test(jpqlStateObject);
	}

	public void test_Query_177() throws Exception {

		// UPDATE DateTime SET date = CURRENT_DATE

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setEntityName("DateTime");



		jpqlQuery(stateObject_177()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_178()).test(jpqlStateObject);
	}

	public void test_Query_179() throws Exception {

		// SELECT OBJECT ( c ) FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_179()).test(jpqlStateObject);
	}

	public void test_Query_180() throws Exception {

		// SELECT c.firstName, c.lastName
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_180()).test(jpqlStateObject);
	}

	public void test_Query_181() throws Exception {

		// SELECT c.address.city
		// FROM Customer AS c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_181()).test(jpqlStateObject);
	}

	public void test_Query_182() throws Exception {

		// SELECT new com.titan.domain.Name(c.firstName, c.lastName)
		// FROM Customer c

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_182()).test(jpqlStateObject);
	}

	public void test_Query_183() throws Exception {

		// SELECT cbn.ship
		// FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_183()).test(jpqlStateObject);
	}

	public void test_Query_184() throws Exception {

		// Select c.firstName, c.lastName, p.number
		// From Customer c Left Join c.phoneNumbers p

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_184()).test(jpqlStateObject);
	}

	public void test_Query_185() throws Exception {

		// SELECT r
		// FROM Reservation AS r
		// WHERE (r.amountPaid * .01) > 300.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_185()).test(jpqlStateObject);
	}

	public void test_Query_186() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_186()).test(jpqlStateObject);
	}

	public void test_Query_187() throws Exception {

		// SELECT r
		// FROM Reservation r, IN ( r.customers ) AS cust
		// WHERE cust = :specificCustomer

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_187()).test(jpqlStateObject);
	}

	public void test_Query_188() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_188()).test(jpqlStateObject);
	}

	public void test_Query_189() throws Exception {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_189()).test(jpqlStateObject);
	}

	public void test_Query_190() throws Exception {

		// SELECT c
		// FROM Customer AS c
		//  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_190()).test(jpqlStateObject);
	}

	public void test_Query_191() throws Exception {

		// SELECT cab
		// FROM Cabin AS cab
		// WHERE cab.deckLevel IN (1,3,5,7)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_191()).test(jpqlStateObject);
	}

	public void test_Query_192() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_192()).test(jpqlStateObject);
	}

	public void test_Query_193() throws Exception {

		// SELECT c
		// FROM Customer c
		// WHERE c.address IS NULL

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_193()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_194()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_195()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_196()).test(jpqlStateObject);
	}

	public void test_Query_197() throws Exception {

		// SELECT c
		// FROM Customer AS C
		// ORDER BY c.lastName

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_197()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_198()).test(jpqlStateObject);
	}

	public void test_Query_199() throws Exception {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_199()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_200()).test(jpqlStateObject);
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



		jpqlQuery(stateObject_201()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_202()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_203()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_204()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_215()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_216()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_219()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_220()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_220()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_221()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_221()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_228()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_228()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_228()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_228()).test(jpqlStateObject);
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

		jpqlQuery(stateObject_229()).test(jpqlStateObject);
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
		subquery.setSelectItem("e");
		subquery.addDerivedCollectionDeclaration("e.phoneNumbers");
		subquery.addWhereClause("a.zipCode = 27519");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.exists(subquery).commit();

		testQuery(query_230(), stateObject_230());
	}
}