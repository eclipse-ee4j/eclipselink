/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - February 2012

package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class URITestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri/uri.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri/uri.json";

    public URITestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        //setWriteControlDocument(XML_WRITE_RESOURCE);
    }

    public Object getControlObject(){

        JAXBElement<URI> jbe = null;
        try {
            jbe = new JAXBElement<URI>(new QName("foo"), URI.class, new URI("http://foo/bar"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }

        return jbe;
    }

    public boolean isUnmarshalTest() {
        return false;
    }

}
