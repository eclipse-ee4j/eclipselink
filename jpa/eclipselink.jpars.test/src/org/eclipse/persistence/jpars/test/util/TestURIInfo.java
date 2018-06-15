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
//      tware - Initial implementation
//      Dmitry Kornilov - upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;

public class TestURIInfo implements UriInfo {

    protected List<PathSegment> pathSegments = new ArrayList<>();
    protected MultivaluedMap<String, String> queryParameters = new StringKeyIgnoreCaseMultivaluedMap<>();

    public TestURIInfo() {
    }

    @Override
    public URI getAbsolutePath() {
        return null;
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return null;
    }

    @Override
    public URI getBaseUri() {
        try {
            return RestUtils.getServerURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        return null;
    }

    @Override
    public List<Object> getMatchedResources() {
        return null;
    }

    @Override
    public List<String> getMatchedURIs() {
        return null;
    }

    @Override
    public List<String> getMatchedURIs(boolean arg0) {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getPath(boolean arg0) {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean arg0) {
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return pathSegments;
    }

    @Override
    public List<PathSegment> getPathSegments(boolean arg0) {
        return pathSegments;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean arg0) {
        return queryParameters;
    }

    @Override
    public URI getRequestUri() {
        // adding a dummy implementation so that printing uriInfo in the web service methods
        // that TestService calls wouldn't throw null pointer exception
        return getBaseUri();
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return null;
    }

    public void addMatrixParameter(String segment, String key, String value) {
        PathSegment segmentToUse = null;
        for (PathSegment pathSegment : pathSegments) {
            if (pathSegment.getPath().equals(segment)) {
                segmentToUse = pathSegment;
            }
        }
        if (segmentToUse == null) {
            segmentToUse = new TestPathSegment(segment);
            pathSegments.add(segmentToUse);
        }

        List<String> parameters = segmentToUse.getMatrixParameters().get(key);
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(value);
        segmentToUse.getMatrixParameters().put(key, parameters);
    }

    public class TestPathSegment implements PathSegment {
        private MultivaluedMap<String, String> matrixParameters = new MultivaluedStringMap();
        private String path;

        public TestPathSegment(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public MultivaluedMap<String, String> getMatrixParameters() {
            // TODO Auto-generated method stub
            return matrixParameters;
        }
    }

    public URI relativize(URI arg0) {
        return null;
    }

    public URI resolve(URI arg0) {
        return null;
    }
}
