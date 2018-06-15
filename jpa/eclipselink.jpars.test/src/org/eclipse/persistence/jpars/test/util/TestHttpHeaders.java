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
package org.eclipse.persistence.jpars.test.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;

/**
 * A fake HTTPHeaders implementation to test the service class
 *
 * @author tware
 */
public class TestHttpHeaders implements HttpHeaders {
    protected List<MediaType> acceptableMediaTypes = new ArrayList<>();
    protected MultivaluedMap<String, String> requestHeaders = new StringKeyIgnoreCaseMultivaluedMap<>();

    public static HttpHeaders generateHTTPHeader(MediaType acceptableMedia, String mediaTypeString) {
        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(acceptableMedia);
        List<String> mediaTypes = new ArrayList<>();
        mediaTypes.add(mediaTypeString);

        headers.getRequestHeaders().put(HttpHeaders.CONTENT_TYPE, mediaTypes);
        return headers;
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        return null;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return acceptableMediaTypes;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return null;
    }

    @Override
    public Date getDate() {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Locale getLanguage() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public List<String> getRequestHeader(String arg0) {
        return requestHeaders.get(arg0);
    }

    @Override
    public String getHeaderString(String s) {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
