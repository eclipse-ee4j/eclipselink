/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation
package org.eclipse.persistence.jpars.test.util;

import org.eclipse.persistence.jpa.rs.PersistenceContext;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Convenient helper class used to construct JPA-RS URIs.
 *
 * @author Dmitry Kornilov
 */
public class UriBuilder {
    private String serverUri;
    private String pu;
    private String version;
    private String resource;
    private String parameters;
    private String hints;
    private String tenantInfo;

    public UriBuilder(PersistenceContext context) throws URISyntaxException {
        String uri = RestUtils.getServerURI().toString();
        serverUri = uri.substring(0, uri.length() - (uri.endsWith("/") ? 1 : 0));
        version = context.getVersion();
        pu = context.getName();
    }

    public UriBuilder addPu(String persistenceUnit) {
        pu = persistenceUnit;
        return this;
    }

    public UriBuilder addEntity(String entityClassName) {
        resource = "entity/" + entityClassName;
        return this;
    }

    public UriBuilder addEntity(String entityClassName, Object entityId) {
        resource = "entity/" + entityClassName + "/" + entityId;
        return this;
    }

    public UriBuilder addEntity(String entityClassName, Object entityId, String attribute) {
        addEntity(entityClassName, entityId);
        resource = resource + "/" + attribute;
        return this;
    }

    public UriBuilder addQuery(String queryName) {
        resource = "query/" + queryName;
        return this;
    }

    public UriBuilder addSingleResultQuery(String queryName) {
        resource = "singleResultQuery/" + queryName;
        return this;
    }

    public UriBuilder addTenantInfo(Map<String, String> tenantId) {
        if (tenantId != null) {
            final StringBuilder tenantBuilder = new StringBuilder();
            for (String key : tenantId.keySet()) {
                tenantBuilder.append(";" + key + "=" + tenantId.get(key));
            }
            tenantInfo = tenantBuilder.toString();
        }
        return this;
    }

    public UriBuilder addParameters(Map<String, Object> paramsMap) {
        if (paramsMap != null && !paramsMap.isEmpty()) {
            final StringBuilder paramsBuilder = new StringBuilder();
            for (String key : paramsMap.keySet()) {
                paramsBuilder.append(";" + key + "=" + paramsMap.get(key));
            }
            parameters = paramsBuilder.toString();
        }
        return this;
    }

    public UriBuilder addHints(Map<String, String> hintsMap) {
        if (hintsMap != null && !hintsMap.isEmpty()) {
            final StringBuilder hintsBuilder = new StringBuilder();
            boolean firstElement = true;
            for (String key : hintsMap.keySet()) {
                if (firstElement) {
                    hintsBuilder.append("?");
                    firstElement = false;
                } else {
                    hintsBuilder.append("&");
                }
                hintsBuilder.append(key + "=" + hintsMap.get(key));
            }
            hints = hintsBuilder.toString();
        }
        return this;
    }

    public String build() {
        final StringBuilder uri = new StringBuilder();
        uri.append(serverUri);
        if (version != null) {
            uri.append("/").append(version);
        }
        if (pu != null) {
            uri.append("/").append(pu);
            if (tenantInfo != null) {
                uri.append(tenantInfo);
            }
            if (resource != null) {
                uri.append("/").append(resource);
            }
        }
        if (parameters != null) {
            uri.append(parameters);
        }
        if (hints != null) {
            uri.append(hints);
        }
        return uri.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
