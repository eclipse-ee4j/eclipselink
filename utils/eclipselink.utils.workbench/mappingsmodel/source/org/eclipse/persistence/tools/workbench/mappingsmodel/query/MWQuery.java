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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public interface MWQuery extends MWNode {

	String getName();
	void setName(String name);
		String NAME_PROPERTY = "name";
	
	String signature();
	void signatureChanged();
	public static final String SIGNATURE_PROPERTY = "signature";
	
	String queryType();
	Iterator queryTypes();
		String READ_ALL_QUERY = "READ_ALL_QUERY";
		String READ_OBJECT_QUERY = "READ_OBJECT_QUERY";
		String REPORT_QUERY = "REPORT_QUERY";
	MWReadAllQuery asReadAllQuery();	
	MWReadObjectQuery asReadObjectQuery();
	MWReportQuery asReportQuery();	

		
	MWQueryParameter addParameter(MWClass type);
	void removeParameter(MWQueryParameter parameter);
	ListIterator parameters();
	int parametersSize();
	MWQueryParameter getParameter(int index);
	int getParameterIndex(MWQueryParameter parameter);
	MWQueryParameter getParameterNamed(String name);
	Iterator parameterNames();
		String PARAMETER_NAME_PREFIX = "arg";
		String PARAMETERS_LIST = "parameters";
	
	boolean isCacheQueryResults();
	void setCacheQueryResults(boolean cacheQueryResults);
		String CACHE_QUERY_RESULTS_PROPERTY = "cacheQueryResults";
		
	boolean isOuterJoinAllSubclasses();
	void setOuterJoinAllSubclasses(boolean outerJoinAllSubclasses);
		String OUTER_JOIN_ALL_SUBCLASSES_PROPERTY = "outerJoinAllSubclasses";

	int getMaximumRows();
	void setMaximumRows(int maximumRows);
		String MAXIMUM_ROWS_PROPERTY = "maximumRows";
		
	int getFirstResult();
	void setFirstResult(int firstResult);
		String FIRST_RESULT_PROPERTY = "firstResult";	
	
	Integer getQueryTimeout();
	void setQueryTimeout(Integer queryTimeout);
		String QUERY_TIMEOUT_PROPERTY = "queryTimeout";
		Integer QUERY_TIMEOUT_UNDEFINED = new Integer(-1);
		Integer QUERY_TIMEOUT_NO_TIMEOUT = new Integer(0);
		Integer QUERY_TIMEOUT_TIMEOUT = new Integer(1);

	boolean isExclusiveConnection();
	void setExclusiveConnection(boolean exclusiveConnection);
		String EXCLUSIVE_CONNECTION_PROPERTY = "exclusiveConnection";
		
	MWAbstractQuery.LockingModel getLocking();
	void setLocking(MWAbstractQuery.LockingModel lockingModel);
		String LOCK = "Acquire Locks";
		String DEFAULT_LOCK_MODE = "Use Descriptor Setting";
		String LOCK_NOWAIT = "Acquire Locks NO WAIT";
		String NO_LOCK = "Do Not Acquire Locks";
		String LOCK_MODE_PROPERTY = "lockMode";

	MWAbstractQuery.DistinctStateModel getDistinctState();
	void setDistinctState(MWAbstractQuery.DistinctStateModel distinctState);
		String UNCOMPUTED_DISTINCT = "Uncomputed Distinct";
		String USE_DISTINCT = "Use Distinct";
		String DONT_USE_DISTINCT = "Do Not Use Distinct";
		String DISTINCT_STATE_PROPERTY = "distinctState";
	
	MWMappingDescriptor getOwningDescriptor();

	
	DatabaseQuery runtimeQuery();

	void adjustFromRuntime(ObjectLevelReadQuery runtimeQuery);
}
