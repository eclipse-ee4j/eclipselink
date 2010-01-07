/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.inheritance.Engineer;
import org.eclipse.persistence.testing.models.ownership.ObjectA;

/**
 * Test query optimization features, joining, batch reading, partial object reading.
 */
public class QueryOptimizationTestSuite extends TestSuite {
    public QueryOptimizationTestSuite() {
        setDescription("This suite tests query optimization, aggregation and batch reading.");
    }

    public void addBatchTests() {
        // Batch reading
        ReadAllBatchReadingTest testbb1 = new ReadAllBatchReadingTest(12);
        testbb1.setName("ReadAllBatchReadingTestAddressManager");
        ReadAllQuery querybb1 = new ReadAllQuery();
        querybb1.setReferenceClass(Employee.class);
        querybb1.addBatchReadAttribute("address");
        querybb1.addBatchReadAttribute("manager");
        querybb1.addBatchReadAttribute("phoneNumbers");
        querybb1.addBatchReadAttribute("projects");
        querybb1.addBatchReadAttribute("responsibilitiesList");
        testbb1.setQuery(querybb1);
        addTest(testbb1);

        ReadAllBatchReadingTest testbb2 = new ReadAllBatchReadingTest(2);
        testbb2.setName("ReadAllBatchReadingTestWhereAddressManager");
        ReadAllQuery querybb2 = new ReadAllQuery();
        querybb2.setReferenceClass(Employee.class);
        querybb2.addBatchReadAttribute("address");
        querybb2.addBatchReadAttribute("manager");
        querybb2.addBatchReadAttribute("phoneNumbers");
        querybb2.addBatchReadAttribute("projects");
        querybb2.addBatchReadAttribute("responsibilitiesList");
        querybb2.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("lastName").equal("Way"));
        testbb2.setQuery(querybb2);
        addTest(testbb2);
        //add the BatchReadingUnitOfWorkTest and BatchReadingUnitOfWorkInTransactionTest
        BatchReadingUnitOfWorkTest testbb3 = new BatchReadingUnitOfWorkTest();
        addTest(testbb3);

        BatchReadingUnitOfWorkInTransactionTest testbb4 = new BatchReadingUnitOfWorkInTransactionTest();
        addTest(testbb4);

        //adding the OneToMany tests
        OneToManyBatchReadingTest testbb5 = new OneToManyBatchReadingTest();
        addTest(testbb5);
        
        addTest(new BatchReadingTest());

        OneToManyBatchReadingCustomSelectionQueryTest testbb6 = new OneToManyBatchReadingCustomSelectionQueryTest();
        addTest(testbb6);

