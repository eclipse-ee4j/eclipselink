/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
