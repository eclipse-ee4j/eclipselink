/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4.2
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;


public class InheritanceNoRootTestCases extends JAXBWithJSONTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/norootelement/inheritance.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/norootelement/inheritance.xml";

    public InheritanceNoRootTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Person.class});
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        initXsiType();
    }

    protected Object getJSONReadControlObject() {

        Customer c = new Customer();
        c.name = "theName";

        QName name = new QName("");

        JAXBElement<Object> jbe = new JAXBElement<Object>(name, Object.class, c );
        return jbe;
    }

    protected Object getControlObject() {
        Customer c = new Customer();
        c.name = "theName";
        return c;
    }

}
