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
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections.map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.collections.map.AggregateAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntityMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.DirectAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.DirectDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.DirectEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityMapHolder;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityEntityMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.MapCollectionsSystem;
import org.eclipse.persistence.testing.tests.expressions.ReadAllExpressionTest;

public class MapCollectionsTestModel extends TestModel {
    
    public MapCollectionsTestModel() {
        setDescription("This model tests reading/writing/deleting of the map collections model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new MapCollectionsSystem());
    }

    public void addTests() {
        addTest(getDirectMapMappingTestSuite());
        addTest(getAggregateCollectionMappingTestSuite());
        addTest(getOneToManyMappingTestSuite());
        addTest(getUnidirectionalOneToManyMappingTestSuite());
        addTest(getManyToManyMappingTestSuite());
    }
    public static TestSuite getDirectMapMappingTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("Direct Map Mapping Map Test Suite");
        suite.setDescription("This suite tests using DirectMapMapping with different types of keys.");
        
        // Read
        suite.addTest(new TestReadDirectDirectMapMapping());
        suite.addTest(new TestReadAggregateDirectMapMapping());
        suite.addTest(new TestReadEntityDirectMapMapping());
        
        // Update
        suite.addTest(new TestUpdateDirectDirectMapMapping());
        suite.addTest(new TestUpdateAggregateDirectMapMapping());
        suite.addTest(new TestUpdateEntityDirectMapMapping());
        
        // Private Owned - DirectMapMappings are automatically private owned
        // as a result, the only relevant test here is one with an EntityKey
        suite.addTest(new TestUpdateEntityDirectMapMapping(true));
        
        // Join
        suite.addTest(new TestReadDirectDirectMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadAggregateDirectMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadEntityDirectMapMapping(ForeignReferenceMapping.INNER_JOIN));

        //Expressions
        ReadAllExpressionTest test = new ReadAllExpressionTest(DirectDirectMapHolder.class, 1);
        ExpressionBuilder holders = new ExpressionBuilder();
        Expression exp = holders.anyOf("directToDirectMap").mapKey().equal(1);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateDirectMapHolder.class, 1);
        AggregateMapKey aggkey = new AggregateMapKey();
        aggkey.setKey(11);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToDirectMap").mapKey().equal(aggkey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityDirectMapHolder.class, 1);
        EntityMapKey entKey = new EntityMapKey();
        entKey.setId(333);
        entKey.setData("data3");
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToDirectMap").mapKey().equal(entKey);
        test.setExpression(exp);
        suite.addTest(test);
        
