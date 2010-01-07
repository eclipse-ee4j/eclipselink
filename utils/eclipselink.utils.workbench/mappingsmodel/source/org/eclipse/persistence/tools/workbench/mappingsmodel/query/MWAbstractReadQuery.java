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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.List;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public abstract class MWAbstractReadQuery 
	extends MWAbstractQuery
	implements MWReadQuery {

	/**
	 * Allows for the resulting objects to be refresh with the data from the database.
	 */
	private volatile boolean refreshIdentityMapResult;

	private volatile boolean refreshRemoteIdentityMapResult;

	private volatile boolean useWrapperPolicy;

	/**
	 * Flag used for a query to bypass the identitymap and unit of work.
	 */
	private volatile boolean maintainCache;


	
	//*********** static methods ************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractReadQuery.class);
			
		((InheritancePolicy)descriptor.getInheritancePolicy()).setParentClass(MWAbstractQuery.class);
		
		XMLDirectMapping maintainCacheMapping = new XMLDirectMapping();
		maintainCacheMapping.setAttributeName("maintainCache");
		maintainCacheMapping.setXPath("maintain-cache/text()");
		maintainCacheMapping.setNullValue(Boolean.TRUE);
		descriptor.addMapping(maintainCacheMapping);

		XMLDirectMapping refreshIdentityMapResultMapping = new XMLDirectMapping();
		refreshIdentityMapResultMapping.setAttributeName("refreshIdentityMapResult");
		refreshIdentityMapResultMapping.setXPath("refresh-identity-map-result/text()");
		refreshIdentityMapResultMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(refreshIdentityMapResultMapping);

		XMLDirectMapping refreshRemoteIdentityMapResultMapping = new XMLDirectMapping();
		refreshRemoteIdentityMapResultMapping.setAttributeName("refreshRemoteIdentityMapResult");
		refreshRemoteIdentityMapResultMapping.setXPath("refresh-remote-identity-map-result/text()");
		refreshRemoteIdentityMapResultMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(refreshRemoteIdentityMapResultMapping);

		XMLDirectMapping useWrapperPolicyMapping = new XMLDirectMapping();
		useWrapperPolicyMapping.setAttributeName("useWrapperPolicy");
		useWrapperPolicyMapping.setXPath("use-wrapper-policy/text()");
		useWrapperPolicyMapping.setNullValue(Boolean.TRUE);
		descriptor.addMapping(useWrapperPolicyMapping);

		return descriptor;
	}
	
	
	/** Default constructor - for TopLink use only. */			
	protected MWAbstractReadQuery() {
		super();
	}

	protected MWAbstractReadQuery(MWQueryManager queryManager, String name) {
		super(queryManager, name);
	}

	/** initialize persistent state*/
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.refreshIdentityMapResult = false;
		this.refreshRemoteIdentityMapResult = false;
		this.useWrapperPolicy = true;
		this.maintainCache = true;
	}
		
	
	// ************* morphing ***********

	public void initializeFrom(MWReadQuery query) {
		super.initializeFrom(query);
		setMaintainCache(query.isMaintainCache());
		setRefreshIdentityMapResult(query.isRefreshIdentityMapResult());
		setRefreshRemoteIdentityMapResult(query.isRefreshRemoteIdentityMapResult());
		setUseWrapperPolicy(query.isUseWrapperPolicy());
	}
	
	
	// ************* maintain cache ***********
	
	public boolean isMaintainCache() {
		return this.maintainCache;
	}
		
	public void setMaintainCache(boolean maintainCache) {
		boolean oldMaintainCache = isMaintainCache();
		this.maintainCache = maintainCache;
		firePropertyChanged(MAINTAIN_CACHE_PROPERTY, oldMaintainCache, maintainCache);
	}

	// ************* refresh identity map result ***********
	
	public boolean isRefreshIdentityMapResult() {
		return this.refreshIdentityMapResult;
	}
	
	public void setRefreshIdentityMapResult(boolean refreshIdentityMapResult) {
		boolean oldRefreshIdentityMapResult = isRefreshIdentityMapResult();
		this.refreshIdentityMapResult = refreshIdentityMapResult;
		setRefreshRemoteIdentityMapResult(refreshIdentityMapResult);
		firePropertyChanged(REFRESH_IDENTITY_MAP_RESULT_PROPERTY, oldRefreshIdentityMapResult, refreshIdentityMapResult);
	}

	// ************* refresh remote identity map result ***********

	public boolean isRefreshRemoteIdentityMapResult() {
		return this.refreshRemoteIdentityMapResult;
	}
	
	
	public void setRefreshRemoteIdentityMapResult(boolean refreshRemoteIdentityMapResult) {
		boolean oldRefreshRemoteIdentityMapResult = isRefreshRemoteIdentityMapResult();
		this.refreshRemoteIdentityMapResult = refreshRemoteIdentityMapResult;
		firePropertyChanged(REFRESH_REMOTE_IDENTITY_MAP_RESULT_PROPERTY, oldRefreshRemoteIdentityMapResult, refreshRemoteIdentityMapResult);
	}
	
	// ************* use wraper policy ***********
	
	public boolean isUseWrapperPolicy() {
		return this.useWrapperPolicy;
	}
		
	public void setUseWrapperPolicy(boolean useWrapperPolicy) {
		boolean oldUseWrapperPolicy = isUseWrapperPolicy();
		this.useWrapperPolicy = useWrapperPolicy;
		firePropertyChanged(USE_WRAPPER_POLICY_PROPERTY, oldUseWrapperPolicy, useWrapperPolicy);
	}

	
	// ************* rules **************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkDoesNotMaintainCacheButDoesRefreshIdentityMapResults(currentProblems);
		this.checkRefreshesIdentityButDoesNotRemoteIdentityMapResults(currentProblems);
	}
	
	private void checkDoesNotMaintainCacheButDoesRefreshIdentityMapResults(List currentProblems) {
		if ( ! this.isMaintainCache()) {
			if (this.isRefreshIdentityMapResult()) {
				currentProblems.add(this.buildProblem(
											ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE, 
											this.getName()));	
			}
			if (this.isRefreshRemoteIdentityMapResult()) {
				currentProblems.add(this.buildProblem(
											ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_REMOTE_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE, 
											this.getName()));	
			}
		}
	}			
	
	private void checkRefreshesIdentityButDoesNotRemoteIdentityMapResults(List currentProblems) {
		if (this.isRefreshIdentityMapResult() && ! this.isRefreshRemoteIdentityMapResult()) {
			currentProblems.add(this.buildProblem(
									ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_REFRESHING_REMOTE_IDENTITY_MAP, 
									this.getName()));		
		}
	}
	
	// ************* runtime conversion ***********

	public DatabaseQuery runtimeQuery() {
		ObjectLevelReadQuery runtimeQuery = (ObjectLevelReadQuery) super.runtimeQuery();
		runtimeQuery.setShouldMaintainCache(isMaintainCache());
		runtimeQuery.setShouldRefreshIdentityMapResult(isRefreshIdentityMapResult());
		runtimeQuery.setShouldRefreshRemoteIdentityMapResult(isRefreshRemoteIdentityMapResult());
		runtimeQuery.setShouldUseWrapperPolicy(isUseWrapperPolicy());
		
		return runtimeQuery;
	}
	
	public void adjustFromRuntime(ObjectLevelReadQuery runtimeQuery) {
		super.adjustFromRuntime(runtimeQuery);
		setMaintainCache(runtimeQuery.shouldMaintainCache());
		setRefreshIdentityMapResult(runtimeQuery.shouldRefreshIdentityMapResult());
		setRefreshRemoteIdentityMapResult(runtimeQuery.shouldRefreshRemoteIdentityMapResult());
		setUseWrapperPolicy(runtimeQuery.shouldUseWrapperPolicy());
		

		
	}
	
}
