/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.collections.map.MapCollectionsSystem;

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
        
        return suite;
    }


}

