/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace;

import java.io.InputStream;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class RootElementIdentifiedByNamespaceTestCases extends OXTestCase {
    private final static String XML_RESOURCE_EMAIL = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbynamespace/EmailAddress.xml";
    private final static String XML_RESOURCE_MAILING = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbynamespace/MailingAddress.xml";
    private final static String XML_RESOURCE_BILLING = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbynamespace/BillingAddress.xml";
    private XMLContext context;
    private XMLUnmarshaller unmarshaller;

    public RootElementIdentifiedByNamespaceTestCases(String name) {
        super(name);
        context = getXMLContext(new RootElementIdentifiedByNamespaceProject());
        unmarshaller = context.createUnmarshaller();
    }

    public void testReadEmailAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_EMAIL);
            EmailAddress testObject = (EmailAddress)unmarshaller.unmarshal(inputStream);
        } catch (ClassCastException e) {
            fail();
        }
    }

    public void testReadMailingAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_MAILING);
            MailingAddress testObject = (MailingAddress)unmarshaller.unmarshal(inputStream);
        } catch (ClassCastException e) {
            fail();
        }
    }

    public void testReadBillingAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_BILLING);
            Object testObject = unmarshaller.unmarshal(inputStream);
            fail();
        } catch (XMLMarshalException e) {
            // PASS - an XMLMarshalException should be caught
        }
    }
}