        ReadAllBatchReadingTest test3 = new ReadAllBatchReadingTest(2);
        test3.setName("ReadAllBatchReadingTestWhereAddressManager-cursor");
        ReadAllQuery query3 = new ReadAllQuery();
        query3.setReferenceClass(Employee.class);
        query3.useCursoredStream();
        query3.addBatchReadAttribute("address");
        query3.addBatchReadAttribute("manager");
        query3.addBatchReadAttribute("phoneNumbers");
        query3.addBatchReadAttribute("projects");
        querybb1.addBatchReadAttribute("responsibilitiesList");
        query3.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("lastName").equal("Way"));
        test3.setQuery(query3);
        addTest(test3);

        NestedOneToManyBatchReadAllTest test3_5 = new NestedOneToManyBatchReadAllTest(org.eclipse.persistence.testing.models.collections.Restaurant.class, 15);
        test3_5.setName("NestedOneToManyBatchReadAllTest");
        ReadAllQuery query3_5 = new ReadAllQuery();
        query3_5.setReferenceClass(org.eclipse.persistence.testing.models.collections.Restaurant.class);
        query3_5.addBatchReadAttribute("menus");
        test3_5.setQuery(query3_5);
        addTest(test3_5);

        //Batch testing on 1-1 mapping
        ReadAllTest test4 = new ReadAllTest(org.eclipse.persistence.testing.models.insurance.Policy.class, 3);
        test4.setName("ReadAllBatchReadingTestPolicyHolder");
        ReadAllQuery query4 = new ReadAllQuery();
        query4.setReferenceClass(org.eclipse.persistence.testing.models.insurance.Policy.class);
        query4.addBatchReadAttribute("policyHolder");
        query4.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("maxCoverage").greaterThan(40000));
        test4.setQuery(query4);
        addTest(test4);

        addTest(new OneToOneBatchReadingTest());

        // Batch testing on 1-M mapping.
        ReadAllTest test5 = new ReadAllTest(org.eclipse.persistence.testing.models.insurance.Policy.class, 4);
        test5.setName("ReadAllBatchReadingTestClaim");
        ReadAllQuery query5 = new ReadAllQuery();
        query5.setReferenceClass(org.eclipse.persistence.testing.models.insurance.Policy.class);
        query5.addBatchReadAttribute("claims");
        query5.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("maxCoverage").greaterThan(30000));
        test5.setQuery(query5);
        addTest(test5);
        addTest(new OneToManyBatchReadingTest());

        addTest(new NestedBatchReadingTest());
        addTest(new AggregateBatchReadingTest());
        addTest(new BatchReadingBatchReadExpressionTest());
        addTest(new BatchReadingWithInvalidQueryKeyTest());
        addTest(new BatchReadValueholderTest());
        addTest(new BatchReadingStackOverflowTest());
    }

    public void addJoinTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee object = (Employee)manager.getObject(Employee.class, "0002");

        // Joining
        ReadObjectTest test = new ReadObjectTest(object);
        test.setName("JoiningReadObjectTestAddress");
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(object);
        query.addJoinedAttribute("address");
        test.setQuery(query);
        addTest(test);

        ReadObjectTest test1a = new ReadObjectTest(object);
        test1a.setName("JoiningReadObjectTestAddressManager");
        ReadObjectQuery query1a = new ReadObjectQuery();
        query1a.setSelectionObject(object);
        query1a.addJoinedAttribute("address");
        query1a.addJoinedAttribute("manager");
        test1a.setQuery(query1a);
        addTest(test1a);

        ReadObjectTest test1m = new ReadObjectTest(object);
        test1m.setName("JoiningReadObjectTestPhones");
        ReadObjectQuery query1m = new ReadObjectQuery();
        query1m.setSelectionObject(object);
        query1m.addJoinedAttribute(query1m.getExpressionBuilder().anyOf("phoneNumbers"));
        test1m.setQuery(query1m);
        addTest(test1m);

        test1m = new ReadObjectTest(object);
        test1m.setName("JoiningReadObjectTestPhonesOuter");
        query1m = new ReadObjectQuery();
        query1m.setSelectionObject(object);
        query1m.addJoinedAttribute(query1m.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        test1m.setQuery(query1m);
        addTest(test1m);

        addTest(new ReadAnyObjectJoinPhoneTest());

        Object person = manager.getObject(Engineer.class, "example2");
        ReadObjectTest test1b = new ReadObjectTest(person);
        test1b.setName("JoiningInheritanceRelationshipTest");
        ReadObjectQuery query1b = new ReadObjectQuery();
        query1b.setSelectionObject(person);
        query1b.addJoinedAttribute("bestFriend");
        query1b.addJoinedAttribute("representitive");
        test1b.setQuery(query1b);
        addTest(test1b);

        ReadObjectTest test1bx = new ReadObjectTest(person);
        test1bx.setName("NestedJoiningInheritanceRelationshipTest");
        ReadObjectQuery query1bx = new ReadObjectQuery();
        query1bx.setSelectionObject(person);
        query1bx.addJoinedAttribute(query1bx.getExpressionBuilder().get("bestFriend"));
        query1bx.addJoinedAttribute(query1bx.getExpressionBuilder().get("bestFriend").getAllowingNull("bestFriend"));
        query1bx.addJoinedAttribute(query1bx.getExpressionBuilder().get("bestFriend").getAllowingNull("representitive"));
        query1bx.addJoinedAttribute(query1bx.getExpressionBuilder().get("representitive"));
        test1bx.setQuery(query1bx);
        addTest(test1bx);

        ReadObjectTest test1c = new ReadObjectTest(object);
        test1c.setName("JoiningReadObjectTestAddressManagerManager");
        ReadObjectQuery query1c = new ReadObjectQuery();
        query1c.setSelectionObject(object);
        query1c.addJoinedAttribute("address");
        query1c.addJoinedAttribute("manager");
        query1c.addJoinedAttribute(query1c.getExpressionBuilder().get("manager").get("manager"));
        test1c.setQuery(query1c);
        addTest(test1c);

        ReadAllTest test2 = new ReadAllTest(Employee.class, 12);
        test2.setName("JoiningReadAllTestAddress");
        ReadAllQuery query2 = new ReadAllQuery();
        query2.setReferenceClass(Employee.class);
        query2.addJoinedAttribute("address");
        query2.addJoinedAttribute(query2.getExpressionBuilder().getAllowingNull("manager"));
        test2.setQuery(query2);
        addTest(test2);

        ReadAllTest testReadAll1m = new ReadAllTest(Employee.class, 12);
        testReadAll1m.setName("JoiningReadAllTestPhones");
        ReadAllQuery queryReadAll1m = new ReadAllQuery();
        queryReadAll1m.setReferenceClass(Employee.class);
        queryReadAll1m.addJoinedAttribute(queryReadAll1m.getExpressionBuilder().anyOf("phoneNumbers"));
        testReadAll1m.setQuery(queryReadAll1m);
        addTest(testReadAll1m);

        ReadAllTest testReadAll21m = new ReadAllTest(Employee.class, 5);
        testReadAll21m.setName("JoiningReadAllTestPhonesManagedEmployeesPhones");
        ReadAllQuery queryReadAll21m = new ReadAllQuery();
        queryReadAll21m.setReferenceClass(Employee.class);
        Expression managedEmployee = queryReadAll21m.getExpressionBuilder().anyOf("managedEmployees");
        queryReadAll21m.addJoinedAttribute(queryReadAll21m.getExpressionBuilder().anyOf("phoneNumbers"));
        queryReadAll21m.addJoinedAttribute(managedEmployee);
        queryReadAll21m.addJoinedAttribute(managedEmployee.anyOf("phoneNumbers"));
        testReadAll21m.setQuery(queryReadAll21m);
        addTest(testReadAll21m);

        ReadAllTest test2a = new ReadAllTest(Employee.class, 2);
        test2a.setName("JoiningReadAllTestWhereLastNameWay");
        ReadAllQuery query2a = new ReadAllQuery();
        query2a.setReferenceClass(Employee.class);
        query2a.addJoinedAttribute("address");
        query2a.setSelectionCriteria(new ExpressionBuilder().get("lastName").equal("Way"));
        test2a.setQuery(query2a);
        addTest(test2a);

        ReadObjectTest test2az = new ReadObjectTest(object);
        test2az.setName("JoiningReadObjectTestCustomSQL");
        ReadObjectQuery query2az = new ReadObjectQuery();
        query2az.setReferenceClass(Employee.class);
        query2az.addJoinedAttribute("address");
        query2az.setSQLString("Select * from EMPLOYEE E, SALARY S, ADDRESS A WHERE E.EMP_ID = S.EMP_ID AND E.ADDR_ID = A.ADDRESS_ID AND E.EMP_ID = " + object.getId());
        test2az.setQuery(query2az);
        addTest(test2az);

        ReadAllTest test2ax = new ReadAllTest(Employee.class, 2);
        test2ax.setName("JoiningReadAllTestCustomSQL");
        ReadAllQuery query2ax = new ReadAllQuery();
        query2ax.setReferenceClass(Employee.class);
        query2ax.addJoinedAttribute("address");
        query2ax.setSQLString("Select * from EMPLOYEE E, SALARY S, ADDRESS A WHERE E.EMP_ID = S.EMP_ID AND E.ADDR_ID = A.ADDRESS_ID AND E.L_NAME = 'Way'");
        test2ax.setQuery(query2ax);
        addTest(test2ax);

        ReadAllTest test2aa = new ReadAllTest(Employee.class, 2);
        test2aa.setName("JoiningReadAllTestWhereLastNameWay-cursor");
        ReadAllQuery query2aa = new ReadAllQuery();
        query2aa.setReferenceClass(Employee.class);
        query2aa.useCursoredStream();
        query2aa.addJoinedAttribute("address");
        query2aa.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("lastName").equal("Way"));
        test2aa.setQuery(query2aa);
        addTest(test2aa);

        ReadAllJoinReadingTest test2b = new ReadAllJoinReadingTest(3, "teamLeader-address");
        ReadAllQuery query2b = new ReadAllQuery();
        query2b.setReferenceClass(LargeProject.class);
        query2b.addJoinedAttribute(query2b.getExpressionBuilder().get("teamLeader"));
        query2b.addJoinedAttribute(query2b.getExpressionBuilder().get("teamLeader").get("address"));
        test2b.setQuery(query2b);
        addTest(test2b);

        ReadAllTest ownerTest = new ReadAllTest(ObjectA.class, 3);
        ownerTest.setName("JoinOwnerA-oneToOne-oneToMany");
        ReadAllQuery ownerQuery = new ReadAllQuery();
        ownerQuery.setReferenceClass(ObjectA.class);
        ownerQuery.addJoinedAttribute(ownerQuery.getExpressionBuilder().get("oneToOne"));
        ownerQuery.addJoinedAttribute(ownerQuery.getExpressionBuilder().get("oneToOne").anyOf("oneToMany"));
        ownerTest.setQuery(ownerQuery);
        addTest(ownerTest);

        ReadAllTest ownerTest3 = new ReadAllTest(ObjectA.class, 3);
        ownerTest3.setName("JoinOwnerA-oneToOne-oneToMany-oneToOne");
        ReadAllQuery ownerQuery3 = new ReadAllQuery();
        ownerQuery3.setReferenceClass(ObjectA.class);
        ownerQuery3.addJoinedAttribute(ownerQuery.getExpressionBuilder().get("oneToOne"));
        Expression join = ownerQuery.getExpressionBuilder().get("oneToOne").anyOf("oneToMany");
        ownerQuery3.addJoinedAttribute(join);
        ownerQuery3.addJoinedAttribute(join.get("oneToOne"));
        ownerTest3.setQuery(ownerQuery3);
        addTest(ownerTest3);

        ReadAllTest ownerTest2 = new ReadAllTest(ObjectA.class, 3);
        ownerTest2.setName("JoinOwnerA-oneToOne(mapping)");
        ReadAllQuery ownerQuery2 = new ReadAllQuery();
        ownerQuery2.setReferenceClass(ObjectA.class);
        ownerTest2.setQuery(ownerQuery2);
        addTest(ownerTest2);

        ReadAllTest test3 = new ReadAllTest(LargeProject.class, 3);
        ReadAllQuery query3 = new ReadAllQuery();
        query3.setReferenceClass(LargeProject.class);
        query3.useCursoredStream();
        query3.addJoinedAttribute(query3.getExpressionBuilder().get("teamLeader"));
        query3.addJoinedAttribute(query3.getExpressionBuilder().get("teamLeader").get("address"));
        test3.setQuery(query3);
        addTest(test3);

        addTest(new ReadObjectMappingJoinReadingTest());
    }

    public void addPartialTests() {
        // Partial objects
        ReadAllPartialReadingTest test3 = new ReadAllPartialReadingTest(12, "fisrtName");
        test3.setName("ReadAllPartialReadingTest-firstName,salary");
        ReadAllQuery query3 = new ReadAllQuery();
        query3.setReferenceClass(Employee.class);
        query3.dontMaintainCache();
        query3.addPartialAttribute("firstName");
        query3.addPartialAttribute("salary");
        test3.setQuery(query3);
        addTest(test3);

        ReadAllPartialReadingTest test4 = new ReadAllPartialReadingTest(1, "address");
        test4.setName("ReadAllPartialReadingTest-address,period");
        ReadAllQuery query4 = new ReadAllQuery();
        query4.setReferenceClass(Employee.class);
        query4.dontMaintainCache();
        query4.addPartialAttribute("address");
        query4.addPartialAttribute("period");
        query4.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("city").equal("Ottawa"));
        test4.setQuery(query4);
        addTest(test4);

        ReadAllPartialReadingTest test5 = new ReadAllPartialReadingTest(12, "city");
        test5.setName("ReadAllPartialReadingTest-city,salary");
        ReadAllQuery query5 = new ReadAllQuery();
        query5.setReferenceClass(Employee.class);
        query5.dontMaintainCache();
        query5.addPartialAttribute(query5.getExpressionBuilder().get("address").get("city"));
        query5.addPartialAttribute("salary");
        test5.setQuery(query5);
        addTest(test5);

        ReadAllPartialReadingTest test6 = new ReadAllPartialReadingTest(12, "city");
        test6.setName("ReadAllPartialReadingTest-city,salary-cursor");
        ReadAllQuery query6 = new ReadAllQuery();
        query6.useCursoredStream();
        query6.setReferenceClass(Employee.class);
        query6.dontMaintainCache();
        query6.addPartialAttribute(query6.getExpressionBuilder().get("address").get("city"));
        query6.addPartialAttribute("salary");
        test6.setQuery(query6);
        addTest(test6);

        ReadAllPartialReadingTest test1m = new ReadAllPartialReadingTest(12, "areaCode");
        test1m.setName("ReadAllPartialReadingTest-type-areaCode");
        ReadAllQuery query1m = new ReadAllQuery();
        query1m.setReferenceClass(Employee.class);
        query1m.dontMaintainCache();
        Expression phones = query1m.getExpressionBuilder().anyOf("phoneNumbers");
        query1m.addPartialAttribute(phones.get("id"));
        query1m.addPartialAttribute(phones.get("type"));
        query1m.addPartialAttribute(phones.get("areaCode"));
        test1m.setQuery(query1m);
        addTest(test1m);

        ReadAllTest test9 = new ReadAllTest(Employee.class, 12);
        test9.setName("ReadAllPartialReadingTest-PolicyMaxCoverage");
        ReadAllQuery query9 = new ReadAllQuery();
        query9.setReferenceClass(Employee.class);
        query9.dontMaintainCache();
        query9.addPartialAttribute("normalHours");
        //query9.addPartialAttribute("policyHolder");
        test9.setQuery(query9);
        addTest(test9);

        ReadAllPartialReadingTest test10 = new ReadAllPartialReadingTest(12, "id");
        test10.setName("ReadAllPartialReadingTest-id-SelectPrimaryKey");
        ReadAllQuery query10 = new ReadAllQuery();
        query10.setReferenceClass(Employee.class);
        query10.dontMaintainCache();
        query10.addPartialAttribute("id");
        test10.setQuery(query10);
        addTest(test10);

        ReadAllPartialReadingTest test11 = new ReadAllPartialReadingTest(12, "firstName");
        test11.setName("ReadAllPartialReadingTest-firstName-OrderByPrimaryKey");
        ReadAllQuery query11 = new ReadAllQuery();
        query11.setReferenceClass(Employee.class);
        query11.dontMaintainCache();
        query11.addPartialAttribute("firstName");
        query11.addAscendingOrdering("id");
        test11.setQuery(query11);
        addTest(test11);

        ReadAllPartialReadingTest test12 = new ReadAllPartialReadingTest(12, "id");
        test12.setName("ReadAllPartialReadingTest-firstName-SelectAndOrderByPrimaryKey");
        ReadAllQuery query12 = new ReadAllQuery();
        query12.setReferenceClass(Employee.class);
        query12.dontMaintainCache();
        query12.addPartialAttribute("id");
        query12.addAscendingOrdering("id");
        test12.setQuery(query12);
        addTest(test12);

        ReadAllPartialReadingAddressTest test14 = new ReadAllPartialReadingAddressTest(12, "id");
        test14.setName("ReadAllPartialReadingAddressTest - Address - SelectDistinctAndOrderByPrimaryKey");
        ReadAllQuery query14 = new ReadAllQuery();
        query14.setReferenceClass(Address.class);
        query14.dontMaintainCache();
        query14.addPartialAttribute("id");
        query14.addAscendingOrdering("id");
        query14.useDistinct();
        test14.setQuery(query14);
        addTest(test14);

        ReadAllPartialReadingAddressTest test15 = new ReadAllPartialReadingAddressTest(12, "id");
        test15.setName("ReadAllPartialReadingAddressTest - Employee - SelectAddressId");
        ReadAllQuery query15 = new ReadAllQuery();
        query15.setReferenceClass(Employee.class);
        query15.dontMaintainCache();
        query15.addPartialAttribute("id");
        query15.addPartialAttribute(new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("id"));
        test15.setQuery(query15);
        addTest(test15);

        addTest(new QueryValidationTest());
    }

    public void addTests() {
        addJoinTests();
        addBatchTests();
        addPartialTests();
        addTest(new ReadAllBindAllParametersTest());
    }
}
