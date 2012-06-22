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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWDescriptorGenerator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWRelationshipHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.projects.TestDatabases;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public class DescriptorGenerationTests 
	extends TestCase 
{
	private MWRelationalProject project;
	
	
	public static Test suite() {
		return new TestSuite(DescriptorGenerationTests.class);
	}

	public DescriptorGenerationTests(String name)
	{
		super(name);
	}
	
	
	// **************** test set up, tear down stuff **************************
	
	protected void setUp() throws Exception {
		super.setUp();
		buildProject();
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	/**
	 * builds a project with tables only
	 */
	private void buildProject()
	{
		DatabasePlatform oraclePlatform = DatabasePlatformRepository.getDefault().platformNamed("Oracle");
		String projectName = ClassTools.shortClassNameForObject(this);
		this.project = new MWRelationalProject(projectName, MappingsModelTestTools.buildSPIManager(), oraclePlatform);
		
		// database
		MWDatabase database = this.project.getDatabase();
		database.setDeploymentLoginSpec(TestDatabases.oracleLoginSpec(database));
		
		
		// customer table
		MWTable customerTable = database.addTable("CUSTOMER");
		
		MWColumn customerIdField = customerTable.addColumn("ID");
		customerIdField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		customerIdField.setPrimaryKey(true);
		
		MWColumn customerNameField = customerTable.addColumn("NAME");
		customerNameField.setDatabaseType(oraclePlatform.databaseTypeNamed("VARCHAR2"));

		
		// item table
		MWTable itemTable = database.addTable("ITEM");
		
		MWColumn itemIdField = itemTable.addColumn("ID");
		itemIdField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		itemIdField.setPrimaryKey(true);
		
		MWColumn itemDescriptionField = itemTable.addColumn("DESCRIPTION");
		itemDescriptionField.setDatabaseType(oraclePlatform.databaseTypeNamed("VARCHAR2"));
		
		MWColumn itemNameField = itemTable.addColumn("NAME");
		itemNameField.setDatabaseType(oraclePlatform.databaseTypeNamed("VARCHAR2"));
		
		
		
		// order table
		MWTable orderTable = database.addTable("ORDER");
		
		MWColumn orderIdField = orderTable.addColumn("ID");
		orderIdField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		orderIdField.setPrimaryKey(true);
		
		MWColumn orderQuantityField = orderTable.addColumn("QUANTITY");
		orderQuantityField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		
		MWColumn orderShippingAddressField = orderTable.addColumn("SHIPPING_ADDRESS");
		orderShippingAddressField.setDatabaseType(oraclePlatform.databaseTypeNamed("VARCHAR2"));
		
		MWColumn orderCustomerIdField = orderTable.addColumn("CUSTOMER_ID");
		orderCustomerIdField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		
		MWColumn orderItemIdField = orderTable.addColumn("ITEM_ID");
		orderItemIdField.setDatabaseType(oraclePlatform.databaseTypeNamed("NUMBER"));
		
		
		// order -> customer reference
		MWReference orderCustomerReference = orderTable.addReference("ORDER_CUSTOMER", customerTable);
		orderCustomerReference.addColumnPair(orderCustomerIdField, customerIdField);
		
		
		// order -> item reference
		MWReference orderItemReference = orderTable.addReference("ORDER_ITEM", itemTable);
		orderItemReference.addColumnPair(orderItemIdField, itemIdField);
	}
	
	
	// **************** convenience *******************************************
	
	private String getPackageName()
	{
		return "foo";
	}
		
	private MWTable getOrderTable()
	{
		return this.project.getDatabase().tableNamed("ORDER");
	}
	
	
	// **************** actual test methods ***********************************
	
	public void testGenerateNormalDescriptorsAndClassDefs()
	{
		generateDescriptors();
		commonTests();
	}
		
	// **************** guts of the tests *************************************
	
	private void commonTests()
	{
		assertTrue("Descriptors were not generated.", this.project.descriptorsSize() == 3);
		
		Collection relationshipMappings = new Vector();
		for (Iterator it = this.project.mappingDescriptors(); it.hasNext(); )
			CollectionTools.addAll(relationshipMappings, ((MWMappingDescriptor) it.next()).tableReferenceMappings());
		assertTrue("Relationships were not generated.", relationshipMappings.size() == 3);
	}	
	
	// **************** generating the descriptors ****************************
	
	private void generateDescriptors()
	{
		MWDescriptorGenerator generator = new MWDescriptorGenerator();
		generator.setGenerateBidirectionalRelationships(true);
		generator.setGenerateMethodAccessors(true);
		generator.setPackageName(getPackageName());
		generator.setProject(this.project);
		generator.setRelationshipsToCreate(relationshipsToCreate());
		generator.setTables(CollectionTools.collection(this.project.getDatabase().tables()));
		generator.generateClassesAndDescriptors();
	}
	
	private Collection relationshipsToCreate()
	{
		Collection relationships = new Vector();
		
		// Customer - Order: 1-M
		MWRelationshipHolder customerToOrder = new MWRelationshipHolder(getOrderTable().referenceNamed("ORDER_CUSTOMER"), true);
		customerToOrder.setOneToMany();
		relationships.add(customerToOrder);
		
		// Order - Customer: 1-1
		MWRelationshipHolder orderToCustomer = new MWRelationshipHolder(getOrderTable().referenceNamed("ORDER_CUSTOMER"), false);
		orderToCustomer.setOneToOne();
		relationships.add(orderToCustomer);
		
		// Order - Item: 1-1
		MWRelationshipHolder orderToItem = new MWRelationshipHolder(getOrderTable().referenceNamed("ORDER_ITEM"), false);
		orderToItem.setOneToOne();
		relationships.add(orderToItem);
		
		return relationships;
	}
}
