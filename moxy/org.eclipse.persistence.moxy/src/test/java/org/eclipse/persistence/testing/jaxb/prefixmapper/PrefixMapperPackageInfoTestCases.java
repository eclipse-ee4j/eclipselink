/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/16/2018-2.7.2 Radek Felcman
//       - 531349 - @XmlSchema Prefix is not honoured if root element is nil

package org.eclipse.persistence.testing.jaxb.prefixmapper;

import java.io.StringWriter;
import java.util.HashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.prefixmapper.packageinfonamespace.EmailAddress;
import org.eclipse.persistence.testing.jaxb.prefixmapper.packageinfonamespace.ObjectFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;


public class PrefixMapperPackageInfoTestCases extends OXTestCase {

    private static final String EXPECTED_ROOT_NAME = "PRE:emailAddress-Root";

    public PrefixMapperPackageInfoTestCases(String name) {
        super(name);
    }

    public void testMarshalWithPackageInfoNamespacePrefix() throws Exception  {

        final ObjectFactory of = new ObjectFactory();
        final JAXBElement<EmailAddress> o = of.createEmailAddress(null);

        final JAXBContext ctx = JAXBContextFactory.createContext(new Class<?>[] { ObjectFactory.class, EmailAddress.class}, new HashMap<>());

        final StringWriter writer = new StringWriter();
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.marshal(o, writer);

        assertTrue("Expected: " + EXPECTED_ROOT_NAME + " But was: " + writer.toString(), writer.toString().indexOf(EXPECTED_ROOT_NAME) != -1);

    }

}
