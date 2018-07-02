/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.queries;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryRedirectorsMetadata;
import org.eclipse.persistence.jpa.config.QueryRedirectors;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class QueryRedirectorsImpl extends MetadataImpl<QueryRedirectorsMetadata> implements QueryRedirectors {

    public QueryRedirectorsImpl() {
        super(new QueryRedirectorsMetadata());
    }

    public QueryRedirectors setAllQueriesRedirector(String defaultRedirector) {
        getMetadata().setDefaultQueryRedirectorName(defaultRedirector);
        return this;
    }

    public QueryRedirectors setDeleteRedirector(String deleteRedirector) {
        getMetadata().setDefaultDeleteObjectQueryRedirectorName(deleteRedirector);
        return this;
    }

    public QueryRedirectors setInsertRedirector(String insertRedirector) {
        getMetadata().setDefaultInsertObjectQueryRedirectorName(insertRedirector);
        return this;
    }

    public QueryRedirectors setReadAllRedirector(String readAllRedirector) {
        getMetadata().setDefaultReadAllQueryRedirectorName(readAllRedirector);
        return this;
    }

    public QueryRedirectors setReadObjectRedirector(String readObjectRedirector) {
        getMetadata().setDefaultReadObjectQueryRedirectorName(readObjectRedirector);
        return this;
    }

    public QueryRedirectors setReportRedirector(String reportRedirector) {
        getMetadata().setDefaultReportQueryRedirectorName(reportRedirector);
        return this;
    }

    public QueryRedirectors setUpdateRedirector(String updateRedirector) {
        getMetadata().setDefaultUpdateObjectQueryRedirectorName(updateRedirector);
        return this;
    }

}
