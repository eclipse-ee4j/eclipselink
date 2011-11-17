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

import org.eclipse.persistence.jpa.jpql.model.ICaseExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.IConditionalExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.INewValueStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.ISelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ModExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TypeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhenClauseStateObject;
import org.junit.Test;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ManualCreationStateObjectTest2_0 extends AbstractStateObjectTest2_0 {

	protected JPQLQueryStateObject buildJPQLQueryStateObject() throws Exception {
		return new JPQLQueryStateObject(getQueryBuilder(), getPersistenceUnit());
	}

	public void test_Query_139() throws Exception {

		// SELECT p
		// FROM Employee e JOIN e.projects p
		// WHERE e.id = :id AND INDEX(p) = 1

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");



		jpqlQuery(stateObject_139()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_205() throws Exception {

		// UPDATE Employee e
		// SET e.salary =
		//    CASE WHEN e.rating = 1 THEN e.salary * 1.1
		//         WHEN e.rating = 2 THEN e.salary * 1.05
		//         ELSE e.salary * 1.01
		//    END

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		CaseExpressionStateObject case_ = new CaseExpressionStateObject(jpqlStateObject);
		case_.parseElse("e.salary * 1.01");

		WhenClauseStateObject when1 = case_.addWhenClause();
		when1.getBuilder().path("e.rating").equal(1).commit();
		when1.parseThen("e.salary * 1.1");

		WhenClauseStateObject when2 = case_.addWhenClause();
		when2.getBuilder().path("e.rating").equal(2).commit();
		when2.parseThen("e.salary * 1.05");

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem("e.salary", case_);

		jpqlQuery(stateObject_205()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_206() throws Exception {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e
		// WHERE e.dept.name = 'Engineering'

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		CaseExpressionStateObject case_ = new CaseExpressionStateObject(jpqlStateObject);
		case_.setElse(new StringLiteralStateObject(case_, "'NonExempt'"));
		case_.setCaseOperand(new TypeExpressionStateObject(case_, "e"));

		WhenClauseStateObject when1 = case_.addWhenClause();
		when1.getBuilder().entityType("Exempt").commit();
		when1.setThen(new StringLiteralStateObject(when1, "'Exempt'"));

		WhenClauseStateObject when2 = case_.addWhenClause();
		when2.getBuilder().entityType("Contractor").commit();
		when2.setThen(new StringLiteralStateObject(when2, "'Contractor'"));

		WhenClauseStateObject when3 = case_.addWhenClause();
		when3.getBuilder().entityType("Intern").commit();
		when3.setThen(new StringLiteralStateObject(when3, "'Intern'"));

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e.name");
		select.addSelectItem(case_);
		select.addWhereClause().getBuilder().path("e.dept.name").equal("'Engineering'").commit();

		jpqlQuery(stateObject_206()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_207_a() throws Exception {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		CaseExpressionStateObject case_ = new CaseExpressionStateObject(jpqlStateObject);
		case_.setElse(new StringLiteralStateObject(case_, "''"));

		WhenClauseStateObject when1 = case_.addWhenClause();
		when1.getBuilder().path("f.annualMiles").greaterThan(50000).commit();
		when1.setThen(new StringLiteralStateObject(when1, "'Platinum '"));

		WhenClauseStateObject when2 = case_.addWhenClause();
		when2.getBuilder().path("f.annualMiles").greaterThan(25000).commit();
		when2.setThen(new StringLiteralStateObject(when2, "'Gold '"));

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.frequentFlierPlan", "f");
		select.addSelectItem("e.name");
		select.addSelectItem("f.name");
		select.addSelectItem(new ConcatExpressionStateObject(
			select,
			case_,
			new StringLiteralStateObject(select, "'Frequent Flyer'")
		));

		jpqlQuery(stateObject_207()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_207_b() throws Exception {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		CaseExpressionStateObject case_ = new CaseExpressionStateObject(jpqlStateObject);
		case_.setElse(new StringLiteralStateObject(case_, "''"));

		WhenClauseStateObject when1 = case_.addWhenClause();
		when1.getBuilder().path("f.annualMiles").greaterThan(50000).commit();
		when1.setThen(new StringLiteralStateObject(when1, "'Platinum '"));

		WhenClauseStateObject when2 = case_.addWhenClause();
		when2.getBuilder().path("f.annualMiles").greaterThan(25000).commit();
		when2.setThen(new StringLiteralStateObject(when2, "'Gold '"));

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e").
			addJoin("e.frequentFlierPlan", "f");

		ISelectExpressionStateObjectBuilder builder = select.getSelectBuilder();
		ICaseExpressionStateObjectBuilder caseBuilder = builder.getCaseBuilder();

		// Select expression
		builder
			.path("e.name").append()
			.path("f.name").append()
			.concat(
				builder.case_(
					caseBuilder.when(
						caseBuilder.path("f.annualMiles").greaterThan(50000),
						caseBuilder.string("'Platinum '")
					).
					when(
						caseBuilder.path("f.annualMiles").greaterThan(25000),
						caseBuilder.string("'Gold '")
					).
					string("''")
				),
				builder.string("'Frequent Flyer'")
			)
		.commit();

		jpqlQuery(stateObject_207()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_208() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (Exempt, Contractor)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.type("e").in(
			builder.entityType("Exempt"),
			builder.entityType("Contractor")
		).commit();

		jpqlQuery(stateObject_208()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_209() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (:empType1, :empType2)

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.type("e").in(builder.parameter(":empType1"), builder.parameter(":empType2")).commit();

		jpqlQuery(stateObject_209()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_210() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN :empTypes

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("e");
		select.addRangeDeclaration("Employee", "e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.type("e").in(builder.parameter(":empTypes")).commit();

		jpqlQuery(stateObject_210()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_211() throws Exception {

		// SELECT TYPE(e)
		// FROM Employee e
		// WHERE TYPE(e) <> Exempt

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem(new TypeExpressionStateObject(select, "e"));
		select.addRangeDeclaration("Employee", "e");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.type("e").different(builder.variable("Exempt")).commit();

		jpqlQuery(stateObject_211()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_212() throws Exception {

		// SELECT t
		// FROM CreditCard c JOIN c.transactionHistory t
		// WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("t");

		IdentificationVariableDeclarationStateObject range = select.addRangeDeclaration("CreditCard", "c");
		range.addJoin("c.transactionHistory", "t");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.
				path("c.holder.name").equal("'John Doe'")
			.and(
				builder.index("t").between(builder.numeric(0), builder.numeric(9))
			)
		.commit();

		jpqlQuery(stateObject_212()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_213() throws Exception {

		// SELECT w.name
		// FROM Course c JOIN c.studentWaitlist w
		// WHERE c.name = 'Calculus'
		//       AND
		//       INDEX(w) = 0

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItem("w.name");

		IdentificationVariableDeclarationStateObject range = select.addRangeDeclaration("Course", "c");
		range.addJoin("c.studentWaitlist", "w");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.
				path("c.name").equal("'Calculus'")
			.and(
				builder.index("w").equal(builder.numeric(0))
			)
		.commit();

		jpqlQuery(stateObject_213()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_214_a() throws Exception {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		CaseExpressionStateObject case_ = new CaseExpressionStateObject(jpqlStateObject);
		case_.parseCaseOperand("e.rating");
		case_.addWhenClause("1", "e.salary * 1.1");
		case_.addWhenClause("2", "e.salary * 1.05");
		case_.parseElse("e.salary * 1.01");

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");
		update.addItem("e.salary", case_);

		jpqlQuery(stateObject_214()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_214_b() throws Exception {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		UpdateStatementStateObject update = jpqlStateObject.addUpdateStatement();
		update.setDeclaration("Employee", "e");

		INewValueStateObjectBuilder newValue = update.addItem("e.salary").getBuilder();
		ICaseExpressionStateObjectBuilder builder = newValue.getCaseBuilder();

		newValue.case_(
			builder.path("e.rating").
			when(
				builder.numeric(1),
				builder.path("e.salary").multiply(builder.numeric(1.1))
			).
			when(
				builder.numeric(2),
				builder.path("e.salary").multiply(builder.numeric(1.05))
			).
			path("e.salary").multiply(builder.numeric(1.01))
		)
		.commit();

		jpqlQuery(stateObject_214()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_217() throws Exception {

		// SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA' AND a.county = 'Santa Clara'
		// ORDER BY o.quantity, taxedCost, a.zipcode

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").
			addJoin("c.orders",  "o").getParent().
			addJoin("c.address", "a");
		select.addSelectItem("o.quantity");
		select.addSelectItemAs("o.cost*1.08", "taxedCost");
		select.addSelectItem("a.zipcode");
		select.addOrderByClause().
			addItem("o.quantity").getParent().
			addItem("taxedCost").getParent().
			addItem("a.zipcode");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.
				path("a.state").equal(builder.string("'CA'"))
			.and(
				builder.path("a.counry").equal(builder.string("'Santa Clara'"))
			)
		.commit();

		jpqlQuery(stateObject_217()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_218_a() throws Exception {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItemAs(new AvgFunctionStateObject(select, "o.quantity"), "q");
		select.addSelectItem("a.zipcode");
		select.addRangeDeclaration("Customer", "c").
			addJoin("c.orders",  "o").getParent().
			addJoin("c.address", "a");
		select.addGroupByClause("a.zipcode");
		select.addOrderByClause().addItemDesc("q");

		IConditionalExpressionStateObjectBuilder builder = select.addWhereClause().getBuilder();
		builder.path("a.state").equal(builder.string("'CA'")).commit();

		jpqlQuery(stateObject_218()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_218_b() throws Exception {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Customer", "c").
			addJoin("c.orders",  "o").getParent().
			addJoin("c.address", "a");

		ISelectExpressionStateObjectBuilder selectBuilder = select.getSelectBuilder();
		selectBuilder.
			avg("o.quantity").resultVariableAs("q").append().
			path("a.zipcode").
		commit();

		select.addWhereClause().getBuilder().path("a.state").equal("'CA'").commit();
		select.addGroupByClause("a.zipcode");
		select.addOrderByClause().addItemDesc("q");

		jpqlQuery(stateObject_218()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_222_a() throws Exception {

		// SELECT e.salary / 1000D n
		// From Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");
		select.addSelectItem("e.salary / 1000D", "n");

		jpqlQuery(stateObject_222()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_222_b() throws Exception {

		// SELECT e.salary / 1000D n
		// From Employee e

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Employee", "e");

		ISelectExpressionStateObjectBuilder builder = select.getSelectBuilder();
		builder.path("e.salary").divide(builder.numeric("1000D")).resultVariable("n").commit();

		jpqlQuery(stateObject_222()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_223_a() throws Exception {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addSelectItemAs(new ModExpressionStateObject(select, "a.id", "2"), "m");
		select.addRangeDeclaration("Address", "a").
			addJoinFetch("a.customerList");
		select.addOrderByClause("m, a.zipcode");

		jpqlQuery(stateObject_223()).test(jpqlStateObject);
	}

	@Test
	public void test_Query_223_b() throws Exception {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		JPQLQueryStateObject jpqlStateObject = buildJPQLQueryStateObject();

		SelectStatementStateObject select = jpqlStateObject.addSelectStatement();
		select.addRangeDeclaration("Address", "a").
			addJoinFetch("a.customerList");
		select.addOrderByClause("m, a.zipcode");

		ISelectExpressionStateObjectBuilder builder = select.getSelectBuilder();
		builder.mod(builder.path("a.id"), builder.numeric(2)).resultVariableAs("m").commit();

		jpqlQuery(stateObject_223()).test(jpqlStateObject);
	}
}