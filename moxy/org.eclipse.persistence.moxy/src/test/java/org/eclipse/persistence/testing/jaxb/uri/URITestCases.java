/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - 2.4 - February 2012

package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.net.URISyntaxException;

import jakarta.xml.bind.JAXBElement;
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
