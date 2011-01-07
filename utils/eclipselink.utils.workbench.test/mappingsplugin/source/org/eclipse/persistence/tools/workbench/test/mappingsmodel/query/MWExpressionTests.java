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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWNullArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;



/**
 * 
 */
public class MWExpressionTests extends TestCase
{
	
	private MWProject employeeProject;

	public static Test suite() 
	{
		return new TestSuite(MWExpressionTests.class);
	}

	public MWExpressionTests(String name)
	{
		super(name);
	}

	private MWOneToOneMapping getAddressMapping()
	{
		return (MWOneToOneMapping)((MWTableDescriptor) getDescriptorWithShortName("Employee")).mappingNamed("address");
	}

	private MWDirectToFieldMapping getCityMapping()
	{
		return (MWDirectToFieldMapping) ((MWTableDescriptor) getDescriptorWithShortName("Address")).mappingNamed("city");
	}	
	
	private MWDirectToFieldMapping getLastNameMapping()
	{
		return (MWDirectToFieldMapping) ((MWTableDescriptor) getDescriptorWithShortName("Employee")).mappingNamed("lastName");
	}	
	
	protected void setUp() throws Exception {
		super.setUp();
		this.employeeProject = new EmployeeProject().getProject();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAddingExpressions()
	{
		MWRelationalReadQuery query = buildTestQuery();
		
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		assertTrue("An expression was not created by default for the query", mainExpression != null);	
		
		MWBasicExpression basicExpression = mainExpression.addBasicExpression();
		assertTrue("An expression was not added",mainExpression.expressionsSize() == 1);
		assertTrue("The first argument is not a queryable argument", MWQueryableArgument.class.isAssignableFrom(basicExpression.getFirstArgument().getClass()));
		assertTrue("The expression added is not a MWBasicExpression", MWBasicExpression.class.isAssignableFrom(basicExpression.getClass()));
		assertTrue("The second argument is not a literal argument", MWLiteralArgument.class.isAssignableFrom(basicExpression.getSecondArgument().getClass()));
		 
		MWTableDescriptor employeeDescriptor = (MWTableDescriptor) query.getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		MWDirectToFieldMapping lastNameMapping = (MWDirectToFieldMapping) employeeDescriptor.mappingNamed("lastName");
		basicExpression.getFirstArgument().setQueryableArgument(lastNameMapping);
		
		assertTrue("The queryable argument was not set", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == lastNameMapping);
		
		employeeDescriptor.removeMapping(lastNameMapping);
		
		assertTrue("The queryable argument was not deleted as a result of the mapping being deleted", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);
		assertTrue("The queryable argument was not deleted as a result of the mapping being deleted", basicExpression.getFirstArgument().getQueryableArgumentElement().getJoinedQueryableElement() == null);
	}
	
	public void testChangingBasicExpressionOperatorType()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().getExpression(0);
		basicExpression.setOperatorType(MWBasicExpression.IS_NULL);		

		basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().getExpression(0);
		
		assertTrue("The operator type was not set to IS_NULL", basicExpression.getOperatorType() == MWBasicExpression.IS_NULL);
		assertTrue("The second argument is not an instanceof MWNullArgument for the unary expression", basicExpression.getSecondArgument() instanceof MWNullArgument);
		assertTrue("Changing the operator type did change the type of the expression", MWBasicExpression.class.isAssignableFrom(basicExpression.getClass()));
		assertTrue("The parent of the queryableArgument was not set correctlyafter morphing to a UnaryExpression", basicExpression.getFirstArgument().getParent() == basicExpression);
		basicExpression.setOperatorType(MWBasicExpression.LIKE_IGNORE_CASE);

		basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().getExpression(0);
		
		assertTrue("The operator type was not set to LIKE_IGNORE_CASE", basicExpression.getOperatorType() == MWBasicExpression.LIKE_IGNORE_CASE);
		assertTrue("The second argument is null for the binary expression", basicExpression.getSecondArgument() != null);
		assertTrue("Changing the operator type did change the type of the expression", MWBasicExpression.class.isAssignableFrom(basicExpression.getClass()));
		assertTrue("The parent of the queryableArgument was not set correctly after morphing to a BinaryExpression", basicExpression.getFirstArgument().getParent() == basicExpression);	
	}
	
