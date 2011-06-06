/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.*;
import java.math.BigDecimal;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.legacy.Order;
import org.eclipse.persistence.testing.models.legacy.Shipment;
import org.eclipse.persistence.testing.models.legacy.Computer;

public class CacheHitAndInMemoryTestSuite extends TestSuite {
    public CacheHitAndInMemoryTestSuite() {
        setDescription("This suite tests cache hits and in-memory querying.");
    }

    public void addCacheTests() {

        /** Cache hit tests */
        PopulationManager manager = PopulationManager.getDefaultManager();
        addTest(new CacheMissTest());
        addTest(new OneToOneCacheHitTest());
        addTest(new CacheHitTest(manager.getObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, "0001")));
        addTest(new CacheHitTest(manager.getObject(Shipment.class, "example1")));

        Order order = (Order)((Shipment)org.eclipse.persistence.testing.models.legacy.Employee.example1().shipments.elementAt(0)).orders.elementAt(0);
        order.shipment.shipmentNumber = new Integer(order.shipment.shipmentNumber.intValue());
        addTest(new CacheHitTest(order));

        org.eclipse.persistence.testing.models.legacy.Employee employee = (org.eclipse.persistence.testing.models.legacy.Employee)manager.getObject(org.eclipse.persistence.testing.models.legacy.Employee.class, "example1");
        addTest(new CacheHitTest(employee));

        Computer computer = employee.computer;
        addTest(new CacheHitTest(computer));

        addTest(new CacheHitWithInheritance());

        addTest(new CacheHitWithNonPKCriteriaTest());
        addTest(new CacheHitOnPKWithInheritanceTest());

