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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class MWExpressionUndoableTests extends TestCase
{

	private MWProject employeeProject;

	public static Test suite() 
	{
		return new TestSuite(MWExpressionUndoableTests.class);
	}

	public MWExpressionUndoableTests(String name)
	{
		super(name);
	}	
	
	protected void setUp() throws Exception {
		super.setUp();
		this.employeeProject = new EmployeeProject().getProject();
	}
	
	private MWOneToOneMapping getAddressMapping()
	{
		return (MWOneToOneMapping)((MWTableDescriptor) getDescriptorWithShortName("Employee")).mappingNamed("address");
	}

	private MWDirectToFieldMapping getCityMapping()
	{
		return (MWDirectToFieldMapping) ((MWTableDescriptor) getDescriptorWithShortName("Address")).mappingNamed("city");
	}
		
	private MWDirectToFieldMapping getFirstNameMapping()
	{
		return (MWDirectToFieldMapping) ((MWTableDescriptor) getDescriptorWithShortName("Employee")).mappingNamed("firstName");
	}	
	
	private MWCompoundExpression buildSimpleExpression()
	{
		MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
		MWQueryManager qm = desc.getQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) qm.addReadObjectQuery("test-query");
		
		
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression =  mainExpression.addBasicExpression();
		
		//set up the basic expression    address.city equals "" 
		MWOneToOneMapping addressMapping = getAddressMapping();
		MWDirectToFieldMapping cityMapping = getCityMapping();
		
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(cityMapping);
		joinedQueryables.add(addressMapping);
		basicExpression.getFirstArgument().setQueryableArgument(joinedQueryables.iterator());
		
		basicExpression.setSecondArgumentToQueryable();
		((MWQueryableArgument) basicExpression.getSecondArgument()).setQueryableArgument(getFirstNameMapping());
		mainExpression.clearChanges();
		return mainExpression;
	}
	
	private MWCompoundExpression buildParameterExpression()
	{
		MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
		MWQueryManager qm = desc.getQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) qm.addReadObjectQuery("test-query");
		query.addParameter(query.typeNamed("java.lang.String")).setName("name");
		query.addParameter(query.typeNamed("java.lang.int")).setName("age");
		
		
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression =  mainExpression.addBasicExpression();
		
		basicExpression.setSecondArgumentToParameter();
		
		mainExpression.clearChanges();
		return mainExpression;
	}
	
	private MWCompoundExpression buildLiteralExpression()
	{
		MWTableDescriptor desc = (MWTableDescriptor) getDescriptorWithShortName("Employee");
		MWQueryManager qm = desc.getQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) qm.addReadObjectQuery("test-query");
		
		
		MWCompoundExpression mainExpression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = mainExpression.addBasicExpression();
		
		basicExpression.setSecondArgumentToLiteral();
		((MWLiteralArgument) basicExpression.getSecondArgument()).setValue("foo");		
		
		mainExpression.clearChanges();
		return mainExpression;
	}	

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testUndoAddBinaryExpression()
	{
		MWCompoundExpression expression = buildSimpleExpression();		
		MWBasicExpression basicExpression = expression.addBasicExpression();		
		expression.restoreChanges();
		
		assertTrue("The binary expression was not removed when the changes were restored", !CollectionTools.collection(expression.expressions()).contains(basicExpression));
	}

	public void testUndoAddNestedExpression()
	{
		MWCompoundExpression expression = buildSimpleExpression();		
		MWCompoundExpression compoundExpression = expression.addSubCompoundExpression();		
		expression.restoreChanges();
		
		assertTrue("The compound expression was not removed when the changes were restored", !CollectionTools.collection(expression.expressions()).contains(compoundExpression));		
	}

	public void testUndoRemoveBinaryExpression()
	{
		MWCompoundExpression expression = buildSimpleExpression();		
		MWBasicExpression basicExpression = expression.addBasicExpression();		
		expression.clearChanges();
		
		expression.removeExpression(basicExpression);
		expression.restoreChanges();
		
		assertTrue("The binary expression not added back when the changes were restored", CollectionTools.collection(expression.expressions()).contains(basicExpression));
	}

	public void testUndoRemoveCompoundExpression()
	{
		MWCompoundExpression expression = buildSimpleExpression();		
		MWCompoundExpression compoundExpression = expression.addSubCompoundExpression();		
		expression.clearChanges();

		expression.removeExpression(compoundExpression);
		expression.restoreChanges();
				
		assertTrue("The compound expression was not added back when the changes were restored", CollectionTools.collection(expression.expressions()).contains(compoundExpression));		
		
	}

	public void testUndoChangeCompoundExpressionOperatorType()
	{
		MWCompoundExpression expression = buildSimpleExpression();		
		expression.setOperatorType(MWCompoundExpression.NOR);
		expression.restoreChanges();
				
		assertTrue("The Operator type was not set back to AND when the changes were restored", expression.getOperatorType() == MWCompoundExpression.AND);		
		
	}

	public void testUndoChangeBinaryExpressionOperatorType()
	{
		MWCompoundExpression expression = buildSimpleExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();		
		basicExpression.setOperatorType(MWBasicExpression.GREATER_THAN_EQUAL);
		expression.restoreChanges();
				
		assertTrue("The Operator type was not set back to EQUAL when the changes were restored", basicExpression.getOperatorType() == MWBasicExpression.EQUAL);				
	}

	public void testUndoChangeUnaryExpressionOperatorType()
	{
		MWCompoundExpression expression = buildSimpleExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();		
		basicExpression.setOperatorType(MWBasicExpression.IS_NULL);
		MWBasicExpression unaryExpression = (MWBasicExpression) expression.expressions().next();
		expression.clearChanges();
		
		unaryExpression.setOperatorType(MWBasicExpression.NOT_NULL);
		expression.restoreChanges();
				
		assertTrue("The Operator type was not set back to IS_NULL when the changes were restored", unaryExpression.getOperatorType() == MWBasicExpression.IS_NULL);				
		
	}

	public void testUndoChangeBinaryExpressionToUnaryExpression()
	{
		MWCompoundExpression expression = buildSimpleExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();		
		basicExpression.setOperatorType(MWBasicExpression.IS_NULL);

		expression.restoreChanges();
		
		MWBasicExpression restoredExpression = (MWBasicExpression) expression.expressions().next();
		assertTrue("The expression was not restored to it's original state", MWBasicExpression.class.isAssignableFrom(restoredExpression.getClass()));						
	}

	public void testUndoChangeFirstArgumentQueryable()
	{
		MWCompoundExpression expression = buildSimpleExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();		
		
		//set up the basic expression    address.city equals "" 
		MWOneToOneMapping addressMapping = getAddressMapping();
		
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(addressMapping);
		basicExpression.getFirstArgument().setQueryableArgument(joinedQueryables.iterator());

		assertTrue("QueryArgument was not set to address", basicExpression.getFirstArgument().getQueryableArgumentElement().getJoinedQueryableElement() == null);		
		
		expression.restoreChanges();
		
		assertTrue("QueryableArgument was not restored to address.city", basicExpression.getFirstArgument().getQueryableArgumentElement().getJoinedQueryableElement() != null);		
	}

	public void testUndoChangeSecondArgumentQueryable()
	{
		MWCompoundExpression expression = buildSimpleExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();		
		
		//set up the basic expression    address.city equals "" 
		MWOneToOneMapping addressMapping = getAddressMapping();
		MWDirectToFieldMapping cityMapping = getCityMapping();
		
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(cityMapping);
		joinedQueryables.add(addressMapping);
		((MWQueryableArgument) basicExpression.getSecondArgument()).setQueryableArgument(joinedQueryables.iterator());

		assertTrue("QueryArgument was not set to address.city", ((MWQueryableArgument) basicExpression.getSecondArgument()).getQueryableArgumentElement().getJoinedQueryableElement() != null);		
		
		expression.restoreChanges();
		
		assertTrue("QueryableArgument was not restored to firstName",((MWQueryableArgument) basicExpression.getSecondArgument()).getQueryableArgumentElement().getQueryable().getName().equals("firstName"));		
		
	}
	
	public void testUndoChangeSecondArgumentParameter()
	{
		MWCompoundExpression expression = buildParameterExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();
		
		MWQueryParameterArgument argument = (MWQueryParameterArgument) basicExpression.getSecondArgument();
		argument.setQueryParameter(expression.getParentQuery().getParameterNamed("age"));
	
		assertTrue("QueryParameterArgument not set to age", argument.getQueryParameter().getName().equals("age")); 
		expression.restoreChanges();
		assertTrue("QueryParameterArgument not set back to name", argument.getQueryParameter().getName().equals("name")); 
	}

	public void testUndoChangeSecondArgumentLiteralType()
	{
		MWCompoundExpression expression = buildLiteralExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();
		
		MWLiteralArgument argument = ((MWLiteralArgument) basicExpression.getSecondArgument());
		argument.setType(new MWTypeDeclaration(argument, expression.typeNamed("java.lang.Integer")));

		assertTrue("LiteralArgument type was not set to Integer ", argument.getLiteralType().getType().equals(expression.typeNamed("java.lang.Integer"))); 
		expression.restoreChanges();
		assertTrue("LiteralArgument value was not set back to String", argument.getLiteralType().getType().equals(expression.typeNamed("java.lang.String"))); 
		
	}

	public void testUndoChangeSecondArgumentLiteralValue()
	{
		MWCompoundExpression expression = buildLiteralExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();
		
		MWLiteralArgument argument = ((MWLiteralArgument) basicExpression.getSecondArgument());
		argument.setValue("bar");		

		assertTrue("LiteralArgument value was not set to 'bar' ", argument.getValue().equals("bar")); 
		expression.restoreChanges();
		assertTrue("LiteralArgument value was not set back to 'foo'", argument.getValue().equals("foo")); 		
	}

	public void testUndoChangeSecondArgumentType()
	{
		MWCompoundExpression expression = buildParameterExpression();
		MWBasicExpression basicExpression = (MWBasicExpression) expression.expressions().next();
		
		basicExpression.setSecondArgumentToQueryable();
		
		assertTrue("The second argment type was not set to MWQueryableArgument", MWQueryableArgument.class.isAssignableFrom(basicExpression.getSecondArgument().getClass()));

		expression.restoreChanges();
		
		assertTrue("The second argment type was not restored to MWQueryParameterArgument", MWQueryParameterArgument.class.isAssignableFrom(basicExpression.getSecondArgument().getClass()));
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
