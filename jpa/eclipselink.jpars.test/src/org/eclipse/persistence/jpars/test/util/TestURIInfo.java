/****************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;

public class TestURIInfo implements UriInfo {

    protected List<PathSegment> pathSegments = new ArrayList<PathSegment>();
    protected MultivaluedMap<String, String> queryParameters = new StringKeyIgnoreCaseMultivaluedMap<String>();
    
    public TestURIInfo(){
        PathSegment segment = new PathSegment() {
                
            MultivaluedMap<String, String> matrixParameters = new MultivaluedMapImpl();
                
            @Override
            public String getPath() {
                // TODO Auto-generated method stub
                return null;
            }
                
            @Override
            public MultivaluedMap<String, String> getMatrixParameters() {
                // TODO Auto-generated method stub
                return matrixParameters;
            }
        };
        pathSegments.add(segment);
    }
    
    @Override
    public URI getAbsolutePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI getBaseUri() {
        // TODO Auto-generated method stub
        try{
        return new URI("http://localhost:8080/app/jpa-rs/");
        } catch (URISyntaxException e){
            return null;
        }
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMatchedResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMatchedURIs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMatchedURIs(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPath(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        // TODO Auto-generated method stub
        return pathSegments;
    }

    @Override
    public List<PathSegment> getPathSegments(boolean arg0) {
        // TODO Auto-generated method stub
        return pathSegments;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        // TODO Auto-generated method stub
        return queryParameters;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean arg0) {
        // TODO Auto-generated method stub
        return queryParameters;
    }

    @Override
    public URI getRequestUri() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void addMatrixParameter(String key, String value){
        PathSegment segment = pathSegments.get(pathSegments.size() - 1);
        List<String> parameters = segment.getMatrixParameters().get(key);
        if (parameters == null){
            parameters = new ArrayList<String>();
        }
        parameters.add(value);
        segment.getMatrixParameters().put(key, parameters);
    }

}
