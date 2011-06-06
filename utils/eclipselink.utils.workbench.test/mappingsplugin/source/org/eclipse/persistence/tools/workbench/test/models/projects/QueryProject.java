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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;


public class QueryProject extends RelationalTestProject 
{
	public QueryProject() 
	{
		super();
	}
	
	public static MWRelationalProject emptyProject() 
	{
		MWRelationalProject project = new MWRelationalProject("Query", spiManager(), mySqlPlatform());
		

		// Defaults policy  
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(100);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
		project.getDefaultsPolicy().setMethodAccessing(false);
		
		return project;
	} 
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	protected Integer getDefaultQueryTimeout() {
		return MWAbstractRelationalReadQuery.QUERY_TIMEOUT_UNDEFINED;
	}
	
	protected String getDefaultQueryLockMode() {
		return MWAbstractRelationalReadQuery.DEFAULT_LOCK_MODE;
	}

	//basic query with default options set
	private void createQuery1()
	{
	    MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery) getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery1");		
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());
	}
	
	//query with some non-default options set 
	private void createQuery2()
	{
	    MWRelationalReadAllQuery query2 = (MWRelationalReadAllQuery)getEmployeeDescriptor().getQueryManager().addReadAllQuery("myQuery2");
               
		query2.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
     	((MWStringQueryFormat)query2.getQueryFormat()).setQueryString("I am a bad SQL query");   	
     	
		//add parameters
     	MWQueryParameter queryParameter1 = query2.addParameter(typeFor(java.lang.String.class));
     	queryParameter1.setName("name");
     	MWQueryParameter queryParameter2 = query2.addParameter(typeFor(java.lang.Integer.class));
     	queryParameter2.setName("age");
     	
 		query2.setRefreshIdentityMapResult(true);	
		query2.setCacheStatement(TriStateBoolean.TRUE);
		query2.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_ONLY);
		query2.setLocking(MWAbstractRelationalReadQuery.LOCK);
		query2.setDistinctState(MWAbstractRelationalReadQuery.USE_DISTINCT);
		query2.setQueryTimeout(new Integer(2));
		query2.setMaximumRows(11);

	}
	
	//more non-default options set and a parameter based expression
	private void createQuery3()
	{
	    MWAbstractRelationalReadQuery query3 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery3");
     	query3.setCacheQueryResults(true);
		query3.setRefreshIdentityMapResult(true);	
		query3.setCacheStatement(TriStateBoolean.FALSE);
		query3.setBindAllParameters(TriStateBoolean.TRUE);
		query3.setCacheUsage(MWRelationalReadQuery.CONFORM_RESULTS_IN_UNIT_OF_WORK);
		query3.setLocking(MWAbstractRelationalReadQuery.LOCK_NOWAIT);
		query3.setDistinctState(MWAbstractRelationalReadQuery.DONT_USE_DISTINCT);
		query3.setInMemoryQueryIndirectionPolicy(MWRelationalReadQuery.IGNORE_EXCEPTION_RETURN_NOT_CONFORMED);
		query3.setQueryTimeout(MWAbstractRelationalReadQuery.QUERY_TIMEOUT_NO_TIMEOUT);
		query3.setMaximumRows(14);

		//add parameters
     	MWQueryParameter queryParameter1 = query3.addParameter(typeFor(java.lang.String.class));
     	queryParameter1.setName("name");
     	MWQueryParameter queryParameter2 = query3.addParameter(typeFor(java.lang.Integer.class));
     	queryParameter2.setName("id");
     	
     	//create expression
     	// firstName equals name  AND  id Less Than Equals id    	
     	MWCompoundExpression baseExpression = ((MWExpressionQueryFormat)query3.getQueryFormat()).getExpression();

     	MWBasicExpression basicExpression1 = baseExpression.addBasicExpression();     	
     	basicExpression1.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("firstName"));
     	basicExpression1.setOperatorType(MWBasicExpression.GREATER_THAN_EQUAL);
     	basicExpression1.setSecondArgumentToParameter();
     	((MWQueryParameterArgument)basicExpression1.getSecondArgument()).setQueryParameter(queryParameter1);
     	
     	
     	MWBasicExpression basicExpression2 = baseExpression.addBasicExpression();     	
     	basicExpression2.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("id"));
     	basicExpression2.setOperatorType(MWBasicExpression.LESS_THAN_EQUAL);
     	basicExpression2.setSecondArgumentToParameter();
     	((MWQueryParameterArgument)basicExpression2.getSecondArgument()).setQueryParameter(queryParameter2);		
	}
	
	private void createQuery4()
	{
	    MWRelationalReadAllQuery query4 = (MWRelationalReadAllQuery) getEmployeeDescriptor().getQueryManager().addReadAllQuery("myQuery4");
    	query4.addParameter(typeFor(java.lang.Integer.class));
		query4.setMaintainCache(false);
 		query4.setCacheStatement(TriStateBoolean.TRUE);
		query4.setBindAllParameters(TriStateBoolean.TRUE);
		query4.setCacheUsage(MWRelationalReadQuery.DO_NOT_CHECK_CACHE);
		query4.setDistinctState(MWAbstractRelationalReadQuery.DONT_USE_DISTINCT);
		query4.setInMemoryQueryIndirectionPolicy(MWRelationalReadQuery.TRIGGER_INDIRECTION);
		query4.setQueryTimeout(MWAbstractRelationalReadQuery.QUERY_TIMEOUT_TIMEOUT);
		query4.setMaximumRows(1);
		query4.setLocking(getDefaultQueryLockMode());
        addAttributesToQuery4(query4);
    }
    
    protected void addAttributesToQuery4(MWRelationalReadAllQuery query) {
        query.addJoinedItem(getEmployeeDescriptor().mappingNamed("manager"));
        query.addOrderingItem(getEmployeeDescriptor().mappingNamed("lastName"));
        query.addOrderingItem(getEmployeeDescriptor().mappingNamed("firstName")).setAscending(false);
        query.addBatchReadItem(getEmployeeDescriptor().mappingNamed("phoneNumbers"));		
	}
    
	private void createQuery5()
	{
	    MWAbstractRelationalReadQuery query5 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery5");
		query5.setMaintainCache(false);
		query5.setRefreshIdentityMapResult(true);
		query5.setCacheStatement(TriStateBoolean.FALSE);
		query5.setBindAllParameters(TriStateBoolean.FALSE);
		query5.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_EXACT_PRIMARY_KEY);
		query5.setDistinctState(MWAbstractRelationalReadQuery.USE_DISTINCT);
		query5.setInMemoryQueryIndirectionPolicy(MWRelationalReadQuery.IGNORE_EXCEPTION_RETURN_CONFORMED);
		query5.setQueryTimeout(new Integer(2));
		query5.setMaximumRows(0);
		query5.setLocking(getDefaultQueryLockMode());

		MWCompoundExpression expression = ((MWExpressionQueryFormat)query5.getQueryFormat()).getExpression();
		expression.setOperatorType(MWCompoundExpression.OR);
		
		MWBasicExpression basicExpression = expression.addBasicExpression();
		basicExpression.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("firstName"));
		((MWLiteralArgument) basicExpression.getSecondArgument()).setValue("Karen");
		basicExpression.setOperatorType(MWBasicExpression.LESS_THAN);	
		
		MWBasicExpression basicExpression2 = expression.addBasicExpression();
		basicExpression2.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
		((MWLiteralArgument) basicExpression2.getSecondArgument()).setValue("moore");
		basicExpression2.setOperatorType(MWBasicExpression.EQUALS_IGNORE_CASE);	
		
	}
	
	private void createQuery6()
	{
	    MWAbstractRelationalReadQuery query6 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery6");
		query6.setRefreshRemoteIdentityMapResult(true);
		query6.setBindAllParameters(TriStateBoolean.FALSE);
		query6.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_THEN_DATABASE);
		query6.setDistinctState(MWAbstractRelationalReadQuery.USE_DISTINCT);
		query6.setInMemoryQueryIndirectionPolicy(MWRelationalReadQuery.IGNORE_EXCEPTION_RETURN_NOT_CONFORMED);
		query6.setQueryTimeout(MWAbstractRelationalReadQuery.QUERY_TIMEOUT_NO_TIMEOUT);
		query6.setMaximumRows(7);
		query6.setLocking(getDefaultQueryLockMode());
		
		MWCompoundExpression expression = ((MWExpressionQueryFormat)query6.getQueryFormat()).getExpression();
		expression.setOperatorType(MWCompoundExpression.NAND);
		
		MWBasicExpression basicExpression = expression.addBasicExpression();
		basicExpression.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("firstName"));
		((MWLiteralArgument) basicExpression.getSecondArgument()).setValue("Karen");
		basicExpression.setOperatorType(MWBasicExpression.GREATER_THAN);	
		
		MWBasicExpression basicExpression2 = expression.addBasicExpression();
		basicExpression2.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
		((MWLiteralArgument) basicExpression2.getSecondArgument()).setValue("moore");
		basicExpression2.setOperatorType(MWBasicExpression.NOT_LIKE);	
	}
	
	//a query using joining   manager.lastName equals "Moore"
	private void createQuery7()
	{
	    MWAbstractRelationalReadQuery query7 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery7");
		query7.setMaintainCache(false);
		query7.setQueryTimeout(getDefaultQueryTimeout());
		query7.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query7.setLocking(getDefaultQueryLockMode());
		
		MWCompoundExpression expression = ((MWExpressionQueryFormat)query7.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
		basicExpression.setOperatorType(MWBasicExpression.NOT_EQUAL);
		
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		MWQueryable queryableObject = (MWDirectToFieldMapping) getEmployeeDescriptor().mappingNamed("lastName");
		
		List<Object> joinedQueryables = new ArrayList<Object>();
		joinedQueryables.add(queryableObject);
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("manager"));
		firstArgument.setQueryableArgument(joinedQueryables.iterator());
	}
	private void createQuery8()
	{
	    MWAbstractRelationalReadQuery query8 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery8");
		query8.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query8.setQueryTimeout(getDefaultQueryTimeout());
		query8.setLocking(getDefaultQueryLockMode());
	
		MWCompoundExpression expression = ((MWExpressionQueryFormat)query8.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
		
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		firstArgument.setQueryableArgument(getEmployeeDescriptor().mappingNamed("firstName"));
				
		((MWLiteralArgument)basicExpression.getSecondArgument()).setValue("Moore");
		basicExpression.setOperatorType(MWBasicExpression.LIKE);	
		
	}
	
	//a query uses joining and a sub compound expression
	private void createQuery9()
	{
	    MWAbstractRelationalReadQuery query6 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery9");
		query6.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query6.setQueryTimeout(getDefaultQueryTimeout());
		query6.setLocking(getDefaultQueryLockMode());
		
		MWCompoundExpression expression = ((MWExpressionQueryFormat)query6.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
		
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		firstArgument.setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
				
		((MWLiteralArgument) basicExpression.getSecondArgument()).setValue("Moore");
		basicExpression.setOperatorType(MWBasicExpression.LIKE_IGNORE_CASE);	
		
		
		MWCompoundExpression compoundExpression = expression.addSubCompoundExpression();
		compoundExpression.setOperatorType(MWCompoundExpression.OR);
		MWQueryableArgument firstArgument2 = ((MWBasicExpression) compoundExpression.getExpression(0)).getFirstArgument();

	
		MWQueryable queryableObject = (MWDirectToFieldMapping) getEmployeeDescriptor().mappingNamed("firstName");
		List joinedQueryables = new ArrayList();
		joinedQueryables.add(queryableObject);
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("manager"));
		firstArgument2.setQueryableArgument(joinedQueryables.iterator());
		
		MWBasicExpression basicExpression2 = compoundExpression.addBasicExpression();
		basicExpression2.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
	}	
	//a query using joining, allowsNull, and a sub compound expression
	private void createQuery10()
	{
	    MWAbstractRelationalReadQuery query6 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery10");
		query6.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query6.setQueryTimeout(getDefaultQueryTimeout());
		query6.setLocking(getDefaultQueryLockMode());

		MWCompoundExpression expression = ((MWExpressionQueryFormat)query6.getQueryFormat()).getExpression();
		expression.setOperatorType(MWCompoundExpression.NAND);
		MWBasicExpression basicExpression = expression.addBasicExpression();
		
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		firstArgument.setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
				
		((MWLiteralArgument) basicExpression.getSecondArgument()).setValue("Moore");
		basicExpression.setOperatorType(MWBasicExpression.LESS_THAN);	
		
		
		MWCompoundExpression compoundExpression = expression.addSubCompoundExpression();
		compoundExpression.setOperatorType(MWCompoundExpression.NOR);
		MWQueryableArgument firstArgument2 = ((MWBasicExpression) compoundExpression.getExpression(0)).getFirstArgument();
		((MWLiteralArgument)((MWBasicExpression)compoundExpression.getExpression(0)).getSecondArgument()).setValue("Roger");

	
		//manager.phonedNumbers.owner.firstName
		MWQueryable queryableObject = (MWDirectToFieldMapping) getEmployeeDescriptor().mappingNamed("firstName");
		List<Object> joinedQueryables = new ArrayList<Object>();
		List<Boolean> allowsNullList = new ArrayList<Boolean>();
		joinedQueryables.add(queryableObject);
		allowsNullList.add(new Boolean(false));
		joinedQueryables.add(getPhoneNumberDescriptor().mappingNamed("owner"));
		allowsNullList.add(new Boolean(true));
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("phoneNumbers"));
		allowsNullList.add(new Boolean(false));
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("manager"));
		allowsNullList.add(new Boolean(true));
		firstArgument2.setQueryableArgument(joinedQueryables.iterator(), allowsNullList.iterator());


		MWBasicExpression basicExpression2 = compoundExpression.addBasicExpression();
		basicExpression2.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("lastName"));
	}

	//a query with unary operators
	private void createQuery11()
	{
	    MWAbstractRelationalReadQuery query7 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery11");
		query7.setMaintainCache(false);
		query7.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query7.setQueryTimeout(getDefaultQueryTimeout());
		query7.setLocking(getDefaultQueryLockMode());
		
		MWCompoundExpression expression = ((MWExpressionQueryFormat)query7.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
		basicExpression.setOperatorType(MWBasicExpression.NOT_NULL);
		
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		firstArgument.setQueryableArgument(getEmployeeDescriptor().mappingNamed("manager"));
		 
		 
		MWBasicExpression basicExpression2 = expression.addBasicExpression();
		basicExpression2.setOperatorType(MWBasicExpression.IS_NULL);
		
		MWQueryableArgument firstArgument2 = basicExpression2.getFirstArgument();
		firstArgument2.setQueryableArgument(getEmployeeDescriptor().mappingNamed("phoneNumbers"));
	}
		
	private void createQuery12()
	{
	    MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery12");
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());

		MWCompoundExpression expression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
	
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		MWQueryable queryableObject = (MWDirectToFieldMapping) getPhoneNumberDescriptor().mappingNamed("areaCode");
		List<Object> joinedQueryables = new ArrayList<Object>();
		joinedQueryables.add(queryableObject);
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("phoneNumbers"));
		firstArgument.setQueryableArgument(joinedQueryables.iterator());
	}	
	//expBuilder.anyOf("phoneNumbers").get("areaCode").equals("123").and(expBuilder.anyOf(phoneNumbers").get("number").like("848"));
	private void createQuery13()
	{
	    MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadObjectQuery("myQuery13");
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());

		MWCompoundExpression expression = ((MWExpressionQueryFormat)query.getQueryFormat()).getExpression();
		MWBasicExpression basicExpression = expression.addBasicExpression();
	
		MWQueryableArgument firstArgument = basicExpression.getFirstArgument();
		MWQueryable queryableObject = (MWDirectToFieldMapping) getPhoneNumberDescriptor().mappingNamed("areaCode");
		List<Object> joinedQueryables = new ArrayList<Object>();
		joinedQueryables.add(queryableObject);
		joinedQueryables.add(getEmployeeDescriptor().mappingNamed("phoneNumbers"));
		firstArgument.setQueryableArgument(joinedQueryables.iterator());
		
		MWLiteralArgument secondArgument = (MWLiteralArgument) basicExpression.getSecondArgument();
		secondArgument.setValue("123");
		
		MWBasicExpression basicExpression2 = expression.addBasicExpression();
		basicExpression2.setOperatorType(MWBasicExpression.LIKE);
		MWQueryableArgument firstArgument2 = basicExpression2.getFirstArgument();
		MWQueryable queryableObject2 = (MWDirectToFieldMapping) getPhoneNumberDescriptor().mappingNamed("number");
		List<Object> joinedQueryables2 = new ArrayList<Object>();
		joinedQueryables2.add(queryableObject2);
		joinedQueryables2.add(getEmployeeDescriptor().mappingNamed("phoneNumbers"));
		firstArgument2.setQueryableArgument(joinedQueryables2.iterator());
		
		MWLiteralArgument secondArgument2 = (MWLiteralArgument) basicExpression2.getSecondArgument();
		secondArgument2.setValue("848");
		
	}	
	private void createQuery14()
	{
	    MWAbstractRelationalReadQuery query14 = (MWAbstractRelationalReadQuery)getEmployeeDescriptor().getQueryManager().addReadAllQuery("myQuery14");
		query14.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query14.setQueryTimeout(getDefaultQueryTimeout());
		query14.setLocking(getDefaultQueryLockMode());
	     	
		//add parameters
     	
     	MWCompoundExpression expression = query14.getQueryFormat().getExpression();
     	expression.setOperatorType(MWCompoundExpression.NAND);
		MWBasicExpression basicExpression = expression.addBasicExpression();
		basicExpression.getFirstArgument().setQueryableArgument(getEmployeeDescriptor().mappingNamed("firstName"));
		
	}

    protected void createReportQuery() {
        MWReportQuery reportQuery = ((MWRelationalQueryManager) getEmployeeDescriptor().getQueryManager()).addReportQuery("reportQuery");
        MWQueryable managerQueryable = getEmployeeDescriptor().mappingNamed("manager");
        MWQueryable firstNameQueryable = getEmployeeDescriptor().mappingNamed("firstName");
        
        Collection<MWQueryable> queryables = new Vector<MWQueryable>();
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);
        
        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.AVERAGE_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);
        
        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.COUNT_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);

        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.DISTINCT_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);

        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.MAXIMUM_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);

        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.MINIMUM_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);

        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.SUM_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);

        reportQuery.addAttributeItem("managerName", queryables.iterator()).setFunction(MWReportAttributeItem.VARIANCE_FUNCTION);
        queryables.add(firstNameQueryable);
        queryables.add(managerQueryable);
    }

    protected void createReportQuery2() {
        MWReportQuery reportQuery = ((MWRelationalQueryManager) getEmployeeDescriptor().getQueryManager()).addReportQuery("reportQuery2");
        reportQuery.setRetrievePrimaryKeys(MWReportQuery.FULL_PRIMARY_KEY);
        reportQuery.setReturnChoice(MWReportQuery.RETURN_SINGLE_ATTRIBUTE);
        
        reportQuery.addOrderingItem(getEmployeeDescriptor().mappingNamed("firstName"));
        
        reportQuery.addGroupingItem(getEmployeeDescriptor().mappingNamed("lastName"));
    }
    
    protected void createReportQuery3() {
        MWReportQuery reportQuery = ((MWRelationalQueryManager) getEmployeeDescriptor().getQueryManager()).addReportQuery("reportQuery3");
        reportQuery.setCacheStatement(TriStateBoolean.TRUE);
        reportQuery.setBindAllParameters(TriStateBoolean.TRUE);
        reportQuery.setPrepare(false);
        reportQuery.setRetrievePrimaryKeys(MWReportQuery.FIRST_PRIMARY_KEY);
        reportQuery.setReturnChoice(MWReportQuery.RETURN_SINGLE_RESULT);
    }
 
    protected void createReportQuery4() {
        MWReportQuery reportQuery = ((MWRelationalQueryManager) getEmployeeDescriptor().getQueryManager()).addReportQuery("reportQuery4");
        reportQuery.setRetrievePrimaryKeys(MWReportQuery.FIRST_PRIMARY_KEY);
        reportQuery.setReturnChoice(MWReportQuery.RETURN_SINGLE_VALUE);
    }
    
	public MWTableDescriptor getEmployeeDescriptor()
	{
		return tableDescriptorWithShortName("Employee");		
	}
	
	public MWTableDescriptor getPhoneNumberDescriptor()
	{
		return tableDescriptorWithShortName("PhoneNumber");
	}

	public MWAggregateDescriptor getEmploymentPeriodDescriptor()
	{
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed(org.eclipse.persistence.tools.workbench.test.models.query.EmploymentPeriod.class.getName());		
	}

	@Override
	protected void initializeDatabase() 
	{
		super.initializeDatabase();
		initializeSequenceTable();
		initializeEmployeeTable();
		initializePhoneTable();
		
		MWTable employeeTable = database().tableNamed("EMPLOYEE");
		MWTable phoneTable = database().tableNamed("PHONE");
		
		addReferenceOnDB("EMPLOYEE_EMPLOYEE", employeeTable, employeeTable, "MANAGER_ID", "EMP_ID");
		addReferenceOnDB("PHONE_EMPLOYEE", phoneTable, employeeTable, "EMP_ID", "EMP_ID");
	}
	
	@Override
	protected void initializeDescriptors() 
	{
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.query.Employee");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.query.EmploymentPeriod");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.query.PhoneNumber");
				
		initializeEmploymentPeriodDescriptor();
		initializePhoneNumberDescriptor();
		initializeEmployeeDescriptor();
	}

	public void initializeEmployeeDescriptor()
	{
		
		MWTableDescriptor employeeDescriptor = getEmployeeDescriptor();
		MWTable employeeTable = tableNamed("EMPLOYEE");
		employeeDescriptor.setPrimaryTable(employeeTable);
			
		//direct to field mappings
		addDirectMapping(employeeDescriptor, "firstName", employeeTable, "F_NAME");
		addDirectMapping(employeeDescriptor, "id", employeeTable, "EMP_ID");
		addDirectMapping(employeeDescriptor, "lastName", employeeTable, "L_NAME");

		//manager
		MWOneToOneMapping managerMapping = employeeDescriptor.addOneToOneMapping(employeeDescriptor.getMWClass().attributeNamed("manager"));
		managerMapping.setReferenceDescriptor(employeeDescriptor);
		managerMapping.setReference(employeeDescriptor.getPrimaryTable().referenceNamed("EMPLOYEE_EMPLOYEE"));
	
		//phoneNumbers
		MWOneToManyMapping phoneNumberMapping = employeeDescriptor.addOneToManyMapping(employeeDescriptor.getMWClass().attributeNamed("phoneNumbers"));
		phoneNumberMapping.setReferenceDescriptor(getPhoneNumberDescriptor());
		phoneNumberMapping.setReference(getPhoneNumberDescriptor().getPrimaryTable().referenceNamed("PHONE_EMPLOYEE"));
		phoneNumberMapping.setPrivateOwned(true);
	
		//aggregate mappings
		MWAggregateMapping periodMapping = employeeDescriptor.addAggregateMapping(employeeDescriptor.getMWClass().attributeNamed("period"));
		periodMapping.setReferenceDescriptor(getEmploymentPeriodDescriptor());
		
		Iterator fieldAssociations = CollectionTools.sort(periodMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"END_DATE", "START_DATE"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(employeeTable.columnNamed(fieldNames[i]));
		}
		
		//queries
		createQuery1();
		createQuery2();
		createQuery3();
		createQuery4();
		createQuery5();
		createQuery6();
		createQuery7(); //problem with deployment xml : writes out "" for constant expression but reads in null
						//bug #3183462 in the runtime needs to be fixed for this to work.  \
						//The mw also needs to allow either null or "" when specifying the expression
		createQuery8();		
		createQuery9(); //problem with deployment xml : writes out "" for constant expression but reads in null
		createQuery10();//problem with deployment xml : writes out "" for constant expression but reads in null
		createQuery11();
		createQuery12();//problem with deployment xml : writes out "" for constant expression but reads in null
		createQuery13();//problem with deployment xml : bug number 2679509 explains this
		createQuery14();//problem with deployment xml : writes out "" for constant expression but reads in null
        
        createReportQuery();
        createReportQuery2();
        createReportQuery3();
        createReportQuery4();
	}
	
	public void initializeEmploymentPeriodDescriptor()
	{
		MWAggregateDescriptor periodDescriptor = (MWAggregateDescriptor)  (getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.query.EmploymentPeriod"));
		
		addDirectMapping(periodDescriptor, "endDate");
		addDirectMapping(periodDescriptor, "startDate");
	}
	
	public void initializePhoneNumberDescriptor()
	{
		MWTableDescriptor phoneNumberDescriptor = getPhoneNumberDescriptor();
		MWTable phoneTable = getProject().getDatabase().tableNamed("PHONE");
		phoneNumberDescriptor.setPrimaryTable(phoneTable);
		
		phoneNumberDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        phoneNumberDescriptor.getCachingPolicy().setCacheSize(100);

        phoneNumberDescriptor.getTransactionalPolicy().setDescriptorAlias("foo");

        MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)phoneNumberDescriptor.getLockingPolicy();
        lockingPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		lockingPolicy.setOptimisticVersionLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP);
		lockingPolicy.setVersionLockField(phoneTable.columnNamed("P_NUMBER"));
		lockingPolicy.setStoreInCache(true);
		
		// dtf's
		addDirectMapping(phoneNumberDescriptor, "areaCode", phoneTable, "AREA_CODE");
		
		MWDirectToFieldMapping numberMapping = addDirectMapping(phoneNumberDescriptor, "number", phoneTable, "P_NUMBER");	
		numberMapping.setReadOnly(true);
		
		addDirectMapping(phoneNumberDescriptor, "type", phoneTable, "TYPE");
	
		MWOneToOneMapping ownerMapping = phoneNumberDescriptor.addOneToOneMapping(phoneNumberDescriptor.getMWClass().attributeNamed("owner"));
		ownerMapping.setReferenceDescriptor(getEmployeeDescriptor());
		ownerMapping.setReference(phoneTable.referenceNamed("PHONE_EMPLOYEE"));
		
		
		MWRelationalQueryManager queryManager = (MWRelationalQueryManager)phoneNumberDescriptor.getQueryManager();
		queryManager.setDeleteSQLString("Deleting stuff");
		queryManager.setInsertSQLString("Inserting stuff");
		queryManager.setReadAllSQLString("Here's how to Read all");
		queryManager.setReadObjectSQLString("Read an object");
		queryManager.setUpdateSQLString("Updating sql");
	}
	
	public void initializePhoneTable()
	{
		MWTable phoneTable = database().addTable("PHONE");
		
		addField(phoneTable,"AREA_CODE", "varchar", 3);
		addPrimaryKeyField(phoneTable,"EMP_ID", "integer");
		addField(phoneTable,"P_NUMBER", "varchar", 7);
		addPrimaryKeyField(phoneTable,"TYPE", "varchar");
	}
	public void initializeEmployeeTable()
	{	
		MWTable employeeTable = database().addTable("EMPLOYEE");
		
		addField(employeeTable,"ADDR_ID", "integer");
		addPrimaryKeyField(employeeTable,"EMP_ID", "integer");
		addField(employeeTable,"END_DATE", "date");
		//addNewField(employeeTable,"END_TIME", "date");
		addField(employeeTable,"F_NAME", "varchar", 20);
		//addNewField(employeeTable,"GENDER", "decimal", 20);
		addField(employeeTable,"L_NAME", "varchar", 20);
		addField(employeeTable,"MANAGER_ID", "integer");
		addField(employeeTable,"START_DATE", "date");
		//addNewField(employeeTable,"START_TIME", "date");
		//addNewField(employeeTable,"VERSION", "integer");		
	}	
	
	@Override
	protected void initializeProject()
	{
		super.initializeProject();
		
		((MWRelationalProjectDefaultsPolicy)getProject().getDefaultsPolicy()).setQueriesBindAllParameters(true);
		((MWRelationalProjectDefaultsPolicy)getProject().getDefaultsPolicy()).setQueriesCacheAllStatements(false);
	}
	
}
