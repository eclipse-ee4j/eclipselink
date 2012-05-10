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
package org.eclipse.persistence.testing.tests.insurance;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.expressions.*;
import org.eclipse.persistence.testing.models.insurance.*;
import org.eclipse.persistence.testing.models.insurance.objectrelational.*;

/**
 * Used to test object-relational features of oracle 8.1 and jdbc 2.0.
 */
public class InsuranceObjectRelationalTestModel extends org.eclipse.persistence.testing.tests.insurance.InsuranceBasicTestModel {
    public void addRequiredSystems() {
        // Must logout to reset type information.
        getDatabaseSession().logout();
        getDatabaseSession().login();
        if (getSession().getPlatform().isOracle9()) {
            addRequiredSystem(new InsuranceORSystem());
        }
    }

    public void addTests() {
        if (getSession().getPlatform().isOracle9()) {
            super.addTests();
            addTest(getNestedTablesReadObjectTestSuite());
            addTest(getObjectArrayUpdateTestSuite());
            // This test suite added for bug 2730536
            addTest(getNullUpdatesTestSuite());
        }
    }

    protected static TestSuite getNestedTablesReadObjectTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("NestedTablesReadObjectTestSuite");
        testSuite.setDescription("nested tables read test");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.anyOf("policies").get("maxCoverage").greaterThan(30000);

        // Should get 3, DISTINCT is not supported in Oracle8i, duplicate data is read in 
        ReadAllExpressionTest test1 = new ReadAllExpressionTest(PolicyHolder.class, 4);
        test1.setName("nested tables read test (PolicyHoler)");
        test1.setExpression(exp1);
        testSuite.addTest(test1);

        builder = new ExpressionBuilder();
        Expression exp2 = builder.get("policyHolder").get("ssn").equal(1111);

        ReadAllExpressionTest test2 = new ReadAllExpressionTest(Policy.class, 1);
        test2.setName("nested tables read test (Policy)");
        test2.setExpression(exp2);
        testSuite.addTest(test2);

        builder = new ExpressionBuilder();
        Expression exp3 = builder.get("policy").get("policyNumber").equal(102);

        ReadAllExpressionTest test3 = new ReadAllExpressionTest(Claim.class, 1);
        test3.setName("nested tables read test(Claim)");
        test3.setExpression(exp3);
        testSuite.addTest(test3);

        builder = new ExpressionBuilder();
        Expression exp4 = builder.get("policyHolder").get("address").get("city").equal("Boston");

        ReadAllExpressionTest test4 = new ReadAllExpressionTest(Policy.class, 2);
        test4.setName("nested tables read test(Address from PolicyHolder from Policy)");
        test4.setExpression(exp4);
        testSuite.addTest(test4);

        return testSuite;
    }

    /**
     * For bug 2730536 try setting various object-relational fields to null.
     * Test difficulties: setting values to null effectively triggers deletes on
     * the database.  This triggers database constraints and also errors as all this
     * is done outside a unit of work.
     * It was not possible to set an existing Ref to null, without also deleting
     * what the ref pointed to.
     */
    protected static TestSuite getNullUpdatesTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("NullUpdatesTestSuite");
        testSuite.setDescription("Tests setting various object-relational fields to null.");

        // Do not use Population manager, as it does not return clones and we are
        // modifying the objects before the test.

        PolicyHolder policyHolder;
        Policy policy;
        Claim claim;
        WriteObjectTest test;

        // Test 1: Setting arrays, object arrays and Structs to null.
        policyHolder = PolicyHolder.example1();
        // Test setting an Array to null.
        policyHolder.setChildrenNames(null);
        // Test setting a Struct to null.
        policyHolder.setAddress(null);
        policyHolder.setOccupation(null);
        // Test seting an ObjectArray to null.
        policyHolder.setPhones(null);
        test = new WriteObjectTest(policyHolder);
        test.setShouldBindAllParameters(true);
        test.setMakesTrivialUpdate(false);
        test.setDescription("Tests setting null on Array, Structure, and ObjectArray mappings.");
        testSuite.addTest(test);

        // Test 2: Setting arrays and object arrays to empty vectors.
        policyHolder = PolicyHolder.example1();
        // Test setting an Array to null.
        policyHolder.setChildrenNames(new Vector());
        // Test seting an ObjectArray to null.
        policyHolder.setPhones(new Vector());
        test = new WriteObjectTest(policyHolder);
        test.setShouldBindAllParameters(true);
        test.setMakesTrivialUpdate(false);
        test.setDescription("Tests setting empty vectors on Array and NestedTable mappings.");
        testSuite.addTest(test);

        // Test 3: Setting nested tables and references to null.
        // This policy does not exist on database and has null values for Ref and NestedTables.
        policy = HousePolicy.example3();
        test = new WriteObjectTest(policy);
        test.setShouldBindAllParameters(true);
        test.setDescription("Tests setting null on Ref and NestedTable mappings.");
        testSuite.addTest(test);

        // Test 4: Setting a nested table to null, with non-trivial update.
        policyHolder = PolicyHolder.example1();
        policy = (Policy)policyHolder.getPolicies().firstElement();
        // Test setting a nested table to null.
        policy.setClaims(null);
        test = new WriteObjectTest(policy);
        test.setShouldBindAllParameters(true);
        test.setMakesTrivialUpdate(false);
        test.setDescription("Tests setting null on a Ref mapping.");
        testSuite.addTest(test);

        return testSuite;
    }

    protected static TestSuite getObjectArrayUpdateTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("ObjectArrayAddRemoveUoWTestSuite");
        testSuite.setDescription("ObjectArray update test");

        testSuite.addTest(new ObjectArrayMappingUpdateTest());

        return testSuite;
    }

    /**
     * Remove the project as will conflict with normal insurance.
     */
    public void reset() {
        getExecutor().removeConfigureSystem(new InsuranceSystem());
    }
}