        addTest(new QueryCacheHitDisabledAndDescriptorDisabledTest());
        addTest(new QueryCacheHitDisabledAndDescriptorEnabledTest());
        addTest(new QueryCacheHitEnabledAndDescriptorDisabledTest());
        addTest(new QueryCacheHitEnabledAndDescriptorEnabledTest());
        addTest(new QueryCacheHitUndefinedAndDescriptorDisabledTest());
        addTest(new QueryCacheHitUndefinedAndDescriptorEnabledTest());
    }

    public void addInMemoryHitTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee example = (Employee)manager.getObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, "0001");
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadObjectQuery query = new ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class, builder.get("id").equal(example.getId()));
        InMemoryCacheHitTest test = new InMemoryCacheHitTest(query);
        test.setName("InMemoryCacheHitTest - by key exact");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheByExactPrimaryKey();
        test.setName("InMemoryCacheHitTest - by key exact required");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery();
        query.setSelectionObject(example);
        test = new InMemoryCacheHitTest(query);
        query.checkCacheByExactPrimaryKey();
        test.setName("InMemoryCacheHitTest - by selection object exact required");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").equal(example.getFirstName())));
        test = new InMemoryCacheHitTest(query);
        test.setName("InMemoryCacheHitTest - by key non exact and");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).or(builder.get("firstName").equal("Jonney5")));
        test = new InMemoryCacheHitTest(query);
        test.setName("InMemoryCacheHitTest - by key non exact or");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").toUpperCase().equal(example.getFirstName().toUpperCase())));
        test = new InMemoryCacheHitTest(query);
        test.setName("InMemoryCacheHitTest - by key non exact upper");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal(example.getFirstName()));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - by name");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("lastName").equal(example.getLastName()).and(builder.get("firstName").toUpperCase().equal(example.getFirstName().toUpperCase())));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - by f/l name upper");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("lastName").concat(builder.get("firstName")).equal(example.getLastName() + example.getFirstName()).and(ExpressionMath.multiply(builder.get("salary"), 2).greaterThan(10)));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - by functions");
        addTest(test);

        builder = new ExpressionBuilder();
        Vector names = new Vector();
        names.addElement("jonesy");
        names.addElement(example.getLastName());
        query = new ReadObjectQuery(Employee.class, builder.get("lastName").in(names));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - in");
        addTest(test);

        builder = new ExpressionBuilder();
        Vector ids = new Vector();
        ids.addElement(new Long(123456789));
        ids.addElement(example.getId());
        query = new ReadObjectQuery(Employee.class, builder.get("id").in(ids));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - in on primary key");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("salary").between(0, 1000000));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheHitTest - between");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("salary").between(0, -1000000));
        test = new InMemoryCacheHitTest(query);
        query.checkCacheOnly();
        test.setName("InMemoryCacheHitTest - miss on only");
        addTest(test);

        builder = new ExpressionBuilder();
        ReadAllQuery queryAll = new ReadAllQuery(Employee.class, builder.get("salary").between(0, 100000));
        InMemoryReadAllCacheHitTest testAll = new InMemoryReadAllCacheHitTest(queryAll, 5);
        queryAll.checkCacheOnly();
        testAll.setName("InMemoryCacheHitTest - read all - between");
        addTest(testAll);

        builder = new ExpressionBuilder();
        queryAll = new ReadAllQuery(Employee.class, builder.get("firstName").greaterThan("ABBA"));
        testAll = new InMemoryReadAllCacheHitTest(queryAll, 5);
        queryAll.checkCacheOnly();
        testAll.setName("InMemoryCacheHitTest - read all - less than");
        addTest(testAll);

        queryAll = new ReadAllQuery(Employee.class);
        //queryAll.setReferenceClass(Employee.class);
        testAll = new InMemoryReadAllCacheHitTest(queryAll, 10);
        queryAll.checkCacheOnly();
        testAll.setName("InMemoryCacheHitTest - with no where clause");
        addTest(testAll);
    }

    public void addInMemoryMissTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee example = (Employee)manager.getObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, "0001");
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadObjectQuery query = new ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").equal(example.getFirstName())));
        query.checkCacheByExactPrimaryKey();
        InMemoryCacheMissTest test = new InMemoryCacheMissTest(query);
        test.setName("InMemoryCacheMissTest - by key exact");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").notEqual(example.getFirstName())));
        test = new InMemoryCacheMissTest(query);
        query.checkCacheByPrimaryKey();
        test.setName("InMemoryCacheMissTest - by non exact");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").like(example.getLastName())));
        test = new InMemoryCacheMissTest(query);
        query.checkCacheByPrimaryKey();
        test.setName("InMemoryCacheMissTest - by non exact - exception");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").like(example.getLastName())));
        test = new InMemoryCacheMissTest(query);
        query.checkCacheThenDatabase();
        test.setName("InMemoryCacheMissTest - by non exact - exception");
        addTest(test);
    }

    public void addTests() {
        addCacheTests();
        addInMemoryHitTests();
        addInMemoryMissTests();
        addUOWConformTests();
        addUOWConformObjectTests();
        addUOWConformWithoutRegisteringTests();
    }

    public void addUOWConformObjectTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee example = (Employee)manager.getObject(Employee.class, "0001");
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, builder.get("id").greaterThan(example.getId().subtract(new BigDecimal(1))).and(builder.get("id").lessThanEqual(example.getId())));
        query.conformResultsInUnitOfWork();
        UnitOfWorkConformObjectTest test = new UnitOfWorkConformObjectTest(query, true);
        test.setName("UnitOfWorkConformTest - hit");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal("Sarah").or(builder.get("lastName").equal("")));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformObjectTest(query, true);
        test.setName("UnitOfWorkConformTest - dead-hit");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal("Bobbyiop").and(builder.get("lastName").equal(null)));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformObjectTest(query, false);
        test.setName("UnitOfWorkConformTest - miss");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal("Bob").and(builder.get("lastName").equal(null)));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformObjectTest(query, true);
        test.setName("UnitOfWorkConformTest - new");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("lastName").equal("Bobo"));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformObjectTest(query, true);
        test.setName("UnitOfWorkConformTest - changed");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal("Sarah"));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformObjectTest(query, false);
        test.setName("UnitOfWorkConformTest - deleted");
        addTest(test);

        addTest(new UnitOfWorkConformLikeTest());
        addTest(new UnitOfWorkConformLikeSpecialCharacterTest());
        addTest(new NamedQueryConformNullPointerException());
        addTest(new MemoryQueryAcrossOneToOneMapping());
        addTest(new MemoryQueryAcrossNestedOneToManyMapping());
        addTest(new MemoryQueryAcrossOneToManyMapping());
        addTest(new MemoryQueryForFunctionsAcrossOneToManyMapping());
        addTest(new MemoryQueryForFunctionsAcrossOneToManyAcrossOneToOneMapping());
        addTest(new MemoryQueryAcrossOneToManyMapping2());
        addTest(new MemoryQueryAcrossOneToManyAcrossOneToOneMapping());
        addTest(new MemoryQueryAcrossManyToManyMapping());
        addTest(new MemoryQueryAcrossManyToManyAcrossOneToManyMapping());
        addTest(new MemoryQueryAcrossDirectCollectionMapping());
        addTest(new MemoryQueryAcrossAggregateCollectionMapping(Agent.class));
        addTest(new MemoryQueryAcrossAggregateCollectionMapping(Builder.class));
        addTest(new NamedQueryConformNullPointerException());
        addTest(new MemoryQueryTriggerIndirection());
        addTest(new MemoryQueryReturnConformedOnIndirection());
        addTest(new MemoryQueryReturnNotConformedOnIndirection());
        addTest(new MemoryQueryThrowExceptionOnIndirection());

        //bug#2679958
        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("lastName").equal(null));
        DescriptorUnitOfWorkConformObjectTest test2 = new DescriptorUnitOfWorkConformObjectTest(query, true);
        test2.setName("DescriptorUOWConformTest - hit");
        addTest(test2);

        builder = new ExpressionBuilder();
        query = new ReadObjectQuery(Employee.class, builder.get("firstName").equal("Sarah"));
        test2 = new DescriptorUnitOfWorkConformObjectTest(query, false);
        test2.setName("DescriptorUOWConformTest - miss");
        addTest(test2);
        addTest(new MemoryQueryLike());
    }

    public void addUOWConformTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee example = (Employee)manager.getObject(Employee.class, "0001");
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("firstName").equal(example.getFirstName())));
        query.conformResultsInUnitOfWork();
        UnitOfWorkConformTest test = new UnitOfWorkConformTest(query, 1);
        test.setName("UnitOfWorkConformTest - by key");
        addTest(test);

        query = new ReadAllQuery(Employee.class);
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 13);
        test.setName("UnitOfWorkConformTest - no selection criteria");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class, builder.get("firstName").equal("Bob"));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 2);
        test.setName("UnitOfWorkConformTest - by name, new object");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class, builder.get("firstName").equal("newBobby"));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 1);
        test.setName("UnitOfWorkConformTest - by name, new object");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class, builder.get("firstName").equal("Sarah"));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 0);
        test.setName("UnitOfWorkConformTest - by name, deleted object");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("lastName").equal(example.getLastName())));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 0);
        test.setName("UnitOfWorkConformTest - by name, changed object, remove");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class, builder.get("id").equal(example.getId()).and(builder.get("lastName").equal("Bobo")));
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformTest(query, 1);
        test.setName("UnitOfWorkConformTest - by name, changed object, add");
        addTest(test);

        builder = new ExpressionBuilder();
        query = new ReadAllQuery(Employee.class);
        query.conformResultsInUnitOfWork();
        test = new UnitOfWorkConformDuplicateTest(query, 13);
        test.setName("UnitOfWorkConformDuplicateTest - all");
        addTest(test);

        addTest(new UnitOfWorkConformAcrossIndirectionTest());
        addTest(new UnitOfWorkConformWithOrderTest());
        
        addTest(new UnitOfWorkConformNewObjectTest());
    }

    public void addUOWConformWithoutRegisteringTests() {
        addTest(new UnitOfWorkConformWithoutRegisteringTest());
    }
}
