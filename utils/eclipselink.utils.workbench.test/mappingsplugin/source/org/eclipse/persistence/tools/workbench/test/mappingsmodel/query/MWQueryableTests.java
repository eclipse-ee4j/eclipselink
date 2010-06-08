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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


/**
 *
 */
public class MWQueryableTests extends TestCase
{
	
	private MWProject employeeProject;
	
	public static Test suite() 
	{
		return new TestSuite(MWQueryableTests.class);
	}

	/**
	 * Constructor for MWQueryableTests.
	 * @param name
	 */
	public MWQueryableTests(String name)
	{
		super(name);
	}
	

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.employeeProject = new EmployeeProject().getProject();
	}

	private MWTableDescriptor getAddressDescriptor()
	{
		return (MWTableDescriptor) getDescriptorWithShortName("Address");
	}

	private MWTableDescriptor getEmployeeDescriptor()
	{
		return (MWTableDescriptor) getDescriptorWithShortName("Employee");
	}	
	
	private MWAggregateDescriptor getEmploymentPeriodDescriptor()
	{
		return (MWAggregateDescriptor) getDescriptorWithShortName("EmploymentPeriod");
	}

	public void testOneToOneMappingQueryableMethods()	
	{
		MWQueryable addressMapping = (MWOneToOneMapping) getEmployeeDescriptor().mappingNamed("address");
		
		assertTrue("The address queryable should have 6 sub queryables: city, country, id, postalCode, province, street", addressMapping.subQueryableElements(Filter.NULL_INSTANCE).size() == 6);
		
		Iterator addressMappings = getAddressDescriptor().mappings();
		for (int i = 0; i < addressMapping.subQueryableElements(Filter.NULL_INSTANCE).size(); i++)
		{
			assertTrue("The queryable does not match the mapping in the Address descriptor", addressMapping.subQueryableElementAt(i, Filter.NULL_INSTANCE) == addressMappings.next());
		}
		
		assertTrue("The address queryable should allow children because it is a 1-1 mapping", addressMapping.allowsChildren());
		
		assertTrue("The address queryable should not be a leaf.", !addressMapping.isLeaf(Filter.NULL_INSTANCE));
		
		assertTrue("The address queryable should not use 'Any Of' because it is a 1-1 mapping", !addressMapping.usesAnyOf());
	}
	
	public void testAddingQueryKeys()
	{
		MWQueryable addressMapping = (MWOneToOneMapping) getEmployeeDescriptor().mappingNamed("address");
		getAddressDescriptor().addQueryKey("user-defined", null);
		assertTrue("The address queryable should have 7 sub queryables: city, country, id, postalCode, province, street, user-defined", addressMapping.subQueryableElements(Filter.NULL_INSTANCE).size() == 7);
		
	}
	
	public void testDirectCollectionMappingQueryableMethods()	
	{
		MWQueryable responsibilitiesListMapping = (MWRelationalDirectCollectionMapping) getEmployeeDescriptor().mappingNamed("responsibilitiesList");
		
		assertTrue("The responsibilitiesList queryable should not have any sub queryables.", responsibilitiesListMapping.subQueryableElements(Filter.NULL_INSTANCE).size() == 0);
		
		assertTrue("The responsibilitiesList queryable should not allow children because it is a direct collection", !responsibilitiesListMapping.allowsChildren());
		
		assertTrue("The responsibilitiesList queryable should be a leaf.", responsibilitiesListMapping.isLeaf(Filter.NULL_INSTANCE));
		
		assertTrue("The responsibilitiesList queryable should use 'Any Of' because it is a direct collection mapping", responsibilitiesListMapping.usesAnyOf());
	}
	
	public void testDirectToFieldMappingQueryableMethods()	
	{
		MWQueryable firstNameMapping = (MWDirectToFieldMapping) getEmployeeDescriptor().mappingNamed("firstName");
		
		assertTrue("The firstName queryable should not have any sub queryables.", firstNameMapping.subQueryableElements(Filter.NULL_INSTANCE).size() == 0);
		
		assertTrue("The firstName queryable should not allow children because it is a direct mapping", !firstNameMapping.allowsChildren());
		
		assertTrue("The firstName queryable should be a leaf.", firstNameMapping.isLeaf(Filter.NULL_INSTANCE));
		
		assertTrue("The firstName queryable should not use 'Any Of' because it is a direct mapping", !firstNameMapping.usesAnyOf());
	}

	public void testUserDefinedQueryKeyQueryableMethods()	
	{
		MWQueryable fooQueryKey = getEmployeeDescriptor().addQueryKey("foo", null);
		
		assertTrue("The foo queryable should not have any sub queryables.", fooQueryKey.subQueryableElements(Filter.NULL_INSTANCE).size() == 0);
		
		assertTrue("The foo queryable should not allow children because it is a user defined query key", !fooQueryKey.allowsChildren());
		
		assertTrue("The foo queryable should be a leaf.", fooQueryKey.isLeaf(Filter.NULL_INSTANCE));
		
		assertTrue("The foo queryable should not use 'Any Of' because it is a user defined query key", !fooQueryKey.usesAnyOf());
	}

	public void testAggregateMappingQueryableMethods()	
	{
		MWQueryable periodMapping = (MWAggregateMapping) getEmployeeDescriptor().mappingNamed("period");
		
		assertTrue("The period queryable should have 2 sub queryables: endDate, startDate", periodMapping.subQueryableElements(Filter.NULL_INSTANCE).size() == 2);
		
		Iterator periodMappings = getEmploymentPeriodDescriptor().mappings();
		for (int i = 0; i < periodMapping.subQueryableElements(Filter.NULL_INSTANCE).size(); i++)
		{
			assertTrue("The queryable does not match the mapping in the EmploymentPeriod descriptor", periodMapping.subQueryableElementAt(i, Filter.NULL_INSTANCE) == periodMappings.next());
		}

		assertTrue("The period queryable should allow children", periodMapping.allowsChildren());
		
		assertTrue("The period queryable should not be a leaf.", !periodMapping.isLeaf(Filter.NULL_INSTANCE));
		
		assertTrue("The period queryable should not use 'Any Of' because it is an aggregate mapping", !periodMapping.usesAnyOf());
	}

	public MWDescriptor getDescriptorWithShortName(String name) {
		for (Iterator stream = this.employeeProject.descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.getMWClass().shortName().equals(name)) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException(name);
	}

}
