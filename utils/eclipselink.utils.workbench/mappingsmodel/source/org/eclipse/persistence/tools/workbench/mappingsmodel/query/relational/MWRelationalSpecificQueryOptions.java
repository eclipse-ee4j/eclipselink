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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public final class MWRelationalSpecificQueryOptions 
	extends MWModel
	implements MWRelationalQuery {

	/**
	 * Cache the prepared statement, this requires full parameter binding as well.
	 */
	private volatile TriStateBoolean cacheStatement;

	/**
	 * Bind all arguments to the SQL statement.
	 */
	private volatile TriStateBoolean bindAllParameters;

	private volatile boolean prepare;


	/**
	 * Query format
	 */
	private volatile MWQueryFormat queryFormat;

	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalSpecificQueryOptions.class);	
		
		XMLCompositeObjectMapping formatMapping = new XMLCompositeObjectMapping();
		formatMapping.setAttributeName("queryFormat");
		formatMapping.setReferenceClass(MWQueryFormat.class);
		formatMapping.setXPath("format");
		descriptor.addMapping(formatMapping);
			
		XMLDirectMapping bindAllParameters =
			(XMLDirectMapping) descriptor.addDirectMapping(
				"bindAllParameters",
				"getBindAllParametersForTopLink",
				"setBindAllParametersForTopLink",
				"bind-all-parameters/text()");
		bindAllParameters.setNullValue(null);
		
		XMLDirectMapping cacheStatement =
			(XMLDirectMapping) descriptor.addDirectMapping(
				"cacheStatement",
				"getCacheStatementForTopLink",
				"setCacheStatementForTopLink",
				"cache-statement/text()");
		cacheStatement.setNullValue(null);

		((XMLDirectMapping) descriptor.addDirectMapping("prepare", "prepare/text()")).setNullValue(Boolean.TRUE);

		return descriptor;
	}	

	
	private MWRelationalSpecificQueryOptions() {
		super();
	}

	MWRelationalSpecificQueryOptions(MWRelationalQuery parent) {
		super(parent);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.cacheStatement = TriStateBoolean.UNDEFINED;
		this.bindAllParameters = TriStateBoolean.TRUE;
		this.prepare = true;
		this.queryFormat = new MWExpressionQueryFormat(this);
	}
	
	protected void addChildrenTo(List list) {
		super.addChildrenTo(list);
		list.add(this.queryFormat);
	}
	
	public MWAbstractQuery getQuery() {
		return (MWAbstractQuery) getParent();
	}
	
	void initializeFrom(MWRelationalSpecificQueryOptions oldOptions) {
		setCacheStatement(oldOptions.isCacheStatement());
		setBindAllParameters(oldOptions.isBindAllParameters());
		setPrepare(oldOptions.isPrepare());
		setQueryFormat(oldOptions.getQueryFormat());
		getQueryFormat().setParent(this);	
	}
	
	
	// ************ Bind All Parameters ************	
	
	public TriStateBoolean isBindAllParameters() {
		return this.bindAllParameters;
	}
	
	public void setBindAllParameters(TriStateBoolean bindAllParameters) {
		TriStateBoolean oldBindAllParameters = isBindAllParameters();
		this.bindAllParameters = bindAllParameters;
		firePropertyChanged(BIND_ALL_PARAMETERS_PROPERTY, oldBindAllParameters, bindAllParameters);
	}

	
	// ************ Cache Statement ************	
	
	public TriStateBoolean isCacheStatement() {
		return this.cacheStatement;
	}
	
	public void setCacheStatement(TriStateBoolean cacheStatement) {
		TriStateBoolean oldCacheStatement = isCacheStatement();
		this.cacheStatement = cacheStatement;
		firePropertyChanged(CACHE_STATEMENT_PROPERTY, oldCacheStatement, cacheStatement);
	}

	
	// ************ prepare ************	
	
	public boolean isPrepare() {
		return this.prepare;
	}
	
	public void setPrepare(boolean prepare) {
		boolean oldPrepare = isPrepare();
		this.prepare = prepare;
		firePropertyChanged(PREPARE_PROPERTY, oldPrepare, prepare);
	}

	
	// ************ query format ************
	
	public MWQueryFormat getQueryFormat() {
		return this.queryFormat;
	}

	public String getQueryFormatType() {
		return this.queryFormat.getType();
	}

	public void setQueryFormatType(String queryFormat) {
		Object oldValue = getQueryFormatType();
		if (oldValue == queryFormat) {
			return;
		}
		if (queryFormat == EXPRESSION_FORMAT) {
			setQueryFormatToExpression();
		}
		else if (queryFormat == AUTO_GENERATED_FORMAT) {
			setQueryFormatToAutoGenerated();
		}
		else if (queryFormat == SQL_FORMAT) {
			setQueryFormatToSql();
		}
		else if (queryFormat == EJBQL_FORMAT) {
			setQueryFormatToEjbql();
		}
		else if (queryFormat == STORED_PROCEDURE_FORMAT) {
			setQueryFormatToStoredProcedure();
		}
		else {
			throw new IllegalArgumentException("queryFormatType must be set to : MWQuery.EXPRESSION_FORMAT, MWQuery.AUTO_GENERATED_FORMAT, MWQuery.SQL_FORMAT, MWQuery.STORED_PROCEDURE_FORMAT, or MWQuery.EJBQL_FORMAT");
		}
		
		firePropertyChanged(QUERY_FORMAT_TYPE_PROPERTY, oldValue, getQueryFormatType());
	}
	

	private void setDefaultQueryFormat() {
		setQueryFormat(new MWExpressionQueryFormat(this));
	}
		
	MWAutoGeneratedQueryFormat setQueryFormatToAutoGenerated() {
		MWAutoGeneratedQueryFormat queryFormat = new MWAutoGeneratedQueryFormat(this);
		setQueryFormat(queryFormat);
		return queryFormat;
	}	

    void setQueryFormatToEjbql() {
		MWStringQueryFormat queryFormat = new MWEJBQLQueryFormat(this);
		setQueryFormat(queryFormat);
        ((MWRelationalQuery) getParent()).formatSetToEjbql();
	}	

    void setQueryFormatToExpression() {
		MWExpressionQueryFormat queryFormat = new MWExpressionQueryFormat(this);
		setQueryFormat(queryFormat);
	}
    
    void setQueryFormatToStoredProcedure() {
    	MWStoredProcedureQueryFormat queryFormat = new MWStoredProcedureQueryFormat(this);
    	setQueryFormat(queryFormat);
    }

    void setQueryFormatToSql() {
		MWStringQueryFormat queryFormat = new MWSQLQueryFormat(this);
		setQueryFormat(queryFormat);
        ((MWRelationalQuery) getParent()).formatSetToSql();
	}
		
    private void setQueryFormat(MWQueryFormat queryFormat) {
		this.queryFormat = queryFormat;
	}
	
	public void notifyExpressionsToRecalculateQueryables() {
		if (getQueryFormat().getExpression() != null) {
			getQueryFormat().getExpression().recalculateQueryables();
		}	
	}

	public MWRelationalSpecificQueryOptions getRelationalOptions() {
		return this;
	}
	   
    public void formatSetToEjbql() {
        // do nothing       
    }
    
    public void formatSetToSql() {
        // do nothing       
    }
    
    
	//	 ************* Problem Handling **************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		checkCachesStatementButDoesNotBindParameters(currentProblems);
	}

	private void checkCachesStatementButDoesNotBindParameters(List currentProblems) {
		if (isCacheStatement().isTrue()) {
			if (isBindAllParameters().isUndefined()) 
			{
				MWRelationalProject project = (MWRelationalProject)getProject();
				if(!((MWRelationalProjectDefaultsPolicy)project.getDefaultsPolicy()).shouldQueriesBindAllParameters()) {
					currentProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_QUERY_CACHES_STATEMENT_WITHOUT_BINDING_PARAMETERS, 
							 getQuery().getName()));
				}
			}
			else if (isBindAllParameters().isFalse()) {
				currentProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_QUERY_CACHES_STATEMENT_WITHOUT_BINDING_PARAMETERS, 
						 getQuery().getName()));
			}
		}
	}
	

	// ***************** runtime conversion ***************
	
	void adjustRuntimeQuery(DatabaseQuery runtimeQuery) {
		
		runtimeQuery.setShouldPrepare(isPrepare());

		if (!isBindAllParameters().isUndefined()) {
			runtimeQuery.setShouldBindAllParameters(isBindAllParameters().booleanValue());
		}
		if (!isCacheStatement().isUndefined()) {
			runtimeQuery.setShouldCacheStatement(isCacheStatement().booleanValue());
		}
		getQueryFormat().convertToRuntime(runtimeQuery);
	}


	public void adjustFromRuntime(DatabaseQuery runtimeQuery) {		
		setPrepare(runtimeQuery.shouldPrepare());
		
		if (!runtimeQuery.shouldIgnoreBindAllParameters())
			setBindAllParameters(new TriStateBoolean(runtimeQuery.shouldBindAllParameters()));
		else
			setBindAllParameters(TriStateBoolean.UNDEFINED);
		if (!runtimeQuery.shouldIgnoreCacheStatement())
			setCacheStatement(new TriStateBoolean(runtimeQuery.shouldCacheStatement()));
		else
			setCacheStatement(TriStateBoolean.UNDEFINED);
		
		//must initialize parameters before this in case there is an expression using a parameter
		setDefaultQueryFormat();		
		if (runtimeQuery.getSQLString() != null) {
			setQueryFormatToSql();
		}
		else if (runtimeQuery.getEJBQLString() != null) {
			setQueryFormatToEjbql();
		}
		
		getQueryFormat().convertFromRuntime(runtimeQuery);
	}
	

	
	// ************ TopLink only methods **************
	
	private Boolean getBindAllParametersForTopLink() {
		return this.bindAllParameters.getValue();
	}
		
	void setBindAllParametersForTopLink(Boolean bindAllParameters) {
		this.bindAllParameters = new TriStateBoolean(bindAllParameters);
	}
	
	private Boolean getCacheStatementForTopLink() {
		return this.cacheStatement.getValue();
	}
	
	private void setCacheStatementForTopLink(Boolean cacheStatement) {		
		this.cacheStatement = new TriStateBoolean(cacheStatement);
	}

}