        return suite;
    }
    
    public static TestSuite getAggregateCollectionMappingTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("AggregateCollectionMapping Map Test Suite");
        suite.setDescription("This suite tests using AggregateCollectionMapping with a Map");

        // Read
        suite.addTest(new TestReadDirectAggregateMapMapping());
        suite.addTest(new TestReadAggregateAggregateMapMapping());
        suite.addTest(new TestReadEntityAggregateMapMapping());
        
        // Update
        suite.addTest(new TestUpdateDirectAggregateMapMapping());
        suite.addTest(new TestUpdateAggregateAggregateMapMapping());
        suite.addTest(new TestUpdateEntityAggregateMapMapping());
        
        // Private Owned - AggregateCollectionMappings are automatically private owned
        // as a result, the only relevant test here is one with an EntityKey
        suite.addTest(new TestUpdateEntityAggregateMapMapping(true));
        
        // Join
        suite.addTest(new TestReadDirectAggregateMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadAggregateAggregateMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadEntityAggregateMapMapping(ForeignReferenceMapping.INNER_JOIN));
        
        ReadAllExpressionTest test = new ReadAllExpressionTest(DirectAggregateMapHolder.class, 1);
        ExpressionBuilder holders = new ExpressionBuilder();
        Expression exp = holders.anyOf("directToAggregateMap").mapKey().equal(1);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateAggregateMapHolder.class, 1);
        AggregateMapKey aggkey = new AggregateMapKey();
        aggkey.setKey(11);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToAggregateMap").mapKey().equal(aggkey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityAggregateMapHolder.class, 1);
        EntityMapKey entKey = new EntityMapKey();
        entKey.setId(111);
        entKey.setData("111");
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToAggregateMap").mapKey().equal(entKey);
        test.setExpression(exp);
        suite.addTest(test);
        
        return suite;
    }
    
    public static TestSuite getOneToManyMappingTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("OneToManyMapping Map Test Suite");
        suite.setDescription("This suite tests using OneToManyMapping with a Map");

        // Read
        suite.addTest(new TestReadDirectEntity1MMapMapping());
        suite.addTest(new TestReadAggregateEntity1MMapMapping());
        suite.addTest(new TestReadEntityEntity1MMapMapping());
        
        // Update
        suite.addTest(new TestUpdateDirectEntity1MMapMapping());
        suite.addTest(new TestUpdateAggregateEntity1MMapMapping());
        suite.addTest(new TestUpdateEntityEntity1MMapMapping());
        
        // Private Owned
        suite.addTest(new TestUpdateDirectEntity1MMapMapping(true));
        suite.addTest(new TestUpdateAggregateEntity1MMapMapping(true));
        suite.addTest(new TestUpdateEntityEntity1MMapMapping(true));
        
        // Joining
        suite.addTest(new TestReadDirectEntity1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadAggregateEntity1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadEntityEntity1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        
        //Expressions
        ReadAllExpressionTest test = new ReadAllExpressionTest(DirectEntity1MMapHolder.class, 1);
        ExpressionBuilder holders = new ExpressionBuilder();
        Expression exp = holders.anyOf("directToEntityMap").mapKey().equal(11);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateEntity1MMapHolder.class, 1);
        AggregateMapKey aggkey = new AggregateMapKey();
        aggkey.setKey(11);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToEntityMap").mapKey().equal(aggkey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateEntity1MMapHolder.class, 1);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToEntityMap").mapKey().get("key").equal(11);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityEntity1MMapHolder.class, 1);
        EntityMapKey entKey = new EntityMapKey();
        entKey.setId(555);
        entKey.setData("data5");
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToEntityMap").mapKey().equal(entKey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityEntity1MMapHolder.class, 1);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToEntityMap").mapKey().get("data").equal("data5");
        test.setExpression(exp);
        suite.addTest(test);

        suite.addTest(new MapKeyDirectEntity1MReportQueryTestCase());
        suite.addTest(new MapKeyAggregateEntity1MReportQueryTestCase());
        suite.addTest(new MapKeyEntityEntity1MReportQueryTestCase());
        
        suite.addTest(new MapEntryDirectEntity1MReportQueryTest());
        
        suite.addTest(new InMemoryDirectEntity1MTest());
        
        return suite;
    }
    
    public static TestSuite getUnidirectionalOneToManyMappingTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("UnidirectionalOneToManyMapping Map Test Suite");
        suite.setDescription("This suite tests using UnidirectionalOneToManyMapping with a Map");
        
        // Read
        suite.addTest(new TestReadDirectEntityU1MMapMapping());
        suite.addTest(new TestReadAggregateEntityU1MMapMapping());
        suite.addTest(new TestReadEntityEntityU1MMapMapping());
        
        // Update
        suite.addTest(new TestUpdateDirectEntityU1MMapMapping());
        suite.addTest(new TestUpdateAggregateEntityU1MMapMapping());
        suite.addTest(new TestUpdateEntityEntityU1MMapMapping());
        
        // Private Owned
        suite.addTest(new TestUpdateDirectEntityU1MMapMapping(true));
        suite.addTest(new TestUpdateAggregateEntityU1MMapMapping(true));
        suite.addTest(new TestUpdateEntityEntityU1MMapMapping(true));
        
        // Joining
        suite.addTest(new TestReadDirectEntityU1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadAggregateEntityU1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadEntityEntityU1MMapMapping(ForeignReferenceMapping.INNER_JOIN));
        
        //Expressions
        ReadAllExpressionTest test = new ReadAllExpressionTest(DirectEntityU1MMapHolder.class, 1);
        ExpressionBuilder holders = new ExpressionBuilder();
        Expression exp = holders.anyOf("directToEntityMap").mapKey().equal(11);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateEntityU1MMapHolder.class, 1);
        AggregateMapKey aggkey = new AggregateMapKey();
        aggkey.setKey(11);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToEntityMap").mapKey().equal(aggkey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityEntityU1MMapHolder.class, 1);
        EntityMapKey entKey = new EntityMapKey();
        entKey.setId(999);
        entKey.setData("data9");
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToEntityMap").mapKey().equal(entKey);
        test.setExpression(exp);
        suite.addTest(test);

        return suite;
    }
    
    public static TestSuite getManyToManyMappingTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("ManyToManyMapping Map Test Suite");
        suite.setDescription("This suite tests using ManyToManyMapping with a Map");
        
        // Read
        suite.addTest(new TestReadDirectEntityMapMapping());
        suite.addTest(new TestReadAggregateEntityMapMapping());
        suite.addTest(new TestReadEntityEntityMapMapping());
        
        // Update
        suite.addTest(new TestUpdateDirectEntityMapMapping());
        suite.addTest(new TestUpdateAggregateEntityMapMapping());
        suite.addTest(new TestUpdateEntityEntityMapMapping());
        
        // private owned
        suite.addTest(new TestUpdateDirectEntityMapMapping(true));
        suite.addTest(new TestUpdateAggregateEntityMapMapping(true));
        suite.addTest(new TestUpdateEntityEntityMapMapping(true));
        
        // Joining
        suite.addTest(new TestReadDirectEntityMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadAggregateEntityMapMapping(ForeignReferenceMapping.INNER_JOIN));
        suite.addTest(new TestReadEntityEntityMapMapping(ForeignReferenceMapping.INNER_JOIN));
        
        //Expressions
        ReadAllExpressionTest test = new ReadAllExpressionTest(DirectEntityMapHolder.class, 1);
        ExpressionBuilder holders = new ExpressionBuilder();
        Expression exp = holders.anyOf("directToEntityMap").mapKey().equal(11);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(AggregateEntityMapHolder.class, 1);
        AggregateMapKey aggkey = new AggregateMapKey();
        aggkey.setKey(11);
        holders = new ExpressionBuilder();
        exp = holders.anyOf("aggregateToEntityMap").mapKey().equal(aggkey);
        test.setExpression(exp);
        suite.addTest(test);
        
        test = new ReadAllExpressionTest(EntityEntityMapHolder.class, 1);
        EntityMapKey entKey = new EntityMapKey();
        entKey.setId(777);
        entKey.setData("data7");
        holders = new ExpressionBuilder();
        exp = holders.anyOf("entityToEntityMap").mapKey().equal(entKey);
        test.setExpression(exp);
        suite.addTest(test);
        
        return suite;
    }


}

