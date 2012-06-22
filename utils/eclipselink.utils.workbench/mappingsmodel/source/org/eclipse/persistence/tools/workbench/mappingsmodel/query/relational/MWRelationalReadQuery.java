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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTypeNames;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery.CacheUsageModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery.InMemoryQueryIndirectionPolicyModel;


public interface MWRelationalReadQuery extends MWReadQuery, MWRelationalQuery, MWTypeNames {

		
	InMemoryQueryIndirectionPolicyModel getInMemoryQueryIndirectionPolicy();
	void setInMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicyModel inMemoryQueryIndirectionPolicyModel);
		String THROW_INDIRECTION_EXCEPTION = "Throw Indirection Exception";
		String TRIGGER_INDIRECTION = "Trigger Indirection";
		String IGNORE_EXCEPTION_RETURN_CONFORMED = "Ignore Exception Return Conformed";
		String IGNORE_EXCEPTION_RETURN_NOT_CONFORMED = "Ignore Exception Return Not Conformed";
		String IN_MEMORY_QUERY_INDIRECTION_PROPERTY = "inMemoryQueryIndirectionPolicy";

	CacheUsageModel getCacheUsage();
	void setCacheUsage(CacheUsageModel model);
		String UNDEFINED_CACHE_USAGE = "Undefined";	
		String CHECK_CACHE_BY_EXACT_PRIMARY_KEY = "Check Cache by Exact Primary Key";
		String CHECK_CACHE_BY_PRIMARY_KEY = "Check Cache by Primary Key";
		String CHECK_CACHE_ONLY = "Check Cache Only";
		String CHECK_CACHE_THEN_DATABASE = "Check Cache Then Database";
		String CONFORM_RESULTS_IN_UNIT_OF_WORK = "Conform Results in Unit of Work";
		String DO_NOT_CHECK_CACHE = "Do Not Check Cache";
		String CACHE_USAGE_PROPERTY = "cacheUsage" ;

	
	String JOINED_ITEMS_LIST = "joinedItems";

}
