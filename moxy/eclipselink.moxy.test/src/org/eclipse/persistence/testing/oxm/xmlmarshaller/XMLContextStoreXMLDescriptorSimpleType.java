/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.StringWriter;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLContextTestProject;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

public class XMLContextStoreXMLDescriptorSimpleType extends XMLContextConstructorUsingXMLSessionConfigLoader {
    public XMLContextStoreXMLDescriptorSimpleType(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLContextStoreXMLDescriptorSimpleType" };
        TestRunner.main(arguments);
    }

    public void testMultipleSessionNames1() {
        XMLContext context = new XMLContext(new XMLContextTestProject());

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new EmailAddress(), writer1);
    }
}
