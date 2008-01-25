/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

public interface MWReadQuery extends MWQuery {
	
	boolean isRefreshIdentityMapResult();
	void setRefreshIdentityMapResult(boolean refreshIdentityMapResult);
		String REFRESH_IDENTITY_MAP_RESULT_PROPERTY = "refreshIdentityMapResult";
	
	boolean isRefreshRemoteIdentityMapResult();
	void setRefreshRemoteIdentityMapResult(boolean refreshRemoteIdentityMapResult);
		String REFRESH_REMOTE_IDENTITY_MAP_RESULT_PROPERTY = "refreshRemoteIdentityMapResult";
		
	boolean isUseWrapperPolicy();
	void setUseWrapperPolicy(boolean useWrapperPolicy);
		String USE_WRAPPER_POLICY_PROPERTY = "useWrapperPolicy";

	boolean isMaintainCache();
	void setMaintainCache(boolean maintainCache);
		String MAINTAIN_CACHE_PROPERTY = "maintainCache";

}
