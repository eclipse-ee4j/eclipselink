/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

public class ReportQueryTestSuite extends TestSuite {
    public ReportQueryTestSuite() {
        setDescription("This suite tests all of the functionality of the query framework.");
    }

    public void addTests() {
        addTest(new Scenario1_1());
        addTest(new Scenario1_2());
        addTest(new Scenario1_3());
        addTest(new Scenario1_4());
        addTest(new Scenario1_5a());
        addTest(new Scenario1_5b());
        addTest(new Scenario1_6());
        addTest(new Scenario1_7a());
        addTest(new Scenario1_7b());
        addTest(new Scenario1_7c());
        addTest(new Scenario1_7d());
        // INVALID ITEM SCENARIOS
        addTest(new Scenario1_8a());
	addTest(new Scenario1_8b());
        addTest(new Scenario1_8d());
        addTest(new Scenario1_8e());
        // AGGREGATE FUNCTIONS
        addTest(new Scenario1_9a());
        addTest(new Scenario1_9b());
        addTest(new Scenario1_9c());
        addTest(new Scenario1_9d());
        addTest(new Scenario1_9e());
        addTest(new Scenario1_9f());
        addTest(new Scenario1_9g());
        addTest(new Scenario1_9h());
        addTest(new Scenario1_9i());
        addTest(new Scenario1_9j());
        //
        addTest(new Scenario2_1a());
        addTest(new Scenario2_1b());
        addTest(new Scenario2_2a());
        addTest(new Scenario2_2b());
        addTest(new Scenario2_2c());
        // addTest(new Scenario2_3());  // Multiple join: employee.manager & employee.manager.address
        addTest(new Scenario5_1a());
        addTest(new Scenario5_1b());
        addTest(new Scenario5_1c());
	// CURSORED STREAM SCENARIOS
	addTest(new Scenario5_2a());
	addTest(new Scenario5_2b()); // Require a CursorStreamPolicy w/ size query
	addTest(new Scenario5_2c());
	addTest(new Scenario5_2d());
	//
        addTest(new Scenario5_3a());
        addTest(new Scenario5_3b());
        addTest(new Scenario6_1());
        addTest(new Scenario6_2());

        //USE DISTINCT
        addTest(new UseDistinctScenario());
        // Bug 2612185
        addTest(new ParallelBuilderReportItemTest());
        // CR 4240 row size check based on total result size, including place holders, not expected fields
        addTest(new PlaceHolderReportQueryTestCase());
        // bug 3608082
        addTest(new GetLeafMappingForReportQueryTest());
        // bug 3608082
        addTest(new ReportItemQueryKeyTest());
        //CR 4290
        addTest(new AttributeConversionTest());
        // Bug 3268040
        addCountDirectCollectionTest();
        addCountDistinctManyToManyTest();
        addCountDistinctManyToManyWithWhereClauseTest();
        addCountDistinctOneToOneTest();
        addCountDistinctOneToOneWithWhereClauseTest();
        addCountManyToManyTest();
        addCountOneToOneTest();
        //bug 3597197 
        addTest(new ReportQueryWithDuplicateQueryKeysTest());

        addTest(new OrderByRandomTest());
        
        addTest(new ClassForInheritanceTestCase());

        // bug 3764121
        addTest(new ReportQueryFunctionTypeTestCase());

        // bug 290311: ReadAllQuery executed instead of ReportQuery 
        addTest(new ReportQueryFunctionTypeTestCase(true));

        //bug 4942640
        addTest(new ReportQueryAndExistsSubQuery());
        addTest(new ReportQueryAndExistsSubQueryWithWhereClause());
        
        // bug 372526
        addTest(new ReportQueryRetrievePrimaryKeysCursorTest());
    }

    private void addCountDirectCollectionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery reportQuery = new ReportQuery(Employee.class, builder);
        reportQuery.addCount("COUNT", builder.anyOf("responsibilitiesList"));

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 8);
        test.setName("CountDirectCollectionTest");
        test.setDescription("Tests count on a direct collection query key (i.e. not a direct field)");
        addTest(test);

    }

    private void addCountDistinctManyToManyTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery reportQuery = new ReportQuery(Employee.class, builder);
        reportQuery.addCount("COUNT", builder.anyOf("projects").distinct());

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 10);
        test.setName("CountDistinctManyToManyTest");
        test.setDescription("Tests count distinct on a many to many query key (i.e. not a direct field)");
        addTest(test);
    }

    private void addCountDistinctManyToManyWithWhereClauseTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("manager").get("salary").greaterThan(builder.get("salary"));

        ReportQuery reportQuery = new ReportQuery(Employee.class, builder);
        reportQuery.addCount("COUNT", builder.anyOf("projects").distinct());
        reportQuery.setSelectionCriteria(exp);

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 4);
        test.setName("CountDistinctManyToManyWithWhereClauseTest");
        test.setDescription("Tests count distinct on a many to many query key (i.e. not a direct field)");
        addTest(test);
    }

    private void addCountDistinctOneToOneTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery reportQuery = new ReportQuery(PhoneNumber.class, builder);
        reportQuery.addCount("COUNT", builder.get("owner").distinct());

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 12);
        test.setName("CountDistinctOneToOneTest");
        test.setDescription("Tests count distinct on a one to one query key (i.e. not a direct field)");
        addTest(test);
    }

    private void addCountDistinctOneToOneWithWhereClauseTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("areaCode").equal(613);

        ReportQuery reportQuery = new ReportQuery(PhoneNumber.class, builder);
        reportQuery.addCount("COUNT", builder.get("owner").distinct());
        reportQuery.setSelectionCriteria(exp);

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 10);
        test.setName("CountDistinctOneToOneWithWhereClauseTest");
        test.setDescription("Tests count distinct on a one to one query key (i.e. not a direct field)");
        addTest(test);
    }

    private void addCountManyToManyTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery reportQuery = new ReportQuery(Employee.class, builder);
        reportQuery.addCount("COUNT", builder.anyOf("projects"));

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 14);
        test.setName("CountManyToManyTest");
        test.setDescription("Tests count on a many to many query key (i.e. not a direct field)");
        addTest(test);
    }

    private void addCountOneToOneTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery reportQuery = new ReportQuery(PhoneNumber.class, builder);
        reportQuery.addCount("COUNT", builder.get("owner"));

        CountReportQueryTest test = new CountReportQueryTest(reportQuery, 26);
        test.setName("CountOneToOneTest");
        test.setDescription("Tests count on a one to one query key (i.e. not a direct field)");
        addTest(test);
    }
}