	public void testChangingCompoundExpressionOperatorType()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWCompoundExpression expression = query.getQueryFormat().getExpression();
		
		assertTrue("The operator was not set to AND by default", expression.getOperatorType().equals(MWCompoundExpression.AND));
		
		expression.setOperatorType(MWCompoundExpression.NOR);
		assertTrue("The operator was not set to NOR", expression.getOperatorType().equals(MWCompoundExpression.NOR));
		
	}
	public void testMorphingDescriptor()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		assertTrue("The queryable argument was not set", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == getCityMapping());
		assertTrue("The joined queryable argument was not set", basicExpression.getFirstArgument().getQueryableArgumentElement().getJoinedQueryableElement().getQueryable() == getAddressMapping());

		//test morphing the address descriptor		
		((MWTableDescriptor) getDescriptorWithShortName("Address")).asMWAggregateDescriptor();		
		assertTrue("The first argument was set to null when the reference descriptor was morphed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable()  != null);
		
	}
	
	public void testRemovingDescriptor()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		//test removing the address descriptor
		MWTableDescriptor descriptor = ((MWTableDescriptor) getDescriptorWithShortName("Address"));	
		descriptor.getProject().removeDescriptor(descriptor);
		assertTrue("The first argument was not set to null when the reference descriptor was removed, thus set to null", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable()  == null);
	}
	
	public void testRenamingDescriptor()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		MWDirectToFieldMapping cityMapping = getCityMapping();
		//test renaming the address descriptor
		MWTableDescriptor descriptor = ((MWTableDescriptor) getDescriptorWithShortName("Address"));	
		descriptor.getMWClass().setName("MyAddress");
		descriptor.setName("MyAddress");
		assertTrue("The first argument is no longer the same", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == cityMapping);
		assertTrue("The descriptor was renamed but the queryable is still holding on to an old copy", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable().getParentDescriptor().getName().equals("MyAddress"));	
	}
	
	public void testRemovingMapping()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();


		//test removing the city mapping
		MWTableDescriptor descriptor = ((MWTableDescriptor) getDescriptorWithShortName("Address"));	
		descriptor.removeMapping(getCityMapping());
		assertTrue("The first argument queryable element was not removed when the mapping was removed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);
		
	}
	
	public void testUnmappingJoinedQueryable()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		//test unmapping the address mapping

		MWOneToOneMapping addressMapping = getAddressMapping();
		addressMapping.getParentDescriptor().removeMapping(addressMapping);
		
		assertTrue("The first argument queryable element was not removed when the joined mapping was unmapped", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);		
	}
	
	public void testMorhpingJoinedQueryable()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		//test unmapping the address mapping
		MWOneToOneMapping addressMapping = getAddressMapping();
		addressMapping.asMWOneToManyMapping();
		
		assertTrue("The first argument queryable element was removed when the joined mapping was morhphed into a 1-many", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == getCityMapping());
	}
	
	public void testRemovingJoinedQueryable()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		//test removing the address mapping
		MWOneToOneMapping addressMapping = getAddressMapping();
		addressMapping.getParentDescriptor().removeMapping(addressMapping);
		
		assertTrue("The first argument queryable element was not removed when the joined mapping was removed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);		
		
	}
	
	public void testRenamingMapping()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		//test renaming the city mapping
		MWDirectToFieldMapping cityMapping = getCityMapping();
		cityMapping.getInstanceVariable().setName("myCity");
		cityMapping.setName("myCity");
		assertTrue("The first argument queryable element was not renamed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable().getName().equals("myCity"));		
	}
	
	public void testMappingReferenceDescriptorSetToNull()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();

		MWOneToOneMapping addressMapping = getAddressMapping();
		addressMapping.setReferenceDescriptor(null);
		assertTrue("The first argument queryable element was not removed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);		
	}
		
	public void testMappingReferenceDescriptorChanged()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpression();
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().getExpression(0);

		MWOneToOneMapping addressMapping = getAddressMapping();
		addressMapping.setReferenceDescriptor(getDescriptorWithShortName("Project"));
		assertTrue("The first argument queryable element was not removed", basicExpression.getFirstArgument().getQueryableArgumentElement().getQueryable() == null);		
	}	
	
	public void testQueryParameterDeleted()
	{
		MWRelationalReadQuery query = buildTestQueryWithExpressionWithParameterArgument();
		query.removeParameter(query.getParameter(0));
		MWBasicExpression basicExpression = (MWBasicExpression)((MWExpressionQueryFormat)query.getQueryFormat()).getExpression().expressions().next();
	
		assertTrue("The parameter arugment was not deleted when the parameter was deleted", ((MWQueryParameterArgument)basicExpression.getSecondArgument()).getQueryParameter() == null);
	}
	
	public void testRemovingExpression()
	{
		MWRelationalReadQuery query = buildTestQueryWithCompoundExpression();
		MWCompoundExpression subCompoundExpression = (MWCompoundExpression) query.getQueryFormat().getExpression().expressions().next();
		MWExpression expressionToRemove = (MWExpression) subCompoundExpression.expressions().next();
		subCompoundExpression.removeExpression(expressionToRemove);
		assertTrue("", subCompoundExpression.expressionsSize() == 1);
	}
    
    public void testRemovingQueryKey()
    {
        MWRelationalReadQuery query = buildTestQueryWithCompoundExpression();
        MWCompoundExpression subCompoundExpression = (MWCompoundExpression) query.getQueryFormat().getExpression().expressions().next();
        MWBasicExpression basicExpression = (MWBasicExpression) subCompoundExpression.getExpression(1);

        MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
        desc.removeQueryKey((MWUserDefinedQueryKey) desc.queryKeyNamed("foo"));
        
        assertTrue("The queryable was not set to null when the query key was removed", ((MWQueryableArgument) basicExpression.getSecondArgument()).getQueryableArgumentElement().getQueryable() == null);        
     }
	//test removing expressions
	//test adding sub compound expressions
	//test removing expressions and make sure sub expressions are removed
	//test MWCompoundExpression.removeAllSubExpressions
	
	
	private MWRelationalReadQuery buildTestQueryWithExpression()
	{
		MWRelationalReadQuery query = buildTestQuery();
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = mainExpression.addBasicExpression();
		
		//set up the basic expression    address.city equals "" 
		MWOneToOneMapping addressMapping = getAddressMapping();
		MWDirectToFieldMapping cityMapping = getCityMapping();
		
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(cityMapping);
		joinedQueryables.add(addressMapping);
		basicExpression.getFirstArgument().setQueryableArgument(joinedQueryables.iterator());
	
		return query;
	}

	private MWRelationalReadQuery buildTestQueryWithCompoundExpression()
	{
		MWRelationalReadQuery query = buildTestQuery();
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWCompoundExpression subCompoundExpression = mainExpression.addSubCompoundExpression();
		MWBasicExpression basicExpression = subCompoundExpression.addBasicExpression();
		
		//set up the basic expression    address.city equals "" 
		MWOneToOneMapping addressMapping = getAddressMapping();
		MWDirectToFieldMapping cityMapping = getCityMapping();
		
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(cityMapping);
		joinedQueryables.add(addressMapping);
		basicExpression.getFirstArgument().setQueryableArgument(joinedQueryables.iterator());
	
        
        MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
        MWUserDefinedQueryKey queryKey = desc.addQueryKey("foo", null);

        basicExpression.setSecondArgumentToQueryable();
        ((MWQueryableArgument) basicExpression.getSecondArgument()).setQueryableArgument(queryKey);
		return query;
	}
	
	private MWRelationalReadQuery buildTestQueryWithExpressionWithParameterArgument()
	{
		MWRelationalReadQuery query = buildTestQuery();
		MWQueryParameter parameter = query.addParameter(this.employeeProject.typeNamed("java.lang.String"));
		parameter.setName("lastName");
		
		
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = mainExpression.addBasicExpression();
		
		//set up the basic expression    lastName equals lastName(parameter) 
		MWDirectToFieldMapping cityMapping = getLastNameMapping();
		basicExpression.getFirstArgument().setQueryableArgument(cityMapping);

		basicExpression.setSecondArgumentToParameter();
		((MWQueryParameterArgument)basicExpression.getSecondArgument()).setQueryParameter(parameter);
		return query;
	}
	
	private MWRelationalReadQuery buildTestQuery() 
	{
		MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
		MWQueryManager qm = desc.getQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) qm.addReadObjectQuery("test-query");
		return query;
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
