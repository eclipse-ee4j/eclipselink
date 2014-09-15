/****************************************************************************
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - Initial implementation
 ******************************************************************************/
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

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;

/**
 * A fake HTTPHeaders implementation to test the service class
 *
 * @author tware
 */
public class TestHttpHeaders implements HttpHeaders {
    protected List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
    protected MultivaluedMap<String, String> requestHeaders = new StringKeyIgnoreCaseMultivaluedMap<String>();

    public static HttpHeaders generateHTTPHeader(MediaType acceptableMedia, String mediaTypeString) {
        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(acceptableMedia);
        List<String> mediaTypes = new ArrayList<String>();
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
    public MultivaluedMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
